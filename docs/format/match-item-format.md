# 🔍Match Item Format

An example of match item format:

```yaml
  items:
    - 'diamond_sword'
  has-name: true
  has-lore: true
  contains-lore:
    - 'test'
  contains-name:
    - 'test'
```

By default, all **Match Rules** need to be met, and you can also use the **ANY** Match Rule to avoid this. These are all clearly introduced below.

```yaml
  any:
    material:
      - 'iron_sword'
    has-lore: true
```

## Match Item Rule List

### None

No item will match, very useful for apply items.

```yaml
match-item: # or black-item:
  none: true
```

### NOT

If item didn't meet the match rule you set in NOT section, we will consider this rule as meet the rule.

```yaml
match-item: # or black-item:
  not:
    items:
      - 'diamond_sword'
```

### ANY&#x20;

If item meet any of those match item rules, we will consider it meet the rule.

```yaml
match-item: # or black-item:
  any:
    rarity: COMMON
    material:
      - 'diamond'
```

### ANY Of

If item meet any of those match item rule GROUPS, we will consider it meet the rule.

```yaml
match-item: # or black-item:
  any:
    1: # Group 1
      contains-name:
        - 'Hello'
      contains-lore:
        - 'Oh'
    2: # Group 2
      contains-lore:
        - 'What'
```

### Items

```yaml
match-item: # or black-item
  # Type item ID here, support third item plugins item ID.
  items:
    - 'diamond_sword'
    - 'superior_sword'
  use-tier-identify: true
```

{% hint style="info" %}
Type item ID or vanilla material ID **only**, no item type.

For MMOItems: Please note that the item ID needs to be capitalized.

For ItemsAdder: Namespace is required, like `namespace_test:item_id_here`.
{% endhint %}

For MMOItems and EcoItems plugin, there is also a `use-tier-identity` option available in `config.yml` file, which represents whether to use item ID or item tier. If changed to true, you can use tier IDs such as **RARE** here instead of the previous item ID.

### Material&#x20;

Require item material must be the value of this list. This is very similar to items rule, but this rule will only use vanilla item material type.

```yaml
match-item: # or black-item
  material:
    - 'diamond'
    - 'diamond_sword'
```

### Material Tag

Supports item tag or block tag.

```yaml
match-item: # or black-item
  material-tag:
    - 'swords'
```

### Contains Name/Contains Lore

{% hint style="info" %}
You shoud not put color code here if you enabled `ignore-color-code` option at `config.yml` file.
{% endhint %}

```yaml
match-item: # or black-item
  contains-lore:
    - 'test1'
  contains-name:
    - 'test1'
```

### Contains Enchants/Stored Enchants

Similar to `has-enchants`, but require the enchantment level must be greater than the value you set.

```yaml
match-item: # or black-item
  contains-enchants:
    POWER: 1
```

### Contains specified levels enchants/stored Enchants  <a href="#remove-enchants" id="remove-enchants"></a>

If you want to make only specified enchantment level take effect, you can use int list at here. For example, I want to make only item has Power level 1 enchantment match, then you should set this section like this:

```yaml
match-item: # or black-item
  contains-enchants:
    POWER: [1] 
    OTHER: [1,2,5] # Means level1, 2 and 5.
```

### Min Enchants Amount

The item must has at least 5 enchantments in this example.

```yaml
match-item: # or black-item
  contains-enchants-amount: 5
```

### Contains Enchants Amount&#x20;

This means item must has 3 or 5 enchantments in this example, if the item has 4 enchantments or other number of enchantments, it will not being matched.

```yaml
match-item: # or black-item
  contains-enchants-amount: [3, 5]
```

### Contains NBT

Require NBTAPI, download it [here](https://www.spigotmc.org/resources/nbt-api.7939/).

Use `;;` distinguish key value hierarchy.&#x20;

```yaml
match-item: # or black-item
  contains-nbt: 
    - 'PublicBukkitValues;;mythicchanger:apply_item_id'
```

### Has name

Whether only items with custom display names will match.

```yaml
match-item: # or black-item
  has-name: true
```

### Has lore

Whether only items with lores will match.

```yaml
match-item: # or black-item
  has-lore: true
```

### Has Any Enchants

```yaml
match-item: # or black-item
  has-enchants:
    - '*'
```

### Has Enchants

```yaml
match-item: # or black-item
  has-enchants:
    - 'POWER'
```

### Has Stored Enchants

```yaml
match-item: # or black-item
  has-stored-enchants:
    - 'POWER'
```

### Enchantable

This item can be enchanted by any of those enchantments.

```yaml
match-item: # or black-item
  enchantable:
    - respiration
```

### Rarity (1.20.5+)&#x20;

Require item must have specified rarity. Rarity require you extra set in item, by default all items don't have rarity, for item don't have custom rarity, plugin will use "NONE" as result.

{% hint style="warning" %}
This is just mean vanilla item's ratity, **NOT** means ratity/tier from custom plugins.
{% endhint %}

```yaml
match-item: # or black-item
  rarity: NONE
```

### NBT String&#x20;

Require NBTAPI, download it [here](https://www.spigotmc.org/resources/nbt-api.7939/).

Require specifed NBT type must be STRING.

Use `;;` distinguish key value hierarchy.&#x20;

The value after the last `;;` symbol represents the required NBT value.

```yaml
match-item: # or black-item
  # Require this NBT value to be A.
  nbt-string:
    - 'PublicBukkitValues;;mythicchanger:apply_item_id;;A'
```

### NBT Int/NBT Double/NBT Byte

Require NBTAPI, download it [here](https://www.spigotmc.org/resources/nbt-api.7939/).

Similar to NBT String, but should has comparison symbols.

Comparsion symbols support:

* \>=
* <=
* \>
* <
* \==

```yaml
match-item: # or black-item
  nbt-double:
    - 'PublicBukkitValues;;mythicchanger:test;;>=;;5'
```

