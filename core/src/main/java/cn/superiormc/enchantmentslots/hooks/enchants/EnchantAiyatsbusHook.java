package cn.superiormc.enchantmentslots.hooks.enchants;

import cc.polarastrum.aiyatsbus.core.Aiyatsbus;
import cc.polarastrum.aiyatsbus.core.AiyatsbusEnchantment;
import cc.polarastrum.aiyatsbus.core.AiyatsbusUtilsKt;
import cc.polarastrum.aiyatsbus.core.data.Displayer;
import cc.polarastrum.aiyatsbus.core.util.StringsKt;
import cc.polarastrum.aiyatsbus.core.util.VariablesKt;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import su.nightexpress.excellentenchants.api.enchantment.CustomEnchantment;
import su.nightexpress.excellentenchants.enchantment.EnchantRegistry;
import su.nightexpress.nightcore.util.text.NightMessage;

import java.util.*;

public class EnchantAiyatsbusHook extends AbstractEnchantHook {

    public EnchantAiyatsbusHook() {
        super("Aiyatsbus");
    }

    @Override
    public String getEnchantName(ItemStack item, Enchantment enchantment, Player player) {
        AiyatsbusEnchantment aiyatsbusEnchantment = Aiyatsbus.INSTANCE.api().getEnchantmentManager().getEnchant(enchantment.getKey());
        if (aiyatsbusEnchantment == null) {
            return null;
        }
        return aiyatsbusEnchantment.getRarity().displayName(aiyatsbusEnchantment.getBasicData().getName());
    }

    @Override
    public String getRawEnchantName(Enchantment enchantment) {
        AiyatsbusEnchantment aiyatsbusEnchantment = Aiyatsbus.INSTANCE.api().getEnchantmentManager().getEnchant(enchantment.getKey());
        if (aiyatsbusEnchantment == null) {
            return null;
        }
        return aiyatsbusEnchantment.getBasicData().getName();
    }

    @Override
    public List<String> getEnchantDescription(ItemStack item, Enchantment enchantment, Player player) {
        AiyatsbusEnchantment aiyatsbusEnchantment = Aiyatsbus.INSTANCE.api().getEnchantmentManager().getEnchant(enchantment.getKey());
        if (aiyatsbusEnchantment == null) {
            return null;
        }
        Displayer displayer = aiyatsbusEnchantment.getDisplayer();
        Map<String, String> tmp = displayer.holders(item.getEnchantmentLevel(enchantment), player, item);
        return Arrays.asList(tmp.get("description").split("\n"));
    }

    @Override
    public boolean hasEnchantDescription() {
        return true;
    }

    @Override
    public Map<Enchantment, Integer> sortEnchants(ItemMeta meta) {
        Map<AiyatsbusEnchantment, Integer> sort = Aiyatsbus.INSTANCE.api().getDisplayManager().sortEnchants(AiyatsbusUtilsKt.getFixedEnchants(meta));
        Map<Enchantment, Integer> result = new HashMap<>();
        for (AiyatsbusEnchantment aEnch : sort.keySet()) {
            result.put(aEnch.getEnchantment(), sort.get(aEnch));
        }
        return result;
    }
}
