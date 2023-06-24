package de.cooperr.cppluginutil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

/**
 * Connects Java with Discord Webhooks
 *
 * @param <T> plugin to which this connector should belong
 */
public class DiscordWebhook<T extends PaperPlugin> {

    private final T plugin;

    private URL webhookUrl;

    /**
     * Tests the url connection
     *
     * @param plugin     plugin to which this connector should belong
     * @param webhookUrl url to the Discord Webhook
     */
    public DiscordWebhook(@NotNull T plugin, @NotNull String webhookUrl) {
        this.plugin = plugin;
        try {
            this.webhookUrl = new URL(webhookUrl);
        } catch (MalformedURLException e) {
            this.webhookUrl = null;
            plugin.getLogger().log(Level.SEVERE, "Failed to connect to Discord Webhook", e);
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }
    }

    /**
     * Sends a specific message to the Discord Webhook
     *
     * @param message message to send
     */
    public void send(String message) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                var connection = (HttpURLConnection) webhookUrl.openConnection();
                var payload = "{\"content\":\"" + message + "\"}";

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                try (var outputStream = connection.getOutputStream()) {
                    var input = payload.getBytes(StandardCharsets.UTF_8);
                    outputStream.write(input, 0, input.length);
                    outputStream.flush();
                }
                connection.getInputStream(); // this needs to be done in order to complete the request-response cycle
                connection.disconnect();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to send message to Discord Webhook", e);
                plugin.getServer().getPluginManager().disablePlugin(plugin);
            }
        });
    }

    public URL getWebhookUrl() {
        return webhookUrl;
    }
}
