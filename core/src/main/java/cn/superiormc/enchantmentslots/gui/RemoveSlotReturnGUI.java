package cn.superiormc.enchantmentslots.gui;

import cn.superiormc.enchantmentslots.EnchantmentSlots;
import cn.superiormc.enchantmentslots.managers.ConfigManager;
import cn.superiormc.enchantmentslots.objects.ObjectRemoveSlotItem;
import cn.superiormc.enchantmentslots.utils.CommonUtil;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RemoveSlotReturnGUI extends InvGUI {

    private final Map<String, Integer> mapping;
    private final Map<Integer, String> choices = new HashMap<>();
    private int remainingSlots;
    private boolean settled;

    public RemoveSlotReturnGUI(Player player, ObjectRemoveSlotItem removeItem, int remainingSlots) {
        super(player);
        this.mapping = removeItem.getReturnMapping();
        this.remainingSlots = remainingSlots;
        constructGUI();
    }

    public static boolean settleSingleAvailableChoice(Player player, ObjectRemoveSlotItem removeItem, int remainingSlots) {
        return settleSingleAvailableChoice(player, removeItem.getReturnMapping(), remainingSlots);
    }

    private static boolean settleSingleAvailableChoice(Player player, Map<String, Integer> mapping, int remainingSlots) {
        Map.Entry<String, Integer> choice = null;
        for (Map.Entry<String, Integer> entry : mapping.entrySet()) {
            if (entry.getValue() > remainingSlots) continue;
            if (ConfigManager.configManager.getExtraSlotItem(entry.getKey(), player) == null) continue;
            if (choice != null) return false;
            choice = entry;
        }
        if (choice == null) return false;
        while (remainingSlots >= choice.getValue()) {
            ItemStack item = ConfigManager.configManager.getExtraSlotItem(choice.getKey(), player);
            if (item == null) break;
            CommonUtil.giveOrDrop(player, item);
            remainingSlots -= choice.getValue();
        }
        return true;
    }

    @Override
    protected void constructGUI() {
        if (Objects.isNull(inv)) {
            int size = Math.max(9, Math.min(54, ConfigManager.configManager.getInt("remove-slot-return-gui.size", 27)));
            inv = EnchantmentSlots.methodUtil.createNewInv(player, size,
                    ConfigManager.configManager.getString(player, "remove-slot-return-gui.title", "Choose slot return"), this);
        }
        inv.clear();
        choices.clear();
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(mapping.entrySet());
        entries.sort(Comparator.comparingInt(Map.Entry::getValue));
        int slot = 0;
        for (Map.Entry<String, Integer> entry : entries) {
            if (slot >= inv.getSize()) break;
            if (remainingSlots < entry.getValue()) continue;
            ItemStack display = ConfigManager.configManager.getExtraSlotItem(entry.getKey(), player);
            if (display == null) continue;
            appendExchangeLore(display, entry.getValue());
            setItem(slot, display);
            choices.put(slot, entry.getKey());
            slot++;
        }
    }

    @Override
    public boolean clickEventHandle(Inventory inventory, ItemStack cursor, int slot) {
        String itemId = choices.get(slot);
        if (itemId == null || settled) return true;
        int value = mapping.get(itemId);
        if (remainingSlots < value) return true;
        give(itemId);
        remainingSlots -= value;
        if (remainingSlots == 0) {
            settled = true;
            player.closeInventory();
        } else if (settleSingleAvailableChoice(player, mapping, remainingSlots)) {
            settled = true;
            player.closeInventory();
        } else {
            constructGUI();
        }
        return true;
    }

    @Override
    public void closeEventHandle(Inventory inventory) {
        settleAutomatically();
    }

    private void settleAutomatically() {
        if (settled) return;
        settled = true;
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(mapping.entrySet());
        entries.sort(Comparator.comparingInt(Map.Entry::getValue));
        for (Map.Entry<String, Integer> entry : entries) {
            while (remainingSlots >= entry.getValue()) {
                give(entry.getKey());
                remainingSlots -= entry.getValue();
            }
        }
    }

    private void give(String itemId) {
        ItemStack item = ConfigManager.configManager.getExtraSlotItem(itemId, player);
        if (item != null) CommonUtil.giveOrDrop(player, item);
    }

    private void appendExchangeLore(ItemStack item, int slotCost) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        List<String> lore = EnchantmentSlots.methodUtil.getItemLore(meta);
        lore = lore == null ? new ArrayList<>() : new ArrayList<>(lore);
        for (String line : ConfigManager.configManager.getStringList(player, "remove-slot-return-gui.add-lore")) {
            lore.add(line.replace("{remaining_slots}", String.valueOf(remainingSlots))
                    .replace("{slot_cost}", String.valueOf(slotCost)));
        }
        EnchantmentSlots.methodUtil.setItemLore(meta, lore, player);
        item.setItemMeta(meta);
    }
}
