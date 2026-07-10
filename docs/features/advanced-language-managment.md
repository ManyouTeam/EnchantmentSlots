# 🌏Advanced Language Managment

## Default Language <a href="#default-language" id="default-language"></a>

You can set default language at `config.yml` file.

```yaml
config-files:
  language: en_US
  # Premium version only.
  per-player-language: true
```

The input here is the name of the language file (without the suffix). All language files are stored in the "`languages`" folder. You can also create a new language file by simply copying the "`en_US.yml`" file and renaming it to the corresponding language code. For example, `zh_CN.yml`.

{% hint style="warning" %}
Content below was added in plugin version **4.8.0**.
{% endhint %}

## Per Player Language <a href="#per-player-language-premium" id="per-player-language-premium"></a>

You can enable `per-player-language` at `config.yml` file. After being enabled, the plugin will determine which language file to display to the player based on their client language. The server must have the relevant language files preloaded; otherwise, it will display content using the default language file.

## Lang Placeholder <a href="#lang-placeholder" id="lang-placeholder"></a>

You can use `{lang:<langKey>}` placeholder in plugin message to enable players from all over the world to display language settings that match their client.

You can add your desired custom language text under the "`override-lang`" section of each language file, in the format of "`ID: text content`". For example:

```yaml
// ... The content originally present in the language file

# Added content
override-lang:
  # This means shop-title it's language key.
  shop-title: 'Item Shop'
```

```yaml
// ... The content originally present in the language file

# Added content
override-lang:
  # This means shop-title it's language key.
  shop-title: '物品商店'
```

Then use `{lang:shop-title}` in the place you want to use.

In `config.yml`, you can directly use `{lang}` to represent the custom language from the language file to be used. The language ID should be synchronized with the configuration file structure in the `config.yml` file, for example:

In `config.yml` file:

```yaml
placeholder:
  # Premium version only
  compare:
    up: '{lang}'
    down: '{lang}'
    same: '{lang}'
```

In each language file:

```yaml
// ... The content originally present in the language file

override-lang:
  placeholder:
    compare:
      up: '↑'
      down: '↓'
      same: '-'
```

Similarly, if we cannot find this custom language in the corresponding language file, we will search for it in the default language file. If it is still not found, then the plugin will not parse this placeholder.

## Advanced Message Format - Premium <a href="#advanced-message-format-premium" id="advanced-message-format-premium"></a>

The default language text is directly filled into the text content that needs to be output, along with [color codes](https://ultimateshop.superiormc.cn/features/color-code). However, you can also utilize this feature to enable the plugin to display not only regular chat box messages, but also actionbars, titles, bossbars, sounds, and more!

You can still use color codes (including MiniMessage) while using our advanced message format.

#### Common Usage <a href="#common-usage" id="common-usage"></a>

```yaml
welcome: 'Welcome to the server!'
```

#### Chat Message <a href="#chat-message" id="chat-message"></a>

```yaml
welcome: '[message]&aWelcome![/message]'
```

#### Title <a href="#title" id="title"></a>

```yaml
welcome: '[title=20,60,20]&6Welcome;;&eThis is sub title[/title]'
# Format: [title=fadeIn,Stay,fadeOut]title;;subTitle[/title]
```

#### Both Chat Message and Title will be sent <a href="#both-chat-message-and-title-will-be-sent" id="both-chat-message-and-title-will-be-sent"></a>

You can mix and match these different message types, just like this.

```yaml
welcome: '[message]&aGood day![/message][title=20,60,20]&6Welcome;;&eThis is sub title[/title]'
```

#### Action Bar <a href="#action-bar" id="action-bar"></a>

```yaml
welcome: '[actionbar]&7Please wait...[/actionbar]'
```

#### Boss Bar - Early Alpha <a href="#boss-bar-early-alpha" id="boss-bar-early-alpha"></a>

```yaml
welcome: '[bossbar=GREEN,SOLID,1.0]&aHappy Today[/bossbar]'
```

#### Sound <a href="#sound" id="sound"></a>

```yaml
welcome: '[sound=ENTITY_EXPERIENCE_ORB_PICKUP,1,1][/sound][sound=ENTITY_PLAYER_LEVELUP,1,1][/sound]'
```
