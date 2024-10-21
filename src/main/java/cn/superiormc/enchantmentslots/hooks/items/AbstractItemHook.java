package cn.superiormc.enchantmentslots.hooks.items;

import cn.superiormc.enchantmentslots.managers.ErrorManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class AbstractItemHook {

    protected String pluginName;

    public AbstractItemHook(String pluginName) {
        this.pluginName = pluginName;
    }

    public abstract ItemStack getHookItemByID(Player player, String itemID);

    public ItemStack returnNullItem(String itemID) {
        ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[EnchantmentSlots] §cError: Can not get "
                + pluginName + " item: " + itemID + "!");
        return null;
    }

    public abstract String getIDByItemStack(ItemStack hookItem);

    public String getSimplyIDByItemStack(ItemStack hookItem) {
        return getIDByItemStack(hookItem);
    }

    public String getPluginName() {
        return pluginName;
    }
}
