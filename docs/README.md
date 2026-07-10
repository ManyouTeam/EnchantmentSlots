# 🎉Welcome

## Links <a href="#links" id="links"></a>

### Get the plugin <a href="#get-the-plugin" id="get-the-plugin"></a>

**SpigotMC:** [PREMIUM](https://www.spigotmc.org/resources/enchantmentslots-add-enchantment-slot-feature-to-your-server-1-14-1-21-11.113048/)

**Polymart:** [PREMIUM](https://polymart.org/product/4295/enchantmentslots)

**BuiltByBit:** [PREMIUM](https://builtbybit.com/resources/enchantmentslots.32808/)

### Get support <a href="#get-support" id="get-support"></a>

* All users have an obligation to comply with our rules after joining the Discord server, which can be viewed in the rules channel. If you do not agree with our rules, you will not be able to receive our service support. Users who violate the rules will be punished according to the situation, including permanent ban.
* Every user is obligated to comply with our plugin terms of use. You can find these in the **LICENSE** file within the JAR file. We do not provide any assistance to users who fail to comply with the terms of use.
* Compared to users who have purchased the paid version, our service support priority for free users will be lower, with more requirements and restrictions. I have invested a lot of time and effort in developing plugins, but despite this, I have provided the complete plugin source code. I have not closed the source or restricted the number of user IP addresses used. Therefore, better service support is not free, and you should not ask me to provide good service for free. If you want your issue to be taken seriously, please consider purchasing a paid version to support plugin development.
* Support is only available at our [Discord](https://discord.gg/rzajeybhbw) server.
* 因 Discord 在中国大陆区域不可用，如果您在中国大陆购买了此插件，可以凭购买的账号平台、名称，在此QQ群享受售后服务：815351827。

## Info

EnchantmentSlots helps you add enchantment slot in your server. All weapons, tools, armors will have a new attribute: enchantment slot. Players can only enchant a maximum of enchantment slot value enchantments to an item. This will help your server avoid player has too many enchantments and break the PvP or PvE balance.

## Features

* Packet based, your server won't get the lore we added, only client will display it, this means we compatibility with most item plugins.
  * Even support creative mode!
* Auto lore update. Support all existed items, even item exist before install this plugin.
* Support other real enchantments plugins, like EcoEnchants, ExcellentEnchantments.
  * AdvancedmentEnchantments and EpicEnchants are not real enchantment plugin, so this plugin won't work for them.
* Built-in condition system, you can make ranked players have more default enchantment slots.
* Can set different item has different enchantment slot, support third-plugin item, vanilla item, and even MMOItems's tier!
* Support enchant in EnchantGui plugin.
* Players can use extra slot item to make their item have more enchantment slots. (Support custom texture with custom-model-data)
  * Can set add-slots, chance, apply-items, success-actions and fail-actions in it.
* Set black rules, make specified items won't display enchantment slot lore but still has enchantment slot feature!
* Add 4 placeholders which can also display enchantment slots info in other item plugins!
  * Do not perfectly support other packet-based plugin, like EcoItems.
* PlaceholderAPI support, can use our expansion to display main hand item's empty slot amount and max slot amount.
* Auto remove illegal extra enchantments.
  * We can only forbidden add enchantments by vanilla ways, like enchanting table and anvil.
  * We can't forbid add enchantments by other way, for example, buy enchant in shop plugin, they will directly change the item enchant without calling an event. This feature will remove the extra enchantments by those illegal ways.
