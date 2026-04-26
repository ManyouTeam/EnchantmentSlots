package cn.superiormc.enchantmentslots.protolcol.packetevents;

import cn.superiormc.enchantmentslots.methods.AddLore;
import cn.superiormc.enchantmentslots.utils.ItemUtil;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.retrooper.packetevents.event.PacketListener;

// 服务端发给客户端
public class WindowItem implements PacketListener {

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (!event.getPacketType().equals(PacketType.Play.Server.WINDOW_ITEMS)) {
            return;
        }
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        WrapperPlayServerWindowItems windowItems = new WrapperPlayServerWindowItems(event);
        Optional<com.github.retrooper.packetevents.protocol.item.ItemStack> optionalCarriedItem = windowItems.getCarriedItem();
        if (optionalCarriedItem.isPresent()) {
            ItemStack carriedItem = SpigotConversionUtil.toBukkitItemStack(optionalCarriedItem.get());
            if (ItemUtil.isValid(carriedItem)) {
                ItemStack clientItemStack = AddLore.autoAddLore(carriedItem, event.getPlayer(), true);
                windowItems.setCarriedItem(SpigotConversionUtil.fromBukkitItemStack(clientItemStack));
            }
        }
        List<com.github.retrooper.packetevents.protocol.item.ItemStack> tempItems = windowItems.getItems();
        List<com.github.retrooper.packetevents.protocol.item.ItemStack> clientItemStack = new ArrayList<>();
        int index = 1;
        for (com.github.retrooper.packetevents.protocol.item.ItemStack item : tempItems) {
            if (item.isEmpty()) {
                clientItemStack.add(item);
                index ++;
                continue;
            }
            boolean isPlayerInventory = windowItems.getWindowId() == 0 || index > windowItems.getItems().size() - 36;
            clientItemStack.add(SpigotConversionUtil.fromBukkitItemStack(AddLore.autoAddLore(SpigotConversionUtil.toBukkitItemStack(item), player, isPlayerInventory)
            ));
            index ++;
        }
        windowItems.setItems(clientItemStack);
    }
}
