package de.cooperr.cppluginutil.connector;

import de.cooperr.cppluginutil.base.PaperPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

/**
 * Connects Java with Discord Webhooks
 */
public class DiscordWebhookConnector {

    private final PaperPlugin plugin;

    private URL webhookUrl;

    /**
     * Tests the url connection
     *
     * @param plugin     plugin to which this connector should belong
     * @param webhookUrl url to the Discord Webhook
     */
    public DiscordWebhookConnector(@NotNull PaperPlugin plugin, @NotNull String webhookUrl) {
        this.plugin = plugin;
        try {
            this.webhookUrl = new URI(webhookUrl).toURL();
        } catch (URISyntaxException | MalformedURLException e) {
            this.webhookUrl = null;
            plugin.getLogger().log(Level.SEVERE, "Failed to connect to Discord Webhook", e);
        }
    }

    /**
     * Sends a specific message to the Discord Webhook
     *
     * @param message message to send
     */
    public void send(@NotNull String message) {
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
            }
        });
    }

    @NotNull
    public URL webhookUrl() {
        return webhookUrl;
    }
}
