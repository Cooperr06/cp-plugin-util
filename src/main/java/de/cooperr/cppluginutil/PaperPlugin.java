package de.cooperr.cppluginutil;

import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Represents the java plugin with some utility methods
 */
public abstract class PaperPlugin extends JavaPlugin {
    
    protected CustomConfig config;
    
    /**
     * Constructor for using <a href="https://github.com/MockBukkit/MockBukkit">MockBukkit</a>
     */
    protected PaperPlugin(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
    }
    
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
     * @param command command to be registered
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
     * @param listener listener to be registered
     * @param <T1>     plugin
     * @param <T2>     event type
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
    public @NotNull CustomConfig getCustomConfig() {
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
