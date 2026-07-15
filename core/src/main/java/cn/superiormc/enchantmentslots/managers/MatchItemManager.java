package cn.superiormc.enchantmentslots.managers;

import cn.superiormc.enchantmentslots.objects.matchitem.*;
import cn.superiormc.enchantmentslots.utils.CommonUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;
import java.util.HashSet;

public class MatchItemManager {

    public static MatchItemManager matchItemManager;

    private final Collection<AbstractMatchItemRule> rules = new HashSet<>();

    public MatchItemManager() {
        matchItemManager = this;
        registerNewRule(new ContainsLore());
        registerNewRule(new ContainsName());
        registerNewRule(new HasEnchants());
        registerNewRule(new HasLore());
        registerNewRule(new HasName());
        registerNewRule(new Items());
        registerNewRule(new Not());
        registerNewRule(new Material());
        registerNewRule(new None());
        if (CommonUtil.checkPluginLoad("NBTAPI")) {
            registerNewRule(new ContainsNBT());
            registerNewRule(new NBTString());
            registerNewRule(new NBTByte());
            registerNewRule(new NBTInt());
            registerNewRule(new NBTDouble());
        }
        if (CommonUtil.getMinorVersion(20, 5)) {
            registerNewRule(new Rarity());
        }
        registerNewRule(new ContainsEnchants());
        registerNewRule(new ContainsEnchantsAmount());
        registerNewRule(new Any());
        registerNewRule(new MaterialTag());
        registerNewRule(new Enchantable());
        registerNewRule(new HasStoredEnchants());
        registerNewRule(new ESHasSlot());
    }

    public void registerNewRule(AbstractMatchItemRule rule) {
        rules.add(rule);
    }

    public boolean getMatch(ConfigurationSection section, ItemStack item) {
        if (section == null) {
            return true;
        }
        if (item == null) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        for (AbstractMatchItemRule rule : rules) {
            if (rule.configNotContains(section)) {
                continue;
            }
            if (!rule.getMatch(section, item, meta)) {
                return false;
            }
        }
        return true;
    }

    public Collection<AbstractMatchItemRule> getRules() {
        return rules;
    }
}
