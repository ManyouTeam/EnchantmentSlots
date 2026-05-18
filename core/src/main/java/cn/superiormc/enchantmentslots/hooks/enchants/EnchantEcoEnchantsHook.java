package cn.superiormc.enchantmentslots.hooks.enchants;

import com.willfp.eco.util.StringUtils;
import com.willfp.ecoenchants.display.EnchantSorter;
import com.willfp.ecoenchants.display.EnchantmentFormattingKt;
import com.willfp.ecoenchants.enchant.EcoEnchantLike;
import com.willfp.ecoenchants.enchant.EcoEnchants;
import com.willfp.libreforge.Holder;
import com.willfp.libreforge.ItemProvidedHolder;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EnchantEcoEnchantsHook extends AbstractEnchantHook {

    public EnchantEcoEnchantsHook() {
        super("EcoEnchants");
    }

    @Override
    public String getEnchantName(ItemStack item, Enchantment enchantment, Player player) {
        EcoEnchantLike ecoEnchant = EcoEnchants.INSTANCE.getByID(enchantment.getKey().getKey());
        if (ecoEnchant != null) {
            return EcoEnchantsHook.getEcoEnchantName(ecoEnchant, item, player);
        }
        return null;
    }

    @Override
    public String getRawEnchantName(Enchantment enchantment) {
        EcoEnchantLike ecoEnchant = EcoEnchants.INSTANCE.getByID(enchantment.getKey().getKey());
        if (ecoEnchant != null) {
            return StringUtils.format(ecoEnchant.getRawDisplayName());
        }
        return null;
    }

    @Override
    public List<String> getEnchantDescription(ItemStack item, Enchantment enchantment, Player player) {
        EcoEnchantLike ecoEnchant = EcoEnchants.INSTANCE.getByID(enchantment.getKey().getKey());
        if (ecoEnchant == null) {
            return Collections.emptyList();
        }
        return EnchantmentFormattingKt.getFormattedDescription(ecoEnchant, item.getEnchantmentLevel(enchantment), player);
    }

    @Override
    public boolean hasEnchantDescription() {
        return true;
    }

    @Override
    public Map<Enchantment, Integer> sortEnchants(ItemMeta meta) {
        Collection<Enchantment> itemEnchants = meta.getEnchants().keySet();
        Collection<Enchantment> enchantments = EnchantSorter.INSTANCE.sortForDisplay(itemEnchants);
        Map<Enchantment, Integer> orderedEnchants = new LinkedHashMap<>();
        for (Enchantment enchantment : enchantments) {
            if (itemEnchants.contains(enchantment)) {
                orderedEnchants.put(enchantment, meta.getEnchantLevel(enchantment));
            }
        }
        return orderedEnchants;
    }
}

class EcoEnchantsHook {

    public static String getEcoEnchantName(EcoEnchantLike ecoEnchant, ItemStack item, Player player) {
        Holder holder = EcoEnchants.INSTANCE.getByID(ecoEnchant.getEnchantment().getKey().getKey()).getLevel(item.getEnchantmentLevel(ecoEnchant.getEnchantment()));
        ItemProvidedHolder itemProvidedHolder = new ItemProvidedHolder(holder, item);
        return EnchantmentFormattingKt.getFormattedName(ecoEnchant, 0, itemProvidedHolder.isShowingAnyNotMet(player));
    }
}
