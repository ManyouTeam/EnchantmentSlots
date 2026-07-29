package cn.superiormc.enchantmentslots.commands;

import cn.superiormc.enchantmentslots.managers.LanguageManager;
import cn.superiormc.enchantmentslots.methods.EnchantDescriptionDisplay;
import org.bukkit.entity.Player;

public class SubToggleDescription extends AbstractCommand {

    public SubToggleDescription() {
        this.id = "toggledescription";
        this.onlyInGame = true;
        this.requiredArgLength = new Integer[]{1};
        this.requiredPermission =  "enchantmentslots." + id;
    }

    @Override
    public void executeCommandInGame(String[] args, Player player) {
        boolean display = EnchantDescriptionDisplay.toggle(player);
        LanguageManager.languageManager.sendStringText(player,
                display ? "description-display-enabled" : "description-display-disabled");
        player.updateInventory();
    }
}
