## 2024-05-14 - Minecraft Settings GUI Improvements
**Learning:** In Minecraft chest GUIs, players shouldn't be forced to rely on keyboard keys (Esc/E) to close menus. Explicit exit buttons (especially in the bottom right corner, slot 26) improve accessibility. Furthermore, changing background items (like stained glass panes) provides powerful, ambient inline validation for configuration screens.
**Action:** Always consider adding explicit "Close" or "Back" buttons to chest-based UIs and leverage background block colors (e.g., Red vs. Gray stained glass) for ambient state feedback.

## 2024-05-15 - Minecraft Settings GUI Power User Interactions
**Learning:** In Minecraft chest GUIs with incremental numerical configuration (like time settings), forcing users to repeatedly click for small increments can cause physical strain and frustration. Supporting power-user modifiers (like Shift-Click) for larger jumps (e.g., ±15s instead of ±5s) significantly improves usability.
**Action:** When designing configuration items that require multiple clicks to reach a desired value, implement Shift-Click modifiers for larger deltas and ensure this interaction is clearly documented in the item's lore tooltip.
