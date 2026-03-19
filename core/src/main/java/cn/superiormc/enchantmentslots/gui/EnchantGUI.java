package cn.superiormc.enchantmentslots.gui;

import cn.superiormc.enchantmentslots.EnchantmentSlots;
import cn.superiormc.enchantmentslots.managers.ConfigManager;
import cn.superiormc.enchantmentslots.managers.LanguageManager;
import cn.superiormc.enchantmentslots.methods.SlotUtil;
import cn.superiormc.enchantmentslots.utils.CommonUtil;
import cn.superiormc.enchantmentslots.utils.ItemUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.Map;
import java.util.Objects;


public class EnchantGUI extends InvGUI {

    private boolean enchanting = false;

    public EnchantGUI(Player player) {
        super(player);
        constructGUI();
    }

    @Override
    protected void constructGUI() {
        if (Objects.isNull(inv)) {
            inv = EnchantmentSlots.methodUtil.createNewInv(player,
                    ConfigManager.configManager.getInt("enchant-gui.size", 27),
                    ConfigManager.configManager.getString(player, "enchant-gui.title", "Enchant UI"));
        }
        inv.setItem(ConfigManager.configManager.getInt("enchant-gui.confirm-slot", 22), ItemUtil.buildItemStack(player,
                ConfigManager.configManager.config.getConfigurationSection("enchant-gui.confirm-item")));
        ConfigurationSection customItemSection = ConfigManager.configManager.config.getConfigurationSection("enchant-gui.custom-item");
        if (customItemSection != null) {
            for (String key : customItemSection.getKeys(false)){
                inv.setItem(Integer.parseInt(key), ItemUtil.buildItemStack(player, customItemSection.getConfigurationSection(key)));
            }
        }
    }

    @Override
    public boolean clickEventHandle(Inventory inventory, ItemStack item, int slot) {
        if (slot == ConfigManager.configManager.getInt("enchant-gui.item-slot", 11)) {
            ItemStack requireItem = inv.getItem(ConfigManager.configManager.getInt("enchant-gui.item-slot", 11));
            if (requireItem != null && !requireItem.getType().isAir()) {
                return false;
            }
            if (item == null || item.getType().isAir()) {
                return true;
            }
            return ConfigManager.configManager.getDefaultLimits(item, player) > 0;
        } else if (slot == ConfigManager.configManager.getInt("enchant-gui.book-slot", 15)) {
            ItemStack enchantBookItem = inv.getItem(ConfigManager.configManager.getInt("enchant-gui.book-slot", 15));
            if (enchantBookItem != null && !enchantBookItem.getType().isAir()) {
                return false;
            }
            if (item == null || item.getType().isAir()) {
                return true;
            }
            return !(item.hasItemMeta() && item.getItemMeta() instanceof EnchantmentStorageMeta);
        }
        return true;
    }

    @Override
    public void afterClickEventHandle(ItemStack item, ItemStack currentItem, int slot) {
        if (slot != ConfigManager.configManager.getInt("enchant-gui.confirm-slot", 22)) {
            return;
        }
        enchanting = true;
        ItemStack requireItem = inv.getItem(ConfigManager.configManager.getInt("enchant-gui.item-slot", 11));
        if (requireItem == null || requireItem.getType().isAir()) {
            enchanting = false;
            return;
        }
        ItemStack enchantBookItem = inv.getItem(ConfigManager.configManager.getInt("enchant-gui.book-slot", 15));
        if (enchantBookItem == null || enchantBookItem.getType().isAir() || !(enchantBookItem.getItemMeta() instanceof EnchantmentStorageMeta)) {
            enchanting = false;
            return;
        }
        ItemStack newItem = requireItem.clone();
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) enchantBookItem.getItemMeta();
        Map<Enchantment, Integer> enchants = meta.getStoredEnchants();
        int maxEnchantments = SlotUtil.getSlot(requireItem);
        if (enchants.size() + requireItem.getEnchantments().size() > maxEnchantments) {
            enchanting = false;
            LanguageManager.languageManager.sendStringText(player, "slots-limit-reached");
            player.closeInventory();
            return;
        }
        int i = 0;
        int b = 0;
        for (Enchantment ench : enchants.keySet()) {
            if (requireItem.getEnchantments().containsKey(ench)) {
                b ++;
                int c = requireItem.getEnchantments().get(ench);
                if (enchants.get(ench) > c) {
                    if (canEnchant(requireItem, ench)) {
                        newItem.addUnsafeEnchantment(ench, enchants.get(ench));
                        i ++;
                    }
                } else {
                    continue;
                }
            }
            if (canEnchant(requireItem, ench)) {
                newItem.addUnsafeEnchantment(ench, enchants.get(ench));
                i ++;
            }
        }
        if (i > 0) {
            requireItem.setAmount(0);
            enchantBookItem.setAmount(0);
            CommonUtil.giveOrDrop(player, newItem);
        } else if (b > 0) {
            enchanting = false;
            LanguageManager.languageManager.sendStringText(player, "same-enchants");
            player.closeInventory();
            return;
        }
        enchanting = false;
    }

    @Override
    public void closeEventHandle(Inventory inventory) {
        if (!enchanting) {
            ItemStack requireItem = inv.getItem(ConfigManager.configManager.getInt("enchant-gui.item-slot", 11));
            if (requireItem != null && !requireItem.getType().isAir()) {
                CommonUtil.giveOrDrop(player, requireItem);
            }
            ItemStack enchantBookItem = inv.getItem(ConfigManager.configManager.getInt("enchant-gui.book-slot", 15));
            if (enchantBookItem != null && !enchantBookItem.getType().isAir()) {
                CommonUtil.giveOrDrop(player, enchantBookItem);
            }
        }
    }

    public static boolean canEnchant(ItemStack item, Enchantment enchantment) {
        if (ConfigManager.configManager.getStringList("enchant-ui.override-allow." + enchantment.getKey().getKey().toLowerCase()).
                contains(item.getType().name().toLowerCase())) {
            return true;
        }
        return enchantment.canEnchantItem(item);
    }
}
