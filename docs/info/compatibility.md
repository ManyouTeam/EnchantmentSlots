# 🔗Compatibility

## **Direct compatibility**

### <mark style="color:red;">Directly</mark> supported custom enchantments plugins list

EnchantmentSlot can automatically obtain information such as the **name, sort** and **rarity** of newly added enchants from these enchantment plugins. Otherwise, you need to set a custom enchantment name in the `enchant-name` section of `config.yml`.

* EcoEnchants
* ExcellentEnchants&#x20;
* Aiyatsbus

### <mark style="color:red;">Directly</mark> supported custom items plugins list

EnchantmentSlot can automatically obtain the IDs of items from these plugins, so that you can set different [**default slots** and **max slots**](../enchantment-slot/item-slot-settings.md) for items from these plugins separately, even if their materials are the same.

* ItemsAdder
* Oraxen
* EcoItems
* EcoArmor
* MMOItems
* MythicMobs
* eco
* NeigeItems
* ExecutableItems
* Nexo

## PlaceholderAPI: Extra placeholders

EnchantmentSlots provides those new placeholders to PlaceholderAPI!

`%enchantmentslots_has_empty_slot%` - Will display whether main hand item has empty slot to use.

`%enchantmentslots_slot_amount%` - Will disply enchantment slot amount of main hand item.
