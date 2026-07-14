# 📖Deenchant Item

{% hint style="info" %}
This feature was added in plugin version **5.0.0**.
{% endhint %}

## Deenchant Item

All deenchant items are stored in the `deenchant_items` folder. The file name, without the `.yml` extension, is used as the item ID. For example, `common.yml` creates a deenchant item with the ID `common`.

Players use a deenchant item by dragging it onto a target item in their inventory. Removed enchantments are returned to the player as enchanted books. This example randomly removes one enchantment:

```yaml
display-item:
  material: BOOK
  name: '{lang:example-deenchanter-name}'
  lore:
    - '{lang:example-deenchanter-lore}'

conditions: []
match-item:
  has-enchants:
    - '*'

remove-type: RANDOM
remove-amount: 1
remove-enchants:
  whitelist:
    - '*'
  blacklist: []
  order: []

success-actions:
  1:
    type: sound
    sound: 'block.note_block.harp'
  2:
    type: message
    message: '{lang:example-deenchanter-success}'
fail-actions:
  1:
    type: sound
    sound: 'block.note_block.bass'
  2:
    type: message
    message: '{lang:example-deenchanter-fail}'
```

## General Options

* `display-item`: The appearance of the deenchant item. Use the simple version of [ItemFormat](../format/itemformat-tm-simply-version.md).
* `conditions`: The conditions the player must meet to use this item. Use [Condition Format](../format/condition-format.md). Use `[]` when no conditions are required.
* `match-item`: Determines which target items can use this deenchant item. Use [Match Item Format](../format/match-item-format.md). Remove this option to allow all items.
* `remove-type`: Controls how enchantments are selected. Available values are `RANDOM`, `ORDER`, and `GUI`. Invalid values fall back to `RANDOM`.
* `remove-amount`: The maximum number of enchantments removed by one use. The minimum and default value are both `1`.
* `remove-enchants`: Controls which enchantments are eligible and, for `ORDER`, their priority. Enchantment IDs may be written as `sharpness` or as a full namespaced ID such as `minecraft:sharpness`.
* `success-actions`: The actions executed after one or more enchantments are removed. Use [Action Format](../format/action-format.md). The `{amount}` placeholder is the number of enchantments removed.
* `fail-actions`: The actions executed when the item cannot remove an enchantment, including a failed condition or item match. Use [Action Format](../format/action-format.md). The `{amount}` placeholder is `0`.

## Remove Types

* `RANDOM`: Randomly selects eligible enchantments.
* `ORDER`: Selects enchantments in the sequence specified by `remove-enchants.order`. Eligible enchantments not listed in `order` are processed afterward, sorted by enchantment ID.
* `GUI`: Opens a selection GUI and lets the player choose which eligible enchantments to remove.

## Enchantment Filter

* `whitelist`: Only enchantments in this list may be removed. An empty list allows all enchantments. Use `'*'` to explicitly allow all enchantments.
* `blacklist`: Enchantments in this list cannot be removed. The blacklist always takes precedence over the whitelist.
* `order`: The removal priority used by `ORDER`. It has no effect on `RANDOM` or `GUI`.

{% hint style="info" %}
The cursor must contain exactly one deenchant item. Deenchant items cannot be used in Creative mode.
{% endhint %}
