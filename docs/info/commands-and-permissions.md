# ⌨️Commands & Permissions

## Commands <a href="#commands" id="commands"></a>

`/enchantmentslots` can be shortened to `/es`. Arguments in `<angle brackets>`
are required, while arguments in `[square brackets]` are optional.

| Command | Description | Available to |
| --- | --- | --- |
| `/es help` | View plugin help. | Players and console |
| `/es reload` | Reload the plugin configuration and language files. | Players and console |
| `/es setslots [amount]` | Set the held item's base enchantment slots. When `amount` is omitted, one slot is added. | Players |
| `/es giveslots [amount]` | Add enchantment slots to the held item. When `amount` is omitted, one slot is added. | Players |
| `/es giveslots <itemID> <playerName> [amount]` | Give a configured extra-slot item to a player. | Players and console |
| `/es givedeenchanteritem <itemID> [playerName] [amount]` | Give a configured deenchanter item. The console must specify `playerName`. | Players and console |
| `/es giveremoveslotitem <itemID> [playerName] [amount]` | Give a configured slot-remover item. The console must specify `playerName`. | Players and console |
| `/es toggledescription` | Toggle enchantment descriptions for the player. | Players |
| `/es openenchantgui` | Open the enchantment GUI. | Players |
| `/es openenchantgui <playerName>` | Open the enchantment GUI for a player. | Console |

`/es toggledescription` is registered only when both of these options are
enabled:

```yaml
settings:
  add-lore:
    placeholder:
      enchants:
        description:
          enabled: true
          command-toggle: true
```

`/es openenchantgui` is registered only when `enchant-gui.enabled` is enabled.

## Permissions <a href="#permissions" id="permissions"></a>

| Permission | Commands or feature |
| --- | --- |
| None | `/es help` |
| `enchantmentslots.admin` | `/es reload` |
| `enchantmentslots.setslots` | `/es setslots` |
| `enchantmentslots.giveslots` | Both forms of `/es giveslots` |
| `enchantmentslots.givedeenchanteritem` | `/es givedeenchanteritem` |
| `enchantmentslots.giveremoveslotitem` | `/es giveremoveslotitem` |
| `enchantmentslots.toggledescription` | `/es toggledescription` |
| `enchantmentslots.openenchantgui` | `/es openenchantgui` |
| `enchantmentslots.displaydescription` | Display enchantment descriptions when `description.require-permission` is enabled. This is separate from the command permission. |
