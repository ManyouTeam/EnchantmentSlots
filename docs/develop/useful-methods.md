# Useful Methods

## Obtain Max Enchantment Limit for the ItamStack object (ItemLimits.java)

```java
/*
Obtain the current maximum enchantment limit of the ItemStack object. The maximum enchantment limit is stored in the item NBT.
If it cannot be obtained, the default slot value is used.

Plugin has built-in default slot system, you can use ConfigReader.getDefaultLimits(player, itemID) method to default slot in this item.
For itemID parameter: You can first use checkValid(ItemStack item) method to parse it into String object here.
 */
public static int getMaxEnchantments(@NotNull ItemStack item, int defaultSlot, @NotNull ItemStack itemID) {
    return 0;
}
/*
Similar to getMaxEnchantments method, but cannot obtain maximum enchantment limit, we will just return 0 instead of return a default value.
 */
public static int getRealMaxEnchantments(@NotNull ItemStack item) {
    return 0;
}
```
