# ⚡Add Lore

EnchantmentSlot supports adding information about enchanting slots to the lore of all matched items, which we call auto add lore.

## Only items has enchantment slot data will add lore

You shoud note that only items been set slot will have enchantment slot lore added to them. There are 2 ways to set item enchantment slot.

* Item Slot Settings: This feature allows you to set the number of default slots and max slots for matching items. If the item matches, it will be automatically added to the enchantment slot. Please note that this process is not instantaneous and requires interaction between the player and the item. In most case, when player obtaining the item in survival mode will be automatically detected.&#x20;
* Commands: You can use `/es setslots` or `/es giveslots` command to set your hold item's enchantment slot data.

## Black Item

You can use [Match Item Format](../format/match-item-format.md) to determine which item will not display enchantment slot lore in `black-item` option, or disable this feature in `enabled` option.

```yaml
  add-lore:
    enabled: true
    black-item:
      material:
        - book
        - enchanted_book
      has-lore: true
```

## Content of Add Lore

You can set content of **Add Lore** at `config.yml` file. For available placeholders, please view [this page](built-in-placeholders.md).

```yaml
  auto-lore:
    # Does not support other packed-based item, enchants plugins.
    # Like EcoEnchants, EcoItems. (You can also try eco as use-listener-plugin, then change packet-listener-priority)
    # They will always put their lore at first location and EnchantmentSlots can do nothing about it.
    at-first-or-last: false
    # Do not change this option when server started!
    # Only change this if your server has stopped!
    display-value:
      - "&#ff3300Enchantment Slots: {enchant_amount}/{slot_amount}"
      - "{enchants}"
      - "{empty_slots}"
    placeholder:
      enchants:
        # Other placeholder: {enchant_level_roman}, {raw_enchant_name}
        format: '&6  {enchant_name} {enchant_level}'
        sort: true
        auto-add-space: true
      empty-slots:
        format: '&7  --- Empty Slot ---'
```
