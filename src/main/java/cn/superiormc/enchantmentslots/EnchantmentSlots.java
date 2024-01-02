package cn.superiormc.enchantmentslots;

import cn.superiormc.enchantmentslots.commands.MainCommand;
import cn.superiormc.enchantmentslots.commands.MainTab;
import cn.superiormc.enchantmentslots.events.EnchantGUIEnchant;
import cn.superiormc.enchantmentslots.events.PlayerClick;
import cn.superiormc.enchantmentslots.events.PlayerEnchant;
import cn.superiormc.enchantmentslots.events.PlayerInventory;
import cn.superiormc.enchantmentslots.papi.PlaceholderAPIExpansion;
import cn.superiormc.enchantmentslots.protolcol.GeneralProtolcol;
import cn.superiormc.enchantmentslots.utils.ConfigReader;
import com.comphenix.protocol.ProtocolLibrary;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class EnchantmentSlots extends JavaPlugin {

    public static JavaPlugin instance;
    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        registerEvents();
        registerCommands();
        registerPackets();
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            PlaceholderAPIExpansion.papi = new PlaceholderAPIExpansion(this);
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[EnchantmentSlots] §fHooking into PlaceholderAPI...");
            if (PlaceholderAPIExpansion.papi.register()){
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[EnchantmentSlots] §fFinished hook!");
            }
        }
        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[EnchantmentSlots] §fPlugin is loaded. Author: PQguanfang.");
    }

    @Override
    public void onDisable() {
        ProtocolLibrary.getProtocolManager().removePacketListeners(this);
        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[EnchantmentSlots] §fPlugin is disabled. Author: PQguanfang.");
    }

    private void registerEvents() {
        if (ConfigReader.getEnchantItemTrigger()) {
            Bukkit.getPluginManager().registerEvents(new PlayerEnchant(), this);
        }
        if (ConfigReader.getInventoryClickTrigger()) {
            Bukkit.getPluginManager().registerEvents(new PlayerClick(), this);
        }
        Bukkit.getPluginManager().registerEvents(new PlayerInventory(), this);
        if (EnchantmentSlots.instance.getServer().getPluginManager().isPluginEnabled("EnchantGui")) {
            Bukkit.getPluginManager().registerEvents(new EnchantGUIEnchant(), this);
        }
    }

    public void registerCommands() {
        Objects.requireNonNull(Bukkit.getPluginCommand("enchantmentslots")).setExecutor(new MainCommand());
        Objects.requireNonNull(Bukkit.getPluginCommand("enchantmentslots")).setTabCompleter(new MainTab());
    }

    public void registerPackets() {
        GeneralProtolcol.init();
    }
}
