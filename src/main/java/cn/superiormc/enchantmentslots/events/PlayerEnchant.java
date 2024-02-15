package cn.superiormc.enchantmentslots.events;

import cn.superiormc.enchantmentslots.configs.ConfigReader;
import cn.superiormc.enchantmentslots.configs.Messages;
import cn.superiormc.enchantmentslots.methods.ItemLimits;
import cn.superiormc.enchantmentslots.methods.ItemModify;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerEnchant implements Listener {
    @EventHandler
    public void onEnchantItem(EnchantItemEvent event) {
        Player player = event.getEnchanter();
        ItemStack item = event.getItem();
        int defaultSlot = ConfigReader.getDefaultLimits(player, item);
        ItemModify.addLore(item, defaultSlot);
        int maxEnchantments = ItemLimits.getMaxEnchantments(item, defaultSlot);
        if (event.getEnchantsToAdd().size() + item.getEnchantments().size() > maxEnchantments) {
            event.setCancelled(true);
            if (ConfigReader.getCloseInventory()) {
                player.closeInventory();
            }
            player.sendMessage(Messages.getMessages("slots-limit-reached"));
        }
    }
}
