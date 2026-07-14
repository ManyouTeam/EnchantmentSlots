# 🪄Extra Slot Item

## Extra Slot Item

All extra slot items are stored in the `extra_slot_items` folder. The file name, without the `.yml` extension, is used as the item ID. For example, `diamond.yml` creates an extra slot item with the ID `diamond`.

Players use an extra slot item by dragging it onto a target item in their inventory. The following example adds 5 enchantment slots to a diamond sword and always succeeds:

```yaml
display-item:
  material: PAPER
  custom-model-data: 5
  name: '{lang:example-extra-slot-name}'
  lore:
    - '{lang:example-extra-slot-lore-1}'
    - '{lang:example-extra-slot-lore-2}'

add-slots: 5
chance: 100

conditions: []
success-actions:
  1:
    type: sound
    sound: 'block.note_block.harp'
  2:
    type: message
    message: '{lang:example-extra-slot-success}'
fail-actions:
  1:
    type: sound
    sound: 'block.note_block.bass'
  2:
    type: message
    message: '{lang:example-extra-slot-fail}'

match-item:
  material:
    - 'diamond_sword'
```

## General Options

* `display-item`: The appearance of the extra slot item. Use the simple version of [ItemFormat](../format/itemformat-tm-simply-version.md).
* `add-slots`: The number of enchantment slots added after a successful use. Defaults to `1`.
* `chance`: The success chance, from `0` to `100`. Defaults to `100`; values outside this range are clamped to the nearest limit.
* `conditions`: The conditions the player must meet to use this item. Use [Condition Format](../format/condition-format.md). Use `[]` when no conditions are required.
* `success-actions`: The actions executed after slots are successfully added. Use [Action Format](../format/action-format.md). The `{amount}` placeholder is the configured `add-slots` value.
* `fail-actions`: The actions executed when the chance roll fails. Use Action Format. The `{amount}` placeholder is `0`.
* `match-item`: Determines which target items can use this extra slot item. Use [Match Item Format](../format/match-item-format.md). Remove this option, or use `match-item: []`, to allow all items.

{% hint style="info" %}
Extra slot items cannot be used in Creative mode. They also cannot increase an item beyond the maximum slot limit configured by the plugin.
{% endhint %}

## Linked-remover tracking

An extra slot item writes per-use slot data to the target item's NBT only when at least one `LINKED_EXTRA` remove slot item references its ID through `linked-extra-slot-item`. Each successful use records the actual number of slots added, including an increase truncated by the maximum slot limit. Unlinked extra slot items do not write this tracking data.
