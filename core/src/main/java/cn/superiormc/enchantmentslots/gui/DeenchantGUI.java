package cn.superiormc.enchantmentslots.gui;

import cn.superiormc.enchantmentslots.EnchantmentSlots;
import cn.superiormc.enchantmentslots.managers.ConfigManager;
import cn.superiormc.enchantmentslots.managers.LanguageManager;
import cn.superiormc.enchantmentslots.methods.EnchantsUtil;
import cn.superiormc.enchantmentslots.objects.ObjectDeenchantItem;
import cn.superiormc.enchantmentslots.utils.ItemUtil;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DeenchantGUI extends InvGUI {

    private final ItemStack targetItem;

    private final ObjectDeenchantItem deenchantItem;

    private final int slotLimit;

    private int remainingRemovals;

    private final Runnable completeAction;

    private boolean completed;

    private final Map<Integer, Enchantment> enchCache = new HashMap<>();

    public DeenchantGUI(Player player, ItemStack targetItem, ObjectDeenchantItem deenchantItem, int removeAmount, Runnable completeAction) {
        this(player, targetItem, deenchantItem, removeAmount, -1, completeAction);
    }

    public static DeenchantGUI forSlotLimit(Player player, ItemStack targetItem, ObjectDeenchantItem deenchantItem, int slotLimit, Runnable completeAction) {
        return new DeenchantGUI(player, targetItem, deenchantItem, -1, slotLimit, completeAction);
    }

    private DeenchantGUI(Player player, ItemStack targetItem, ObjectDeenchantItem deenchantItem, int removeAmount, int slotLimit, Runnable completeAction) {
        super(player);
        this.targetItem = targetItem;
        this.deenchantItem = deenchantItem;
        this.remainingRemovals = removeAmount;
        this.slotLimit = slotLimit;
        this.completeAction = completeAction == null ? () -> { } : completeAction;
        constructGUI();
    }

    @Override
    protected void constructGUI() {
        if (Objects.isNull(inv)) {
            inv = EnchantmentSlots.methodUtil.createNewInv(player, ConfigManager.configManager.getInt("deenchant-gui.size", 54),
                    ConfigManager.configManager.getString(player, "deenchant-gui.title", "Deenchant GUI"), this);
        }
        enchCache.clear();
        inv.clear();
        int index = 0;
        for (Enchantment enchantment : deenchantItem.getEligibleEnchantments(targetItem)) {
            ItemStack displayItem = ItemUtil.generateEnchantedBook(enchantment, targetItem.getEnchantments().get(enchantment));
            ItemMeta itemMeta = displayItem.getItemMeta();
            List<String> lore = EnchantmentSlots.methodUtil.getItemLore(itemMeta);
            if (lore == null) lore = new ArrayList<>();
            lore.addAll(ConfigManager.configManager.getStringList(player, "deenchant-gui.ench-item"));
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
        if (enchantment == null || isComplete()) {
            return true;
        }
        if (!deenchantItem.removeEnchantment(player, targetItem, enchantment)) {
            return true;
        }
        if (slotLimit < 0) {
            remainingRemovals--;
        }
        if (isComplete()) {
            complete();
            player.closeInventory();
        } else {
            constructGUI();
        }
        return true;
    }

    private boolean isComplete() {
        return slotLimit >= 0 ? EnchantsUtil.getUsedSlot(targetItem) <= slotLimit : remainingRemovals <= 0;
    }

    @Override
    public void closeEventHandle(Inventory inventory) {
        if (slotLimit >= 0) {
            complete();
        } else if (!isComplete() && !completed) {
            completed = true;
            LanguageManager.languageManager.sendStringText(player, "deenchant-fail-incomplete");
            deenchantItem.doFailAction(player);
        }
    }

    private void complete() {
        if (completed) return;
        completed = true;
        completeAction.run();
    }
}
