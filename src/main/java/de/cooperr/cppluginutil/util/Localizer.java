package de.cooperr.cppluginutil.util;

import de.cooperr.cppluginutil.base.PaperPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Localizer for localizing messages with parameter arguments.<br>
 * Colors can be added in the properties files by using the following format: {@literal %&}COLOR{@literal %}Colored text
 */
public class Localizer {

    private final PaperPlugin plugin;

    private final Locale defaultLocale;
    private final Map<Locale, Properties> languageProperties;

    /**
     * Sets this localizer up with the given language properties and the default (fallback) locale
     *
     * @param plugin             plugin to which this localizer should belong
     * @param defaultLocale      fallback locale if a query with a non-supported locale occurs<br>
     *                           (this means that the languageProperties must contain this locale)
     * @param languageProperties map of all supported locales with their properties
     */
    public Localizer(@NotNull PaperPlugin plugin, @NotNull Locale defaultLocale, @NotNull Map<Locale, Properties> languageProperties) {
        this.plugin = plugin;
        this.defaultLocale = defaultLocale;
        this.languageProperties = languageProperties;
    }

    /**
     * Sets this localizer up with the given language property file names and the default (fallback) locale
     *
     * @param plugin            plugin to which this localizer should belong
     * @param defaultLocale     fallback locale if a query with a non-supported locale occurs<br>
     *                          (this means that the languageProperties must contain this locale)
     * @param propertyFileNames property file names named according to the scheme:<br>
     *                          {@code contentType_languageCode.properties} (optionally with parent directories except<br>
     *                          {@code '/resources'}; directories without {@code '.'})
     */
    public Localizer(@NotNull PaperPlugin plugin, @NotNull Locale defaultLocale, @NotNull String... propertyFileNames) {
        this.plugin = plugin;
        this.defaultLocale = defaultLocale;
        this.languageProperties = new HashMap<>();

        for (var propertyFileName : propertyFileNames) {
            try {
                var property = new Properties();
                var propertyLocale = Locale.of(propertyFileName.split("_")[1].substring(0, 2));

                property.load(plugin.resourceReader(propertyFileName));
                languageProperties.put(propertyLocale, property);
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to load property file \"%s\"".formatted(propertyFileName), e);
                plugin.getServer().getPluginManager().disablePlugin(plugin);
            } catch (IndexOutOfBoundsException e) {
                plugin.getLogger().log(Level.SEVERE, "Property file \"%s\" is not named correctly".formatted(propertyFileName), e);
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
    public TextComponent localizeMessage(@NotNull String key, @Nullable Locale locale, @Nullable String... args) {
        var property = languageProperties.getOrDefault(locale == null ? null : Locale.of(locale.getLanguage()), languageProperties.get(defaultLocale));
        var message = property.getProperty(key);
        if (message == null) {
            plugin.getLogger().severe("The key \"%s\" is not defined in the language properties!".formatted(key));
            return Component.empty();
        }
        message = MessageFormat.format(message, (Object[]) args);

        var component = Component.empty();

        var messageParts = message.split("%");
        for (var i = 0; i < messageParts.length; i++) {
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
    public Locale defaultLocale() {
        return defaultLocale;
    }

    @NotNull
    public Map<Locale, Properties> languageProperties() {
        return languageProperties;
    }
}
