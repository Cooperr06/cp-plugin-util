package de.cooperr.cppluginutil.util;

import de.cooperr.cppluginutil.base.PaperPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

/**
 * Timer for all players on the server, be advised: config has to be set up in order to use a timer
 */
public class Timer {

    private final PaperPlugin plugin;
    private final Localizer localizer;

    private BukkitTask task;
    private boolean running = false;
    private int time = 0;

    /**
     * Sets the default value for the timer
     *
     * @param plugin plugin to which the timer task belongs to
     */
    public Timer(@NotNull PaperPlugin plugin) {
        this.plugin = plugin;

        if (plugin.customConfig() == null) {
            this.localizer = null;

            plugin.getLogger().severe("Custom Config has to be initialized to use Playtimer");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return;
        }
        plugin.customConfig().setAndSave("timer.time", 0);

        if (plugin.localizer() == null) {
            this.localizer = null;
            return;
        }

        var defaultProperties = plugin.localizer().languageProperties().get(plugin.localizer().defaultLocale());
        if (defaultProperties.getProperty("timer.start.message") != null &&
                defaultProperties.getProperty("timer.start.title") != null &&
                defaultProperties.getProperty("timer.start.subtitle") != null &&
                defaultProperties.getProperty("timer.stop.message") != null &&
                defaultProperties.getProperty("timer.stop.title") != null &&
                defaultProperties.getProperty("timer.stop.subtitle") != null
        ) {
            this.localizer = plugin.localizer();
        } else {
            this.localizer = null;
            plugin.getLogger().info("Localizer is initialized, but the messages for the timer are not set, using default messages");
        }
    }

    /**
     * Formats the given time to a fancy string, e. g.:<br>
     * <code>10h 4m 0s; 3s; 2d 0s; 5d 9h 8m 1s</code>
     *
     * @param time time to format
     * @return formatted time
     */
    @NotNull
    public static String formatTime(long time) {
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

        return (days == 0 ? "" : days + "d ") +
                (hours == 0 ? "" : hours + "h ") +
                (minutes == 0 ? "" : minutes + "m ") +
                seconds + "s";
    }

    /**
     * Starts the timer if it is running
     */
    public void start() {
        if (running) {
            return;
        }

        running = true;
        time = plugin.customConfig().getInt("timer.time");

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            if (localizer == null) {
                plugin.getServer().broadcast(Component.text("Timer started!", NamedTextColor.GOLD, TextDecoration.BOLD));
                plugin.getServer().showTitle(Title.title(
                        Component.text("Timer", NamedTextColor.GOLD, TextDecoration.BOLD),
                        Component.text("started", NamedTextColor.GREEN, TextDecoration.BOLD),
                        Title.Times.times(Duration.ofMillis(750), Duration.ofMillis(1250), Duration.ofMillis(750))));
            } else {
                plugin.getServer().getOnlinePlayers().forEach(onlinePlayer -> {
                    var locale = onlinePlayer.locale();

                    onlinePlayer.sendMessage(localizer.localizeMessage("timer.start.message", locale));
                    onlinePlayer.showTitle(Title.title(
                            localizer.localizeMessage("timer.start.title", locale),
                            localizer.localizeMessage("timer.start.subtitle", locale),
                            Title.Times.times(Duration.ofMillis(750), Duration.ofMillis(1250), Duration.ofMillis(750))));
                });
            }
        });
        task = plugin.getServer().getScheduler().runTaskTimer(plugin, () ->
                plugin.getServer().sendActionBar(Component.text(formatTime(++time), NamedTextColor.GOLD, TextDecoration.BOLD)), 20, 20);
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

        if (localizer == null) {
            plugin.getServer().broadcast(Component.text("Timer stopped! The time is " + formatTime(time) + "!",
                    NamedTextColor.GOLD, TextDecoration.BOLD));
            plugin.getServer().showTitle(Title.title(
                    Component.text("Timer", NamedTextColor.GOLD, TextDecoration.BOLD),
                    Component.text("stopped", NamedTextColor.RED, TextDecoration.BOLD),
                    Title.Times.times(Duration.ofMillis(750), Duration.ofMillis(1250), Duration.ofMillis(750))));
        } else {
            plugin.getServer().getOnlinePlayers().forEach(onlinePlayer -> {
                var locale = onlinePlayer.locale();

                onlinePlayer.sendMessage(localizer.localizeMessage("timer.stop.message", locale, formatTime(time)));
                onlinePlayer.showTitle(Title.title(
                        localizer.localizeMessage("timer.stop.title", locale),
                        localizer.localizeMessage("timer.stop.subtitle", locale),
                        Title.Times.times(Duration.ofMillis(750), Duration.ofMillis(1250), Duration.ofMillis(750))));
            });
        }

        plugin.customConfig().setAndSave("timer.time", reset ? 0 : time);
        time = 0;
    }

    public boolean running() {
        return running;
    }

    public int time() {
        return time;
    }
}
