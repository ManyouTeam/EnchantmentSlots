package cn.superiormc.enchantmentslots.commands;

import cn.superiormc.enchantmentslots.managers.ConfigManager;
import cn.superiormc.enchantmentslots.managers.HookManager;
import cn.superiormc.enchantmentslots.managers.LanguageManager;
import cn.superiormc.enchantmentslots.methods.ItemLimits;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SubSetSlots extends AbstractCommand {

    public SubSetSlots() {
        this.id = "setslots";
        this.requiredPermission =  "enchantmentslots." + id;
        this.onlyInGame = true;
        this.requiredArgLength = new Integer[]{1, 2};
        this.premiumOnly = true;
    }

    @Override
    public void executeCommandInGame(String[] args, Player player) {
        ItemStack target = player.getInventory().getItemInMainHand();
        if (target.getType().isAir()) {
            LanguageManager.languageManager.sendStringText("error-item-not-found");
            return;
        }
        if (args.length == 1) {
            String itemID = HookManager.hookManager.parseItemID(target);
            int slot = ItemLimits.getMaxEnchantments(target, ConfigManager.configManager.getDefaultLimits(player, itemID), itemID);
            ItemLimits.setMaxEnchantments(target, slot + 1);
            LanguageManager.languageManager.sendStringText(player, "success-set", "amount", String.valueOf(slot + 1));
            return;
        }
        ItemLimits.setMaxEnchantments(target, Integer.parseInt(args[1]));
        LanguageManager.languageManager.sendStringText(player, "success-set", "amount", args[1]);
    }

    @Override
    public List<String> getTabResult(String[] args) {
        List<String> tempVal1 = new ArrayList<>();
        switch (args.length) {
            case 2:
                tempVal1.add("1");
                tempVal1.add("5");
                tempVal1.add("10");
                break;
        }
        return tempVal1;
    }
}
