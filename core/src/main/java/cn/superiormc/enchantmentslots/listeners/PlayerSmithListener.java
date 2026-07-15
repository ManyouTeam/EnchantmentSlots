package cn.superiormc.enchantmentslots.listeners;

import cn.superiormc.enchantmentslots.managers.ConfigManager;
import cn.superiormc.enchantmentslots.managers.LanguageManager;
import cn.superiormc.enchantmentslots.methods.EnchantsUtil;
import cn.superiormc.enchantmentslots.utils.ItemUtil;
import cn.superiormc.enchantmentslots.methods.SlotUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerSmithListener implements Listener {
    @EventHandler
    public void onSmithItem(SmithItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        ItemStack item = event.getInventory().getResult();
        if (!ItemUtil.isValid(item)) {
            return;
        }
        int defaultSlot = ConfigManager.configManager.getDefaultLimits(item, player);
        int currentBaseSlots = SlotUtil.getBaseSlot(item);
        int maxEnchantments = SlotUtil.getSlot(item);
        if (ConfigManager.configManager.getBoolean("settings.set-slot-trigger.SmithItemEvent.enabled", true)) {
            if (ConfigManager.configManager.getBoolean("settings.set-slot-trigger.SmithItemEvent.keep-greater-slot", true) && currentBaseSlots > defaultSlot) {
                defaultSlot = currentBaseSlots;
            }
            if (ConfigManager.configManager.getBoolean("settings.set-slot-trigger.SmithItemEvent.reset-previous-slot", true)) {
                event.getInventory().setResult(SlotUtil.setSlot(item, defaultSlot, true));
            } else {
                event.getInventory().setResult(SlotUtil.setSlot(item, defaultSlot, false));
            }
            maxEnchantments = SlotUtil.getSlot(item);
        }
        if (!ConfigManager.configManager.isIgnore(item) && EnchantsUtil.getUsedSlot(item) > maxEnchantments) {
            event.setCancelled(true);
            if (ConfigManager.configManager.getBoolean("settings.close-inventory-if-reached-limit", true)) {
                player.closeInventory();
            }
            LanguageManager.languageManager.sendStringText(player, "slots-limit-reached-after-smith");
        }
    }
}
