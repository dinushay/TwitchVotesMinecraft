## 2026-07-25 - GUI Sound Boundaries
**Learning:** Auditory feedback in Bukkit GUIs significantly improves UX when reaching input limits.
**Action:** Use negative sound effects like `ENTITY_VILLAGER_NO` when users attempt to push settings past their bounds, rather than repeating positive click sounds.
## 2024-05-19 - GUI Visual Affordance
**Learning:** Adding enchantment glint to specific action buttons (like a primary 'Confirm' button) creates a strong visual affordance in Bukkit GUIs, highlighting the primary action clearly for users.
**Action:** Use `ItemMeta.setEnchantmentGlintOverride(true)` on items that act as primary call-to-action buttons in Minecraft interfaces to draw attention and indicate readiness.

## 2024-08-02 - Dual-Channel Feedback for Countdowns
**Learning:** In Minecraft plugins, visual cues like BossBar updates for countdowns can easily be missed during intensive gameplay.
**Action:** Always pair critical visual UI state changes (like the final seconds of a timer or a voting phase concluding) with non-obtrusive, familiar audio cues (e.g., `UI_BUTTON_CLICK` for ticking, `ENTITY_PLAYER_LEVELUP` for success) to create accessible, dual-channel feedback.
