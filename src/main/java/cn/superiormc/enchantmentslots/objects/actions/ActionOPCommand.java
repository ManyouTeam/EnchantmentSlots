package cn.superiormc.enchantmentslots.objects.actions;

import cn.superiormc.enchantmentslots.utils.CommonUtil;
import org.bukkit.entity.Player;

public class ActionOPCommand extends AbstractRunAction {

    public ActionOPCommand() {
        super("op_command");
        setRequiredArgs("command");
    }

    @Override
    protected void onDoAction(ObjectSingleAction singleAction, Player player, int amount) {
        CommonUtil.dispatchOpCommand(player, singleAction.getString("command", player, amount));
    }
}
