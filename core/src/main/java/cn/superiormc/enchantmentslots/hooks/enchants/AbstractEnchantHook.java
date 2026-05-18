package cn.superiormc.enchantmentslots.hooks.enchants;

import cn.superiormc.enchantmentslots.methods.EnchantsUtil;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class AbstractEnchantHook {

    protected String pluginName;

    public AbstractEnchantHook(String pluginName) {
        this.pluginName = pluginName;
    }

    public String getEnchantName(ItemStack item, Enchantment enchantment, Player player) {
        return getRawEnchantName(enchantment);
    }

    public abstract String getRawEnchantName(Enchantment enchantment);

    public List<String> getEnchantDescription(ItemStack item, Enchantment enchantment, Player player) {
        return Collections.emptyList();
    }

    public boolean hasEnchantDescription() {
        return false;
    }

    public Map<Enchantment, Integer> sortEnchants(ItemMeta meta) {
        return EnchantsUtil.getEnchantments(meta, false);
    }

    public String getPluginName() {
        return pluginName;
    }
}
