package cn.superiormc.enchantmentslots.utils;

import cn.superiormc.enchantmentslots.libs.ColorParser;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TextUtil {

    public static String parse(String text) {
        return ColorParser.parse(text);
    }

    public static String parse(Player player, String text) {
        if (CommonUtil.checkPluginLoad("PlaceholderAPI")) {
            return parse(PlaceholderAPI.setPlaceholders(player, text));
        }
        else {
            return parse(text);
        }
    }

    public static String withPAPI(String text, Player player) {
        if (text.contains("%") && CommonUtil.checkPluginLoad("PlaceholderAPI")) {
            return PlaceholderAPI.setPlaceholders(player, text);
        }
        else {
            return text;
        }
    }

    public static List<String> getListWithColor(List<String> inList) {
        List<String> resultList = new ArrayList<>();
        for (String s : inList) {
            resultList.add(TextUtil.parse(s));
        }
        return resultList;
    }
}
