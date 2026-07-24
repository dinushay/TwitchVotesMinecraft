package twitchvotesminecraft.twitch;

import twitchvotesminecraft.App;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.time.Duration;
import java.util.concurrent.CompletionStage;
import java.util.function.BiConsumer;

public class TwitchChatClient implements WebSocket.Listener {

    private static final String WS_URL = "wss://irc-ws.chat.twitch.tv:443";
    private final App plugin;
    private final String channel;
    private final BiConsumer<String, String> messageConsumer;
    private WebSocket webSocket;
    private StringBuilder buffer = new StringBuilder();

    public TwitchChatClient(App plugin, String channel, BiConsumer<String, String> messageConsumer) {
        this.plugin = plugin;
        this.channel = channel.toLowerCase().trim();
        this.messageConsumer = messageConsumer;
    }

    public void connect() {
        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            client.newWebSocketBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .buildAsync(URI.create(WS_URL), this)
                    .thenAccept(ws -> {
                        this.webSocket = ws;
                        int randomId = 10000 + (int)(Math.random() * 89999);
                        ws.sendText("NICK justinfan" + randomId, true);
                        ws.sendText("JOIN #" + channel, true);
                        plugin.getLogger().info("Connected to Twitch chat channel: #" + channel);
                    })
                    .exceptionally(ex -> {
                        plugin.getLogger().warning("Failed to connect to Twitch chat: " + ex.getMessage());
                        return null;
                    });
        } catch (Exception e) {
            plugin.getLogger().warning("Error initializing Twitch chat connection: " + e.getMessage());
        }
    }

    public void disconnect() {
        if (webSocket != null) {
            try {
                webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "Plugin shutting down");
            } catch (Exception ignored) {}
        }
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        buffer.append(data);
        if (last) {
            String fullMessage = buffer.toString();
            buffer = new StringBuilder();
            String[] lines = fullMessage.split("\r?\n");
            for (String line : lines) {
                parseIrcLine(line);
            }
        }
        webSocket.request(1);
        return null;
    }

    private void parseIrcLine(String line) {
        if (line.startsWith("PING")) {
            if (webSocket != null) {
                webSocket.sendText("PONG :tmi.twitch.tv", true);
            }
            return;
        }

        // IRC PRIVMSG line format: :username!username@username.tmi.twitch.tv PRIVMSG #channel :message
        if (line.contains("PRIVMSG #" + channel + " :")) {
            try {
                int userEndIdx = line.indexOf("!");
                if (userEndIdx > 1 && line.startsWith(":")) {
                    String username = line.substring(1, userEndIdx);
                    int msgIdx = line.indexOf("PRIVMSG #" + channel + " :");
                    if (msgIdx != -1) {
                        String message = line.substring(msgIdx + ("PRIVMSG #" + channel + " :").length());
                        messageConsumer.accept(username, message);
                    }
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Error parsing IRC line: " + e.getMessage());
            }
        }
    }
}
