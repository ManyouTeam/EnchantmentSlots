# 📍Item Placeholder

## Placeholder List

We support use those [built-in placeholder](../general-configs/built-in-placeholders.md) in other item plugins, you can use `[]` symbol to replace `{}` symbol too.

To use this feature, you have to:

* Enable `auto-lore.placeholder.auto-parse` option at `config.yml` file.
* Enable `auto-add-lore` option in each item slot settings file.

{% hint style="info" %}
MythicChanger-Premium plugin provide many extra item placeholders like `{name}`, can display almost all info of items at lore! Buy it [here](https://www.spigotmc.org/resources/mythicchanger-premium-match-and-modify-all-your-items-without-trouble-1-14-1-20.115913/).
{% endhint %}

## MMOItems Issue

For MMOItems **6.9.4** version users:

* You can use `[]` to replace `{}` to solve placeholder can not be used problem. This is MMOItems' problem. Like `[enchants]`.

Before 6.9.4 and after 6.9.4 version, MMOItems does not have this problem anymore.

## Creative Mode Issue

Item Placeholder can not be used in creative mode when you use them outside EnchantmentSlots (like put them into MMOItems), otherwise the placeholders will never be updated anymore.&#x20;
