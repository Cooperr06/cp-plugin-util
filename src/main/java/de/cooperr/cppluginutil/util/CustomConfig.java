package de.cooperr.cppluginutil.util;

import de.cooperr.cppluginutil.base.PaperPlugin;
import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;

/**
 * Custom config with variable file(-name)
 */
public class CustomConfig extends YamlConfiguration {

    private final PaperPlugin plugin;
    private final File file;

    /**
     * Creates a config for the plugin with the specific file by creating the parent directories as well
     *
     * @param plugin   plugin to which the config should belong
     * @param file     file which represents the config
     * @param defaults configuration file which contains the default values
     */
    public CustomConfig(@NotNull PaperPlugin plugin, @NotNull File file, @Nullable File defaults) {
        this.plugin = plugin;
        this.file = file;

        try {
            FileUtils.touch(file);
            load(file);
        } catch (IOException | InvalidConfigurationException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load config \"%s\"".formatted(file.getName()), e);
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }

        if (defaults != null) {
            setDefaults(YamlConfiguration.loadConfiguration(defaults));
            save();
        }
    }

    /**
     * Creates a config for the plugin with the specific file name in the plugin data folder
     *
     * @param plugin   plugin to which the config should belong
     * @param file     file which represents the config
     * @param defaults default values which will be added if not already set
     * @see CustomConfig#CustomConfig(PaperPlugin, File, File)
     */
    public CustomConfig(@NotNull PaperPlugin plugin, @NotNull File file, @Nullable Map<String, Object> defaults) {
        this(plugin, file, (File) null);
        if (defaults != null) {
            addDefaults(defaults);
            save();
        }
    }

    /**
     * Creates a config for the plugin with the specific file name in the plugin data folder
     *
     * @param plugin   plugin to which the config should belong
     * @param fileName filename of the config
     * @param defaults configuration file which contains the default values
     * @see CustomConfig#CustomConfig(PaperPlugin, File, File)
     */
    public CustomConfig(@NotNull PaperPlugin plugin, @NotNull String fileName, @Nullable File defaults) {
        this(plugin, new File(plugin.getDataFolder(), fileName), defaults);
    }

    /**
     * Creates a config for the plugin with the specific file name in the plugin data folder and copies default values if file
     *
     * @param plugin   plugin to which the config should belong
     * @param fileName filename of the config
     * @param defaults default values which will be added if not already set
     * @see CustomConfig#CustomConfig(PaperPlugin, File, Map)
     */
    public CustomConfig(@NotNull PaperPlugin plugin, @NotNull String fileName, @Nullable Map<String, Object> defaults) {
        this(plugin, new File(plugin.getDataFolder(), fileName), defaults);
    }

    /**
     * Sets the value to the path and saves the config
     *
     * @param path  path to set the value to
     * @param value value to be set to the path
     * @see #save()
     */
    public void setAndSave(@NotNull String path, @Nullable Object value) {
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
            plugin.getLogger().log(Level.SEVERE, "Failed to save config \"%s\"".formatted(file.getName()), e);
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }
    }
}
