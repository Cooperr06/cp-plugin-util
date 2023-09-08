package de.cooperr.cppluginutil.util;

import de.cooperr.cppluginutil.base.PaperPlugin;
import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Custom config with variable file
 */
public class CustomConfig extends YamlConfiguration {

    private final PaperPlugin plugin;

    private final File file;

    /**
     * Creates a config for the plugin with the specific file name in the plugin data folder and optionally copies the defaults
     *
     * @param plugin   plugin to which the config should belong
     * @param fileName filename of the config (incl. {@code '.yml'}) with<br>
     *                 optionally parent directories (excl. plugin data folder)
     * @param defaults configuration file which contains the default values
     */
    public CustomConfig(@NotNull PaperPlugin plugin, @NotNull String fileName, @Nullable String defaults) {

        this.plugin = plugin;

        file = new File(plugin.getDataFolder(), fileName);
        try {
            FileUtils.touch(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to create config file \"%s\"".formatted(file.getName()), e);
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }
        try {
            load(file);
        } catch (IOException | InvalidConfigurationException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load config file \"%s\"".formatted(file.getName()), e);
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }

        options.copyDefaults(true);
        if (defaults != null) {
            try {
                setDefaults(YamlConfiguration.loadConfiguration(new FileReader(defaults)));
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to load default config file \"%s\"".formatted(defaults), e);
                plugin.getServer().getPluginManager().disablePlugin(plugin);
            }
        }
    }

    /**
     * Creates a config for the plugin in the specific file and optionally copies the defaults as well
     *
     * @param plugin        plugin to which the config should belong
     * @param fileName      filename of the config (incl. {@code '.yml'}) with<br>
     *                      optionally parent directories (excl. plugin data folder)
     * @param defaultConfig whether the default config.yml file of the resources should be copied or not<br>
     *                      (requires a config.yml file in the resource folder)
     */
    public CustomConfig(@NotNull PaperPlugin plugin, @NotNull String fileName, boolean defaultConfig) {
        this(plugin, fileName, null);
        if (defaultConfig) {
            setDefaults(YamlConfiguration.loadConfiguration(plugin.resourceReader("config.yml")));
        }
    }

    /**
     * Sets the value to the path and saves the config
     *
     * @param path  path to set the value to
     * @param value value to be set to the path, null to remove the entry
     * @see CustomConfig#save()
     */
    public void setAndSave(@NotNull String path, @Nullable Object value) {
        set(path, value);
        save();
    }

    /**
     * Saves the config
     *
     * @see FileConfiguration#save(File)
     */
    public void save() {
        try {
            save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save config file \"%s\"".formatted(file.getName()), e);
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }
    }

    @NotNull
    public File file() {
        return file;
    }
}
