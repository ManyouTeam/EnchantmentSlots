package cn.superiormc.enchantmentslots.managers;

import cn.gtemc.itembridge.api.ItemBridge;
import cn.gtemc.itembridge.api.util.Pair;
import cn.gtemc.itembridge.core.BukkitItemBridge;
import cn.superiormc.enchantmentslots.EnchantmentSlots;
import cn.superiormc.enchantmentslots.hooks.enchants.*;
import cn.superiormc.enchantmentslots.hooks.items.*;
import cn.superiormc.enchantmentslots.hooks.items.AbstractItemHook;
import cn.superiormc.enchantmentslots.methods.AddLore;
import cn.superiormc.enchantmentslots.methods.EnchantsUtil;
import cn.superiormc.enchantmentslots.papi.PlaceholderAPIExpansion;
import cn.superiormc.enchantmentslots.utils.CommonUtil;
import cn.superiormc.enchantmentslots.utils.TextUtil;
import com.loohp.interactivechat.api.InteractiveChatAPI;
import com.loohp.interactivechat.objectholders.ICPlayer;
import com.loohp.interactivechat.objectholders.ICPlayerFactory;
import me.arasple.mc.trchat.module.internal.hook.HookPlugin;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HookManager {

    public static HookManager hookManager;

    private Map<String, AbstractItemHook> itemHooks;

    private Map<String, AbstractEnchantHook> enchantHooks;

    private final Map<String, Map<Enchantment, String>> enchantmentNameCache = new HashMap<>();

    private ItemBridge<ItemStack, Player> itemBridgeHook = null;

    public PlaceholderAPIExpansion papi = null;

    public HookManager() {
        hookManager = this;
        initNormalHook();
        initItemHook();
        if (ConfigManager.configManager.getString("hook-item-method").equalsIgnoreCase("ITEMBRIDGE")) {
            itemBridgeHook = BukkitItemBridge.builder()
                    .onHookSuccess(p -> TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fUSItemBridge successfully hook into " + p + "."))
                    .detectSupportedPlugins()
                    .build();
        }
        initEnchantHook();
    }

    private void initNormalHook() {
        if (CommonUtil.checkPluginLoad("PlaceholderAPI")) {
            papi = new PlaceholderAPIExpansion(EnchantmentSlots.instance);
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fHooking into PlaceholderAPI...");
            if (papi.register()) {
                TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fFinished hook!");
            }
        }
        if (CommonUtil.checkPluginLoad("InteractiveChat")) {
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fHooking into InteractiveChat...");
            InteractiveChatAPI.registerItemStackTransformProvider(EnchantmentSlots.instance, 10, (itemStack, uuid) -> {
                ICPlayer icPlayer = ICPlayerFactory.getICPlayer(uuid);
                return AddLore.addLore(itemStack, icPlayer.getLocalPlayer());
            });
        }
        if (CommonUtil.checkPluginLoad("TrChat")) {
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fHooking into TrChat...");
            HookPlugin.INSTANCE.registerDisplayItemHook("EnchantmentSlots", AddLore::addLore);
        }
    }

    private void initItemHook() {
        itemHooks = new HashMap<>();
        if (CommonUtil.checkPluginLoad("ItemsAdder")) {
            registerNewItemHook("ItemsAdder", new ItemItemsAdderHook());
        }
        if (CommonUtil.checkPluginLoad("Oraxen")) {
            registerNewItemHook("Oraxen", new ItemOraxenHook());
        }
        if (CommonUtil.checkPluginLoad("MMOItems")) {
            registerNewItemHook("MMOItems", new ItemMMOItemsHook());
        }
        if (CommonUtil.checkPluginLoad("EcoItems")) {
            registerNewItemHook("EcoItems", new ItemEcoItemsHook());
        }
        if (CommonUtil.checkPluginLoad("EcoArmor")) {
            registerNewItemHook("EcoArmor", new ItemEcoArmorHook());
        }
        if (CommonUtil.checkPluginLoad("MythicMobs")) {
            registerNewItemHook("MythicMobs", new ItemMythicMobsHook());
        }
        if (CommonUtil.checkPluginLoad("eco")) {
            registerNewItemHook("eco", new ItemecoHook());
        }
        if (CommonUtil.checkPluginLoad("NeigeItems")) {
            registerNewItemHook("NeigeItems", new ItemNeigeItemsHook());
        }
        if (CommonUtil.checkPluginLoad("ExecutableItems")) {
            registerNewItemHook("ExecutableItems", new ItemExecutableItemsHook());
        }
        if (CommonUtil.checkPluginLoad("CraftEngine")) {
            registerNewItemHook("CraftEngine", new ItemCraftEngineHook());
        }
    }

    private void initEnchantHook() {
        enchantHooks = new HashMap<>();
        if (CommonUtil.checkPluginLoad("EcoEnchants")) {
            registerNewEnchantHook("EcoEnchants", new EnchantEcoEnchantsHook());
        }
        if (CommonUtil.checkPluginLoad("Aiyatsbus")) {
            registerNewEnchantHook("Aiyatsbus", new EnchantAiyatsbusHook());
        }
        if (CommonUtil.checkPluginLoad("ExcellentEnchants")) {
            registerNewEnchantHook("ExcellentEnchants", new EnchantExcellentEnchantsHook());
        }
    }

    public void registerNewItemHook(String pluginName,
                                    AbstractItemHook itemHook) {
        if (!itemHooks.containsKey(pluginName)) {
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fHooking into " + pluginName + "...");
            itemHooks.put(pluginName, itemHook);
        }
    }

    public void registerNewEnchantHook(String pluginName,
                                    AbstractEnchantHook enchantHook) {
        if (!enchantHooks.containsKey(pluginName)) {
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fHooking into " + pluginName + "...");
            enchantHooks.put(pluginName, enchantHook);
        }
    }

    public String parseItemID(ItemStack hookItem) {
        if (!hookItem.hasItemMeta()) {
            return hookItem.getType().name().toLowerCase();
        }
        if (itemBridgeHook != null) {
            Pair<String, String> tempVal1 = itemBridgeHook.getFirstId(hookItem);
            if (tempVal1 != null) {
                return tempVal1.right;
            }
        }
        for (AbstractItemHook itemHook : itemHooks.values()) {
            String tempVal1 = itemHook.getSimplyIDByItemStack(hookItem);
            if (tempVal1 != null) {
                return tempVal1;
            }
        }
        return hookItem.getType().name().toLowerCase();
    }

    // Without color code.
    public String getEnchantName(ItemStack item, Enchantment enchantment, Player player, boolean showTierColor) {
        try {
            if (!enchantmentNameCache.containsKey(player.getLocale())) {
                enchantmentNameCache.put(player.getLocale(), new HashMap<>());
            }
            Map<Enchantment, String> enchantmentStringMap = enchantmentNameCache.get(player.getLocale());
            if (enchantmentStringMap.containsKey(enchantment)) {
                return enchantmentStringMap.get(enchantment);
            }
            if (ConfigManager.configManager.getBoolean("enchant-name.force-override", false)) {
                String enchantmentName = ConfigManager.configManager.getString(player, "enchant-name." + enchantment.getKey().getKey(), enchantment.getKey().getKey());
                if (enchantmentName.equals(enchantment.getKey().getKey())) {
                    for (AbstractEnchantHook enchantHook : enchantHooks.values()) {
                        String tempVal1;
                        if (showTierColor) {
                            tempVal1 = enchantHook.getEnchantName(item, enchantment, player);
                        } else {
                            tempVal1 = enchantHook.getRawEnchantName(enchantment);
                        }
                        if (tempVal1 != null) {
                            return tempVal1;
                        }
                    }
                }
                enchantmentStringMap.put(enchantment, enchantmentName);
                return enchantmentName;
            } else {
                for (AbstractEnchantHook enchantHook : enchantHooks.values()) {
                    String tempVal1;
                    if (showTierColor) {
                        tempVal1 = enchantHook.getEnchantName(item, enchantment, player);
                    } else {
                        tempVal1 = enchantHook.getRawEnchantName(enchantment);
                    }
                    if (tempVal1 != null) {
                        return tempVal1;
                    }
                }
                return ConfigManager.configManager.getString(player, "enchant-name." + enchantment.getKey().getKey(), enchantment.getKey().getKey());
            }
        } catch (Throwable throwable) {
            return "ERROR";
        }
    }

    public List<String> getEnchantDescription(ItemStack item, Enchantment enchantment, Player player) {
        for (AbstractEnchantHook enchantHook : enchantHooks.values()) {
            try {
                if (enchantHook.hasEnchantDescription()) {
                    List<String> tempVal1 = enchantHook.getEnchantDescription(item, enchantment, player);
                    if (tempVal1 != null && !tempVal1.isEmpty()) {
                        return tempVal1;
                    }
                }
            } catch (Throwable ignored) {
                ignored.printStackTrace();
            }
        }
        return Collections.emptyList();
    }

    public Map<Enchantment, Integer> sortEnchantments(ItemMeta meta) {
        if (enchantHooks.values().isEmpty()) {
            return EnchantsUtil.getEnchantments(meta, false);
        }
        Map<Enchantment, Integer> enchantments = new HashMap<>();
        for (AbstractEnchantHook enchantHook : enchantHooks.values()) {
            enchantments = enchantHook.sortEnchants(meta);
        }
        return enchantments;
    }
}
