package cn.superiormc.enchantmentslots.hooks.enchants;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import su.nightexpress.excellentenchants.EnchantsUtils;
import su.nightexpress.excellentenchants.api.enchantment.CustomEnchantment;
import su.nightexpress.excellentenchants.enchantment.EnchantRegistry;
import su.nightexpress.nightcore.util.text.NightMessage;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EnchantExcellentEnchantsHook extends AbstractEnchantHook {

    public EnchantExcellentEnchantsHook() {
        super("ExcellentEnchants");
    }

    @Override
    public String getRawEnchantName(Enchantment enchantment) {
        CustomEnchantment excellentEnchant = EnchantRegistry.getByKey(enchantment.getKey());
        if (excellentEnchant != null) {
            return NightMessage.asLegacy(excellentEnchant.getDisplayName());
        }
        return null;
    }

    @Override
    public String getEnchantName(ItemStack item, Enchantment enchantment, Player player) {
        CustomEnchantment excellentEnchant = EnchantRegistry.getByKey(enchantment.getKey());
        if (excellentEnchant != null) {
            return NightMessage.asLegacy(excellentEnchant.getDisplayName());
        }
        return null;
    }

    @Override
    public List<String> getEnchantDescription(ItemStack item, Enchantment enchantment, Player player) {
        CustomEnchantment excellentEnchant = EnchantRegistry.getByKey(enchantment.getKey());
        if (excellentEnchant != null) {
            return NightMessage.asLegacy(excellentEnchant.getDescription(item.getEnchantmentLevel(enchantment)));
        }
        return null;
    }

    @Override
    public boolean hasEnchantDescription() {
        return true;
    }

    @Override
    public Map<Enchantment, Integer> sortEnchants(ItemMeta meta) {
        Map<CustomEnchantment, Integer> enchantments = EnchantsUtils.getCustomEnchantments(meta);
        Map<Enchantment, Integer> orderedEnchants = new LinkedHashMap<>();
        for (CustomEnchantment enchantment : enchantments.keySet()) {
            orderedEnchants.put(enchantment.getBukkitEnchantment(), enchantments.get(enchantment));
        }
        return orderedEnchants;
    }
}
