package cn.superiormc.enchantmentslots.protolcol.ProtocolLib;

import cn.superiormc.enchantmentslots.listeners.PlayerCacheListener;
import cn.superiormc.enchantmentslots.managers.ConfigManager;
import cn.superiormc.enchantmentslots.methods.AddLore;
import cn.superiormc.enchantmentslots.utils.CommonUtil;
import cn.superiormc.enchantmentslots.methods.SlotUtil;
import cn.superiormc.enchantmentslots.utils.ItemUtil;
import cn.superiormc.enchantmentslots.utils.SchedulerUtil;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SetSlots implements PacketListener {

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (!event.getPacketType().equals(PacketType.Play.Server.SET_SLOT)) {
            return;
        }
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        WrapperPlayServerSetSlot serverSetSlot = new WrapperPlayServerSetSlot(event);
        int windowID = serverSetSlot.getWindowId();
        ItemStack item = SpigotConversionUtil.toBukkitItemStack(serverSetSlot.getItem());
        if (!ItemUtil.isValid(item)) {
            return;
        }
        int slot = serverSetSlot.getSlot();
        if (slot < 0) {
            return;
        }
        int spigotSlot = CommonUtil.convertNMSSlotToBukkitSlot(slot, windowID, event.getPlayer());
        if (spigotSlot < 0) {
            return;
        }
        boolean inPlayerInventory = CommonUtil.inPlayerInventory(event.getPlayer(), slot, windowID);
        if (inPlayerInventory && (ConfigManager.configManager.getBoolean(
                "settings.set-slot-trigger.SetSlotPacket.enabled", true) ||
                ConfigManager.configManager.getBoolean(
                        "settings.set-slot-trigger.SetSlotPacket.remove-illegal-excess-enchant.enabled",
                        true))) {
            ItemStack targetItem = CommonUtil.getItemFromSlot(event.getPlayer(), spigotSlot);
            if (ItemUtil.isValid(targetItem)) {
                ItemMeta meta = targetItem.getItemMeta();
                if (meta != null) {
                    if (ConfigManager.configManager.getBoolean("settings.set-slot-trigger.add-hide-enchant-flag", false)) {
                        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        targetItem.setItemMeta(meta);
                    }
                    if (ConfigManager.configManager.getBoolean("settings.set-slot-trigger.SetSlotPacket.enabled", true)) {
                        SlotUtil.setSlot(targetItem, event.getPlayer(), false);
                    }
                    if (PlayerCacheListener.loadedPlayers.contains(event.getPlayer()) && !ConfigManager.configManager.isIgnore(targetItem) && ConfigManager.configManager.getBoolean("settings.set-slot-trigger.SetSlotPacket.remove-illegal-excess-enchant.enabled", true)) {
                        if (ConfigManager.configManager.getBoolean("settings.set-slot-trigger.SetSlotPacket.remove-illegal-excess-enchant.run-sync", true)) {
                            SchedulerUtil.runSync(() -> SlotUtil.removeExcessEnchantments(targetItem, event.getPlayer()));
                        } else {
                            SlotUtil.removeExcessEnchantments(targetItem, event.getPlayer());
                        }
                    }
                }
            }
        }
        serverSetSlot.setItem(SpigotConversionUtil.fromBukkitItemStack(AddLore.autoAddLore(item, player, inPlayerInventory)));
    }
}
