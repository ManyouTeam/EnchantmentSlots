package cn.superiormc.enchantmentslots.listeners;

import cn.superiormc.enchantmentslots.gui.DeenchantGUI;
import cn.superiormc.enchantmentslots.gui.RemoveSlotReturnGUI;
import cn.superiormc.enchantmentslots.gui.RemoveSlotDeenchantGUI;
import cn.superiormc.enchantmentslots.managers.ConfigManager;
import cn.superiormc.enchantmentslots.managers.LanguageManager;
import cn.superiormc.enchantmentslots.methods.EnchantsUtil;
import cn.superiormc.enchantmentslots.methods.SlotUtil;
import cn.superiormc.enchantmentslots.objects.ObjectDeenchantItem;
import cn.superiormc.enchantmentslots.objects.ObjectExtraSlotsItem;
import cn.superiormc.enchantmentslots.objects.ObjectRemoveSlotItem;
import cn.superiormc.enchantmentslots.utils.CommonUtil;
import cn.superiormc.enchantmentslots.utils.ItemUtil;
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

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class ExtraSlotItemsListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onExtraItemUse(InventoryClickEvent event) {
        if (!isApplicableClick(event)) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        ItemStack targetItem = event.getCurrentItem();
        ItemStack extraItem = event.getCursor();
        ObjectExtraSlotsItem item = ConfigManager.configManager.getExtraSlotItemValue(extraItem);
        if (item == null) {
            return;
        }
        if (isCreative(player)) {
            return;
        }
        event.setCancelled(true);
        if (item.hasReachedUseLimit(targetItem)) {
            sendUseLimitReached(player, item.getUseCount(targetItem), item.getUseLimit());
            return;
        }
        if (!item.meetsConditions(player)) {
            LanguageManager.languageManager.sendStringText(player, "extra-slot-fail-condition");
            return;
        }
        if (!item.matchesItem(targetItem)) {
            LanguageManager.languageManager.sendStringText(player, "extra-slot-fail-not-match");
            return;
        }
        if (!SlotUtil.hasBaseSlot(targetItem)) {
            LanguageManager.languageManager.sendStringText(player, "extra-slot-fail-no-slots");
            return;
        }
        int currentSlots = SlotUtil.getSlot(targetItem);
        int maxValue = ConfigManager.configManager.getMaxLimits(targetItem, player);
        if (maxValue > 0 && currentSlots >= maxValue) {
            LanguageManager.languageManager.sendStringText(player, "max-slots-reached");
            return;
        }
        int value = item.getAddSlot();
        if (maxValue > 0 && currentSlots + value > maxValue) {
            if (ConfigManager.configManager.getBoolean("settings.cancel-add-slot-if-reached-max-slot", true)) {
                LanguageManager.languageManager.sendStringText(player, "max-slots-reached");
            } else {
                extraItem.setAmount(extraItem.getAmount() - 1);
                item.recordUse(targetItem);
                if (value == 0) {
                    LanguageManager.languageManager.sendStringText(player, "extra-slot-fail-chance");
                } else {
                    int addedSlots = maxValue - currentSlots;
                    ObjectExtraSlotsItem.recordUsedSlots(targetItem, item.getId(), addedSlots);
                    LanguageManager.languageManager.sendStringText(player, "extra-slot-success", "amount", String.valueOf(addedSlots));
                }
            }
            return;
        }
        extraItem.setAmount(extraItem.getAmount() - 1);
        item.recordUse(targetItem);
        if (value == 0) {
            LanguageManager.languageManager.sendStringText(player, "extra-slot-fail-chance");
        } else {
            ObjectExtraSlotsItem.recordUsedSlots(targetItem, item.getId(), value);
            LanguageManager.languageManager.sendStringText(player, "extra-slot-success", "amount", String.valueOf(value));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeenchantItemUse(InventoryClickEvent event) {
        if (!isApplicableClick(event)) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        ItemStack targetItem = event.getCurrentItem();
        ItemStack deenchantStack = event.getCursor();
        ObjectDeenchantItem deenchantItem = ConfigManager.configManager.getDeenchantItemValue(deenchantStack);
        if (deenchantItem == null) {
            return;
        }
        if (isCreative(player)) {
            return;
        }
        event.setCancelled(true);
        if (deenchantStack.getAmount() != 1) {
            LanguageManager.languageManager.sendStringText(player, "deenchant-fail-stack");
            return;
        }
        if (deenchantItem.hasReachedUseLimit(targetItem)) {
            sendUseLimitReached(player, deenchantItem.getUseCount(targetItem), deenchantItem.getUseLimit());
            return;
        }
        if (!deenchantItem.meetsConditions(player)) {
            LanguageManager.languageManager.sendStringText(player, "deenchant-fail-condition");
            return;
        }
        if (!deenchantItem.matchesItem(targetItem)) {
            LanguageManager.languageManager.sendStringText(player, "deenchant-fail-not-match");
            return;
        }
        if (deenchantItem.getEligibleEnchantments(targetItem).isEmpty()) {
            LanguageManager.languageManager.sendStringText(player, "deenchant-fail-no-enchantment");
            return;
        }
        deenchantStack.setAmount(0);
        deenchantItem.recordUse(targetItem);
        if (deenchantItem.getRemoveType() == ObjectDeenchantItem.RemoveType.GUI) {
            DeenchantGUI gui = new DeenchantGUI(player, targetItem, deenchantItem, deenchantItem.getRemoveAmount(), () -> {
                LanguageManager.languageManager.sendStringText(player, "deenchant-success",
                        "amount", String.valueOf(deenchantItem.getRemoveAmount()));
            });
            SchedulerUtil.runTaskLater(gui::openGUI, 1L);
            return;
        }
        int removed = deenchantItem.removeAutomatically(player, targetItem, deenchantItem.getRemoveAmount());
        if (removed > 0) {
            LanguageManager.languageManager.sendStringText(player, "deenchant-success", "amount", String.valueOf(removed));
        }
        else LanguageManager.languageManager.sendStringText(player, "deenchant-fail-no-enchantment");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onRemoveSlotItemUse(InventoryClickEvent event) {
        if (!isApplicableClick(event)) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        ItemStack targetItem = event.getCurrentItem();
        ItemStack removeStack = event.getCursor();
        ObjectRemoveSlotItem removeItem = ConfigManager.configManager.getRemoveSlotItemValue(removeStack);
        if (removeItem == null) {
            return;
        }
        if (isCreative(player)) {
            return;
        }
        event.setCancelled(true);
        if (removeItem.hasReachedUseLimit(targetItem)) {
            sendUseLimitReached(player, removeItem.getUseCount(targetItem), removeItem.getUseLimit());
            return;
        }
        if (!removeItem.meetsConditions(player)) {
            LanguageManager.languageManager.sendStringText(player, "remove-slot-fail-condition");
            return;
        }
        if (!removeItem.matchesItem(targetItem)) {
            LanguageManager.languageManager.sendStringText(player, "remove-slot-fail-not-match");
            return;
        }
        if (!SlotUtil.hasBaseSlot(targetItem)) {
            LanguageManager.languageManager.sendStringText(player, "remove-slot-fail-no-slots");
            return;
        }
        int baseSlots = SlotUtil.getBaseSlot(targetItem);
        int currentSlots = SlotUtil.getSlot(targetItem);
        int extraSlots = ObjectExtraSlotsItem.getExtraSlots(targetItem);
        int usedSlots = EnchantsUtil.getUsedSlot(targetItem);
        int requestedSlots;
        if (removeItem.isLinkedExtraItem()) {
            if (!ConfigManager.configManager.getExtraSlotsItemMap().containsKey(removeItem.getLinkedExtraItemId())) {
                LanguageManager.languageManager.sendStringText(player, "remove-slot-fail-linked-item-not-found");
                return;
            }
            if (removeItem.isIllegalSlotCheckEnabled() && !ObjectExtraSlotsItem.isRecordedSlotAmountConsistent(targetItem)) {
                LanguageManager.languageManager.sendStringText(player, "remove-slot-fail-illegal-data");
                return;
            }
            requestedSlots = ObjectExtraSlotsItem.getNextRecordedSlots(targetItem, removeItem.getLinkedExtraItemId());
            if (requestedSlots <= 0 || currentSlots - requestedSlots < baseSlots) {
                LanguageManager.languageManager.sendStringText(player, "remove-slot-fail-no-linked-use");
                return;
            }
        } else {
            requestedSlots = removeItem.getSlotsToRemove(currentSlots, usedSlots);
        }
        if (requestedSlots <= 0) {
            LanguageManager.languageManager.sendStringText(player, "remove-slot-fail-no-excess");
            return;
        }
        int newSlotLimit = Math.max(baseSlots, currentSlots - Math.min(requestedSlots, extraSlots));
        int removedSlots = currentSlots - newSlotLimit;
        if (removedSlots <= 0) {
            LanguageManager.languageManager.sendStringText(player, "remove-slot-fail-no-slots");
            return;
        }
        if (usedSlots > newSlotLimit) {
            if (removeItem.getDeenchantItemMode() == ObjectRemoveSlotItem.DeenchantItemMode.GUI) {
                RemoveSlotDeenchantGUI gui = new RemoveSlotDeenchantGUI(player, targetItem, newSlotLimit, () -> {
                    removeStack.setAmount(removeStack.getAmount() - 1);
                    finishRemoveSlots(player, targetItem, removeItem, newSlotLimit, removedSlots);
                });
                SchedulerUtil.runTaskLater(gui::openGUI, 1L);
                return;
            }
            List<Enchantment> plannedEnchantments = planRandomDeenchantments(targetItem, newSlotLimit);
            if (plannedEnchantments == null) {
                LanguageManager.languageManager.sendStringText(player, "remove-slot-fail-not-enough-enchantments");
                return;
            }
            removeStack.setAmount(removeStack.getAmount() - 1);
            removeEnchantments(player, targetItem, plannedEnchantments);
            finishRemoveSlots(player, targetItem, removeItem, newSlotLimit, removedSlots);
            return;
        }
        removeStack.setAmount(removeStack.getAmount() - 1);
        finishRemoveSlots(player, targetItem, removeItem, newSlotLimit, removedSlots);
    }

    private void finishRemoveSlots(Player player, ItemStack targetItem, ObjectRemoveSlotItem removeItem, int newSlotLimit, int removedSlots) {
        int consumedSlots;
        boolean reversedLinkedUse = false;
        if (removeItem.isLinkedExtraItem()) {
            reversedLinkedUse = ObjectExtraSlotsItem.consumeLatestRecordedSlots(
                    targetItem, removeItem.getLinkedExtraItemId(), removedSlots);
            consumedSlots = reversedLinkedUse ? removedSlots : 0;
        } else {
            consumedSlots = ObjectExtraSlotsItem.consumeRecordedSlots(targetItem, removedSlots);
        }
        if (consumedSlots <= 0) return;
        removeItem.recordUse(targetItem);
        LanguageManager.languageManager.sendStringText(player, "remove-slot-success", "amount", String.valueOf(consumedSlots));
        if (removeItem.isLinkedExtraItem()) {
            if (reversedLinkedUse) {
                ItemStack extraItem = ConfigManager.configManager.getExtraSlotItem(removeItem.getLinkedExtraItemId(), player);
                if (extraItem != null) {
                    CommonUtil.giveOrDrop(player, extraItem);
                }
            }
            return;
        }
        if (removeItem.isReturnExtraItems()) {
            if (RemoveSlotReturnGUI.settleSingleAvailableChoice(player, removeItem, consumedSlots)) {
                return;
            }
            RemoveSlotReturnGUI gui = new RemoveSlotReturnGUI(player, removeItem, consumedSlots);
            SchedulerUtil.runTaskLater(gui::openGUI, 1L);
        }
    }

    private List<Enchantment> planRandomDeenchantments(ItemStack targetItem, int slotLimit) {
        List<Enchantment> candidates = new ArrayList<>(targetItem.getEnchantments().keySet());
        Collections.shuffle(candidates);
        List<Enchantment> planned = new ArrayList<>();
        int remainingSlots = EnchantsUtil.getUsedSlot(targetItem);
        for (Enchantment enchantment : candidates) {
            if (remainingSlots <= slotLimit) {
                break;
            }
            planned.add(enchantment);
            remainingSlots -= EnchantsUtil.getUsedSlot(enchantment);
        }
        return remainingSlots <= slotLimit ? planned : null;
    }

    private void removeEnchantments(Player player, ItemStack targetItem, List<Enchantment> enchantments) {
        org.bukkit.inventory.meta.ItemMeta meta = targetItem.getItemMeta();
        if (meta == null) return;
        List<ItemStack> returnedBooks = new ArrayList<>();
        for (Enchantment enchantment : enchantments) {
            Integer level = meta.getEnchants().get(enchantment);
            if (level == null) {
                continue;
            }
            meta.removeEnchant(enchantment);
            returnedBooks.add(ItemUtil.generateEnchantedBook(enchantment, level));
        }
        targetItem.setItemMeta(meta);
        for (ItemStack book : returnedBooks) {
            cn.superiormc.enchantmentslots.utils.CommonUtil.giveOrDrop(player, book);
        }
    }

    private boolean isApplicableClick(InventoryClickEvent event) {
        if (!(event.getClickedInventory() instanceof PlayerInventory)) {
            return false;
        }
        if (!event.getClick().isRightClick() && !event.getClick().isLeftClick()) {
            return false;
        }
        if (event.isCancelled() || !(event.getWhoClicked() instanceof Player)) {
            return false;
        }
        return ItemUtil.isValid(event.getCurrentItem()) && ItemUtil.isValid(event.getCursor());
    }

    private boolean isCreative(Player player) {
        if (player.getGameMode() != GameMode.CREATIVE) {
            return false;
        }
        LanguageManager.languageManager.sendStringText(player, "error-creative-mode");
        return true;
    }

    private void sendUseLimitReached(Player player, int used, int limit) {
        LanguageManager.languageManager.sendStringText(player, "item-use-limit-reached",
                "used", String.valueOf(used), "limit", String.valueOf(limit));
    }
}
