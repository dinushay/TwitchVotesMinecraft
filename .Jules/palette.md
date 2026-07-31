## 2026-07-25 - GUI Sound Boundaries
**Learning:** Auditory feedback in Bukkit GUIs significantly improves UX when reaching input limits.
**Action:** Use negative sound effects like `ENTITY_VILLAGER_NO` when users attempt to push settings past their bounds, rather than repeating positive click sounds.
## 2024-05-19 - GUI Visual Affordance
**Learning:** Adding enchantment glint to specific action buttons (like a primary 'Confirm' button) creates a strong visual affordance in Bukkit GUIs, highlighting the primary action clearly for users.
**Action:** Use `ItemMeta.setEnchantmentGlintOverride(true)` on items that act as primary call-to-action buttons in Minecraft interfaces to draw attention and indicate readiness.
## 2025-01-20 - Dual-Channel Feedback for Events
**Learning:** Relying solely on visual cues (like BossBar or Scoreboard) for time-critical events (like voting phases ending) can lead to missed context, especially during chaotic gameplay.
**Action:** Pair visual state changes with auditory cues (e.g., ticking sounds for countdowns, distinct chimes for phase starts/ends) to provide dual-channel feedback, improving awareness and accessibility.
