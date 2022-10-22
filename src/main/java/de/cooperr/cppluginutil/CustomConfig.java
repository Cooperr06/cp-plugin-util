package de.cooperr.cppluginutil;

import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Custom config with variable file(-name)
 */
public class CustomConfig extends YamlConfiguration {
    
    private final PaperPlugin plugin;
    @Getter
    private final File file;
    
    /**
     * Creates a config for the plugin with the specific file
     *
     * @param plugin plugin to which the config belongs
     * @param file   file which represents the config
     */
    public CustomConfig(@NotNull PaperPlugin plugin, @NotNull File file) {
        this.plugin = plugin;
        this.file = file;
    
        try {
            FileUtils.touch(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, String.format("Failed to create config \"%s\"", file.getName()), e);
        }
    
        try {
            load(file);
        } catch (IOException | InvalidConfigurationException e) {
            plugin.getLogger().log(Level.SEVERE, String.format("Failed to load config \"%s\"", file.getName()), e);
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }
    }
    
    /**
     * Sets the value to the path and saves the config
     *
     * @param path  path to set the value to
     * @param value value to be set to the path
     * @see #save()
     */
    public void setAndSave(String path, Object value) {
        set(path, value);
        save();
    }
    
    /**
     * Saves the config
     *
     * @see #save(File)
     */
    public void save() {
        try {
            save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, String.format("Failed to save config \"%s\"", file.getName()), e);
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }
    }
}
