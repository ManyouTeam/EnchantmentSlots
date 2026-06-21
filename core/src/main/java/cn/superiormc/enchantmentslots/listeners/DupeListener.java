package cn.superiormc.enchantmentslots.listeners;

import cn.superiormc.enchantmentslots.managers.ConfigManager;
import cn.superiormc.enchantmentslots.managers.LanguageManager;
import cn.superiormc.enchantmentslots.methods.Dupe;
import cn.superiormc.enchantmentslots.methods.SlotUtil;
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class DupeListener implements Listener {

    @EventHandler
    public void onCheckChange(PlayerInventorySlotChangeEvent event) {
        ItemStack item = event.getNewItemStack();
        Player player = event.getPlayer();
        if (ConfigManager.configManager.getBoolean("enchant-gui.anti-dupe-checker", false)) {
            if (Dupe.isGuiDisplayItem(item)) {
                player.getInventory().setItem(event.getSlot(), null);
                LanguageManager.languageManager.sendStringText(event.getPlayer(), "dupe-removed");
            }
        }
        if (ConfigManager.configManager.getBoolean("settings.set-slot-trigger.PlayerInventorySlotChangeEvent.enabled", true)) {
            int defaultSlot = ConfigManager.configManager.getDefaultLimits(item, player);
            player.getInventory().setItem(event.getSlot(), SlotUtil.setSlot(item, defaultSlot, false));
            if (PlayerCacheListener.loadedPlayers.contains(player.getUniqueId()) && !ConfigManager.configManager.isIgnore(item) && ConfigManager.configManager.getBoolean("settings.set-slot-trigger.PlayerInventorySlotChangeEvent.remove-illegal-excess-enchant.enabled", true)) {
                SlotUtil.removeExcessEnchantments(item, player);
            }
        }
    }
}
