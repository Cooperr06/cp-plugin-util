package de.cooperr.cppluginutil;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;

public abstract class PaperCommand implements TabExecutor {
    
    protected final PaperPlugin plugin;
    
    public PaperCommand(@NotNull PaperPlugin plugin) {
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
