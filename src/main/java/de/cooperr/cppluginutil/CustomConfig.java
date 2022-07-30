package de.cooperr.cppluginutil;

import lombok.Getter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class CustomConfig extends YamlConfiguration {
    
    private final PaperPlugin plugin;
    @Getter
    private final File file;
    
    public CustomConfig(@NotNull PaperPlugin plugin, @NotNull File file) {
        this.plugin = plugin;
        this.file = file;
        
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
