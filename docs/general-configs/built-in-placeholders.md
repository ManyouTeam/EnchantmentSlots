# 🔧Built-in Placeholders

## Available placeholders:

* {enchants} - Display item's enchantments. We don't auto hide enchants of the item, you have to add **HIDE\_ENCHANTS** flag to item by yourself!
* {empty\_slots} - Display item's empty slots.
* {enchant\_amount} - Display item's enchantment amount.
* {slot\_amount} - Display item's max slot amount.
* {used\_slot\_amount}

You can further set options for the `{enchants}`, `{empty_slots}` placeholder in `auto-lore.placeholder` option in `config.yml` file.

{% hint style="info" %}
You can also use those placeholder in other plugin's item lore, for more info, please view [Item Placeholder](../features/item-placeholder.md) page.
{% endhint %}

## {enchants} Placeholder

Available sub placeholders for `{enchants}`:

* {enchant\_name}
* {enchant\_level}
* {enchant\_used\_slot}
* {enchant\_level\_roman}
* {raw\_enchant\_name} - Without ratity color exist in [supported custom enchantment plugins](../info/compatibility.md).

### Enchantment Sort for {enchants} Placeholder

EnchantmentSlots supports auto enchantment sort in `{enchants}` placeholder: we will hook into EcoEnchants v10+ or ExcellentEnchants v4 and use it's API to get enchantment display sort in those plugins, for vanilla enchantments, it's depend on how long it was obtained.

To use this feautre, you need enable `sort` option in `config.yml` file.

```yaml
    placeholder:
      enchants:
        sort: true
```

### Custom Enchantment Name for {enchant\_name} sub placeholder

By default, other plugins can not get enchantments name that displayed in client. You have to set them in `config.yml` to make EnchantmentSlots correctly display enchantment name for custom enchantments. For example of vanilla enchantments:

```yaml
# Enchant Name
enchant-name:
  # Default providing vanilla enchantments here.
  # For third enchantment plugins:
  # Plugin will auto get enchantment display name that provided by EcoEnchants and ExcellentEnchants
  # If you are not using the 2 plugins, please using "Enchantment Key: Enchantment Display Name" format
  # Like:
  # an_other_enchantment: 'This is just an example here!'
  protection: 'Protection'
  fire_protection: 'Fire Protection'
  feather_falling: 'Feather Falling'
  blast_protection: 'Blast Protection'
  projectile_protection: 'Projectile Protection'
  respiration: 'Respiration'
  aqua_affinity: 'Aqua Affinity'
  thorns: 'Thorns'
  depth_strider: 'Depth Strider'
  frost_walker: 'Frost Walker'
  binding_curse: 'Binding Curse'
  sharpness: 'Sharpness'
  smite: 'Smite'
  bane_of_arthropods: 'Bane of Arthropods'
  knockback: 'Knockback'
  fire_aspect: 'Fire Aspect'
  looting: 'Looting'
  sweeping: 'Sweeping'
  # For 1.21+
  sweeping_edge: 'Sweeping Edge'
  efficiency: 'Efficiency'
  silk_touch: 'Silk Touch'
  unbreaking: 'Unbreaking'
  fortune: 'Fortune'
  power: 'Power'
  punch: 'Punch'
  flame: 'Flame'
  infinity: 'Infinity'
  luck_of_the_sea: 'Luck of the Sea'
  lure: 'Lure'
  loyalty: 'Loyalty'
  impaling: 'Impaling'
  riptide: 'Riptide'
  channeling: 'Channeling'
  multishot: 'Multishot'
  quick_charge: 'Quick Charge'
  piercing: 'Piercing'
  mending: 'Mending'
  vanishing_curse: 'Vanishing Curse'
  soul_speed: 'Soul Speed'
  wind_burst: 'Wind Burst'
  breach: 'Breach'
  density: 'Density'
```

But, EnchantmentSlots will hook into EcoEnchants v10+ or ExcellentEnchants v4 (v3 is supported in version before 3.2.0) and use it's API to get enchantment name, so if you are using the 2 plugins, you don't need manually set enchantment name at `config.yml`.

### Custom Enchantment Level for {enchant\_level} sub placeholder

By default, plugin will print number itself at `{enchant_level}` sub placeholder, if you want to override the result of this sub placeholder, you can do that in `config.yml` file.

For example of better number display:

```yaml
# Enchant Level
enchant-level:
  1: ①
  2: ②
  3: ③
  4: ④
  5: ⑤
  6: ⑥
```

### Custom Enchantment Used Slot Display for {enchant\_used\_slot} sub placeholder

By default, plugin will print number itself at `{enchant_used_slot}` sub placeholder, you can override it's result in `config.yml` file. For example:

```yaml
# Enchant Used Slot
enchant-used-slot:
  placeholder:
    0: '&c☆'
    1: ''
    2: '&7☆'
    3: '&f☆'
    4: '&e☆'
    5: '&6☆'
```

### Auto Add Space&#x20;

If enabled, plugin will auto add space if the placeholder has valid result.

```yaml
    placeholder:
      enchants:
        format: '&6  {enchant_name}{enchant_level}{enchant_used_slot}'
        auto-add-space: true
```
