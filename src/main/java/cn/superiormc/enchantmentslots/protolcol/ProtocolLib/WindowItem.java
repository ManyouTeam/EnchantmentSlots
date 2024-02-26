package cn.superiormc.enchantmentslots.protolcol.ProtocolLib;

import cn.superiormc.enchantmentslots.EnchantmentSlots;
import cn.superiormc.enchantmentslots.configs.ConfigReader;
import cn.superiormc.enchantmentslots.methods.ItemModify;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

// 服务端发给客户端
public class WindowItem extends GeneralPackets {

    public WindowItem() {
        super();
    }

    @Override
    protected void initPacketAdapter() {
        packetAdapter = new PacketAdapter(EnchantmentSlots.instance, ConfigReader.getPriority(), PacketType.Play.Server.WINDOW_ITEMS) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (ConfigReader.getDebug()) {
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[EnchantmentSlots] §f" +
                            "Found WindowsItem packet. Window ID: " + event.getPacket().getIntegers().read(0));
                }
                if (event.getPlayer() == null) {
                    return;
                }
                PacketContainer packet = event.getPacket();
                StructureModifier<ItemStack> singleItemStackStructureModifier = packet.getItemModifier();
                if (singleItemStackStructureModifier.size() != 0) {
                    ItemStack serverItemStack = singleItemStackStructureModifier.read(0);
                    if (ConfigReader.getAutoAddLore()) {
                        ItemModify.addLore(event.getPlayer(), serverItemStack);
                    }
                    ItemStack clientItemStack = ItemModify.serverToClient(serverItemStack);
                    // client 是加过 Lore 的，server 是没加过的！
                    singleItemStackStructureModifier.write(0, clientItemStack);
                }
                StructureModifier<List<ItemStack>> itemStackStructureModifier = packet.getItemListModifier();
                List<ItemStack> serverItemStack = itemStackStructureModifier.read(0);
                List<ItemStack> clientItemStack = new ArrayList<>();
                boolean isPlayerInventory = event.getPacket().getIntegers().read(0) == 0 || serverItemStack.size() % 9 != 0;
                int index = 1;
                for (ItemStack itemStack : serverItemStack) {
                    if (itemStack.getType().isAir()) {
                        clientItemStack.add(itemStack);
                        continue;
                    }
                    if (ConfigReader.getAutoAddLore() && (!ConfigReader.getOnlyInInventory() || isPlayerInventory || index > serverItemStack.size() - 36)) {
                        ItemModify.addLore(event.getPlayer(), itemStack);
                    }
                    clientItemStack.add(ItemModify.serverToClient(itemStack));
                    index ++;
                }
                // client 是加过 Lore 的，server 是没加过的！
                itemStackStructureModifier.write(0, clientItemStack);
            }
        };
    }
}
