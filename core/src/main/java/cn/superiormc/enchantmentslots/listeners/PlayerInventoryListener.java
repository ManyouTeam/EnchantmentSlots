package cn.superiormc.enchantmentslots.listeners;

import cn.superiormc.enchantmentslots.gui.DeenchantGUI;
import cn.superiormc.enchantmentslots.managers.ConfigManager;
import cn.superiormc.enchantmentslots.managers.HookManager;
import cn.superiormc.enchantmentslots.managers.LanguageManager;
import cn.superiormc.enchantmentslots.managers.MatchItemManager;
import cn.superiormc.enchantmentslots.objects.ObjectExtraSlotsItem;
import cn.superiormc.enchantmentslots.methods.SlotUtil;
import cn.superiormc.enchantmentslots.utils.CommonUtil;
import cn.superiormc.enchantmentslots.utils.ItemUtil;
import cn.superiormc.enchantmentslots.utils.RandomUtil;
import cn.superiormc.enchantmentslots.utils.SchedulerUtil;
import org.bukkit.GameMode;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;

import static cn.superiormc.enchantmentslots.methods.DeenchanterUtil.ENCHANTMENT_SLOTS_EXTRA;

public class PlayerInventoryListener implements Listener {

    @EventHandler (priority = EventPriority.LOWEST)
    public void onExtraItemUse(InventoryClickEvent event) {
        if (!(event.getClickedInventory() instanceof PlayerInventory)) {
            return;
        }
        if (!event.getClick().isRightClick() && !event.getClick().isLeftClick()) {
            return;
        }
        if (event.isCancelled()) {
            return;
        }
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        ItemStack targetItem = event.getCurrentItem();
        if (!ItemUtil.isValid(targetItem)) {
            return;
        }
        ItemStack extraItem = event.getCursor();
        if (extraItem.getType().isAir()) {
            return;
        }
        ObjectExtraSlotsItem item = ConfigManager.configManager.getExtraSlotItemValue(extraItem);
        if (item == null || !item.canApply(player, targetItem)) {
            return;
        }
        int value = item.getAddSlot();
        int baseValue = SlotUtil.getSlot(targetItem);
        if (baseValue == 0) {
            return;
        }
        if (player.getGameMode() == GameMode.CREATIVE) {
            LanguageManager.languageManager.sendStringText(player, "error-creative-mode");
            return;
        }
        event.setCancelled(true);
        int maxValue = ConfigManager.configManager.getMaxLimits(targetItem, player);
        if (baseValue >= maxValue) {
            LanguageManager.languageManager.sendStringText(player, "max-slots-reached");
            return;
        }
        if (maxValue != -1 && baseValue + value > maxValue) {
            if (ConfigManager.configManager.getBoolean("settings.cancel-add-slot-if-reached-max-slot", true)) {
                LanguageManager.languageManager.sendStringText(player, "max-slots-reached");
            } else {
                extraItem.setAmount(extraItem.getAmount() - 1);
                if (value == 0) {
                    item.doFailAction(player);
                } else {
                    SlotUtil.setSlot(targetItem, maxValue, true);
                    item.doSuccessAction(player);
                }
            }
            return;
        }
        extraItem.setAmount(extraItem.getAmount() - 1);
        if (value == 0) {
            item.doFailAction(player);
        } else {
            SlotUtil.setSlot(targetItem, baseValue + value, true);
            item.doSuccessAction(player);
        }
    }

    @EventHandler
    public void onDeenchantItemUse(InventoryClickEvent event) {
        if (!(event.getClickedInventory() instanceof PlayerInventory)) {
            return;
        }
        if (!event.getClick().isRightClick() && !event.getClick().isLeftClick()) {
            return;
        }
        if (event.isCancelled()) {
            return;
        }
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        ItemStack targetItem = event.getCurrentItem();
        if (targetItem == null || targetItem.getType().isAir()) {
            return;
        }
        ItemStack extraItem = event.getCursor();
        if (extraItem.getType().isAir()) {
            return;
        }
        ItemMeta meta = extraItem.getItemMeta();
        if (!meta.getPersistentDataContainer().has(ENCHANTMENT_SLOTS_EXTRA, PersistentDataType.BOOLEAN)) {
            return;
        }
        if (player.getGameMode() == GameMode.CREATIVE) {
            LanguageManager.languageManager.sendStringText(player, "error-creative-mode");
            return;
        }
        event.setCancelled(true);
        if (extraItem.getAmount() != 1) {
            LanguageManager.languageManager.sendStringText(player, "error-item-only-one");
            return;
        }
        Map<Enchantment, Integer> enchMap = targetItem.getEnchantments();
        if (enchMap.isEmpty()) {
            LanguageManager.languageManager.sendStringText(player, "error-no-enchantment");
            return;
        }
        boolean commonOrPick = meta.getPersistentDataContainer().get(ENCHANTMENT_SLOTS_EXTRA, PersistentDataType.BOOLEAN);
        if (commonOrPick) {
            if (MatchItemManager.matchItemManager.getMatch(ConfigManager.configManager.getSection("common-deenchanter.match-item"), targetItem)) {
                Enchantment pickedEnch = RandomUtil.getRandomKey(enchMap);
                if (pickedEnch != null) {
                    ItemMeta itemMeta = targetItem.getItemMeta();
                    int level = itemMeta.getEnchants().get(pickedEnch);
                    itemMeta.removeEnchant(pickedEnch);
                    targetItem.setItemMeta(itemMeta);
                    LanguageManager.languageManager.sendStringText(player, "deenchant-success", "enchant",
                            HookManager.hookManager.getEnchantName(targetItem, pickedEnch, player, true));
                    extraItem.setAmount(extraItem.getAmount() - 1);
                    CommonUtil.giveOrDrop(player, ItemUtil.generateEnchantedBook(pickedEnch, level));
                } else {
                    LanguageManager.languageManager.sendStringText(player, "error-no-enchantment");
                }
            } else {
                LanguageManager.languageManager.sendStringText(player, "error-not-match");
            }
        } else {
            if (MatchItemManager.matchItemManager.getMatch(ConfigManager.configManager.getSection("common-deenchanter.match-item"), targetItem)) {
                DeenchantGUI gui = new DeenchantGUI(player, targetItem, extraItem);
                SchedulerUtil.runTaskLater(gui::openGUI, 1L);
            } else {
                LanguageManager.languageManager.sendStringText(player, "error-not-match");
            }
        }

    }
}
