package cn.superiormc.enchantmentslots.managers;

import cn.superiormc.enchantmentslots.EnchantmentSlots;
import cn.superiormc.enchantmentslots.gui.InvGUI;
import cn.superiormc.enchantmentslots.listeners.*;
import cn.superiormc.enchantmentslots.methods.AddLore;
import cn.superiormc.enchantmentslots.protolcol.eco.EcoDisplayModule;
import cn.superiormc.enchantmentslots.protolcol.packetevents.*;
import cn.superiormc.enchantmentslots.utils.CommonUtil;
import cn.superiormc.enchantmentslots.utils.TextUtil;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketListenerCommon;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ListenerManager {

    public static ListenerManager listenerManager;

    private final Map<UUID, InvGUI> listeners = new HashMap<>();

    private boolean packetEventsRegistered;

    private String plugin;

    public ListenerManager() {
        listenerManager = this;
        registerListeners();
        registerPacketListeners();
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new GUIListener(), EnchantmentSlots.instance);
        Bukkit.getPluginManager().registerEvents(new PlayerCacheListener(), EnchantmentSlots.instance);
        Bukkit.getPluginManager().registerEvents(new PlayerEnchantListener(), EnchantmentSlots.instance);
        Bukkit.getPluginManager().registerEvents(new PlayerAnvilListener(), EnchantmentSlots.instance);
        if (EnchantmentSlots.instance.getConfig().getBoolean("settings.set-slot-trigger.InventoryClickEvent.enabled", true)) {
            Bukkit.getPluginManager().registerEvents(new PlayerClickListener(), EnchantmentSlots.instance);
        }
        Bukkit.getPluginManager().registerEvents(new PlayerInventoryListener(), EnchantmentSlots.instance);
        if (CommonUtil.getMajorVersion(16)) {
            Bukkit.getPluginManager().registerEvents(new PlayerSmithListener(), EnchantmentSlots.instance);
        }
        if (EnchantmentSlots.instance.getServer().getPluginManager().isPluginEnabled("EnchantGui")) {
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fHooking into EnchantGui...");
            Bukkit.getPluginManager().registerEvents(new EnchantGUIEnchantListener(), EnchantmentSlots.instance);
        }
        if (CommonUtil.checkPluginLoad("QuickShop-Hikari")) {
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fHooking into QuickShop-Hikari...");
            Bukkit.getPluginManager().registerEvents(new QuickShopListener(), EnchantmentSlots.instance);
        }
        if (CommonUtil.getMajorVersion(19) && EnchantmentSlots.methodUtil.methodID().equals("paper") &&
                ConfigManager.configManager.getBoolean("enchant-gui.anti-dupe-checker", false)) {
            Bukkit.getPluginManager().registerEvents(new DupeListener(), EnchantmentSlots.instance);
        }
    }

    private void registerPacketListeners() {
        plugin = ConfigManager.configManager.getString("settings.add-lore.use-listener-plugin",
                ConfigManager.configManager.getString("settings.use-listener-plugin", "packetevents"));
        if (plugin.equals("packetevents") && CommonUtil.checkPluginLoad("packetevents")) {
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fHooking into packetevents...");
            PacketEventsListener.registerPacketEventsListeners();
            packetEventsRegistered = true;
            AddLore.lorePrefix = ConfigManager.configManager.getString("settings.add-lore.lore-prefix", "§y");
        } else if (plugin.equals("eco") && CommonUtil.checkPluginLoad("eco")) {
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fHooking into eco....");
            EcoDisplayModule.init();
            AddLore.lorePrefix = "§z";
        } else {
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §cCan not found any packet " +
                    "listener plugin, enchantment slot won't displayed in your server!");
        }
    }

    public void registerNewGUIListener(Player player, InvGUI inv) {
        unregisterListeners(player);
        listeners.put(player.getUniqueId(), inv);
    }

    public void unregisterNewGUIListener(Player player, InvGUI inv) {
        listeners.remove(player.getUniqueId(), inv);
    }

    public void unregisterListeners(Player player) {
        listeners.remove(player.getUniqueId());
        if (packetEventsRegistered) {
            PacketEventsListener.removePlayer(player.getUniqueId());
        }
    }

    public InvGUI getInvGUI(Player player) {
        return listeners.get(player.getUniqueId());
    }

    public void unregisterAllListener() {
        HandlerList.unregisterAll(EnchantmentSlots.instance);
        listeners.clear();
        PlayerCacheListener.clearCaches();
        if (packetEventsRegistered) {
            PacketEventsListener.unregisterPacketEventsListeners();
            packetEventsRegistered = false;
        }
    }
}

class PacketEventsListener {

    private static final Collection<PacketListenerCommon> packetListeners = new ArrayList<>();

    public static void registerPacketEventsListeners() {
        registerPacketListener(new SetSlots());
        registerPacketListener(new WindowItem());
        registerPacketListener(new WindowMerchant());
        registerPacketListener(new SetCreativeSlots());
        if (CommonUtil.getMinorVersion(21, 5)) {
            registerPacketListener(new SetCursorItem());
            registerPacketListener(new ContainerClick());
        }
    }

    public static void unregisterPacketEventsListeners() {
        if (!packetListeners.isEmpty()) {
            PacketEvents.getAPI().getEventManager().unregisterListeners(packetListeners.toArray(new PacketListenerCommon[0]));
        }
        packetListeners.clear();
        SetCursorItem.clearCache();
    }

    public static void removePlayer(UUID playerId) {
        SetCursorItem.removePlayer(playerId);
    }

    private static void registerPacketListener(PacketListener listener) {
        packetListeners.add(PacketEvents.getAPI().getEventManager().registerListener(listener, getPriority()));
    }

    private static PacketListenerPriority getPriority() {
        return PacketListenerPriority.valueOf(ConfigManager.configManager.getString(
                "settings.add-lore.packet-listener-priority",
                ConfigManager.configManager.getString("settings.packet-listener-priority", "MONITOR")).toUpperCase());
    }
}
