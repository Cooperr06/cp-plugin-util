package de.cooperr.cppluginutil;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;

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
     * @see #getCommandUsage()
     */
    protected void sendCommandUsage(@NotNull CommandSender sender) {
        sender.sendMessage(Component.text(String.format("Usage: %s", getCommandUsage()), NamedTextColor.DARK_RED));
    }

    /**
     * Sends a message to the sender that a player is required
     *
     * @param sender sender to send the message to
     */
    protected void sendWrongSenderMessage(@NotNull CommandSender sender) {
        sender.sendMessage(Component.text("You have to be a player to use this command!", NamedTextColor.DARK_RED));
    }

    /**
     * Sends an error message to the sender
     *
     * @param message message to send
     * @param sender  sender to send the message to
     */
    protected void sendErrorMessage(@NotNull CommandSender sender, @NotNull String message) {
        sender.sendMessage(Component.text(message, NamedTextColor.DARK_RED));
    }

    /**
     * @return command name in lower case
     */
    @NotNull
    public abstract String getCommandName();

    /**
     * @return intended command usage (incl. '/')
     */
    @NotNull
    public abstract String getCommandUsage();
}
