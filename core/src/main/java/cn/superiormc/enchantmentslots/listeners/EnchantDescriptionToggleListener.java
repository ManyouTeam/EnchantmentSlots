package cn.superiormc.enchantmentslots.listeners;

import cn.superiormc.enchantmentslots.managers.ConfigManager;
import cn.superiormc.enchantmentslots.managers.LanguageManager;
import cn.superiormc.enchantmentslots.methods.EnchantDescriptionDisplay;
import cn.superiormc.enchantmentslots.utils.ItemUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class EnchantDescriptionToggleListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK
                || event.getHand() != EquipmentSlot.HAND
                || !event.getPlayer().isSneaking()
                || !ItemUtil.isValid(event.getItem())) {
            return;
        }
        if (!ConfigManager.configManager.getBoolean(
                "settings.add-lore.placeholder.enchants.description.enabled", false)
                || !ConfigManager.configManager.getBoolean(
                "settings.add-lore.placeholder.enchants.description.shift-click-ground-toggle", true)) {
            return;
        }
        if (ConfigManager.configManager.getBoolean(
                "settings.add-lore.placeholder.enchants.description.require-permission", false)
                && !event.getPlayer().hasPermission(EnchantDescriptionDisplay.DISPLAY_PERMISSION)) {
            return;
        }

        event.setCancelled(true);
        boolean display = EnchantDescriptionDisplay.toggle(event.getPlayer());
        LanguageManager.languageManager.sendStringText(event.getPlayer(),
                display ? "description-display-enabled" : "description-display-disabled");
        event.getPlayer().updateInventory();
    }
}
