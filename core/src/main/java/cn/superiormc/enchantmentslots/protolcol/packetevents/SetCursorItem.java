package cn.superiormc.enchantmentslots.protolcol.packetevents;

import cn.superiormc.enchantmentslots.methods.AddLore;
import cn.superiormc.enchantmentslots.utils.ItemUtil;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.item.HashedStack;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetCursorItem;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SetCursorItem implements PacketListener {

    private static final Map<UUID, HashedStack> hashedStackMap = new ConcurrentHashMap<>();

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (!event.getPacketType().equals(PacketType.Play.Server.SET_CURSOR_ITEM)) {
            return;
        }
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        WrapperPlayServerSetCursorItem serverSetSlot = new WrapperPlayServerSetCursorItem(event);
        com.github.retrooper.packetevents.protocol.item.ItemStack original = serverSetSlot.getStack();
        ItemStack item = SpigotConversionUtil.toBukkitItemStack(original);
        if (!ItemUtil.isValid(item)) {
            return;
        }
        com.github.retrooper.packetevents.protocol.item.ItemStack result = SpigotConversionUtil.fromBukkitItemStack(AddLore.autoAddLore(item, player, true));
        serverSetSlot.setStack(result);
        hashedStackMap.put(player.getUniqueId(), HashedStack.fromItemStack(original));
    }

    public static HashedStack getHashedStack(UUID playerId) {
        return hashedStackMap.get(playerId);
    }

    public static void removePlayer(UUID playerId) {
        hashedStackMap.remove(playerId);
    }

    public static void clearCache() {
        hashedStackMap.clear();
    }
}
