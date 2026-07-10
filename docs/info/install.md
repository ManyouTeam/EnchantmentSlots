# ⚙️Install

## Install <a href="#install" id="install"></a>

* Put the `.jar` file into your server's `plugins` folder.
* Stop your server and then restart it. <mark style="color:red;">Cannot load plugins in any other way while the server is starting</mark>.
* When updating plugins, please be sure to remove old versions.
* You should install one of the follow plugin in your server to make EnchantmentSlots display lore in your server.
  * eco ([https://polymart.org/resource/eco.773](https://polymart.org/resource/eco.773))
  * packetevents [(](https://modrinth.com/plugin/packetevents)[https://modrinth.com/plugin/packetevents](https://modrinth.com/plugin/packetevents))
* EnchantmentSlots is not a drag-to-use plugin, you maybe need carefully adjust the options provided by the plugin to better utilize the EnchantmentSlots on your server.
* If you plan to use other custom enchanting plugins on your server, you need to follow the additional steps prompted below.

## Hide Enchantment Description

*   The best way is check if the custom enchantment plugin has this feature.

    ### EcoEnchants

    If you want to hide enchantment description lore provided by EcoEnchants, you have to:

    * Disable `display.enabled` option in EcoEnchants's `config.yml`.&#x20;

    ### ExcellentEnchants

    If you want to hide enchantment description lore provided by ExcellentEnchants, you have to:

    * Keep `use-listener-plugin` option to `packetevents` as this plugin is also trying use this plugin.
    * Keep `packet-listener-priority` option to `LOWEST`, do not change it to other value, othervise ExcellentEnchants will still try display it's enchantment description.
* If the custom enchantment plugin doesn't has, you can use enable `auto-hide-enchants` option in each item slot settings file. After enable, EnchantmentSlots will auto add **HIDE ENCHANTS** flag to the item new generated in player inventory. Existed item can be converted by click it in player inventory. For more info, please view [this page](../enchantment-slot/item-slot-settings.md).
* Sometimes change `packet-listener-priority` option maybe work if the enchantment plugin is also packet based on same packet listener plugin that EnchantmentSlots using.
* If this still does not work for you, then you have to ask cutom enchantment author add this feature for you, EnchantmentSlots can do nothing for this.
