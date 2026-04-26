package cn.superiormc.enchantmentslots.protolcol.packetevents;

import cn.superiormc.enchantmentslots.listeners.PlayerCacheListener;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import org.bukkit.entity.Player;

public class ContainerClick implements PacketListener {

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!event.getPacketType().equals(PacketType.Play.Client.CLICK_WINDOW)) {
            return;
        }

        WrapperPlayClientClickWindow wrapper = new WrapperPlayClientClickWindow(event);
        Player player = event.getPlayer();

        if (PlayerCacheListener.hashedStackMap.containsKey(player)) {
            wrapper.setCarriedHashedStack(PlayerCacheListener.hashedStackMap.get(player));
        }
    }
}
