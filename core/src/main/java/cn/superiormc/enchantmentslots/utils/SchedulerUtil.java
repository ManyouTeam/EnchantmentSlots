package cn.superiormc.enchantmentslots.utils;

import cn.superiormc.enchantmentslots.EnchantmentSlots;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;

public class SchedulerUtil {

    private BukkitTask bukkitTask;

    private ScheduledTask scheduledTask;

    public SchedulerUtil(BukkitTask bukkitTask) {
        this.bukkitTask = bukkitTask;
    }

    public SchedulerUtil(ScheduledTask scheduledTask) {
        this.scheduledTask = scheduledTask;
    }

    public void cancel() {
        if (EnchantmentSlots.isFolia) {
            scheduledTask.cancel();
        } else {
            bukkitTask.cancel();
        }
    }

    // 在主线程上运行任务
    public static void runSync(Runnable task) {
        if (EnchantmentSlots.isFolia) {
            Bukkit.getGlobalRegionScheduler().execute(EnchantmentSlots.instance, task);
        } else {
            Bukkit.getScheduler().runTask(EnchantmentSlots.instance, task);
        }
    }

    public static void runSync(Entity entity, Runnable task) {
        if (EnchantmentSlots.isFolia) {
            entity.getScheduler().run(EnchantmentSlots.instance, scheduledTask -> task.run(), null);
        } else {
            Bukkit.getScheduler().runTask(EnchantmentSlots.instance, task);
        }
    }

    // 在异步线程上运行任务
    public static void runTaskAsynchronously(Runnable task) {
        if (EnchantmentSlots.isFolia) {
            task.run();
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(EnchantmentSlots.instance, task);
        }
    }

    // 延迟执行任务
    public static SchedulerUtil runTaskLater(Runnable task, long delayTicks) {
        if (EnchantmentSlots.isFolia) {
            return new SchedulerUtil(Bukkit.getGlobalRegionScheduler().runDelayed(EnchantmentSlots.instance,
                    scheduledTask -> task.run(), delayTicks));
        } else {
            return new SchedulerUtil(Bukkit.getScheduler().runTaskLater(EnchantmentSlots.instance, task, delayTicks));
        }
    }

    // 定时循环任务
    public static SchedulerUtil runTaskTimer(Runnable task, long delayTicks, long periodTicks) {
        if (EnchantmentSlots.isFolia) {
            return new SchedulerUtil(Bukkit.getGlobalRegionScheduler().runAtFixedRate(EnchantmentSlots.instance,
                    scheduledTask -> task.run(), delayTicks, periodTicks));
        } else {
            return new SchedulerUtil(Bukkit.getScheduler().runTaskTimer(EnchantmentSlots.instance, task, delayTicks, periodTicks));
        }
    }

    // 延迟执行任务
    public static SchedulerUtil runTaskLaterAsynchronously(Runnable task, long delayTicks) {
        if (EnchantmentSlots.isFolia) {
            return new SchedulerUtil(Bukkit.getGlobalRegionScheduler().runDelayed(EnchantmentSlots.instance,
                    scheduledTask -> task.run(), delayTicks));
        } else {
            return new SchedulerUtil(Bukkit.getScheduler().runTaskLaterAsynchronously(EnchantmentSlots.instance, task, delayTicks));
        }
    }

}
