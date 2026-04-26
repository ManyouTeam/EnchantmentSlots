package cn.superiormc.enchantmentslots.protolcol.packetevents;

import cn.superiormc.enchantmentslots.methods.AddLore;
import cn.superiormc.enchantmentslots.methods.SlotUtil;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.recipe.data.MerchantOffer;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerMerchantOffers;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class WindowMerchant implements PacketListener {

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType().equals(PacketType.Play.Server.MERCHANT_OFFERS)) {
            Player player = event.getPlayer();
            if (player == null) {
                return;
            }
            WrapperPlayServerMerchantOffers merchantOffers = new WrapperPlayServerMerchantOffers(event);
            List<MerchantOffer> merchantOfferList = merchantOffers.getMerchantOffers();
            for (MerchantOffer recipe : merchantOfferList) {
                ItemStack item1 = SpigotConversionUtil.toBukkitItemStack(recipe.getOutputItem());
                ItemStack item2 = SpigotConversionUtil.toBukkitItemStack(recipe.getFirstInputItem());
                ItemStack item3 = SpigotConversionUtil.toBukkitItemStack(recipe.getSecondInputItem());
                SlotUtil.setSlot(item1, event.getPlayer(), false);
                SlotUtil.setSlot(item2, event.getPlayer(), false);
                SlotUtil.setSlot(item3, event.getPlayer(), false);
                recipe.setOutputItem(SpigotConversionUtil.fromBukkitItemStack(AddLore.addLore(item1, event.getPlayer())));
                recipe.setFirstInputItem(SpigotConversionUtil.fromBukkitItemStack(AddLore.addLore(item2, event.getPlayer())));
                recipe.setSecondInputItem(SpigotConversionUtil.fromBukkitItemStack(AddLore.addLore(item3, event.getPlayer())));
            }
            merchantOffers.setMerchantOffers(merchantOfferList);
        }
    }
}
