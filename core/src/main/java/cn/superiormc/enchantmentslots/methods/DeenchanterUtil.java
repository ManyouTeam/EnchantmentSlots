package cn.superiormc.enchantmentslots.methods;

import cn.superiormc.enchantmentslots.managers.ConfigManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/** Backwards-compatible factory for configured deenchant items. */
public class DeenchanterUtil {

    public static ItemStack generateDeenchantItem(Player player, String id, int amount) {
        ItemStack item = ConfigManager.configManager.getDeenchantItem(id, player);
        if (item != null) item.setAmount(amount);
        return item;
    }

    public static ItemStack generateCommonDeenchanterItem(Player player, int amount) {
        return generateDeenchantItem(player, "common", amount);
    }

    public static ItemStack generateAdvancedDeenchanterItem(Player player, int amount) {
        return generateDeenchantItem(player, "advanced", amount);
    }
}
