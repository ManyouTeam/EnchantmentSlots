package cn.superiormc.enchantmentslots.protolcol.packetevents;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.item.HashedStack;
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

        HashedStack hashedStack = SetCursorItem.getHashedStack(player.getUniqueId());
        if (hashedStack != null) {
            wrapper.setCarriedHashedStack(hashedStack);
        }
    }
}
