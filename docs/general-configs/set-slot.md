# 🎯Set Slot

This page documents the `settings` section of `config.yml`, except for `settings.add-lore`. For lore rendering and its placeholders, see [Add Lore](add-lore.md).

## Setting slots when an item is received

`settings.set-slot-trigger` controls when the plugin applies the default slot limit from an item's matching item-slot setting. It can also remove existing enchantments that exceed that limit.

```yaml
settings:
  set-slot-trigger:
    SetSlotPacket:
      enabled: true
      remove-illegal-excess-enchant:
        enabled: true
        hide-remove-message: false
        ignore-join-time: -1
        run-sync: true
```

`SetSlotPacket` uses PacketEvents and handles server `SET_SLOT` packets for a player's inventory. It is the normal choice for assigning default slots to matching items as they are received. It also updates the item shown to the client.

`remove-illegal-excess-enchant.enabled` removes enchantments when their total slot cost exceeds the item's limit. Enchantments are removed in the order provided by the item metadata until the item fits. `hide-remove-message` suppresses the player and console notification for that removal.

`ignore-join-time` delays illegal-enchantment checks after a player joins. It is measured in server ticks; use `-1` to disable the delay. `run-sync` performs the packet-triggered slot and enchantment update on the server thread and is recommended unless a compatibility investigation requires otherwise.

## Paper inventory trigger

```yaml
settings:
  set-slot-trigger:
    PlayerInventorySlotChangeEvent:
      enabled: false
      ignore-equipment-when-open-other-invenotry: true
      remove-illegal-excess-enchant:
        enabled: true
```

`PlayerInventorySlotChangeEvent` is a Paper-only alternative to `SetSlotPacket`. Enable it only on Paper. It assigns default slots when a player inventory slot changes and can remove excess enchantments.

Keep `ignore-equipment-when-open-other-invenotry` enabled to avoid a known Paper equipment-slot issue while another inventory is open. The option name is spelled `invenotry` in the configuration and must remain spelled that way.

The `enabled` and `hide-remove-message` values from the `SetSlotPacket` illegal-enchantment section are also used by this trigger. Its nested `enabled` value controls whether excess enchantments are removed.

## Enchanting, anvil, and smithing

```yaml
settings:
  set-slot-trigger:
    EnchantItemEvent:
      enabled: true
      cancel-if-reached-slot: false
    AnvilItemEvent:
      enabled: true
      update-item: false
    SmithItemEvent:
      enabled: true
      reset-previous-slot: true
      keep-greater-slot: true
```

`EnchantItemEvent.enabled` assigns default slots before an enchanting-table result is checked. If an enchant would exceed the limit, `cancel-if-reached-slot: true` cancels the enchant. When it is `false`, the plugin allows the operation and removes enchantments as needed to fit the limit. Books are always cancelled when the limit would be exceeded.

`AnvilItemEvent.enabled` checks an anvil result against the original item's slot limit. `update-item` performs an extra item update after a rejected anvil operation; enable it only if your server displays stale anvil items.

`SmithItemEvent.enabled` applies slot handling to smithing results. `reset-previous-slot: true` discards the old item's base slot value and regenerates it from the upgraded item. `keep-greater-slot: true` preserves the old base-slot value when it is higher than the new item's default. If a result still exceeds its limit, smithing is cancelled.

## Inventory-click trigger

```yaml
settings:
  set-slot-trigger:
    InventoryClickEvent:
      enabled: true
```

This applies default slots when a player clicks an item in their own inventory. It is enabled by default; disable it only when troubleshooting a confirmed incompatibility with another inventory plugin.

## Ignored items

```yaml
settings:
  ignore-slot-item:
    not:
      has-slot: 1
```

Items matching `ignore-slot-item` are excluded from slot-limit enforcement. Use the Match Item Format to build more specific rules. The example above excludes every item with fewer than one EnchantmentSlots slot, so only items that already have slot data are checked. This does not prevent an item from being matched by an item-slot setting; it prevents the enforcement checks that use this list.

For example, this `ignore-slot-item` rule ignores every item with fewer than one slot:

```yaml
ignore-slot-item:
  not:
    has-slot: 1
```

## Other settings

```yaml
settings:
  use-tier-identify-slots: false
  close-inventory-if-reached-limit: true
  cancel-add-slot-if-reached-max-slot: true
  cache-enchant-name: false
```

`use-tier-identify-slots` changes item matching for EcoItems, EcoArmor, and MMOItems from item ID to item tier when that plugin provides a tier. See Match Item Format for matching rules.

`close-inventory-if-reached-limit` closes the relevant inventory when an enchanting, anvil, smithing, or GUI operation is rejected because the slot limit is reached.

`cancel-add-slot-if-reached-max-slot` controls extra-slot items when their full value would exceed an item's maximum. With `true`, the use is cancelled. With `false`, the item is consumed and only the remaining available slots are added.

`cache-enchant-name` is present in the default configuration but is not read by the current implementation, so it has no effect.
