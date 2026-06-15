package cn.superiormc.enchantmentslots.managers;

import cn.superiormc.enchantmentslots.EnchantmentSlots;
import cn.superiormc.enchantmentslots.objects.ObjectExtraSlotsItem;
import cn.superiormc.enchantmentslots.objects.ObjectItemSlot;
import cn.superiormc.enchantmentslots.utils.TextUtil;
import com.willfp.eco.core.display.DisplayPriority;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.*;

import static cn.superiormc.enchantmentslots.objects.ObjectExtraSlotsItem.ENCHANTMENT_SLOTS_EXTRA;

public class ConfigManager {

    public static ConfigManager configManager;

    public FileConfiguration config;

    public Map<String, ObjectExtraSlotsItem> extraSlotsItemMap = new HashMap<>();

    public Map<String, ObjectItemSlot> itemSlotMap = new HashMap<>();

    public ConfigManager() {
        configManager = this;
        this.config = EnchantmentSlots.instance.getConfig();
        initExtraSlotItemConfigs();
        initItemSlotSettingsConfigs();
    }

    private void initExtraSlotItemConfigs() {
        File dir = new File(EnchantmentSlots.instance.getDataFolder(), "extra_slot_items");
        if (!dir.exists()) {
            dir.mkdir();
        }
        loadExtraSlotItem(dir);
    }

    private void loadExtraSlotItem(File folder) {
        File[] files = folder.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                loadExtraSlotItem(file); // 递归调用以加载子文件夹内的文件
            } else {
                String fileName = file.getName();
                if (fileName.endsWith(".yml")) {
                    String substring = fileName.substring(0, fileName.length() - 4);
                    if (extraSlotsItemMap.containsKey(substring)) {
                        ErrorManager.errorManager.sendErrorMessage("§cError: Already loaded a extra slot item config called: " +
                                fileName + "!");
                        continue;
                    }
                    extraSlotsItemMap.put(substring, new ObjectExtraSlotsItem(substring, YamlConfiguration.loadConfiguration(file)));
                    TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fLoaded extra slot item: " + substring + "!");
                }
            }
        }
    }

    private void initItemSlotSettingsConfigs() {
        File dir = new File(EnchantmentSlots.instance.getDataFolder(), "item_slots_settings");
        if (!dir.exists()) {
            dir.mkdir();
        }
        loadItemSlotSettings(dir);
    }

    private void loadItemSlotSettings(File folder) {
        File[] files = folder.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                loadItemSlotSettings(file); // 递归调用以加载子文件夹内的文件
            } else {
                String fileName = file.getName();
                if (fileName.endsWith(".yml")) {
                    String substring = fileName.substring(0, fileName.length() - 4);
                    if (itemSlotMap.containsKey(substring)) {
                        ErrorManager.errorManager.sendErrorMessage("§cError: Already loaded a item slot config called: " +
                                fileName + "!");
                        continue;
                    }
                    itemSlotMap.put(substring, new ObjectItemSlot(substring, YamlConfiguration.loadConfiguration(file)));
                    TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fLoaded item slot config: " + substring + "!");
                }
            }
        }
    }

    public ItemStack getExtraSlotItem(String itemID, Player player) {
        ObjectExtraSlotsItem item = extraSlotsItemMap.get(itemID);
        if (item == null) {
            return null;
        }
        return item.getItem(player);
    }

    public Map<String, ObjectExtraSlotsItem> getExtraSlotsItemMap() {
        return extraSlotsItemMap;
    }

    public ObjectExtraSlotsItem getExtraSlotItemValue(ItemStack item) {
        if (!item.hasItemMeta()) {
            return null;
        }
        ItemMeta meta = item.getItemMeta();
        if (!meta.getPersistentDataContainer().has(ENCHANTMENT_SLOTS_EXTRA, PersistentDataType.STRING)) {
            return null;
        }
        String id = meta.getPersistentDataContainer().get(ENCHANTMENT_SLOTS_EXTRA, PersistentDataType.STRING);
        return extraSlotsItemMap.get(id);
    }

    public boolean getBoolean(String path, boolean defaultValue) {
        return config.getBoolean(path, defaultValue);
    }

    public String getString(String path, String... args) {
        String s = config.getString(path);
        if (s == null) {
            if (args.length == 0) {
                return null;
            }
            s = args[0];
        }
        for (int i = 1 ; i < args.length ; i += 2) {
            String var = "{" + args[i] + "}";
            if (args[i + 1] == null) {
                s = s.replace(var, "");
            }
            else {
                s = s.replace(var, args[i + 1]);
            }
        }
        return s.replace("{plugin_folder}", String.valueOf(EnchantmentSlots.instance.getDataFolder()));
    }

    public String getString(Player player, String path, String... args) {
        String tempVal1 = getString(path, args);
        if (tempVal1.equalsIgnoreCase("{lang}")) {
            String tempVal2 = LanguageManager.languageManager.getStringText(player, "override-lang." + path, args);
            if (tempVal2 != null) {
                return tempVal2;
            }
        }
        return tempVal1;
    }

    public int getInt(String path, int defaultValue) {
        return config.getInt(path, defaultValue);
    }

    public long getLong(String path, long defaultValue) {
        return config.getLong(path, defaultValue);
    }

    public DisplayPriority getEcoPriority() {
        return DisplayPriority.valueOf(EnchantmentSlots.instance.getConfig().getString(
                "settings.add-lore.packet-listener-priority", EnchantmentSlots.instance.getConfig().getString(
                        "settings.packet-listener-priority", "HIGHEST")).toUpperCase());
    }

    public int getDefaultLimits(ItemStack item, Player player) {
        int result = 0;
        for (ObjectItemSlot itemSlot : itemSlotMap.values()) {
            int tempVal1 = itemSlot.getDefaultSlot(item, player);
            if (tempVal1 > 0 && tempVal1 > result) {
                result = tempVal1;
            }
        }
        return result;
    }

    public int getMaxLimits(ItemStack item, Player player) {
        int result = 0;
        for (ObjectItemSlot itemSlot : itemSlotMap.values()) {
            int tempVal1 = itemSlot.getMaxSlot(item, player);
            if (tempVal1 > 0 && tempVal1 > result) {
                result = tempVal1;
            }
        }
        return result;
    }

    public boolean isAutoAddLore(ItemStack item, Player player, boolean playerInInventory) {
        if (!modifyOnlyInPlayerInventory(player, playerInInventory)) {
            return false;
        }
        for (ObjectItemSlot itemSlot : itemSlotMap.values()) {
            if (itemSlot.isAutoAddLore(item)) {
                return true;
            }
        }
        return false;
    }

    private boolean modifyOnlyInPlayerInventory(Player player, boolean playerInInventory) {
        if (!config.getBoolean("settings.add-lore.only-in-player-inventory",
                config.getBoolean("settings.only-in-player-inventory", true))) {
            return true;
        }
        InventoryView view = player.getOpenInventory();
        if (view.getType().equals(InventoryType.CHEST)) {
            return playerInInventory || view.getTitle().equals("Chest");
        }
        return playerInInventory || config.getBoolean("settings.add-lore.check-chests-only");
    }

    public boolean isIgnore(ItemStack item) {
        ConfigurationSection matchItemSection = getSection("settings.ignore-slot-item");
        if (matchItemSection != null) {
            return MatchItemManager.matchItemManager.getMatch(matchItemSection, item);
        }
        return false;
    }

    public List<String> getStringList(String path) {
        return config.getStringList(path);
    }

    public List<String> getStringList(Player player, String path) {
        List<String> tempVal1 = config.getStringList(path);
        if (tempVal1.contains("{lang}")) {
            List<String> tempVal2 = LanguageManager.languageManager.getStringListText(player, "override-lang." + path);
            if (tempVal2 != null) {
                return tempVal2;
            }
        }
        return tempVal1;
    }

    public ConfigurationSection getSection(String path) {
        return config.getConfigurationSection(path);
    }

    public boolean canDisplay(ItemStack item) {
        if (!getBoolean("settings.add-lore.enabled", true)) {
            return false;
        }
        ConfigurationSection matchItemSection = getSection("settings.add-lore.black-item");
        if (matchItemSection != null) {
            return !MatchItemManager.matchItemManager.getMatch(matchItemSection, item);
        }
        return true;
    }

}
