package cn.superiormc.enchantmentslots.utils;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public interface SpecialMethodUtil {

    String methodID();

    void dispatchCommand(String command);

    void dispatchCommand(Player player, String command);

    void dispatchOpCommand(Player player, String command);

    void spawnEntity(Location location, EntityType entity);

    void playerTeleport(Player player, Location location);

    SkullMeta setSkullMeta(SkullMeta meta, String skull);

    void setItemName(ItemMeta meta, String name);

    void setItemItemName(ItemMeta meta, String itemName);

    void setItemLore(ItemMeta meta, List<String> lore);

    void sendMessage(Player player, String text);

    String legacyParse(String text);

    String getItemName(ItemMeta meta);

    String getItemItemName(ItemMeta meta);

    List<String> getItemLore(ItemMeta meta);
}
