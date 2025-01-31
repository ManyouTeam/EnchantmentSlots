package cn.superiormc.enchantmentslots.protolcol.ProtocolLib;

import cn.superiormc.enchantmentslots.EnchantmentSlots;
import cn.superiormc.enchantmentslots.managers.ConfigManager;
import cn.superiormc.enchantmentslots.managers.LanguageManager;
import cn.superiormc.enchantmentslots.methods.AddLore;
import cn.superiormc.enchantmentslots.methods.SlotUtil;
import cn.superiormc.enchantmentslots.utils.CommonUtil;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SetSlots extends GeneralPackets {
    public SetSlots() {
        super();
    }

    @Override
    protected void initPacketAdapter() {
        packetAdapter = new PacketAdapter(EnchantmentSlots.instance, ConfigManager.configManager.getPriority(), PacketType.Play.Server.SET_SLOT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (ConfigManager.configManager.getBoolean("debug", false)) {
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
                if (ConfigManager.configManager.getBoolean("debug", false)) {
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[EnchantmentSlots] §f" +
                            "Packet Slot ID: " + slot + ", Window ID: " + windowID + ", Top Size: " +
                            event.getPlayer().getOpenInventory().getTopInventory().getSize() + ".");
                }
                if (CommonUtil.inPlayerInventory(event.getPlayer(), slot) && (ConfigManager.configManager.getBoolean(
                        "settings.set-slot-trigger.SetSlotPacket.enabled", true)) ||
                        ConfigManager.configManager.getBoolean(
                                "settings.set-slot-trigger.SetSlotPacket.remove-illegal-excess-enchant",
                                true)) {
                    ItemStack targetItem = event.getPlayer().getInventory().getItem(spigotSlot);
                    if (ConfigManager.configManager.getBoolean(
                            "settings.set-slot-trigger.SetSlotPacket.enabled", true)) {
                        if (targetItem != null && !targetItem.getType().isAir()) {
                            SlotUtil.setSlot(targetItem, event.getPlayer(), false);
                        }
                    }
                    if (!ConfigManager.configManager.isIgnore(targetItem) && ConfigManager.configManager.getBoolean("settings.set-slot-trigger.SetSlotPacket.remove-illegal-excess-enchant", true)) {
                        // plib的事件获取到的Player可能不是Bukkit的Player
                        Player player = Bukkit.getPlayer(event.getPlayer().getUniqueId());
                        if (player != null) {
                            checkEnchantment(player, spigotSlot);
                        }
                    }
                }
                ItemStack clientItemStack = AddLore.addLore(serverItemStack, event.getPlayer());
                // client 是加过 Lore 的，server 是没加过的！
                itemStackStructureModifier.write(0, clientItemStack);
            }
        };
    }

    private void checkEnchantment(Player player, int slot) {
        Bukkit.getScheduler().runTask(EnchantmentSlots.instance, () -> {
            if (!player.isOnline()) {
                return;
            }
            ItemStack targetItem = player.getInventory().getItem(slot);
            if (targetItem != null && !targetItem.getType().isAir()) {
                // 他这里读取到的是发包的时候的物品，假如发包时invsync未同步完成，这里就会是上次这个服务器的物品，他获取这个物品的最大附魔数可能是0
                int maxEnchantments = SlotUtil.getSlot(targetItem);
                // 如果invsync把物品同步过来了 他给invsync新同步的物品，按照限制0的数量给附魔全移除了
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
                    if (!ConfigManager.configManager.getBoolean("settings.set-slot-trigger.SetSlotPacket.hide-remove-message", false)) {
                        LanguageManager.languageManager.sendStringText(player, "remove-excess-enchants");
                    }
                }
            }
        });
    }

}
