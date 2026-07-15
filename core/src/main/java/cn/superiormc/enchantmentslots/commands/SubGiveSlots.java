package cn.superiormc.enchantmentslots.commands;

import cn.superiormc.enchantmentslots.managers.ConfigManager;
import cn.superiormc.enchantmentslots.managers.LanguageManager;
import cn.superiormc.enchantmentslots.utils.CommonUtil;
import cn.superiormc.enchantmentslots.methods.SlotUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SubGiveSlots extends AbstractCommand {

    public SubGiveSlots() {
        this.id = "giveslots";
        this.requiredPermission =  "enchantmentslots." + id;
        this.onlyInGame = false;
        this.requiredArgLength = new Integer[]{1, 2, 3, 4};
        this.requiredConsoleArgLength = new Integer[]{3, 4};
        this.premiumOnly = true;
    }

    @Override
    public void executeCommandInGame(String[] args, Player player) {
        if (args.length <= 2) {
            ItemStack targetItem = player.getInventory().getItemInMainHand();
            if (targetItem.getType().isAir()) {
                LanguageManager.languageManager.sendStringText(player, "error-no-item");
                return;
            }
            if (args.length == 1) {
                int newBaseSlots = SlotUtil.getBaseSlot(targetItem) + 1;
                SlotUtil.setSlot(targetItem, newBaseSlots, true);
                LanguageManager.languageManager.sendStringText(player, "success-set", "amount", String.valueOf(newBaseSlots));
                return;
            }
            int newBaseSlots = SlotUtil.getBaseSlot(targetItem) + Integer.parseInt(args[1]);
            SlotUtil.setSlot(targetItem, newBaseSlots, true);
            LanguageManager.languageManager.sendStringText(player, "success-set", "amount", String.valueOf(newBaseSlots));
            return;
        }
        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) {
            LanguageManager.languageManager.sendStringText(player, "error-player-not-found", "player", args[2]);
            return;
        }
        ItemStack item = ConfigManager.configManager.getExtraSlotItem(args[1], target);
        if (item == null) {
            LanguageManager.languageManager.sendStringText(player, "error-item-not-found");
            return;
        }
        int amount = 1;
        if (args.length == 4) {
            amount = Integer.parseInt(args[3]);
        }
        item.setAmount(amount);
        CommonUtil.giveOrDrop(target, item);
        LanguageManager.languageManager.sendStringText(player, "give-extra-slot-item",
                "player", target.getName(),
                "item", args[1],
                "amount", String.valueOf(amount));
    }

    @Override
    public void executeCommandInConsole(String[] args) {
        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) {
            LanguageManager.languageManager.sendStringText("error-player-not-found", "player", args[2]);
            return;
        }
        ItemStack item = ConfigManager.configManager.getExtraSlotItem(args[1], target);
        if (item == null) {
            LanguageManager.languageManager.sendStringText("error-item-not-found");
            return;
        }
        int amount = 1;
        if (args.length == 4) {
            amount = Integer.parseInt(args[3]);
        }
        item.setAmount(amount);
        CommonUtil.giveOrDrop(target, item);
        LanguageManager.languageManager.sendStringText("give-extra-slot-item",
                "player", target.getName(),
                "item", args[1],
                "amount", String.valueOf(amount));
    }

    @Override
    public List<String> getTabResult(String[] args) {
        List<String> tempVal1 = new ArrayList<>();
        switch (args.length) {
            case 2:
                tempVal1.add("1");
                tempVal1.add("5");
                tempVal1.add("10");
                tempVal1.addAll(ConfigManager.configManager.getExtraSlotsItemMap().keySet());
                break;
            case 3:
                for (Player player : Bukkit.getOnlinePlayers()) {
                    tempVal1.add(player.getName());
                }
                break;
            case 4:
                tempVal1.add("1");
                tempVal1.add("5");
                tempVal1.add("10");
                break;
        }
        return tempVal1;
    }
}
