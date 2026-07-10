# ⛔Limitation

## eco limitations

Some options does not work when you are using **eco** as packet listener plugin, but ~~**eco** has better performance than **ProtolcolLib**~~**&#x20;(plugin no longer support ProtolcolLib and use packetevents instead)**, choose one of them that meet your requirement.

## Use Item Placeholder in Creative Mode <a href="#creative-mode" id="creative-mode"></a>

Items includes **Item Placeholder** will no longer be updated after you swap the gamemode into creative mode, and this feature does not work in creative mode. In the creative mode, the enchantment slot lore added by the plugin will still works normally.

## Compatibility of other packet based item plugins

Some other item plugins and enchantment plugins **MAY** are also using packet to change items, in which case EnchantmentSlots's **enchantment slot lore** may not work on items from these plugins.&#x20;

## Won't work for fake custom enchantment plugin

EnchantmentSlots does <mark style="color:red;">**NOT**</mark> support **fake custom enchantment plugins**. The simplest way to determine if your enchantment plugin is a fake custom enchantment plugin is to use the `/enchant` command. If this command introduces the new enchantments in your custom enchantment plugin, then your custom enchantment is real. Otherwise, they are just fake custom enchantment plugins that provide similar functionality.

Confirmed real custom enchantment plugin that EnchantmentSlots supports:

* EcoEnchants
* ExcellentEnchants
* MythicEnchants
