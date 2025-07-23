package cn.superiormc.enchantmentslots.objects.conditions;

import org.bukkit.entity.Player;

public class ConditionWorld extends AbstractCheckCondition {

    public ConditionWorld() {
        super("world");
        setRequiredArgs("world");
    }

    @Override
    protected boolean onCheckCondition(ObjectSingleCondition singleCondition, Player player, int amount) {
        return player.getWorld().getName().equals(singleCondition.getString("world", player, amount));
    }
}
