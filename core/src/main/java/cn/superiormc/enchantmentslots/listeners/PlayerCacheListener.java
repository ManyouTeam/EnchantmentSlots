package cn.superiormc.enchantmentslots.listeners;

import cn.superiormc.enchantmentslots.managers.ConfigManager;
import cn.superiormc.enchantmentslots.managers.ListenerManager;
import cn.superiormc.enchantmentslots.utils.SchedulerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerCacheListener implements Listener {

    public static final Set<UUID> loadedPlayers = ConcurrentHashMap.newKeySet();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        long time = ConfigManager.configManager.getLong("settings.set-slot-trigger.SetSlotPacket.remove-illegal-excess-enchant.ignore-join-time", -1);
        if (time < 0) {
            loadedPlayers.add(event.getPlayer().getUniqueId());
            return;
        }
        UUID playerId = event.getPlayer().getUniqueId();
        SchedulerUtil.runTaskLater(() -> {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null && player.isOnline()) {
                loadedPlayers.add(playerId);
            }
        }, time);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        ListenerManager.listenerManager.unregisterListeners(event.getPlayer());
        loadedPlayers.remove(event.getPlayer().getUniqueId());
    }

    public static void clearCaches() {
        loadedPlayers.clear();
    }
}
