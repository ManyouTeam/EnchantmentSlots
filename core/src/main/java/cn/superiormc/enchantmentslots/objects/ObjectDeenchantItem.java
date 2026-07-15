package cn.superiormc.enchantmentslots.objects;

import cn.superiormc.enchantmentslots.EnchantmentSlots;
import cn.superiormc.enchantmentslots.managers.MatchItemManager;
import cn.superiormc.enchantmentslots.utils.CommonUtil;
import cn.superiormc.enchantmentslots.utils.ItemUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ObjectDeenchantItem {

    public static final NamespacedKey ENCHANTMENT_SLOTS_DEENCHANT = new NamespacedKey(EnchantmentSlots.instance, "deenchant_item");

    public enum RemoveType {
        RANDOM,
        ORDER,
        GUI
    }

    private final String id;

    private final ConfigurationSection section;

    private final ConfigurationSection matchItem;

    private final int removeAmount;

    private final RemoveType removeType;

    private final List<String> whitelist;

    private final List<String> blacklist;

    private final List<String> order;

    private final ObjectCondition condition;

    private final ItemUseLimit useLimit;

    public ObjectDeenchantItem(String id, ConfigurationSection section) {
        this.id = id;
        this.section = section;
        this.matchItem = section.getConfigurationSection("match-item");
        this.removeAmount = Math.max(1, section.getInt("remove-amount", 1));
        this.removeType = parseType(section.getString("remove-type", "RANDOM"));
        ConfigurationSection enchantSection = section.getConfigurationSection("remove-enchants");
        this.whitelist = getList(enchantSection, "whitelist", section.getStringList("enchant-whitelist"));
        this.blacklist = getList(enchantSection, "blacklist", section.getStringList("enchant-blacklist"));
        this.order = getList(enchantSection, "order", section.getStringList("enchant-order"));
        this.condition = new ObjectCondition(section.getConfigurationSection("conditions"));
        this.useLimit = new ItemUseLimit("deenchant", id, section);
    }

    private List<String> getList(ConfigurationSection section, String path, List<String> fallback) {
        List<String> values = section == null ? fallback : section.getStringList(path);
        List<String> result = new ArrayList<>();
        for (String value : values) {
            result.add(value.toLowerCase(Locale.ROOT));
        }
        return result;
    }

    private RemoveType parseType(String value) {
        try {
            return RemoveType.valueOf(value.toUpperCase(Locale.ROOT).replace('-', '_'));
        } catch (IllegalArgumentException exception) {
            return RemoveType.RANDOM;
        }
    }

    public ItemStack getItem(Player player) {
        ItemStack item = ItemUtil.buildItemStack(player, section.getConfigurationSection("display-item"));
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(ENCHANTMENT_SLOTS_DEENCHANT, PersistentDataType.STRING, id);
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

    public List<Enchantment> getEligibleEnchantments(ItemStack item) {
        List<Enchantment> result = new ArrayList<>();
        for (Enchantment enchantment : item.getEnchantments().keySet()) {
            String key = enchantment.getKey().getKey().toLowerCase(Locale.ROOT);
            String fullKey = enchantment.getKey().toString().toLowerCase(Locale.ROOT);
            if ((!whitelist.isEmpty() && !matches(whitelist, key, fullKey)) || matches(blacklist, key, fullKey)) {
                continue;
            }
            result.add(enchantment);
        }
        return result;
    }

    private boolean matches(List<String> values, String key, String fullKey) {
        return values.contains("*") || values.contains(key) || values.contains(fullKey);
    }

    public List<Enchantment> getAutomaticEnchantments(ItemStack item, int amount) {
        List<Enchantment> result = getEligibleEnchantments(item);
        if (removeType == RemoveType.RANDOM) {
            Collections.shuffle(result);
        } else {
            result.sort(Comparator.comparingInt(this::getOrder).thenComparing(enchantment -> enchantment.getKey().toString()));
        }
        return result.subList(0, Math.min(Math.max(amount, 0), result.size()));
    }

    private int getOrder(Enchantment enchantment) {
        String key = enchantment.getKey().getKey().toLowerCase(Locale.ROOT);
        String fullKey = enchantment.getKey().toString().toLowerCase(Locale.ROOT);
        int simpleIndex = order.indexOf(key);
        int fullIndex = order.indexOf(fullKey);
        int index = simpleIndex >= 0 ? simpleIndex : fullIndex;
        return index >= 0 ? index : Integer.MAX_VALUE;
    }

    public boolean removeEnchantment(Player player, ItemStack targetItem, Enchantment enchantment) {
        if (!getEligibleEnchantments(targetItem).contains(enchantment)) {
            return false;
        }
        ItemMeta meta = targetItem.getItemMeta();
        Integer level = meta.getEnchants().get(enchantment);
        if (level == null) {
            return false;
        }
        meta.removeEnchant(enchantment);
        targetItem.setItemMeta(meta);
        CommonUtil.giveOrDrop(player, ItemUtil.generateEnchantedBook(enchantment, level));
        return true;
    }

    public int removeAutomatically(Player player, ItemStack targetItem, int amount) {
        int removed = 0;
        for (Enchantment enchantment : getAutomaticEnchantments(targetItem, amount)) {
            if (removeEnchantment(player, targetItem, enchantment)) {
                removed++;
            }
        }
        return removed;
    }

    public String getId() {
        return id;
    }

    public int getRemoveAmount() {
        return removeAmount;
    }

    public RemoveType getRemoveType() {
        return removeType;
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
