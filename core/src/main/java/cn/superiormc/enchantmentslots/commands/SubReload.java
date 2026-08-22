package cn.superiormc.enchantmentslots.commands;

import cn.superiormc.enchantmentslots.EnchantmentSlots;
import cn.superiormc.enchantmentslots.managers.CommandManager;
import cn.superiormc.enchantmentslots.managers.ConfigManager;
import cn.superiormc.enchantmentslots.managers.LanguageManager;
import org.bukkit.entity.Player;

public class SubReload extends AbstractCommand {

    public SubReload() {
        this.id = "reload";
        this.requiredPermission =  "enchantmentslots.admin";
        this.onlyInGame = false;
        this.requiredArgLength = new Integer[]{1};
    }

    @Override
    public void executeCommandInGame(String[] args, Player player) {
        EnchantmentSlots.instance.reloadConfig();
        new LanguageManager();
        new ConfigManager();
        CommandManager.commandManager.reloadSubCommands();
        LanguageManager.languageManager.sendStringText(player, "plugin-reloaded");
    }

    @Override
    public void executeCommandInConsole(String[] args) {
        EnchantmentSlots.instance.reloadConfig();
        new LanguageManager();
        new ConfigManager();
        CommandManager.commandManager.reloadSubCommands();
        LanguageManager.languageManager.sendStringText("plugin-reloaded");
    }

}
