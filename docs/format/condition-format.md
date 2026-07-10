# ⚖️Condition Format

## Available Placeholders

* {world}
* {amount}
* {player\_x}
* {player\_y}
* {player\_z}
* {player\_pitch}
* {player\_yaw}
* {player}
* {amount} - The new item slot.

## World <a href="#world" id="world"></a>

Player must be in the world.

```yaml
  conditions:
    1:
      type: world
      world: lobby
```

## Biome

Player must be in the biome.

```yaml
  conditions:
    1:
      type: biome
      biome: oraxen
```

## Permission

Player must has the permission.

**Remember that OP players will always have all permissions unless plugin set it not by default, so if you want to test this condition, you have to deop yourself.**

```yaml
  conditions:
    1:
      type: permission
      permission: 'group.vip'
```

## Placeholder

Player must be meet the placeholder condition.

Rule can be set to:

* \>=
* <=
* \>
* <
* \== (String)
* \= (Number)
* != (Number or string)
* !\*= (Number or string) Not contains.
* \*= (String) Contains, for example, str \*= string is true, but example \*= ple is false.

```yaml
  conditions:
    1:
      type: placeholder
      placeholder: '%player_health%'
      rule: '<='
      value: 5
```

## Any

```yaml
  conditions:
    1:
      type: any
      conditions:
        1:
          type: placeholder
          placeholder: '%eco_balance%'
          rule: '>='
          value: 200
        2:
          type: placeholder
          placeholder: '%player_points%'
          rule: '>='
          value: 400
```

## Not

```yaml
  conditions:
    1:
      type: not
      conditions:
        1:
          type: placeholder
          placeholder: '%eco_balance%'
          rule: '>='
          value: 200
```
