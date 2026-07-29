package cn.superiormc.enchantmentslots.methods;

import cn.superiormc.enchantmentslots.EnchantmentSlots;
import cn.superiormc.enchantmentslots.managers.ConfigManager;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class EnchantDescriptionDisplay {

    public static final String DISPLAY_PERMISSION = "enchantmentslots.displaydescription";

    private static final NamespacedKey DISABLED_KEY = new NamespacedKey(EnchantmentSlots.instance, "description-display-disabled");

    private static final Set<UUID> DISABLED_PLAYERS = ConcurrentHashMap.newKeySet();

    private EnchantDescriptionDisplay() {
    }

    public static boolean canDisplay(Player player) {
        if (player == null) {
            return false;
        }
        if (ConfigManager.configManager.getBoolean(
                "settings.add-lore.placeholder.enchants.description.require-permission", false)
                && !player.hasPermission(DISPLAY_PERMISSION)) {
            return false;
        }
        return !DISABLED_PLAYERS.contains(player.getUniqueId());
    }

    public static boolean toggle(Player player) {
        UUID playerId = player.getUniqueId();
        if (DISABLED_PLAYERS.remove(playerId)) {
            player.getPersistentDataContainer().remove(DISABLED_KEY);
            return true;
        }
        DISABLED_PLAYERS.add(playerId);
        player.getPersistentDataContainer().set(DISABLED_KEY, PersistentDataType.BYTE, (byte) 1);
        return false;
    }

    public static void load(Player player) {
        Byte disabled = player.getPersistentDataContainer().get(DISABLED_KEY, PersistentDataType.BYTE);
        if (disabled != null && disabled != 0) {
            DISABLED_PLAYERS.add(player.getUniqueId());
        } else {
            DISABLED_PLAYERS.remove(player.getUniqueId());
        }
    }

    public static void unload(Player player) {
        DISABLED_PLAYERS.remove(player.getUniqueId());
    }

    public static void clearCache() {
        DISABLED_PLAYERS.clear();
    }
}
