## 2026-07-25 - GUI Sound Boundaries
**Learning:** Auditory feedback in Bukkit GUIs significantly improves UX when reaching input limits.
**Action:** Use negative sound effects like `ENTITY_VILLAGER_NO` when users attempt to push settings past their bounds, rather than repeating positive click sounds.
## 2024-05-19 - GUI Visual Affordance
**Learning:** Adding enchantment glint to specific action buttons (like a primary 'Confirm' button) creates a strong visual affordance in Bukkit GUIs, highlighting the primary action clearly for users.
**Action:** Use `ItemMeta.setEnchantmentGlintOverride(true)` on items that act as primary call-to-action buttons in Minecraft interfaces to draw attention and indicate readiness.
## 2024-05-15 - Shift-Click for Faster GUI Value Modification
**Learning:** In Minecraft GUIs, adjusting time values that span a large range (e.g., 15-120 seconds) using only small increments (+/- 5s) forces repetitive clicking, which degrades the UX.
**Action:** Always consider adding a modifier key (like Shift-Click) to allow for larger, faster value adjustments when building configuration GUIs with wide value ranges.
