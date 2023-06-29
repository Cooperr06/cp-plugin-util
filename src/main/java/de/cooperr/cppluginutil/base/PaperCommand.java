package de.cooperr.cppluginutil.base;

import de.cooperr.cppluginutil.util.Localizer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * Custom command which has some utility methods
 *
 * @param <T> plugin to which this command should belong
 */
public abstract class PaperCommand<T extends PaperPlugin> implements TabExecutor {

    protected final T plugin;

    /**
     * Registers the command to the plugin
     *
     * @param plugin plugin to which the command should belong
     */
    public PaperCommand(@NotNull T plugin) {
        this.plugin = plugin;

        plugin.registerCommand(this);
    }

    /**
     * Sends the intended usage of this command to a sender
     *
     * @param sender sender to send the message to
     * @see #commandUsage()
     */
    protected void sendCommandUsage(@NotNull CommandSender sender) {
        if (plugin.localizer() == null) {
            sender.sendMessage(Component.text(String.format("Usage: %s", commandUsage()), NamedTextColor.DARK_RED));
        } else {
            var locale = sender instanceof Player ? ((Player) sender).locale() : null;
            sender.sendMessage(plugin.localizer().localizeMessage("command.usage", locale,
                    Component.text(commandUsage(), NamedTextColor.DARK_RED)));
        }
    }

    /**
     * Sends a message to the sender that a player is required
     *
     * @param sender sender to send the message to
     */
    protected void sendWrongSenderMessage(@NotNull CommandSender sender) {
        if (plugin.localizer() == null) {
            sender.sendMessage(Component.text("You have to be a player to use this command!", NamedTextColor.DARK_RED));
        } else {
            var locale = sender instanceof Player ? ((Player) sender).locale() : null;
            sender.sendMessage(plugin.localizer().localizeMessage("command.wrong_sender", locale));
        }
    }

    /**
     * Sends an error message to the sender
     *
     * @param sender sender to send the message to
     * @param error  error key or if no localization should be used, the error message
     * @param args   arguments for localization
     * @see Localizer#localizeMessage(String, Locale, TextComponent...)
     */
    protected void sendErrorMessage(@NotNull CommandSender sender, @NotNull String error, @Nullable TextComponent... args) {
        if (plugin.localizer() == null) {
            sender.sendMessage(Component.text(error, NamedTextColor.DARK_RED));
        } else {
            var locale = sender instanceof Player ? ((Player) sender).locale() : null;
            sender.sendMessage(plugin.localizer().localizeMessage(error, locale, args));
        }
    }

    /**
     * @return command name in lower case
     */
    @NotNull
    public abstract String commandName();

    /**
     * @return intended command usage (incl. '/')
     */
    @NotNull
    public abstract String commandUsage();
}
