package cn.superiormc.enchantmentslots.gui;

import cn.superiormc.enchantmentslots.EnchantmentSlots;
import cn.superiormc.enchantmentslots.managers.ConfigManager;
import cn.superiormc.enchantmentslots.methods.EnchantsUtil;
import cn.superiormc.enchantmentslots.utils.CommonUtil;
import cn.superiormc.enchantmentslots.utils.ItemUtil;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public class RemoveSlotDeenchantGUI extends InvGUI {

    private final ItemStack targetItem;

    private final int slotLimit;

    private final Runnable completeAction;

    private final Consumer<Player> failAction;

    private final Map<Integer, Enchantment> enchCache = new LinkedHashMap<>();

    private final Set<Enchantment> selectedEnchantments = new LinkedHashSet<>();

    private boolean completed;

    public RemoveSlotDeenchantGUI(Player player, ItemStack targetItem, int slotLimit, Runnable completeAction, Consumer<Player> failAction) {
        super(player);
        this.targetItem = targetItem;
        this.slotLimit = slotLimit;
        this.completeAction = completeAction == null ? () -> { } : completeAction;
        this.failAction = failAction == null ? ignored -> { } : failAction;
        constructGUI();
    }

    @Override
    protected void constructGUI() {
        if (Objects.isNull(inv)) {
            inv = EnchantmentSlots.methodUtil.createNewInv(player,
                    ConfigManager.configManager.getInt("remove-slot-deenchant-gui.size", 54),
                    ConfigManager.configManager.getString(player, "remove-slot-deenchant-gui.title", "Choose enchantments to remove"), this);
        }
        enchCache.clear();
        inv.clear();
        int index = 0;
        for (Map.Entry<Enchantment, Integer> entry : targetItem.getEnchantments().entrySet()) {
            Enchantment enchantment = entry.getKey();
            ItemStack displayItem = ItemUtil.generateEnchantedBook(enchantment, entry.getValue());
            ItemMeta itemMeta = displayItem.getItemMeta();
            List<String> lore = EnchantmentSlots.methodUtil.getItemLore(itemMeta);
            lore = lore == null ? new ArrayList<>() : new ArrayList<>(lore);
            lore.addAll(ConfigManager.configManager.getStringList(player, "remove-slot-deenchant-gui.ench-item"));
            if (selectedEnchantments.contains(enchantment)) {
                lore.addAll(ConfigManager.configManager.getStringList(player, "remove-slot-deenchant-gui.selected-ench-item"));
            }
            EnchantmentSlots.methodUtil.setItemLore(itemMeta, lore, player);
            displayItem.setItemMeta(itemMeta);
            setItem(index, displayItem);
            enchCache.put(index, enchantment);
            if (++index >= inv.getSize()) break;
        }
    }

    @Override
    public boolean clickEventHandle(Inventory inventory, ItemStack cursor, int slot) {
        Enchantment enchantment = enchCache.get(slot);
        if (enchantment == null || completed) {
            return true;
        }
        if (!selectedEnchantments.add(enchantment)) {
            selectedEnchantments.remove(enchantment);
        }
        if (hasSelectedEnoughSlots()) {
            applySelection();
            completed = true;
            completeAction.run();
            player.closeInventory();
        } else {
            constructGUI();
        }
        return true;
    }

    private boolean hasSelectedEnoughSlots() {
        int plannedSlots = 0;
        for (Enchantment enchantment : selectedEnchantments) {
            plannedSlots += EnchantsUtil.getUsedSlot(enchantment);
        }
        return EnchantsUtil.getUsedSlot(targetItem) - plannedSlots <= slotLimit;
    }

    private void applySelection() {
        ItemMeta meta = targetItem.getItemMeta();
        if (meta == null) return;
        List<ItemStack> returnedBooks = new ArrayList<>();
        for (Enchantment enchantment : selectedEnchantments) {
            Integer level = meta.getEnchants().get(enchantment);
            if (level == null) {
                continue;
            }
            meta.removeEnchant(enchantment);
            returnedBooks.add(ItemUtil.generateEnchantedBook(enchantment, level));
        }
        targetItem.setItemMeta(meta);
        for (ItemStack book : returnedBooks) {
            CommonUtil.giveOrDrop(player, book);
        }
    }

    @Override
    public void closeEventHandle(Inventory inventory) {
        if (!completed) {
            failAction.accept(player);
        }
    }
}
