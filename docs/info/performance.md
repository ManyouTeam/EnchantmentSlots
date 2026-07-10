# 🚀Performance

If you found EnchantmentSlots costs server resource than other plugins, please consider:

* Enable `set-slot-trigger.XXX.enabled` option. If enabled, the plugin will store the enchantment slot of the item in NBT format within the plugin, instead of automatically calculating the enchantment slot of the item every time it interacts.
* Enable `add-lore.only-in-player-inventory` option. If enabled, only item in player inventory will deal with enchantment slot feature, and this can save many plugin performance.
* Both options maybe have incompatible issue with other plugins, you have to test it before change them in production environment.
