package de.cooperr.cppluginutil;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

/**
 * Timer for all players on the server
 */
public class Playtimer<T extends PaperPlugin> {

    private final T plugin;

    private BukkitTask task;
    private boolean running = false;
    private long time = 0;

    /**
     * Sets the default value for the timer
     *
     * @param plugin plugin to which the timer task belongs to
     */
    public Playtimer(@NotNull T plugin) {
        this.plugin = plugin;

        plugin.getCustomConfig().addDefault("timer.time", 0);
    }
    
    /**
     * Starts the timer if it is running
     */
    public void start() {
        
        if (running) {
            return;
        }

        running = true;
        time = plugin.getCustomConfig().getInt("timer.time");
        
        plugin.getServer().broadcast(Component.text("Timer started!", NamedTextColor.GOLD, TextDecoration.BOLD));
        
        plugin.getServer().showTitle(Title.title(
            Component.text("Timer", NamedTextColor.GOLD, TextDecoration.BOLD),
            Component.text("started", NamedTextColor.GREEN, TextDecoration.BOLD),
            Title.Times.times(Duration.ofMillis(750), Duration.ofMillis(1250), Duration.ofMillis(750))));
        
        task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> plugin.getServer().sendActionBar(formatTime(++time)), 20, 20);
    }
    
    /**
     * Stops the timer if it is running and saves or resets the time
     *
     * @param reset whether to reset the time
     */
    public void stop(boolean reset) {
        
        if (!running) {
            return;
        }
        
        task.cancel();
        running = false;
        
        plugin.getServer().broadcast(Component.text("Timer stopped! Your time is " + formatTime(time) + "!",
            NamedTextColor.GOLD, TextDecoration.BOLD));
        
        plugin.getServer().showTitle(Title.title(
            Component.text("Timer", NamedTextColor.GOLD, TextDecoration.BOLD),
            Component.text("started", NamedTextColor.RED, TextDecoration.BOLD),
            Title.Times.times(Duration.ofMillis(750), Duration.ofMillis(1250), Duration.ofMillis(750))));

        plugin.getCustomConfig().setAndSave("timer.time", reset ? 0 : time);
        time = 0;
    }
    
    /**
     * Formats the given time to a fancy text component
     * e. g.:
     * <code>
     * 10h 4m 0s;
     * 3s;
     * 2d 0s;
     * 5d 9h 8m 1s
     * </code>
     *
     * @param time time to format
     * @return formatted time as a {@link TextComponent}
     */
    @NotNull
    public TextComponent formatTime(long time) {
        var seconds = time;
        var minutes = 0;
        var hours = 0;
        var days = 0;
        
        while (seconds >= 60) {
            minutes++;
            seconds -= 60;
        }
        while (minutes >= 60) {
            hours++;
            minutes -= 60;
        }
        while (hours >= 24) {
            days++;
            hours -= 24;
        }

        return Component.text((days == 0 ? "" : days + "d ") +
                (hours == 0 ? "" : hours + "h ") +
                (minutes == 0 ? "" : minutes + "m ") +
                seconds + "s", NamedTextColor.GOLD, TextDecoration.BOLD);
    }

    public boolean isRunning() {
        return running;
    }

    public long getTime() {
        return time;
    }
}
