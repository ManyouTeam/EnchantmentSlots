package cn.superiormc.enchantmentslots.objects;

import cn.superiormc.enchantmentslots.EnchantmentSlots;
import cn.superiormc.enchantmentslots.managers.MatchItemManager;
import cn.superiormc.enchantmentslots.utils.ItemUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class ObjectRemoveSlotItem {

    public static final NamespacedKey ENCHANTMENT_SLOTS_REMOVE = new NamespacedKey(EnchantmentSlots.instance, "remove_slot_item");

    public enum RemoveType {
        FIXED,
        EXCESS,
        LINKED_EXTRA
    }

    public enum DeenchantItemMode {
        RANDOM,
        GUI
    }

    private final String id;

    private final ConfigurationSection section;

    private final ConfigurationSection matchItem;

    private final RemoveType removeType;

    private final int removeSlots;

    private final DeenchantItemMode deenchantItemMode;

    private final String linkedExtraItemId;

    private final boolean illegalSlotCheck;

    private final boolean returnExtraItems;

    private final Map<String, Integer> returnMapping = new LinkedHashMap<>();

    private final ObjectCondition condition;

    private final ObjectAction successAction;

    private final ObjectAction failAction;

    private final ItemUseLimit useLimit;

    public ObjectRemoveSlotItem(String id, ConfigurationSection section) {
        this.id = id;
        this.section = section;
        this.matchItem = section.getConfigurationSection("match-item");
        this.removeType = parseType(section.getString("remove-type", "FIXED"));
        this.removeSlots = Math.max(1, section.getInt("remove-slots", 1));
        this.deenchantItemMode = parseDeenchantItemMode(section.getString("deenchant-item-mode", "RANDOM"));
        this.linkedExtraItemId = section.getString("linked-extra-slot-item", section.getString("extra-slot-item", ""));
        this.illegalSlotCheck = section.getBoolean("illegal-slot-check", true);
        ConfigurationSection refund = section.getConfigurationSection("return-extra-items");
        this.returnExtraItems = refund != null ? refund.getBoolean("enabled", false) : section.getBoolean("return-extra-items", false);
        ConfigurationSection mapping = refund == null ? section.getConfigurationSection("return-mapping") : refund.getConfigurationSection("mapping");
        if (mapping != null) {
            for (String itemId : mapping.getKeys(false)) {
                int value = mapping.getInt(itemId, 0);
                if (value > 0) {
                    returnMapping.put(itemId, value);
                }
            }
        }
        this.condition = new ObjectCondition(section.getConfigurationSection("conditions"));
        this.successAction = new ObjectAction(section.getConfigurationSection("success-actions"));
        this.failAction = new ObjectAction(section.getConfigurationSection("fail-actions"));
        this.useLimit = new ItemUseLimit("remove-slot", id, section);
    }

    private RemoveType parseType(String value) {
        try {
            String normalized = value.toUpperCase(Locale.ROOT).replace('-', '_');
            if (normalized.equals("REMOVE_EXCESS")) {
                return RemoveType.EXCESS;
            }
            if (normalized.equals("LINKED") || normalized.equals("EXTRA_SLOT_ITEM")) {
                return RemoveType.LINKED_EXTRA;
            }
            return RemoveType.valueOf(normalized);
        } catch (IllegalArgumentException exception) {
            return RemoveType.FIXED;
        }
    }

    private DeenchantItemMode parseDeenchantItemMode(String value) {
        try {
            return DeenchantItemMode.valueOf(value.toUpperCase(Locale.ROOT).replace('-', '_'));
        } catch (IllegalArgumentException exception) {
            return DeenchantItemMode.RANDOM;
        }
    }

    public ItemStack getItem(Player player) {
        ItemStack item = ItemUtil.buildItemStack(player, section.getConfigurationSection("display-item"));
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(ENCHANTMENT_SLOTS_REMOVE, PersistentDataType.STRING, id);
        item.setItemMeta(meta);
        return item;
    }

    public boolean canApply(Player player, ItemStack item) {
        return meetsConditions(player) && matchesItem(item);
    }

    public boolean meetsConditions(Player player) {
        return condition.getAllBoolean(player, 1);
    }

    public boolean matchesItem(ItemStack item) {
        return matchItem == null || MatchItemManager.matchItemManager.getMatch(matchItem, item);
    }

    public int getSlotsToRemove(int currentSlots, int usedSlots) {
        if (removeType == RemoveType.EXCESS) {
            return Math.max(0, currentSlots - usedSlots);
        }
        return removeSlots;
    }

    public String getId() {
        return id;
    }

    public DeenchantItemMode getDeenchantItemMode() {
        return deenchantItemMode;
    }

    public boolean isLinkedExtraItem() {
        return removeType == RemoveType.LINKED_EXTRA;
    }

    public String getLinkedExtraItemId() {
        return linkedExtraItemId;
    }

    public boolean isIllegalSlotCheckEnabled() {
        return illegalSlotCheck;
    }

    public boolean isReturnExtraItems() {
        return returnExtraItems && !returnMapping.isEmpty();
    }

    public Map<String, Integer> getReturnMapping() {
        return returnMapping;
    }

    public void doSuccessAction(Player player, int amount) {
        successAction.runAllActions(player, amount);
    }

    public void doFailAction(Player player) {
        failAction.runAllActions(player, 0);
    }

    public boolean hasReachedUseLimit(ItemStack targetItem) {
        return useLimit.isReached(targetItem);
    }

    public void recordUse(ItemStack targetItem) {
        useLimit.recordUse(targetItem);
    }

    public int getUseLimit() {
        return useLimit.getLimit();
    }

    public int getUseCount(ItemStack targetItem) {
        return useLimit.getUseCount(targetItem);
    }
}
