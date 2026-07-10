# 🪄Extra Slot Item

## Extra Slot Item

All apply items are saved in `extra_slot_items` folder, an example file is here:

```yaml
display-item:
  material: PAPER
  custom-model-data: 5
  name: '&dExtra Enchantment Slot &7(+5)'
  lore:
    - '&fDrag this item into your item to use!'
    - '&f100% success but only work for diamond sword.'
add-slots: 5
conditions: []
success-actions:
  1:
    type: sound
    sound: 'block.note_block.harp'
  2:
    type: message
    message: '&#98FB98[EnchantmentSlots] &aAdd {amount} extra enchantment slot to your item!'
fail-actions:
  1:
    type: sound
    sound: 'block.note_block.bass'
  2:
    type: message
    message: '&#98FB98[EnchantmentSlots] &cYou are not very lucky, slot item has broken!'
match-item:
  material:
    - 'diamond_sword'
```

## General Options

* display-item: The display item of this extra slot item. Should use [**ItemFormat**](../format/itemformat-tm-simply-version.md) simple version, for more info, please view [this page](../format/itemformat-tm-simply-version.md).
* add-slots: Represents the number of expansion slots.
* success-actions: Represents actions will excute after success use extra slot item. Use [Action Format](../format/action-format.md) here.
* fail-actions: Represents actions will excute after fail to use extra slot item. Use [Action Format](../format/action-format.md) here.
* conditions: Represents what conditions player should meet to use this extra slot item. Use [Condition Format](../format/condition-format.md) here.
* match-item: Read [Match Item Format](../format/match-item-format.md) page for more info, which item can use this extra slot item, if removed, all items can use this apply item.
