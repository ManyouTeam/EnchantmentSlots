# ⚡Add Lore

Add Lore renders enchantment-slot information, enchantments, and optional enchantment descriptions in an item's lore. The feature is configured under `settings.add-lore` in `config.yml`.

## How lore is added

Items with slot data use `display-value`. An item without slot data can use the separate `display-value-with-no-slot` layout, which is useful when you want to show its enchantments but not slot totals or empty slots.

Newly obtained matching items receive slot data when their item-slot setting has `auto-add-lore: true`. You can also add slot data manually with `/es setslots` or `/es giveslots`.

```yaml
settings:
  add-lore:
    enabled: true
    enable-for-no-slot: true
    display-value:
      - '&eSlots: {used_slot_amount}/{slot_amount}'
      - '{enchants}'
      - '{empty_slots}'
    display-value-with-no-slot:
      - '{enchants}'
```

`enable-for-no-slot` controls whether the no-slot layout is added at all. Items with `HIDE_ENCHANTS` (or `HIDE_STORED_ENCHANTS` for stored enchantments) do not receive the no-slot layout.

## Layout placeholders

Use these values as complete lines in `display-value` and `display-value-with-no-slot`:

| Placeholder          | Result                                                        |
| -------------------- | ------------------------------------------------------------- |
| `{enchants}`         | One or more formatted lore lines for the item's enchantments. |
| `{empty_slots}`      | One formatted line for every unused enchantment slot.         |
| `{slot_amount}`      | Total slots on the item.                                      |
| `{used_slot_amount}` | Slots consumed by the item's enchantments.                    |
| `{enchant_amount}`   | Number of enchantments on the item.                           |

`{enchants}` and `{empty_slots}` must be on their own line to expand into multiple lore lines. For all available placeholder details, see [Built-in Placeholders](built-in-placeholders.md).

Use `{lang}` as a line when the matching list should come from the player's language file rather than `config.yml`. It is resolved through `override-lang.settings.add-lore...` in that language file.

## Formatting enchantments and empty slots

```yaml
settings:
  add-lore:
    placeholder:
      auto-parse: true
      enchants:
        format: '&6  {enchant_name}{enchant_level}{enchant_used_slot}'
        no-slot-format: '&6{enchant_name}{enchant_level}{enchant_used_slot}'
        sort: true
        auto-add-space: true
        level-hide-one: true
      empty-slots:
        format: '&7  — Empty slot —'
```

`format` is used for an item with slot data, while `no-slot-format` is used by `display-value-with-no-slot`. The enchantment format supports `{enchant_name}`, `{enchant_raw_name}`, `{enchant_level}`, `{enchant_level_roman}`, `{enchant_used_slot}`, and `{enchant_description}`.

`sort` uses supported enchantment-plugin ordering when available. `auto-add-space` inserts a leading space before a non-empty level or slot-cost placeholder. `level-hide-one` suppresses the level for enchantments whose maximum level is one.

With `auto-parse: true`, the plugin also replaces Add Lore placeholders already written in an item's existing lore. This is needed when placeholders are placed in item definitions from another plugin. See [Item Placeholder](../features/item-placeholder.md) for usage notes.

## Enchantment descriptions

Description support requires a compatible third-party enchantment plugin. When none is installed, enabling this section has no visible effect.

```yaml
settings:
  add-lore:
    placeholder:
      enchants:
        description:
          enabled: true
          command-toggle: true
          require-permission: false
          format: '&7    {enchant_description}'
          no-slot-format: '&7  {enchant_description}'
```

`format` and `no-slot-format` follow the same slot-data rule as enchantment names. Descriptions with line breaks or `;;` are rendered as separate lore lines.

When both `enabled` and `command-toggle` are true, players can use `/es toggledescription` to show or hide descriptions for themselves. The command is registered only when both options are enabled. If `require-permission` is true, a player needs `enchantmentslots.displaydescription` to see or toggle descriptions.

## Display scope and exclusions

```yaml
settings:
  add-lore:
    remove-lore-first: true
    at-first-or-last: false
    black-creative: true
    black-item:
      any:
        material:
          - book
          - enchanted_book
```

`remove-lore-first` removes previously generated lore before adding it again; keep it enabled to avoid duplicate lines. Generated lines are identified using `lore-prefix` (default: `§y`), so do not reuse that marker in normal lore.

`at-first-or-last: false` appends generated lore; set it to `true` to insert it before existing lore. `black-creative` disables Add Lore while the viewer is in Creative mode. `black-item` uses the same syntax as [Match Item Format](../format/match-item-format.md) and excludes every item that matches it.

## Performance and listener settings

```yaml
settings:
  add-lore:
    only-in-player-inventory: true
    check-chests-only: true
    use-listener-plugin: packetevents
    packet-listener-priority: LOWEST
```

`only-in-player-inventory` limits updates to items in a player's own inventory. With `check-chests-only: true`, the limitation is relaxed for non-chest interfaces; set it to `false` to apply the limitation to every open inventory.

`packetevents` is the recommended `use-listener-plugin` value. `eco` is an alternative for servers that use the Eco listener. `packet-listener-priority` accepts the priorities supported by the selected listener; use it only when resolving lore ordering conflicts with another plugin. Some packet-based item or enchantment plugins always place their lore first and cannot be reordered by Add Lore.

Restart the server after changing display placement or listener settings.
