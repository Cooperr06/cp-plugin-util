package de.cooperr.cppluginutil;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the java plugin with some utility methods
 */
public abstract class PaperPlugin extends JavaPlugin {
    
    protected CustomConfig config;
    
    /**
     * Registers all commands by instantiating every {@link PaperCommand}.
     * Should only be overridden if plugin has commands
     */
    protected void commandRegistration() {
    }
    
    /**
     * Registers all commands by instantiating every {@link PaperListener}.
     * Should only be overridden if plugin has commands
     */
    protected void listenerRegistration() {
    }
    
    /**
     * Registers the command to the plugin
     *
     * @param command command to be registered
     */
    public void registerCommand(@NotNull PaperCommand command) {
        var pluginCommand = getCommand(command.getCommandName());
        if (pluginCommand == null) {
            getLogger().severe(String.format("Failed to register command \"%s\"", command.getCommandName()));
            getServer().getPluginManager().disablePlugin(this);
        } else {
            pluginCommand.setExecutor(command);
        }
    }
    
    /**
     * Registers the listener to the plugin
     *
     * @param listener listener to be registered
     * @param <T>      event type
     */
    public <T extends Event> void registerListener(@NotNull PaperListener<T> listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }
    
    /**
     * Returns the custom config of this plugin
     *
     * @return custom config of this plugin
     * @see CustomConfig
     */
    @Override
    public @NotNull FileConfiguration getConfig() {
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
