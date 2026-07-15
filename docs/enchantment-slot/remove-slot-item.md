# ️✂️Remove Slot Item

{% hint style="info" %}
This feature was added in plugin version **5.0.0**.
{% endhint %}

## Remove Slot Item

All remove slot items are stored in the `remove_slot_items` folder. The file name, without the `.yml` extension, is used as the item ID. For example, `fixed.yml` creates a remove slot item with the ID `fixed`.

Players use a remove slot item by dragging it onto a target item in their inventory. The following example removes 1 slot. If the new slot limit would be lower than the number of slots currently in use, `deenchant-item-mode` determines how the required enchantments are handled:

```yaml
display-item:
  material: SHEARS
  name: '{lang:example-remove-slot-name}'
  lore:
    - '{lang:example-remove-slot-lore}'

conditions: []
match-item:
  has-enchants:
    - '*'

remove-type: FIXED
remove-slots: 1
deenchant-item-mode: RANDOM
use-limit: -1

return-extra-items:
  enabled: true
  mapping:
    small: 1
    large: 5

success-actions:
  1:
    type: sound
    sound: 'block.note_block.harp'
  2:
    type: message
    message: '{lang:example-remove-slot-success}'
fail-actions:
  1:
    type: sound
    sound: 'block.note_block.bass'
  2:
    type: message
    message: '{lang:example-remove-slot-fail}'
```

## General Options

* `display-item`: The appearance of the remove slot item. Use the simple version of [ItemFormat](../format/itemformat-tm-simply-version.md).
* `conditions`: The conditions the player must meet to use this item. Use [Condition Format](../format/condition-format.md). Use `[]` when no conditions are required.
* `match-item`: Determines which target items can use this remove slot item. Use [Match Item Format](../format/match-item-format.md). Remove this option, or use `match-item: []`, to allow all items.
* `remove-type`: The slot removal mode. Available values are `FIXED`, `EXCESS`, and `LINKED_EXTRA`. Invalid values fall back to `FIXED`.
* `remove-slots`: The number of slots removed in `FIXED` mode. The minimum and default value are both `1`. This option is ignored by the other modes.
* `deenchant-item-mode`: Used only when reducing the limit would leave more used slots than the new limit. Support value: **RANOM** or **GUI**.
  * `RANDOM` plans random enchantments first, then removes the planned set.&#x20;
  * `GUI` lets the player plan enchantments first. Closing that GUI before enough enchantments are selected cancels the operation: no enchantment and no remove slot item is consumed.
* `return-extra-items`: Optionally converts removed slots into configured extra slot items. This option applies to `FIXED` and `EXCESS` only.
* `linked-extra-slot-item`: The ID of the extra slot item reversed by `LINKED_EXTRA`. Do not include `.yml`.
* `illegal-slot-check`: Used by `LINKED_EXTRA`. When `true` (the default), removal fails if the current slot limit does not match the original limit plus all tracked extra-slot contributions.
* `success-actions`: The actions executed after slots are removed. Use Action Format. The `{amount}` placeholder is the number of slots actually removed.
* `fail-actions`: The actions executed when slots cannot be removed, including a failed condition, item match, linked record check, or an incomplete GUI selection. Use Action Format. The `{amount}` placeholder is `0`.
* `use-limits`: Maximum uses of this config ID on one target item. `-1` means unlimited.

The slot limit never drops below `1`. Consequently, the actual `{amount}` may be lower than `remove-slots`.

## Remove Types

#### FIXED

Removes the number of slots configured in `remove-slots`:

```yaml
remove-type: FIXED
remove-slots: 2
deenchant-item-mode: RANDOM
```

#### EXCESS

Removes every currently unused slot, leaving the slot limit equal to the number of used slots. The minimum slot limit is still `1`:

```yaml
remove-type: EXCESS
```

#### LINKED\_EXTRA

Reverses the most recent tracked use of one specific extra slot item and returns one copy of that extra slot item to the player:

```yaml
remove-type: LINKED_EXTRA
linked-extra-slot-item: small
illegal-slot-check: true
deenchant-item-mode: GUI
```

Only extra slot items referenced by a linked remover write tracking NBT. Each use is tracked separately, including the actual number of slots added when the maximum slot limit truncates an increase. A linked remover reverses one most-recent tracked use and returns one linked extra slot item. It fails if no matching use is recorded or if reversing it would reduce the slot limit below `1`.

## Returning Extra Slot Items

`return-extra-items.mapping` maps each extra slot item ID to the number of removed slots needed to receive one copy:

```yaml
return-extra-items:
  enabled: true
  mapping:
    small: 1
    large: 5
```

When several conversions are possible, a GUI lets the player choose. Items that cost more than the currently remaining exchange slots are hidden. Each displayed item appends the configured `remove-slot-return-gui.add-lore`, including `{remaining_slots}` and `{slot_cost}`. If filtering leaves only one valid item, it is exchanged automatically without opening the GUI. If the GUI is closed before all removed slots are converted, the remaining slots are converted automatically from the smallest configured value upward. Any remainder smaller than every available mapping value is not returned.

{% hint style="info" %}
Remove slot items cannot be used in Creative mode. `LINKED_EXTRA` returns its linked item automatically and does not use `return-extra-items` or open the return-selection GUI.
{% endhint %}
