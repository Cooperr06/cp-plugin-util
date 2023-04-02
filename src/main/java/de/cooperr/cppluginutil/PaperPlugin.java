package de.cooperr.cppluginutil;

import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the java plugin with some utility methods
 */
public abstract class PaperPlugin extends JavaPlugin {

    protected CustomConfig config;

    /**
     * Registers all commands by instantiating every {@link PaperCommand}.<br>
     * Register a new command via calling its constructor.
     */
    protected void commandRegistration() {
    }

    /**
     * Registers all listeners by instantiating every {@link PaperListener}.<br>
     * Register a new listener via calling its constructor.
     */
    protected void listenerRegistration() {
    }

    /**
     * Registers the command to the plugin
     *
     * @param command command to register
     * @param <T>     plugin to which the command should be registered
     */
    public <T extends PaperPlugin> void registerCommand(@NotNull PaperCommand<T> command) {
        var pluginCommand = getCommand(command.getCommandName());
        if (pluginCommand == null) {
            getLogger().severe("Failed to register command \"%s\"".formatted(command.getCommandName()));
            getServer().getPluginManager().disablePlugin(this);
        } else {
            pluginCommand.setExecutor(command);
        }
    }

    /**
     * Registers the listener to the plugin
     *
     * @param listener listener to register
     * @param <T1>     plugin to which the listener should be registered
     * @param <T2>     event to which the listener should listen
     */
    public <T1 extends PaperPlugin, T2 extends Event> void registerListener(@NotNull PaperListener<T1, T2> listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    /**
     * Returns the custom config of this plugin
     *
     * @return custom config of this plugin
     * @see CustomConfig
     */
    @NotNull
    public CustomConfig getCustomConfig() {
        return config;
    }

    /**
     * Saves the custom config of this plugin
     *
     * @see CustomConfig#save()
     */
    @Override
    public void saveConfig() {
        config.save();
    }
}
