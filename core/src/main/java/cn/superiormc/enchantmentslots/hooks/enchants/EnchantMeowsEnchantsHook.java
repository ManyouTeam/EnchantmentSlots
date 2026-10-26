package cn.superiormc.enchantmentslots.hooks.enchants;

import com.windsor.meowsEnchants.api.MeowsEnchantsAPI;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class EnchantMeowsEnchantsHook extends AbstractEnchantHook {

    public EnchantMeowsEnchantsHook() {
        super("MeowsEnchants");
    }

    @Override
    public String getRawEnchantName(Enchantment enchantment) {
        return MeowsEnchantsAPI.getEnchantName(null, enchantment, null);
    }

    @Override
    public String getEnchantName(ItemStack item, Enchantment enchantment, Player player) {
        return MeowsEnchantsAPI.getEnchantName(item, enchantment, player);
    }

    @Override
    public List<String> getEnchantDescription(ItemStack item, Enchantment enchantment, Player player) {
        return MeowsEnchantsAPI.getEnchantDescription(item, enchantment, player);
    }

    @Override
    public boolean hasEnchantDescription() {
        return true;
    }
}
