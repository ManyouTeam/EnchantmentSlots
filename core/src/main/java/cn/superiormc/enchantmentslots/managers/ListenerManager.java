package cn.superiormc.enchantmentslots.managers;

import cn.superiormc.enchantmentslots.EnchantmentSlots;
import cn.superiormc.enchantmentslots.gui.InvGUI;
import cn.superiormc.enchantmentslots.listeners.*;
import cn.superiormc.enchantmentslots.methods.AddLore;
import cn.superiormc.enchantmentslots.protolcol.packetevents.*;
import cn.superiormc.enchantmentslots.protolcol.eco.EcoDisplayModule;
import cn.superiormc.enchantmentslots.protolcol.packetevents.*;
import cn.superiormc.enchantmentslots.utils.CommonUtil;
import cn.superiormc.enchantmentslots.utils.TextUtil;
import com.github.retrooper.packetevents.PacketEvents;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ListenerManager {

    public static ListenerManager listenerManager;

    private final Map<UUID, InvGUI> listeners = new HashMap<>();

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
            PacketEventsListener.registerPacketEventsListener();
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
    }

    public InvGUI getInvGUI(Player player) {
        return listeners.get(player.getUniqueId());
    }

    public void unregisterAllListener() {
        HandlerList.unregisterAll(EnchantmentSlots.instance);
    }

    public String getPlugin() {
        return plugin;
    }
}

class PacketEventsListener {
    public static void registerPacketEventsListener() {
        PacketEvents.getAPI().getEventManager().registerListener(new SetSlots(), ConfigManager.configManager.getPriority());
        PacketEvents.getAPI().getEventManager().registerListener(new WindowItem(), ConfigManager.configManager.getPriority());
        PacketEvents.getAPI().getEventManager().registerListener(new WindowMerchant(), ConfigManager.configManager.getPriority());
        PacketEvents.getAPI().getEventManager().registerListener(new SetCreativeSlots(), ConfigManager.configManager.getPriority());
        if (CommonUtil.getMinorVersion(21, 5)) {
            PacketEvents.getAPI().getEventManager().registerListener(new SetCursorItem(), ConfigManager.configManager.getPriority());
            PacketEvents.getAPI().getEventManager().registerListener(new ContainerClick(), ConfigManager.configManager.getPriority());
        }
    }
}
