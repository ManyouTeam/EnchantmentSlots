package cn.superiormc.enchantmentslots.protolcol.ProtocolLib;

import cn.superiormc.enchantmentslots.EnchantmentSlots;
import cn.superiormc.enchantmentslots.configs.ConfigReader;
import cn.superiormc.enchantmentslots.hooks.CheckValidHook;
import cn.superiormc.enchantmentslots.methods.ItemLimits;
import cn.superiormc.enchantmentslots.methods.ItemModify;
import cn.superiormc.enchantmentslots.utils.CommonUtil;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SetSlots extends GeneralPackets {
    public SetSlots() {
        super();
    }
    @Override
    protected void initPacketAdapter(){
        packetAdapter = new PacketAdapter(EnchantmentSlots.instance, ConfigReader.getPriority(), PacketType.Play.Server.SET_SLOT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (ConfigReader.getDebug()) {
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[EnchantmentSlots] §f" +
                            "Found SetSlots packet.");
                }
                if (event.getPlayer() == null) {
                    return;
                }
                PacketContainer packet = event.getPacket();
                int windowID = packet.getIntegers().read(0);
                StructureModifier<ItemStack> itemStackStructureModifier = packet.getItemModifier();
                ItemStack serverItemStack = itemStackStructureModifier.read(0);
                if (serverItemStack == null || serverItemStack.getType().isAir()) {
                    return;
                }
                int slot = packet.getIntegers().read(packet.getIntegers().size() - 1);
                int spigotSlot;
                if (slot >= 36) {
                    spigotSlot = slot - 36;
                } else if (slot <= 8) {
                    spigotSlot = slot + 31;
                } else {
                    spigotSlot = slot;
                }
                if (ConfigReader.getDebug()) {
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[EnchantmentSlots] §f" +
                            "Packet Slot ID: " + slot + ", Window ID: " + windowID + ", Top Size: " +
                            event.getPlayer().getOpenInventory().getTopInventory().getSize() + ".");
                }
                boolean dontAddLore = false;
                if (CommonUtil.inPlayerInventory(event.getPlayer(), slot) && (ConfigReader.getSetSlotPacketTrigger() || ConfigReader.getRemoveExtraEnchants())) {
                    ItemStack targetItem = event.getPlayer().getInventory().getItem(spigotSlot);
                    if (ConfigReader.getSetSlotPacketTrigger()) {
                        if (targetItem != null && !targetItem.getType().isAir()) {
                            String itemID = CheckValidHook.checkValid(targetItem);
                            int defaultSlot = ConfigReader.getDefaultLimits(event.getPlayer(), itemID);
                            ItemModify.addLore(targetItem, defaultSlot, itemID);
                            dontAddLore = true;
                        }
                    }
                    if (ConfigReader.getRemoveExtraEnchants()) {
                        if (targetItem != null && !targetItem.getType().isAir()) {
                            int maxEnchantments = ItemLimits.getRealMaxEnchantments(serverItemStack);
                            if (maxEnchantments > 0 && targetItem.getEnchantments().size() > maxEnchantments) {
                                int removeAmount = targetItem.getEnchantments().size() - maxEnchantments;
                                for (Enchantment enchant : targetItem.getEnchantments().keySet()) {
                                    if (removeAmount <= 0) {
                                        break;
                                    }
                                    ItemMeta meta = targetItem.getItemMeta();
                                    if (meta == null) {
                                        break;
                                    }
                                    meta.removeEnchant(enchant);
                                    targetItem.setItemMeta(meta);
                                    removeAmount--;
                                }
                            }
                        }
                    }
                }
                if (!dontAddLore && ConfigReader.getAutoAddLore() && ConfigReader.getOnlyInPlayerInventory(event.getPlayer(),
                        CommonUtil.inPlayerInventory(event.getPlayer(), slot))) {
                    String itemID = CheckValidHook.checkValid(serverItemStack);
                    int defaultSlot = ConfigReader.getDefaultLimits(event.getPlayer(), itemID);
                    ItemModify.addLore(serverItemStack, defaultSlot, itemID);
                }
                ItemStack clientItemStack = ItemModify.serverToClient(serverItemStack, event.getPlayer());
                // client 是加过 Lore 的，server 是没加过的！
                itemStackStructureModifier.write(0, clientItemStack);
            }
        };
    }
}
