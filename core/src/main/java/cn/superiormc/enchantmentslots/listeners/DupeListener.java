package cn.superiormc.enchantmentslots.listeners;

import cn.superiormc.enchantmentslots.managers.ConfigManager;
import cn.superiormc.enchantmentslots.managers.LanguageManager;
import cn.superiormc.enchantmentslots.methods.Dupe;
import cn.superiormc.enchantmentslots.methods.SlotUtil;
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class DupeListener implements Listener {

    @EventHandler
    public void onCheckChange(PlayerInventorySlotChangeEvent event) {
        ItemStack item = event.getNewItemStack();
        Player player = event.getPlayer();
        int slot = event.getSlot();
        if (ConfigManager.configManager.getBoolean("enchant-gui.anti-dupe-checker", false)) {
            if (Dupe.isGuiDisplayItem(item)) {
                player.getInventory().setItem(slot, null);
                LanguageManager.languageManager.sendStringText(player, "dupe-removed");
                return;
            }
        }
        if (ConfigManager.configManager.getBoolean("settings.set-slot-trigger.PlayerInventorySlotChangeEvent.enabled", false)) {
            if (ConfigManager.configManager.getBoolean("settings.set-slot-trigger.PlayerInventorySlotChangeEvent.ignore-equipment-when-open-other-invenotry", true) &&
                    !(player.getOpenInventory().getTopInventory() instanceof CraftingInventory)) {
                switch (slot) {
                    case 5:
                        if (player.getInventory().getItem(EquipmentSlot.HEAD).hashCode() == item.hashCode()) {
                            return;
                        }
                        break;
                    case 6:
                        if (player.getInventory().getItem(EquipmentSlot.CHEST).hashCode() == item.hashCode()) {
                            return;
                        }
                        break;
                    case 7:
                        if (player.getInventory().getItem(EquipmentSlot.LEGS).hashCode() == item.hashCode()) {
                            return;
                        }
                        break;
                    case 8:
                        if (player.getInventory().getItem(EquipmentSlot.FEET).hashCode() == item.hashCode()) {
                            return;
                        }
                        break;
                    case 45:
                        if (player.getInventory().getItem(EquipmentSlot.OFF_HAND).hashCode() == item.hashCode()) {
                            return;
                        }
                        break;
                }
            }
            int defaultSlot = ConfigManager.configManager.getDefaultLimits(item, player);
            player.getInventory().setItem(slot, SlotUtil.setSlot(item, defaultSlot, false));
            if (PlayerCacheListener.loadedPlayers.contains(player.getUniqueId()) && !ConfigManager.configManager.isIgnore(item) && ConfigManager.configManager.getBoolean("settings.set-slot-trigger.PlayerInventorySlotChangeEvent.remove-illegal-excess-enchant.enabled", true)) {
                SlotUtil.removeExcessEnchantments(item, player);
            }
        }
    }
}
