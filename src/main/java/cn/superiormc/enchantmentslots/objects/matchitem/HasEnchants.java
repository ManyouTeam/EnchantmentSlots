package cn.superiormc.enchantmentslots.objects.matchitem;

import cn.superiormc.enchantmentslots.utils.CommonUtil;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class HasEnchants extends AbstractMatchItemRule {
    public HasEnchants() {
        super();
    }

    @Override
    public boolean getMatch(ConfigurationSection section, ItemStack item, ItemMeta meta) {
        for (String ench : section.getStringList("has-enchants")) {
            Enchantment vanillaEnchant = Registry.ENCHANTMENT.get(CommonUtil.parseNamespacedKey(ench.toLowerCase()));
            if (vanillaEnchant != null && meta.getEnchants().containsKey(vanillaEnchant)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean configNotContains(ConfigurationSection section) {
        return section.getStringList("has-enchants").isEmpty();
    }
}
