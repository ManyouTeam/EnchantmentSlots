package cn.superiormc.enchantmentslots.papi;

import cn.superiormc.enchantmentslots.EnchantmentSlots;
import cn.superiormc.enchantmentslots.utils.ItemLimits;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPIExpansion extends PlaceholderExpansion {

    public static PlaceholderAPIExpansion papi = null;

    private final EnchantmentSlots plugin;
    @Override
    public boolean canRegister() {
        return true;
    }

    public PlaceholderAPIExpansion(EnchantmentSlots plugin) {
        this.plugin = plugin;
    }

    @NotNull
    @Override
    public String getAuthor() {
        return "PQguanfang";
    }

    @NotNull
    @Override
    public String getIdentifier() {
        return "enchantmentslot";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, String params) {
        Player player = offlinePlayer.getPlayer();
        if (player == null) {
            return null;
        }
        else if (params.equals("slot_amount")) {
            ItemStack mainHandItem = player.getInventory().getItemInMainHand();
            if (mainHandItem.getType().isAir()) {
                return "0";
            }
            return String.valueOf(ItemLimits.getMaxEnchantments(player, mainHandItem));
        }
        else if (params.equals("has_empty_slot")) {
            ItemStack mainHandItem = player.getInventory().getItemInMainHand();
            if (mainHandItem.getType().isAir()) {
                return "false";
            }
            if (mainHandItem.getEnchantments().size() >= ItemLimits.getMaxEnchantments(player, mainHandItem)) {
                return "false";
            }
            return "true";
        }
        return null;
    }
}
