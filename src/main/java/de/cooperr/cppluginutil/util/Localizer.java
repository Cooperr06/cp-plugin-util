package de.cooperr.cppluginutil.util;

import de.cooperr.cppluginutil.base.PaperPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Localizer for localizing messages with parameter arguments
 */
public class Localizer {

    private final PaperPlugin plugin;

    private final Map<Locale, Properties> languageProperties;
    private final Locale defaultLocale;

    /**
     * Sets this localizer up with the given language properties and the default (fallback) locale
     *
     * @param languageProperties map of all supported locales with their properties
     * @param defaultLocale      fallback locale if a query with a non-supported locale occurs (this means that the languageProperties must contain this locale)
     */
    public Localizer(@NotNull PaperPlugin plugin, @NotNull Map<Locale, Properties> languageProperties, @NotNull Locale defaultLocale) {
        this.plugin = plugin;
        this.languageProperties = languageProperties;
        this.defaultLocale = defaultLocale;
    }

    /**
     * Sets this localizer up with the given language properties and the default (fallback) locale
     *
     * @param propertyFiles property files named according to the schema: "<contentType>_<languageCode>.properties"
     * @param defaultLocale fallback locale if a query with a non-supported locale occurs (this means that the languageProperties must contain this locale)
     */
    public Localizer(@NotNull PaperPlugin plugin, @NotNull File[] propertyFiles, @NotNull Locale defaultLocale) {
        this.plugin = plugin;
        this.languageProperties = new HashMap<>();
        this.defaultLocale = defaultLocale;

        for (var propertyFile : propertyFiles) {
            try (var inputStream = new FileInputStream(propertyFile)) {
                var property = new Properties();
                var locale = Locale.of(propertyFile.getName().split("_")[1].substring(0, 2));

                property.load(inputStream);
                languageProperties.put(locale, property);
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to read property file \"%s\"".formatted(propertyFile.getName()), e);
                plugin.getServer().getPluginManager().disablePlugin(plugin);
            } catch (IndexOutOfBoundsException e) {
                plugin.getLogger().log(Level.SEVERE, "Property file \"%s\" is not named correctly".formatted(propertyFile.getName()), e);
                plugin.getServer().getPluginManager().disablePlugin(plugin);
            }
        }
    }

    /**
     * Localizes a message with the given arguments
     *
     * @param key    properties key to identify message to send
     * @param locale locale in which the message should be sent
     * @param args   arguments to replace the placeholders with
     * @return localized message as text component
     */
    @NotNull
    public TextComponent localizeMessage(@NotNull String key, @Nullable Locale locale, @Nullable TextComponent... args) {
        var property = languageProperties.getOrDefault(locale, languageProperties.get(defaultLocale));
        var message = property.getProperty(key);
        var component = Component.empty();

        var plainTextArguments = new HashMap<Integer, String>();
        if (args != null) {
            for (var i = 0; i < args.length; i++) {
                var color = args[i].color() == null ? NamedTextColor.WHITE : args[i].color();
                plainTextArguments.put(i, "%&" + color.examinableName() + "%" + args[i].content());
            }
        }

        var messageParts = message.split("%");
        for (var i = 0; i < messageParts.length; i++) {
            // if there are arguments, replace them
            if (messageParts[i].matches(".*[{]\\d[}].*")) {
                for (int j = 0; j < args.length; j++) {
                    messageParts[i] = messageParts[i].replaceFirst("[{]\\d[}]", plainTextArguments.get(j));
                }
            }

            // checks if the part is a color and then colors the following text until a new color is defined
            if (messageParts[i].startsWith("&")) {
                if (messageParts.length <= i + 1) {
                    continue; // continue if there is no text after the color
                }
                var color = NamedTextColor.NAMES.value(messageParts[i].substring(1).toLowerCase());
                component = component.append(Component.text(messageParts[i + 1], color));
                messageParts[i + 1] = "";
            } else {
                component = component.append(Component.text(messageParts[i]));
            }
        }
        return component;
    }

    @NotNull
    public Map<Locale, Properties> languageProperties() {
        return languageProperties;
    }

    @NotNull
    public Locale defaultLocale() {
        return defaultLocale;
    }
}
