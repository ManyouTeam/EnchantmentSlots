package cn.superiormc.enchantmentslots.objects;

import cn.superiormc.enchantmentslots.EnchantmentSlots;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ItemUseLimit {

    private static final NamespacedKey USE_COUNTS = new NamespacedKey(EnchantmentSlots.instance, "item_use_counts");

    private final String key;

    private final int limit;

    public ItemUseLimit(String type, String id, ConfigurationSection section) {
        this.key = type + ":" + id;
        this.limit = Math.max(-1, section.getInt("use-limit", -1));
    }

    public boolean isReached(ItemStack targetItem) {
        return limit >= 0 && getUseCount(targetItem) >= limit;
    }

    public int getLimit() {
        return limit;
    }

    public int getUseCount(ItemStack targetItem) {
        return readCounts(targetItem).getOrDefault(key, 0);
    }

    public void recordUse(ItemStack targetItem) {
        if (limit < 0 || targetItem == null || !targetItem.hasItemMeta()) {
            return;
        }
        Map<String, Integer> counts = readCounts(targetItem);
        int current = counts.getOrDefault(key, 0);
        counts.put(key, current == Integer.MAX_VALUE ? current : current + 1);
        saveCounts(targetItem, counts);
    }

    private static Map<String, Integer> readCounts(ItemStack targetItem) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        if (targetItem == null || !targetItem.hasItemMeta()) {
            return counts;
        }
        String serialized = targetItem.getItemMeta().getPersistentDataContainer().get(USE_COUNTS, PersistentDataType.STRING);
        if (serialized == null || serialized.isEmpty()) {
            return counts;
        }
        for (String entry : serialized.split(";")) {
            String[] parts = entry.split(":", 2);
            if (parts.length != 2) continue;
            try {
                String entryKey = new String(Base64.getUrlDecoder().decode(parts[0]), StandardCharsets.UTF_8);
                int count = Integer.parseInt(parts[1]);
                if (count >= 0) {
                    counts.put(entryKey, count);
                }
            } catch (IllegalArgumentException ignored) {
                // Ignore malformed entries without blocking item use.
            }
        }
        return counts;
    }

    private static void saveCounts(ItemStack targetItem, Map<String, Integer> counts) {
        ItemMeta meta = targetItem.getItemMeta();
        if (meta == null) {
            return;
        }
        StringBuilder serialized = new StringBuilder();
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            if (serialized.length() > 0) {
                serialized.append(';');
            }
            serialized.append(Base64.getUrlEncoder().withoutPadding()
                            .encodeToString(entry.getKey().getBytes(StandardCharsets.UTF_8)))
                    .append(':')
                    .append(entry.getValue());
        }
        meta.getPersistentDataContainer().set(USE_COUNTS, PersistentDataType.STRING, serialized.toString());
        targetItem.setItemMeta(meta);
    }
}
