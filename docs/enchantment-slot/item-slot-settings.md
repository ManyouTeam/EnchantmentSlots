# ↔️Item Slot Settings

All item slot settings are saved in `item_slot_settings` folder, an example file is here:

```yaml
match-item:
  material:
    - wooden_pickaxe
    - stone_pickaxe
    - iron_pickaxe
    - golden_pickaxe
    - diamond_pickaxe
    - netherite_pickaxe
    - wooden_hoe
    - stone_hoe
    - iron_hoe
    - golden_hoe
    - diamond_hoe
    - netherite_hoe
    - wooden_axe
    - stone_axe
    - iron_axe
    - golden_axe
    - diamond_axe
    - netherite_axe
    - wooden_shovel
    - stone_shovel
    - iron_shovel
    - golden_shovel
    - diamond_shovel
    - netherite_shovel
    - wooden_sword
    - stone_sword
    - iron_sword
    - golden_sword
    - diamond_sword
    - netherite_sword
    - fishing_rod
    - trident
    - elytra
    - bow
    - crossbow
    - carrot_on_a_stick
    - shield
    - turtle_helmet
    - leather_helmet
    - chainmail_helmet
    - iron_helmet
    - golden_helmet
    - diamond_helmet
    - netherite_helmet
    - leather_chestplate
    - chainmail_chestplate
    - iron_chestplate
    - golden_chestplate
    - diamond_chestplate
    - netherite_chestplate
    - leather_leggings
    - chainmail_leggings
    - iron_leggings
    - golden_leggings
    - diamond_leggings
    - netherite_leggings
    - leather_boots
    - chainmail_boots
    - iron_boots
    - golden_boots
    - diamond_boots
    - netherite_boots
    #- enchanted_book
    #- book
    - mace

auto-add-lore: false
auto-hide-enchants: true

default-slots:
  default: 3
  vip: 5

max-slots:
  default: 10
  vip: 15

slots-conditions:
  vip:
    1:
      type: permission
      permission: 'group.vip'
```

This feature allows you to set the number of default slots and max slots for matching items. If the item matches, it will be automatically added to the enchantment slot. Please note that this process is not instantaneous and requires interaction between the player and the item. In most survival modes, obtaining the item will be automatically detected. If not, it can be interacted with once.

## Auto Add Lore

If enabled, even if the item has not been **set slot** due to not being interacted with yet, we will still calculate its default slot value and add enchantment slot lore. This process will consume a certain amount of server performance.

Set slot represents writing a special NBT to the player's item to record how many enchanted slots the item currently has. You can set when to execute set slots through `set-slots-trigger` section at `config.yml` file.

{% hint style="info" %}
Please do not use both the `SetSlotPacket.enabled` and `PlayerInventorySlotChangeEvent.enabled` options simultaneously, as this will not only result in double performance consumption but also cause new issues.
{% endhint %}

```yaml
# This is when will the plugin trying adds enchantment slot NBT for an enchantable item which also means set fixed
  # enchantment slot for an item.
  set-slot-trigger:
    # This option support packetevents only.
    # Enable this maybe improve plugin performance.
    SetSlotPacket:
      enabled: false
      # Only plugin has enchantment slot NBT will be checked.
      remove-illegal-excess-enchant:
        enabled: true
        hide-remove-message: false
        ignore-join-time: -1
        run-sync: true
    # Require Paper.
    # A better choice to replace "SetSlotPacket"
    PlayerInventorySlotChangeEvent:
      enabled: true
      # Only plugin has enchantment slot NBT will be checked.
      remove-illegal-excess-enchant:
        enabled: true
        # Other options share same value from "SetSlotPacket" section.
    EnchantItemEvent:
      enabled: true
      # Whether cancel the enchantment event or remove extra enchantment if
      # item reached slot limit after enchant.
      cancel-if-reached-slot: false
    AnvilItemEvent:
      enabled: true
      # If your item has display issue after use anvil, you can try to enable this.
      update-item: false
    SmithItemEvent:
      enabled: true
      # If set to true, we will reset old item enchantment slot and then regenerate new slot
      # value for new item, enchantments won't affect by this option.
      reset-previous-slot: true
      # If set to true, if new generated slot value smaller than existed slot value, we will still
      # keep use existed value as new upgraded item's slot limit.
      keep-greater-slot: true
    # Enable this maybe improve plugin performance.
    # Maybe has incompatible issue with other plugins.
    InventoryClickEvent:
      enabled: true
```

## Auto Hide Enchants

By default, if placeholder `{enchants}` used in enchantment slot lore, we will auto add hide enchants flag in fake lore packet.

If enabled, item will be auto add hide enchants flag when obtained. The hide enchants flag will actually modify the item and cannot be undone, even if the plugin is removed.

If disabled, plugin will still try hide enchants from item, but modifications will only be made at the client level. After the plugin is deleted, the hide enchants flag will also be withdrawn. However, some custom enchantments plugin also use fake lore packet and sometimes they can not found our hide enchants flag, this lead to their enchant lore can not be hideen by EnchantmentSlots, in this case, you need enable this option.&#x20;

## Match Item

Which item will use this item slot settings, should use [Match Item Format](../format/match-item-format.md). In this example, `material` is just a rule type in Match Item Format, you can choose delete it, and select other rule here. You are allow to use unlimited match item rules here. For example:

```yaml
  match-item:
    contains-lore:
      - 'Enchantable'
      - 'Epic'
```

## Default Slots

The `default-slots` section determines the default slot quantity for all items.&#x20;

Among them, `default` represents the default quantity, while the other options are the **condition ID**s in the `slot-conditions` section. Players who meet the corresponding conditions will use the value after the corresponding condition ID. We will ultimately take the highest value.

{% hint style="info" %}
For now, if you want to **set different defult slots for different items**, you have to create new item slot settings config and then use another match item rule configs to match different items and then set different default slot values.
{% endhint %}

## Max Slots

Players can use [Extra slot items](extra-slot-item.md) to increase the enchantment slots of items, but this is not infinite. The `max-slots` section determines the maximum number of items that can be expanded through **Extra slot items.**

## Slot Conditions

You can set countless condition groups here, and each **condition ID** can be used in the options above. Players who meet specific conditions can use values different from the `default` option. Use [Condition Format](../format/condition-format.md) in each condition group section.

## Random Slots

You can use the `<start number>~<end number>` format to make the slot be random, for example:

```yaml
default-slots:
  default: 1~2
  vip: 3~4
```

To use this feature, you have to <mark style="color:red;">**DISABLE**</mark> `auto-add-lore` option in the slot settings file, after disable, item without slot data will no longer auto display it's enchantment slot lore.
