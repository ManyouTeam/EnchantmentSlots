package cn.superiormc.enchantmentslots.gui;

import cn.superiormc.enchantmentslots.EnchantmentSlots;
import cn.superiormc.enchantmentslots.managers.ConfigManager;
import cn.superiormc.enchantmentslots.managers.HookManager;
import cn.superiormc.enchantmentslots.managers.LanguageManager;
import cn.superiormc.enchantmentslots.utils.CommonUtil;
import cn.superiormc.enchantmentslots.utils.ItemUtil;
import cn.superiormc.enchantmentslots.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class DeenchantGUI extends InvGUI {

    private ItemStack targetItem;

    private ItemStack extraItem;

    private Map<Integer, Enchantment> enchCache = new HashMap<>();

    public DeenchantGUI(Player player, ItemStack targetItem, ItemStack extraItem) {
        super(player);
        if (targetItem == null || targetItem.getType().isAir()) {
            return;
        }
        if (extraItem == null || extraItem.getType().isAir()) {
            return;
        }
        this.targetItem = targetItem;
        this.extraItem = extraItem;
        constructGUI();
    }

    @Override
    protected void constructGUI() {
        if (Objects.isNull(inv)) {
            inv = EnchantmentSlots.methodUtil.createNewInv(player, ConfigManager.configManager.getInt("deenchant-gui.size", 54),
                    ConfigManager.configManager.getString(player, "deenchant-gui.title", "Deenchant GUI"));
        }
        int i = 0;
        for (Enchantment ench : targetItem.getEnchantments().keySet()) {
            ItemStack tempItem = ItemUtil.generateEnchantedBook(ench, targetItem.getEnchantments().get(ench));
            ItemMeta itemMeta = tempItem.getItemMeta();
            List<String> lore = EnchantmentSlots.methodUtil.getItemLore(itemMeta);
            if (lore == null) {
                lore = new ArrayList<>();
            }
            lore.addAll(ConfigManager.configManager.getStringList(player, "deenchant-gui.ench-item"));
            EnchantmentSlots.methodUtil.setItemLore(itemMeta, lore, player);
            tempItem.setItemMeta(itemMeta);
            inv.setItem(i, tempItem);
            enchCache.put(i, ench);
            i ++;
            if (i >= ConfigManager.configManager.getInt("deenchant-gui.size", 54)) {
                break;
            }
        }
    }

    @Override
    public boolean clickEventHandle(Inventory inventory, ItemStack item, int slot) {
        Enchantment ench = enchCache.get(slot);
        if (ench != null && extraItem.getAmount() > 0) {
            ItemMeta itemMeta = targetItem.getItemMeta();
            int level = itemMeta.getEnchants().get(ench);
            itemMeta.removeEnchant(ench);
            targetItem.setItemMeta(itemMeta);
            extraItem.setAmount(extraItem.getAmount() - 1);
            player.getInventory().addItem(ItemUtil.generateEnchantedBook(ench, level));
            player.closeInventory();
            LanguageManager.languageManager.sendStringText(player, "deenchant-success", "enchant",
                    HookManager.hookManager.getEnchantName(item, ench, player, true));
            return true;
        }
        return true;
    }
}
