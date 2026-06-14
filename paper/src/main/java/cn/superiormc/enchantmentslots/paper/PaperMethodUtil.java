package cn.superiormc.enchantmentslots.paper;

import cn.superiormc.enchantmentslots.EnchantmentSlots;
import cn.superiormc.enchantmentslots.managers.ConfigManager;
import cn.superiormc.enchantmentslots.paper.utils.PaperTextUtil;
import cn.superiormc.enchantmentslots.utils.SchedulerUtil;
import cn.superiormc.enchantmentslots.utils.SpecialMethodUtil;
import cn.superiormc.enchantmentslots.utils.TextUtil;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class PaperMethodUtil implements SpecialMethodUtil {

    @Override
    public String methodID() {
        return "paper";
    }

    @Override
    public void dispatchCommand(String command) {
        if (EnchantmentSlots.isFolia) {
            Bukkit.getGlobalRegionScheduler().run(EnchantmentSlots.instance, task -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
            return;
        }
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    @Override
    public void dispatchCommand(Player player, String command) {
        if (EnchantmentSlots.isFolia) {
            player.getScheduler().run(EnchantmentSlots.instance, task -> Bukkit.dispatchCommand(player, command), () -> {
            });
            return;
        }
        Bukkit.dispatchCommand(player, command);
    }

    @Override
    public void dispatchOpCommand(Player player, String command) {
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

    @Override
    public void spawnEntity(Location location, EntityType entity) {
        if (EnchantmentSlots.isFolia) {
            Bukkit.getRegionScheduler().run(EnchantmentSlots.instance, location, task -> location.getWorld().spawnEntity(location, entity));
            return;
        }
        location.getWorld().spawnEntity(location, entity);
    }

    @Override
    public void playerTeleport(Player player, Location location) {
        if (EnchantmentSlots.isFolia) {
            player.teleportAsync(location);
        } else {
            player.teleport(location);
        }
    }

    public Map<String, PlayerProfile> playerProfiles = Collections.synchronizedMap(
            new LinkedHashMap<>(256, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, PlayerProfile> eldest) {
                    return size() > 256;
                }
            });

    @Override
    public SkullMeta setSkullMeta(SkullMeta meta, String skull) {
        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID(), "");
        if (skull.length() > 16) {
            profile.setProperty(new ProfileProperty("textures", skull));
            meta.setPlayerProfile(profile);
        } else {
            if (!playerProfiles.containsKey(skull)) {
                playerProfiles.put(skull, Bukkit.getOfflinePlayer(skull).getPlayerProfile());
            }
            meta.setPlayerProfile(playerProfiles.get(skull));
        }
        return meta;
    }

    @Override
    public void setItemName(ItemMeta meta, String name, Player player) {
        name = TextUtil.withPAPI(name, player);
        if (PaperTextUtil.containsLegacyCodes(name)) {
            name = "<!i>" + name;
        }
        meta.displayName(PaperTextUtil.modernParse(name));
    }

    @Override
    public void setItemLore(ItemMeta meta, List<String> lores, Player player) {
        List<Component> veryNewLore = new ArrayList<>();
        for (String lore : lores) {
            for (String singleLore : lore.split("\\\\n")) {
                singleLore = TextUtil.withPAPI(singleLore, player);
                if (PaperTextUtil.containsLegacyCodes(singleLore)) {
                    singleLore = "<!i>" + singleLore;
                }
                veryNewLore.add(PaperTextUtil.modernParse(singleLore));
            }
        }
        if (!veryNewLore.isEmpty()) {
            meta.lore(veryNewLore);
        }
    }

    @Override
    public void sendChat(Player player, String text) {
        if (player == null) {
            Bukkit.getConsoleSender().sendMessage(PaperTextUtil.modernParse(text));
        } else {
            player.sendMessage(PaperTextUtil.modernParse(text, player));
        }
    }

    @Override
    public void sendTitle(Player player, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        if (player == null) {
            return;
        }
        player.showTitle(Title.title(PaperTextUtil.modernParse(title, player),
                PaperTextUtil.modernParse(subTitle, player),
                Title.Times.times(Ticks.duration(fadeIn),
                        Ticks.duration(stay),
                        Ticks.duration(fadeOut))));
    }

    @Override
    public void sendActionBar(Player player, String message) {
        if (player == null) {
            return;
        }
        player.sendActionBar(PaperTextUtil.modernParse(message, player));
    }

    @Override
    public void sendBossBar(Player player,
                            String title,
                            float progress,
                            String color,
                            String style) {
        if (player == null) {
            return;
        }

        if (style != null && style.equalsIgnoreCase("SOLID")) {
            style = "PROGRESS";
        }

        BossBar bar = BossBar.bossBar(
                title == null ? Component.empty() : PaperTextUtil.modernParse(title, player),
                Math.max(0f, Math.min(1f, progress)),
                color == null ? BossBar.Color.PINK : BossBar.Color.valueOf(color.toUpperCase()),
                style == null ? BossBar.Overlay.PROGRESS : BossBar.Overlay.valueOf(style.toUpperCase())
        );

        player.showBossBar(bar);
        SchedulerUtil.runTaskLater(() -> player.hideBossBar(bar), 60);
    }

    @Override
    public String legacyParse(String text) {
        if (text == null) {
            return "";
        }
        if (!ConfigManager.configManager.getBoolean("config-files.force-parse-mini-message", true)) {
            return TextUtil.colorize(text);
        }
        return LegacyComponentSerializer.legacySection().serialize(PaperTextUtil.modernParse(text));
    }

    @Override
    public String getItemName(ItemMeta meta) {
        return PaperTextUtil.changeToString(meta.displayName());
    }

    @Override
    public String getItemItemName(ItemMeta meta) {
        return PaperTextUtil.changeToString(meta.itemName());
    }

    @Override
    public List<String> getItemLore(ItemMeta meta) {
        return PaperTextUtil.changeToString(meta.lore());
    }

    @Override
    public Inventory createNewInv(Player player, int size, String text) {
        return Bukkit.createInventory(player, size, PaperTextUtil.modernParse(text, player));
    }
}
