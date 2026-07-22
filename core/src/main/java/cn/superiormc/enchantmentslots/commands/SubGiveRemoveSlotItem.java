package cn.superiormc.enchantmentslots.commands;

import cn.superiormc.enchantmentslots.managers.ConfigManager;
import cn.superiormc.enchantmentslots.managers.LanguageManager;
import cn.superiormc.enchantmentslots.utils.CommonUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SubGiveRemoveSlotItem extends AbstractCommand {

    public SubGiveRemoveSlotItem() {
        this.id = "giveremoveslotitem";
        this.requiredPermission = "enchantmentslots." + id;
        this.onlyInGame = false;
        this.requiredArgLength = new Integer[]{2, 3, 4};
        this.requiredConsoleArgLength = new Integer[]{3, 4};
        this.premiumOnly = true;
    }

    @Override
    public void executeCommandInGame(String[] args, Player player) {
        Player target = args.length > 2 ? Bukkit.getPlayer(args[2]) : player;
        give(args, player, target);
    }

    @Override
    public void executeCommandInConsole(String[] args) {
        give(args, null, Bukkit.getPlayer(args[2]));
    }

    private void give(String[] args, Player sender, Player target) {
        if (target == null) {
            if (sender == null) {
                LanguageManager.languageManager.sendStringText("error-player-not-found", "player", args.length > 2 ? args[2] : "");
            } else {
                LanguageManager.languageManager.sendStringText(sender, "error-player-not-found", "player", args.length > 2 ? args[2] : "");
            }
            return;
        }
        int amount = args.length == 4 ? Integer.parseInt(args[3]) : 1;
        ItemStack item = ConfigManager.configManager.getRemoveSlotItem(args[1], target);
        if (item == null) {
            if (sender == null) {
                LanguageManager.languageManager.sendStringText("error-item-not-found");
            } else {
                LanguageManager.languageManager.sendStringText(sender, "error-item-not-found");
            }
            return;
        }
        item.setAmount(amount);
        CommonUtil.giveOrDrop(target, item);
        if (sender == null) {
            LanguageManager.languageManager.sendStringText("give-extra-slot-item", "player", target.getName(), "item", args[1], "amount", String.valueOf(amount));
        } else {
            LanguageManager.languageManager.sendStringText(sender, "give-extra-slot-item", "player", target.getName(), "item", args[1], "amount", String.valueOf(amount));
        }
    }

    @Override
    public List<String> getTabResult(String[] args) {
        List<String> result = new ArrayList<>();
        switch (args.length) {
            case 2:
                result.addAll(ConfigManager.configManager.removeSlotItemMap.keySet());
                break;
            case 3:
                for (Player player : Bukkit.getOnlinePlayers()) {
                    result.add(player.getName());
                }
                break;
            case 4:
                result.add("1");
                result.add("5");
                result.add("10");
                break;
        }
        return result;
    }
}
