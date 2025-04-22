package cn.superiormc.enchantmentslots.protolcol.ProtocolLib;

import cn.superiormc.enchantmentslots.methods.AddLore;
import cn.superiormc.enchantmentslots.utils.ItemUtil;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCreativeInventoryAction;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SetCreativeSlots implements PacketListener {

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        WrapperPlayClientCreativeInventoryAction creative = new WrapperPlayClientCreativeInventoryAction(event);
        ItemStack item = SpigotConversionUtil.toBukkitItemStack(creative.getItemStack());
        if (!ItemUtil.isValid(item)) {
            return;
        }
        creative.setItemStack(SpigotConversionUtil.fromBukkitItemStack(AddLore.removeLore(item)));
    }
}
