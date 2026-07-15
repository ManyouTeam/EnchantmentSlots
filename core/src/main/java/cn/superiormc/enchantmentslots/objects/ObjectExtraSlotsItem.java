package cn.superiormc.enchantmentslots.objects;

import cn.superiormc.enchantmentslots.EnchantmentSlots;
import cn.superiormc.enchantmentslots.managers.MatchItemManager;
import cn.superiormc.enchantmentslots.methods.SlotUtil;
import cn.superiormc.enchantmentslots.utils.CommonUtil;
import cn.superiormc.enchantmentslots.utils.ItemUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ObjectExtraSlotsItem {

    public static final NamespacedKey ENCHANTMENT_SLOTS_EXTRA = new NamespacedKey(EnchantmentSlots.instance, "enchantment_extra");

    /** JSON array containing one record for every successful ExtraSlotItem expansion. */
    public static final NamespacedKey USED_EXTRA_SLOTS = new NamespacedKey(EnchantmentSlots.instance, "used_extra_slots");

    private final String id;

    private double chance;

    private final int minAddSlot;

    private final int maxAddSlot;

    private final ConfigurationSection matchItem;

    private final ConfigurationSection section;

    private final ObjectCondition condition;

    private final ItemUseLimit useLimit;

    public ObjectExtraSlotsItem(String id, ConfigurationSection section) {
        this.id = id;
        this.chance = section.getDouble("chance", 100);
        if (chance > 100) {
            chance = 100;
        } else if (chance < 0) {
            chance = 0;
        }
        this.matchItem = section.getConfigurationSection("match-item");
        int[] addSlotRange = CommonUtil.parseAddSlotRange(section.getString("add-slots", "1"));
        this.minAddSlot = Math.max(1, addSlotRange[0]);
        this.maxAddSlot = Math.max(minAddSlot, addSlotRange[1]);
        this.condition = new ObjectCondition(section.getConfigurationSection("conditions"));
        this.useLimit = new ItemUseLimit("extra-slot", id, section);
        this.section = section;
    }

    public ItemStack getItem(Player player) {
        ConfigurationSection itemSection = section.getConfigurationSection("display-item");
        if (itemSection == null) {
            itemSection = section;
        }
        ItemStack resultItem = ItemUtil.buildItemStack(player, itemSection);
        ItemMeta meta = resultItem.getItemMeta();
        meta.getPersistentDataContainer().set(ENCHANTMENT_SLOTS_EXTRA,
                PersistentDataType.STRING,
                id);
        resultItem.setItemMeta(meta);
        return resultItem;
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

    public int getAddSlot() {
        double rollNumber = ThreadLocalRandom.current().nextDouble(100);
        if (chance > rollNumber) {
            if (minAddSlot == maxAddSlot) {
                return minAddSlot;
            }
            return (int) ThreadLocalRandom.current().nextLong(minAddSlot, (long) maxAddSlot + 1);
        }
        return 0;
    }

    public String getId() {
        return id;
    }

    /** Adds one successful use and the actual number of extra slots it contributed. */
    public static void recordUsedSlots(ItemStack targetItem, String extraItemId, int slots) {
        if (slots <= 0 || extraItemId == null || extraItemId.isEmpty()
                || targetItem == null || !SlotUtil.hasBaseSlot(targetItem)) return;
        List<UsedSlots> usedSlots = getUsedSlots(targetItem.getItemMeta());
        if (usedSlots == null) usedSlots = new ArrayList<>();
        usedSlots.add(new UsedSlots(extraItemId, slots));
        saveUsedSlots(targetItem, usedSlots);
    }

    /** Returns the actual slots from the most recently recorded use. */
    public static int getNextRecordedSlots(ItemStack targetItem, String extraItemId) {
        if (targetItem == null || !targetItem.hasItemMeta()) return 0;
        List<UsedSlots> usedSlots = getUsedSlots(targetItem.getItemMeta());
        if (usedSlots == null) return 0;
        for (int i = usedSlots.size() - 1; i >= 0; i--) {
            UsedSlots used = usedSlots.get(i);
            if (used.itemId.equals(extraItemId)) return used.slots;
        }
        return 0;
    }

    /**
     * Consumes slots from one, most recently recorded use. Returns true when
     * that complete ExtraSlotItem use has been reversed.
     */
    public static boolean consumeLatestRecordedSlots(ItemStack targetItem, String extraItemId, int slots) {
        if (slots <= 0 || targetItem == null || !targetItem.hasItemMeta()) return false;
        List<UsedSlots> usedSlots = getUsedSlots(targetItem.getItemMeta());
        if (usedSlots == null) return false;
        int index = -1;
        for (int i = usedSlots.size() - 1; i >= 0; i--) {
            if (usedSlots.get(i).itemId.equals(extraItemId)) {
                index = i;
                break;
            }
        }
        if (index < 0) return false;
        UsedSlots used = usedSlots.get(index);
        if (used.slots != slots) return false;
        usedSlots.remove(index);
        saveUsedSlots(targetItem, usedSlots);
        return true;
    }

    /** Removes up to {@code slots} from the newest extra-slot records. */
    public static int consumeRecordedSlots(ItemStack targetItem, int slots) {
        if (slots <= 0 || targetItem == null || !targetItem.hasItemMeta()) return 0;
        List<UsedSlots> usedSlots = getUsedSlots(targetItem.getItemMeta());
        if (usedSlots == null) return 0;
        int remaining = slots;
        for (int i = usedSlots.size() - 1; i >= 0 && remaining > 0; i--) {
            UsedSlots used = usedSlots.get(i);
            int consumed = Math.min(remaining, used.slots);
            used.slots -= consumed;
            remaining -= consumed;
            if (used.slots == 0) usedSlots.remove(i);
        }
        int consumed = slots - remaining;
        if (consumed > 0) saveUsedSlots(targetItem, usedSlots);
        return consumed;
    }

    public static int getExtraSlots(ItemStack targetItem) {
        return targetItem != null && targetItem.hasItemMeta() ? getExtraSlots(targetItem.getItemMeta()) : 0;
    }

    public static int getExtraSlots(ItemMeta meta) {
        List<UsedSlots> usedSlots = getUsedSlots(meta);
        if (usedSlots == null) return 0;
        long total = 0;
        for (UsedSlots used : usedSlots) total += used.slots;
        return total > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) total;
    }

    /** Checks that base-slot data exists and all extra-slot records are valid. */
    public static boolean isRecordedSlotAmountConsistent(ItemStack targetItem) {
        if (!SlotUtil.hasBaseSlot(targetItem)) return false;
        List<UsedSlots> usedSlots = getUsedSlots(targetItem.getItemMeta());
        if (usedSlots == null) return false;
        long trackedSlots = 0;
        for (UsedSlots used : usedSlots) trackedSlots += used.slots;
        return trackedSlots <= Integer.MAX_VALUE;
    }

    private static List<UsedSlots> getUsedSlots(ItemMeta meta) {
        List<UsedSlots> result = new ArrayList<>();
        if (meta == null) return result;
        String serialized = meta.getPersistentDataContainer().get(USED_EXTRA_SLOTS, PersistentDataType.STRING);
        if (serialized == null || serialized.isEmpty()) return result;
        try {
            JSONArray array = new JSONArray(serialized);
            for (int i = 0; i < array.length(); i++) {
                JSONObject record = array.getJSONObject(i);
                String itemId = record.getString("id");
                int slots = record.getInt("slots");
                if (itemId.isEmpty() || slots <= 0) return null;
                result.add(new UsedSlots(itemId, slots));
            }
            return result;
        } catch (RuntimeException exception) {
            return null;
        }
    }

    private static void saveUsedSlots(ItemStack targetItem, List<UsedSlots> usedSlots) {
        ItemMeta meta = targetItem.getItemMeta();
        if (meta == null) return;
        if (usedSlots.isEmpty()) {
            meta.getPersistentDataContainer().remove(USED_EXTRA_SLOTS);
        } else {
            JSONArray serialized = new JSONArray();
            for (UsedSlots used : usedSlots) {
                serialized.put(new JSONObject().put("id", used.itemId).put("slots", used.slots));
            }
            meta.getPersistentDataContainer().set(USED_EXTRA_SLOTS, PersistentDataType.STRING, serialized.toString());
        }
        targetItem.setItemMeta(meta);
    }

    private static final class UsedSlots {
        private final String itemId;
        private int slots;

        private UsedSlots(String itemId, int slots) {
            this.itemId = itemId;
            this.slots = slots;
        }
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
