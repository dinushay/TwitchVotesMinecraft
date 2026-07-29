## 2026-07-25 - GUI Sound Boundaries
**Learning:** Auditory feedback in Bukkit GUIs significantly improves UX when reaching input limits.
**Action:** Use negative sound effects like `ENTITY_VILLAGER_NO` when users attempt to push settings past their bounds, rather than repeating positive click sounds.
## 2024-05-19 - GUI Visual Affordance
**Learning:** Adding enchantment glint to specific action buttons (like a primary 'Confirm' button) creates a strong visual affordance in Bukkit GUIs, highlighting the primary action clearly for users.
**Action:** Use `ItemMeta.setEnchantmentGlintOverride(true)` on items that act as primary call-to-action buttons in Minecraft interfaces to draw attention and indicate readiness.
## 2024-07-29 - Dual Channel Feedback for Voting Phases
**Learning:** Relying solely on a boss bar or scoreboard for voting timers lacks sufficient affordance, especially when players are distracted by gameplay. Pairing countdown visual updates with distinct Bukkit sounds enhances situational awareness and improves accessibility.
**Action:** Use audio cues (e.g., PLING for start, UI_BUTTON_CLICK for ticking countdowns, LEVELUP for completion) in tandem with visual elements like BossBars to create a comprehensive dual-channel feedback loop for time-sensitive UI components in Minecraft plugins.
