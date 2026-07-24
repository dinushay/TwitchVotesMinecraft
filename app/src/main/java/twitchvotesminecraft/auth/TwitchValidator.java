package twitchvotesminecraft.auth;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public final class TwitchValidator {

    private static final String TWITCH_VALIDATE_URL = "https://id.twitch.tv/oauth2/validate";
    private static final String TWITCH_HELIX_USERS_URL = "https://api.twitch.tv/helix/users?login=";
    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    private TwitchValidator() {}

    public record ValidationResult(boolean isValid, String message) {}

    /**
     * Validates Twitch credentials (client-id, access-token, refresh-token) locally
     * and checks token validity via Twitch OAuth2 API endpoint.
     */
    public static ValidationResult validateCredentials(FileConfiguration config) {
        String clientId = config.getString("twitch.client-id");
        String accessToken = config.getString("twitch.access-token");
        String refreshToken = config.getString("twitch.refresh-token");

        // 1. Check local presence & format
        if (isPlaceholderOrBlank(clientId)) {
            return new ValidationResult(false, "Twitch 'client-id' is missing or set to default placeholder.");
        }
        if (isPlaceholderOrBlank(accessToken)) {
            return new ValidationResult(false, "Twitch 'access-token' is missing or set to default placeholder.");
        }
        if (isPlaceholderOrBlank(refreshToken)) {
            return new ValidationResult(false, "Twitch 'refresh-token' is missing or set to default placeholder.");
        }

        // 2. Perform OAuth validation request against Twitch API
        try (HttpClient client = HttpClient.newBuilder()
                .connectTimeout(TIMEOUT)
                .build()) {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(TWITCH_VALIDATE_URL))
                    .timeout(TIMEOUT)
                    .header("Authorization", "OAuth " + accessToken.trim())
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 401) {
                return new ValidationResult(false, "Twitch access token is invalid or expired (HTTP 401 Unauthorized).");
            }

            if (response.statusCode() != 200) {
                return new ValidationResult(false, "Twitch OAuth validation endpoint returned HTTP " + response.statusCode());
            }

            // Parse response JSON
            JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
            if (!json.has("client_id")) {
                return new ValidationResult(false, "Twitch OAuth validation response did not contain a 'client_id'.");
            }

            String returnedClientId = json.get("client_id").getAsString();
            if (!returnedClientId.equalsIgnoreCase(clientId.trim())) {
                return new ValidationResult(false, "Configured 'client-id' (" + clientId + ") does not match token owner ('" + returnedClientId + "').");
            }

            return new ValidationResult(true, "Twitch credentials are valid.");

        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return new ValidationResult(false, "Failed to connect to Twitch OAuth validation endpoint: " + e.getMessage());
        }
    }

    /**
     * Checks if a Twitch channel / user exists using the Twitch Helix API.
     * @param config Plugin configuration containing client-id and access-token.
     * @param channelName Twitch channel name to look up.
     * @return true if the channel exists on Twitch, false otherwise.
     */
    public static boolean checkChannelExists(FileConfiguration config, String channelName) {
        String clientId = config.getString("twitch.client-id", "").trim();
        String accessToken = config.getString("twitch.access-token", "").trim();

        if (clientId.isEmpty() || accessToken.isEmpty()) {
            return false;
        }

        try (HttpClient client = HttpClient.newBuilder()
                .connectTimeout(TIMEOUT)
                .build()) {

            String authHeader = accessToken.toLowerCase().startsWith("bearer ") ? accessToken : "Bearer " + accessToken;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(TWITCH_HELIX_USERS_URL + channelName.toLowerCase().trim()))
                    .timeout(TIMEOUT)
                    .header("Client-ID", clientId)
                    .header("Authorization", authHeader)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
                if (json.has("data") && json.get("data").isJsonArray()) {
                    JsonArray data = json.getAsJsonArray("data");
                    return data.size() > 0;
                }
            }
        } catch (Exception ignored) {}

        return false;
    }

    private static boolean isPlaceholderOrBlank(String value) {
        if (value == null || value.isBlank()) {
            return true;
        }
        String trimmed = value.trim().toLowerCase();
        return trimmed.contains("your_") || trimmed.contains("placeholder") || trimmed.equals("xyz");
    }
}
