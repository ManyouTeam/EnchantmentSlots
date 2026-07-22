package cn.superiormc.enchantmentslots.objects;

import cn.superiormc.enchantmentslots.EnchantmentSlots;
import cn.superiormc.enchantmentslots.managers.MatchItemManager;
import cn.superiormc.enchantmentslots.utils.CommonUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class ObjectItemSlot {

    private final String id;

    private final ConfigurationSection matchItemSection;

    private final ConfigurationSection defaultSlotsSection;

    private final ConfigurationSection maxSlotsSection;

    private final ConfigurationSection conditionSection;

    private final boolean autoAddLore;

    private final boolean autoHideEnchants;

    public ObjectItemSlot(String id, ConfigurationSection section) {
        this.id = id;
        this.matchItemSection = section.getConfigurationSection("match-item");
        this.defaultSlotsSection = section.getConfigurationSection("default-slots");
        this.maxSlotsSection = section.getConfigurationSection("max-slots");
        this.conditionSection = section.getConfigurationSection("slots-conditions");
        this.autoAddLore = section.getBoolean("auto-add-lore", false);
        this.autoHideEnchants = section.getBoolean("auto-hide-enchants", true);
    }

    public int getDefaultSlot(ItemStack item, Player player) {
        return getDefaultSlot(item, player, true);
    }

    public int getDefaultSlot(ItemStack item, Player player, boolean addHideEnchantsFlag) {
        if (matchItemSection == null || MatchItemManager.matchItemManager.getMatch(matchItemSection, item)) {
            if (addHideEnchantsFlag && autoHideEnchants) {
                ItemMeta meta = item.getItemMeta();
                if (meta instanceof EnchantmentStorageMeta) {
                    ItemFlag flag;
                    try {
                        flag = ItemFlag.valueOf("HIDE_STORED_ENCHANTS");
                    } catch (IllegalArgumentException e) {
                        flag = ItemFlag.valueOf("HIDE_STORED_ENCHANTMENTS");
                    }
                    meta.addItemFlags(flag);
                }
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                item.setItemMeta(meta);
            }
            if (defaultSlotsSection == null) {
                return -1;
            }
            if (conditionSection == null) {
                return parseInt(defaultSlotsSection, "default", "5");
            }
            Set<String> groupNameSet = conditionSection.getKeys(false);
            List<Integer> result = new ArrayList<>();
            for (String groupName : groupNameSet) {
                ObjectCondition condition = new ObjectCondition(conditionSection.getConfigurationSection(groupName));
                if (groupName.equals("default") || (defaultSlotsSection.contains(groupName) &&
                        condition.getAllBoolean(player, 0))) {
                    result.add(parseInt(defaultSlotsSection, groupName, "5"));
                }
            }
            if (result.isEmpty()) {
                result.add(parseInt(defaultSlotsSection, "default", "5"));
            }
            return Collections.max(result);
        }
        return -1;
    }

    public int getMaxSlot(ItemStack item, Player player) {
        if (matchItemSection == null || MatchItemManager.matchItemManager.getMatch(matchItemSection, item)) {
            if (maxSlotsSection == null) {
                return -1;
            }
            if (conditionSection == null) {
                return parseInt(maxSlotsSection, "default", "-1");
            }
            Set<String> groupNameSet = conditionSection.getKeys(false);
            List<Integer> result = new ArrayList<>();
            for (String groupName : groupNameSet) {
                ObjectCondition condition = new ObjectCondition(conditionSection.getConfigurationSection(groupName));
                if (groupName.equals("default") || (maxSlotsSection.contains(groupName) &&
                        condition.getAllBoolean(player, 0))) {
                    result.add(parseInt(maxSlotsSection, groupName, "-1"));
                }
            }
            if (result.isEmpty()) {
                result.add(parseInt(maxSlotsSection, "default", "-1"));
            }
            return Collections.max(result);
        }
        return -1;
    }

    public boolean isAutoAddLore(ItemStack item) {
        if (!MatchItemManager.matchItemManager.getMatch(matchItemSection, item)) {
            return false;
        }
        return autoAddLore;
    }

    public String getId() {
        return id;
    }

    private int parseInt(ConfigurationSection section, String key, String defaultValue) {
        String input = section.getString(key, defaultValue);
        int[] range = CommonUtil.parseAddSlotRange(input);
        if (autoAddLore || range[0] == range[1]) {
            return range[1];
        }
        return (int) ThreadLocalRandom.current().nextLong(range[0], (long) range[1] + 1);
    }
}
