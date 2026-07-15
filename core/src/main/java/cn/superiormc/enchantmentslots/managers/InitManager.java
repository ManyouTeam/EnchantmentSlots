package cn.superiormc.enchantmentslots.managers;

import cn.superiormc.enchantmentslots.EnchantmentSlots;
import cn.superiormc.enchantmentslots.utils.CommonUtil;

import java.io.File;

public class InitManager {
    public static InitManager initManager;

    private boolean firstLoad = false;

    public InitManager() {
        initManager = this;
        File file = new File(EnchantmentSlots.instance.getDataFolder(), "config.yml");
        if (!file.exists()) {
            EnchantmentSlots.instance.saveDefaultConfig();
            firstLoad = true;
        }
        init();
    }

    public void init() {
        resourceOutput("languages/en_US.yml", true);
        resourceOutput("languages/zh_CN.yml", true);
        resourceOutput("item_slots_settings/example.yml", false);
        resourceOutput("extra_slot_items/A.yml", false);
        resourceOutput("extra_slot_items/B.yml", false);
        resourceOutput("deenchant_items/common.yml", false);
        resourceOutput("deenchant_items/advanced.yml", false);
        resourceOutput("remove_slot_items/fixed.yml", false);
        resourceOutput("remove_slot_items/excess.yml", false);
        resourceOutput("remove_slot_items/linked_a.yml", false);
    }

    public boolean isFirstLoad() {
        return firstLoad;
    }

    private void resourceOutput(String fileName, boolean fix) {
        File tempVal1 = new File(EnchantmentSlots.instance.getDataFolder(), fileName);
        if (!tempVal1.exists()) {
            if (!firstLoad && !fix) {
                return;
            }
            File tempVal2 = new File(fileName);
            if (tempVal2.getParentFile() != null) {
                CommonUtil.mkDir(tempVal2.getParentFile());
            }
            EnchantmentSlots.instance.saveResource(tempVal2.getPath(), false);
        }
    }
}
