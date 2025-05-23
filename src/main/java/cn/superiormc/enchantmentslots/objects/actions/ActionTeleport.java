package cn.superiormc.enchantmentslots.objects.actions;

import cn.superiormc.enchantmentslots.EnchantmentSlots;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ActionTeleport extends AbstractRunAction {

    public ActionTeleport() {
        super("teleport");
        setRequiredArgs("world", "x", "y", "z");
    }

    @Override
    protected void onDoAction(ObjectSingleAction singleAction, Player player, int amount) {
        Location loc = new Location(Bukkit.getWorld(singleAction.getString("world")),
                    singleAction.getDouble("x"),
                    singleAction.getDouble("y"),
                    singleAction.getDouble("z"),
                    singleAction.getInt("yaw", (int) player.getLocation().getYaw()),
                    singleAction.getInt("pitch", (int) player.getLocation().getPitch()));
        if (EnchantmentSlots.isFolia) {
            player.teleportAsync(loc);
        } else {
            player.teleport(loc);
        }
        player.teleport(loc);
    }
}
