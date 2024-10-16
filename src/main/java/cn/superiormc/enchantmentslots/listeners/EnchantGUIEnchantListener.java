package cn.superiormc.enchantmentslots.listeners;

import cn.superiormc.enchantmentslots.EnchantmentSlots;
import cn.superiormc.enchantmentslots.managers.ConfigManager;
import cn.superiormc.enchantmentslots.managers.LanguageManager;
import cn.superiormc.enchantmentslots.hooks.CheckValidHook;
import cn.superiormc.enchantmentslots.methods.ItemLimits;
import cn.superiormc.enchantmentslots.methods.ItemModify;
import cn.superiormc.enchantmentslots.utils.CommonUtil;
import com.gmail.legamemc.enchantgui.api.event.PlayerEnchantItemEvent;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;

public class EnchantGUIEnchantListener implements Listener {
    @EventHandler
    public void onEnchantItem(PlayerEnchantItemEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        String itemID = CheckValidHook.checkValid(item);
        int defaultSlot = ConfigManager.configManager.getDefaultLimits(player, itemID);
        ItemModify.setSlot(item, defaultSlot, itemID);
        int maxEnchantments = ItemLimits.getMaxEnchantments(item, defaultSlot, itemID);
        if (item.getEnchantments().size() >= maxEnchantments) {
            int buyLevel = event.getLevel();
            int nowLevel = event.getItem().getEnchantmentLevel(event.getEnchantment());
            int originalLevel = nowLevel - buyLevel;
            if (item.hasItemMeta()) {
                item.getItemMeta().removeEnchant(event.getEnchantment());
                if (originalLevel > 0) {
                    item.getItemMeta().addEnchant(event.getEnchantment(), originalLevel, true);
                }
            }
            event.getPlayer().giveExp(event.getCost().getExp());
            event.getPlayer().giveExpLevels(event.getCost().getLevel());
            if (event.getCost().getLapis() > 0) {
                ItemStack lapis = new ItemStack(Material.LAPIS_LAZULI);
                lapis.setAmount(event.getCost().getLapis());
                event.getPlayer().getInventory().addItem(lapis);
            }
            if (CommonUtil.checkPluginLoad("Vault") && event.getCost().getMoney() > 0) {
                RegisteredServiceProvider<Economy> rsp = EnchantmentSlots.instance.getServer().getServicesManager().getRegistration(Economy.class);
                if (rsp == null) {
                    return;
                }
                Economy eco = rsp.getProvider();
                eco.withdrawPlayer(player, event.getCost().getMoney());
            }
            if (ConfigManager.configManager.getBoolean("settings.close-inventory-if-reached-limit", true)) {
                player.closeInventory();
            }
            LanguageManager.languageManager.sendStringText(player, "slots-limit-reached");
        }
    }
}
