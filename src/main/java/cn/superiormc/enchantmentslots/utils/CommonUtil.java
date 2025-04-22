package cn.superiormc.enchantmentslots.utils;

import cn.superiormc.enchantmentslots.EnchantmentSlots;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;

public class CommonUtil {

    public static boolean checkPluginLoad(String pluginName) {
        if (pluginName.equals("EcoEnchants") && EnchantmentSlots.instance.getServer().getPluginManager().isPluginEnabled(pluginName)) {
            return Integer.parseInt(EnchantmentSlots.instance.getServer().getPluginManager().getPlugin("EcoEnchants").getDescription().
                    getVersion().split("\\.")[0]) > 10;
        }
        return EnchantmentSlots.instance.getServer().getPluginManager().isPluginEnabled(pluginName);
    }

    public static void giveOrDrop(Player player, ItemStack... item) {
        HashMap<Integer, ItemStack> result = player.getInventory().addItem(item);
        if (!result.isEmpty()) {
            for (int id : result.keySet()) {
                player.getWorld().dropItem(player.getLocation(), result.get(id));
            }
        }
    }

    public static boolean getMajorVersion(int version) {
        return EnchantmentSlots.majorVersion >= version;
    }

    public static boolean getMinorVersion(int majorVersion, int minorVersion) {
        return EnchantmentSlots.majorVersion > majorVersion || (EnchantmentSlots.majorVersion == majorVersion &&
                EnchantmentSlots.minorVersion >= minorVersion);
    }

    public static boolean inPlayerInventory(Player player, int slot, int windowID) {
        int topSize = player.getOpenInventory().getTopInventory().getSize();
        if (windowID == 0 || (player.getOpenInventory().getTopInventory() instanceof CraftingInventory &&
                player.getOpenInventory().getTopInventory().getSize() == 5)) {
            return slot >= 5 && slot <= 44;
        }
        return slot >= topSize;
    }

    public static ItemStack getItemFromSlot(Player player, int slot) {
        if (slot == 36) {
            return player.getInventory().getItem(EquipmentSlot.HAND);
        } else if (slot == 37) {
            return player.getInventory().getItem(EquipmentSlot.CHEST);
        } else if (slot == 38) {
            return player.getInventory().getItem(EquipmentSlot.LEGS);
        } else if (slot == 39) {
            return player.getInventory().getItem(EquipmentSlot.FEET);
        } else if (slot == 40) {
            return player.getInventory().getItem(EquipmentSlot.OFF_HAND);
        }
        return player.getInventory().getItem(slot);
    }

    public static void dispatchCommand(String command) {
        if (EnchantmentSlots.isFolia) {
            Bukkit.getGlobalRegionScheduler().run(EnchantmentSlots.instance, task -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
            return;
        }
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    public static void dispatchCommand(Player player, String command) {
        if (EnchantmentSlots.isFolia) {
            player.getScheduler().run(EnchantmentSlots.instance, task -> Bukkit.dispatchCommand(player, command), () -> {
            });
            return;
        }
        Bukkit.dispatchCommand(player, command);
    }

    public static void dispatchOpCommand(Player player, String command) {
        if (EnchantmentSlots.isFolia) {
            player.getScheduler().run(EnchantmentSlots.instance, task -> {
                boolean playerIsOp = player.isOp();
                try {
                    player.setOp(true);
                    Bukkit.dispatchCommand(player, command);
                } finally {
                    player.setOp(playerIsOp);
                }
            }, () -> {
            });
            return;
        }
        boolean playerIsOp = player.isOp();
        try {
            player.setOp(true);
            Bukkit.dispatchCommand(player, command);
        } finally {
            player.setOp(playerIsOp);
        }
    }

    public static String modifyString(String text, String... args) {
        for (int i = 0 ; i < args.length ; i += 2) {
            String var1 = "{" + args[i] + "}";
            String var2 = "[" + args[i] + "]";
            if (args[i + 1] == null) {
                text = text.replace(var1, "").replace(var2, "");
            }
            else {
                text = text.replace(var1, args[i + 1]).replace(var2, args[i + 1]);
            }
        }
        return text;
    }

    public static boolean getClass(String className) {
        try {
            Class.forName(className);
            return true;
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean checkClass(String className, String methodName) {
        try {
            Class<?> targetClass = Class.forName(className);
            Method[] methods = targetClass.getDeclaredMethods();

            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    return true;
                }
            }

            return false;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static NamespacedKey parseNamespacedKey(String key) {
        String[] keySplit = key.split(":");
        if (keySplit.length == 1) {
            return NamespacedKey.minecraft(key.toLowerCase());
        }
        return NamespacedKey.fromString(key);
    }

    public static void mkDir(File dir) {
        if (!dir.exists()) {
            File parentFile = dir.getParentFile();
            if (parentFile == null) {
                return;
            }
            String parentPath = parentFile.getPath();
            mkDir(new File(parentPath));
            dir.mkdir();
        }
    }

    public static int convertNMSSlotToBukkitSlot(int slot, int windowID, Player player) {
        if (windowID == 0) {
            if (slot < 5 || slot > 44) {
                return -1;
            }
            int spigotSlot;
            if (slot >= 36) {
                spigotSlot = slot - 36;
            } else if (slot <= 8) {
                spigotSlot = slot + 31;
            } else {
                spigotSlot = slot;
            }
            return spigotSlot;
        } else {
            int topSize = player.getOpenInventory().getTopInventory().getSize();
            if (topSize == 5 && CommonUtil.inPlayerInventory(player, slot, windowID)) {
                topSize = 9;
            }
            if (slot < topSize || slot > topSize + 36) {
                return -1;
            }
            int spigotSlot;
            // 如果是最后9个格子
            if (slot >= 27 + topSize) {
                spigotSlot = slot - 27 - topSize;
                // 如果是中间三排
            } else {
                spigotSlot = slot - topSize + 9;
            }
            return spigotSlot;
        }
    }
}
