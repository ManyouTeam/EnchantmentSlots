package cn.superiormc.enchantmentslots.managers;

import cn.superiormc.enchantmentslots.commands.*;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CommandManager {

    public static CommandManager commandManager;

    private final Map<String, AbstractCommand> registeredCommands = new HashMap<>();

    public CommandManager(){
        commandManager = this;
        registerBukkitCommands();
        reloadSubCommands();
    }

    private void registerBukkitCommands(){
        Objects.requireNonNull(Bukkit.getPluginCommand("enchantmentslots")).setExecutor(new MainCommand());
        Objects.requireNonNull(Bukkit.getPluginCommand("enchantmentslots")).setTabCompleter(new MainCommandTab());
    }

    private void registerObjectCommand() {
        registerNewSubCommand(new SubHelp());
        registerNewSubCommand(new SubGiveSlots());
        registerNewSubCommand(new SubGiveDeenchanterItem());
        registerNewSubCommand(new SubGiveRemoveSlotItem());
        registerNewSubCommand(new SubSetSlots());
        if (ConfigManager.configManager.getBoolean(
                "settings.add-lore.placeholder.enchants.description.enabled", false)
                && ConfigManager.configManager.getBoolean(
                "settings.add-lore.placeholder.enchants.description.command-toggle", false)) {
            registerNewSubCommand(new SubToggleDescription());
        }
        registerNewSubCommand(new SubReload());
        if (ConfigManager.configManager.getBoolean("enchant-gui.enabled", true)) {
            registerNewSubCommand(new SubOpenEnchantGUI());
        }
    }

    public void reloadSubCommands() {
        registeredCommands.clear();
        registerObjectCommand();
    }

    public Map<String, AbstractCommand> getSubCommandsMap() {
        return registeredCommands;
    }

    public void registerNewSubCommand(AbstractCommand command) {
        registeredCommands.put(command.getId(), command);
    }

}
