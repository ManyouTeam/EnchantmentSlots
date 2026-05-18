package cn.superiormc.enchantmentslots.methods;

import cn.superiormc.enchantmentslots.EnchantmentSlots;
import cn.superiormc.enchantmentslots.managers.ConfigManager;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class Dupe {

    private static final NamespacedKey antiDupe = new NamespacedKey(EnchantmentSlots.instance, "anti_dupe");

    public static ItemStack markGuiDisplayItem(ItemStack item) {
        if (!ConfigManager.configManager.getBoolean("enchant-gui.anti-dupe-checker", false)) {
            return item;
        }
        if (item == null || item.getType().isAir()) {
            return item;
        }
        ItemStack result = item.clone();
        ItemMeta meta = result.getItemMeta();
        if (meta == null) {
            return result;
        }
        meta.getPersistentDataContainer().set(antiDupe, PersistentDataType.BYTE, (byte) 1);
        result.setItemMeta(meta);
        return result;
    }

    public static boolean isGuiDisplayItem(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType().isAir() || !itemStack.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = itemStack.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(antiDupe, PersistentDataType.BYTE);
    }

}
