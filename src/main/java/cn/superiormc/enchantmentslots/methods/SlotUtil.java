package cn.superiormc.enchantmentslots.methods;

import cn.superiormc.enchantmentslots.EnchantmentSlots;
import cn.superiormc.enchantmentslots.managers.ConfigManager;
import cn.superiormc.enchantmentslots.managers.LanguageManager;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class SlotUtil {

    public static final NamespacedKey ENCHANTMENT_SLOTS_KEY = new NamespacedKey(EnchantmentSlots.instance, "enchantment_slots");

    public static ItemStack setSlot(ItemStack item, Player player, boolean override) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }
        item.setItemMeta(setSlot(item, meta, player, override));
        return item;
    }

    public static ItemMeta setSlot(ItemStack item, ItemMeta meta, Player player, boolean override) {
        if (override) {
            meta.getPersistentDataContainer().remove(ENCHANTMENT_SLOTS_KEY);
        }
        if (meta.getPersistentDataContainer().has(ENCHANTMENT_SLOTS_KEY, PersistentDataType.INTEGER)) {
            return meta;
        }
        int defaultSlot = ConfigManager.configManager.getDefaultLimits(item, player);
        if (defaultSlot > 0) {
            meta.getPersistentDataContainer().set(ENCHANTMENT_SLOTS_KEY,
                    PersistentDataType.INTEGER, defaultSlot);
        }
        if (ConfigManager.configManager.getBoolean("settings.set-slot-trigger.add-hide-enchant-flag", false)) {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        return meta;
    }

    public static ItemStack setSlot(ItemStack item, int slotValue, boolean override) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }
        if (override) {
            meta.getPersistentDataContainer().remove(ENCHANTMENT_SLOTS_KEY);
        }
        if (meta.getPersistentDataContainer().has(ENCHANTMENT_SLOTS_KEY, PersistentDataType.INTEGER)) {
            return item;
        }
        if (slotValue > 0) {
            meta.getPersistentDataContainer().set(ENCHANTMENT_SLOTS_KEY,
                    PersistentDataType.INTEGER,
                    slotValue);
        }
        if (ConfigManager.configManager.getBoolean("settings.set-slot-trigger.add-hide-enchant-flag", false)) {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        item.setItemMeta(meta);
        return item;
    }

    public static int getSlot(ItemStack item) {
        if (!item.hasItemMeta()) {
            return 0;
        }
        ItemMeta meta = item.getItemMeta();
        return getSlot(meta);
    }

    public static int getSlot(ItemMeta meta) {
        if (!meta.getPersistentDataContainer().has(ENCHANTMENT_SLOTS_KEY, PersistentDataType.INTEGER)) {
            return 0;
        }
        return meta.getPersistentDataContainer().get(ENCHANTMENT_SLOTS_KEY, PersistentDataType.INTEGER);
    }

    public static void removeSlot(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        meta.getPersistentDataContainer().remove(ENCHANTMENT_SLOTS_KEY);
    }

    public static ItemStack removeExcessEnchantments(ItemStack item, Player player) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }
        item.setItemMeta(removeExcessEnchantments(meta, player));
        return item;
    }

    public static ItemMeta removeExcessEnchantments(ItemMeta meta, Player player) {
        int maxEnchantments = SlotUtil.getSlot(meta);
        int enchantmentAmount = EnchantsUtil.getUsedSlot(meta);
        int loggedRemoveAmount;
        if (maxEnchantments > 0 && enchantmentAmount > maxEnchantments) {
            int removeAmount = enchantmentAmount - maxEnchantments;
            loggedRemoveAmount = removeAmount;
            for (Enchantment enchant : meta.getEnchants().keySet()) {
                if (removeAmount <= 0) {
                    break;
                }
                meta.removeEnchant(enchant);
                removeAmount = removeAmount - EnchantsUtil.getUsedSlot(enchant);
            }
            if (!ConfigManager.configManager.getBoolean("settings.set-slot-trigger.SetSlotPacket.remove-illegal-excess-enchant.hide-remove-message", false)) {
                LanguageManager.languageManager.sendStringText(player, "remove-excess-enchants", "max", String.valueOf(maxEnchantments), "remove", String.valueOf(loggedRemoveAmount));
                LanguageManager.languageManager.sendStringText("remove-excess-enchants-console", "player", player.getName(),
                        "max", String.valueOf(maxEnchantments), "remove", String.valueOf(loggedRemoveAmount));
            }
        }
        return meta;
    }

}
