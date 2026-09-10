package cn.superiormc.enchantmentslots.hooks.enchants;

import cn.superiormc.enchantmentslots.methods.EnchantsUtil;
import com.willfp.eco.util.StringUtils;
import com.willfp.ecoenchants.display.EnchantSorter;
import com.willfp.ecoenchants.display.EnchantmentFormattingKt;
import com.willfp.ecoenchants.enchant.EcoEnchant;
import com.willfp.ecoenchants.enchant.EcoEnchantLike;
import com.willfp.ecoenchants.enchant.EcoEnchants;
import com.willfp.ecoenchants.enchant.UtilKt;
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

    private EcoEnchantLike getEcoEnchant(Enchantment enchantment) {
        EcoEnchantLike ecoEnchant = UtilKt.wrap(enchantment);

        if (ecoEnchant instanceof EcoEnchant) {
            return ecoEnchant;
        }

        if (!"minecraft".equals(enchantment.getKey().getNamespace())) {
            return null;
        }

        if (ecoEnchant.getConfig().getKeys(false).isEmpty()) {
            return null;
        }

        return ecoEnchant;
    }

    @Override
    public String getEnchantName(ItemStack item, Enchantment enchantment, Player player) {
        EcoEnchantLike ecoEnchant = getEcoEnchant(enchantment);
        if (ecoEnchant != null) {
            return EcoEnchantsHook.getEcoEnchantName(ecoEnchant, item, player);
        }
        return null;
    }

    @Override
    public String getRawEnchantName(Enchantment enchantment) {
        EcoEnchantLike ecoEnchant = getEcoEnchant(enchantment);
        if (ecoEnchant != null) {
            return StringUtils.format(ecoEnchant.getRawDisplayName());
        }
        return null;
    }

    @Override
    public List<String> getEnchantDescription(ItemStack item, Enchantment enchantment, Player player) {
        EcoEnchantLike ecoEnchant = getEcoEnchant(enchantment);
        if (ecoEnchant == null) {
            return Collections.emptyList();
        }
        return EnchantmentFormattingKt.getFormattedDescription(ecoEnchant, EnchantsUtil.getEnchantmentLevel(item, enchantment), player);
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

    public static String getEcoEnchantName(EcoEnchantLike ecoEnchant,
                                           ItemStack item,
                                           Player player) {
        boolean showNotMet = false;

        if (ecoEnchant instanceof EcoEnchant ecoEnchantImpl) {
            int level = item.getEnchantmentLevel(
                    ecoEnchant.getEnchantment()
            );

            Holder holder =
                    ecoEnchantImpl.getLevel(level);

            ItemProvidedHolder itemProvidedHolder =
                    new ItemProvidedHolder(
                            holder,
                            item
                    );

            showNotMet =
                    itemProvidedHolder.isShowingAnyNotMet(player);
        }

        String name =
                EnchantmentFormattingKt.getFormattedName(
                        ecoEnchant,
                        0,
                        showNotMet
                );

        if (name == null || name.isEmpty()) {
            return null;
        }

        return name;
    }
}