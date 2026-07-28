## 2026-07-25 - GUI Sound Boundaries
**Learning:** Auditory feedback in Bukkit GUIs significantly improves UX when reaching input limits.
**Action:** Use negative sound effects like `ENTITY_VILLAGER_NO` when users attempt to push settings past their bounds, rather than repeating positive click sounds.
## 2024-05-19 - GUI Visual Affordance
**Learning:** Adding enchantment glint to specific action buttons (like a primary 'Confirm' button) creates a strong visual affordance in Bukkit GUIs, highlighting the primary action clearly for users.
**Action:** Use `ItemMeta.setEnchantmentGlintOverride(true)` on items that act as primary call-to-action buttons in Minecraft interfaces to draw attention and indicate readiness.

## 2026-07-28 - Dual-Channel Feedback in Real-Time Environments
**Learning:** In fast-paced, real-time gaming environments, visual-only UI changes (like boss bars updating or scoreboards appearing) are often missed by users due to high cognitive load or visual impairments. The UI needs dual-channel feedback to ensure state changes are noticed.
**Action:** Whenever introducing a significant UI state change or time-sensitive event, pair visual updates with appropriate auditory cues to improve accessibility and awareness.
