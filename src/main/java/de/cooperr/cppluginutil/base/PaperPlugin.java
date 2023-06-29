package de.cooperr.cppluginutil.base;

import de.cooperr.cppluginutil.challenge.Challenge;
import de.cooperr.cppluginutil.util.CustomConfig;
import de.cooperr.cppluginutil.util.Localizer;
import de.cooperr.cppluginutil.util.Timer;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the java plugin with some utility methods
 */
public abstract class PaperPlugin extends JavaPlugin {

    protected final List<Challenge<? extends PaperPlugin>> challenges = new ArrayList<>();

    protected CustomConfig config;
    protected Timer timer;
    protected Localizer localizer;

    /**
     * Registers all challenges by instantiating every {@link Challenge}.<br>
     * Register a new challenge via calling its constructor.
     */
    protected void challengeRegistration() {
    }

    /**
     * Registers all commands by instantiating every {@link PaperCommand}.<br>
     * Register a new command via calling its constructor.
     *
     * @see Challenge#Challenge(PaperPlugin)
     */
    protected void commandRegistration() {
    }

    /**
     * Registers all listeners by instantiating every {@link PaperListener}.<br>
     * Register a new listener via calling its constructor.
     *
     * @see PaperListener#PaperListener(PaperPlugin)
     */
    protected void listenerRegistration() {
    }

    /**
     * Registers the specific challenge to this plugin
     *
     * @param challenge challenge to register
     * @param <T>       plugin to which the challenge should be registered
     * @see PaperCommand#PaperCommand(PaperPlugin)
     */
    public <T extends PaperPlugin> void registerChallenge(@NotNull Challenge<T> challenge) {
        challenges.add(challenge);
    }

    /**
     * Registers the specific command to this plugin
     *
     * @param command command to register
     * @param <T>     plugin to which the command should be registered
     */
    public <T extends PaperPlugin> void registerCommand(@NotNull PaperCommand<T> command) {
        var pluginCommand = getCommand(command.commandName());
        if (pluginCommand == null) {
            getLogger().severe("Failed to register command \"%s\"".formatted(command.commandName()));
            getServer().getPluginManager().disablePlugin(this);
        } else {
            pluginCommand.setExecutor(command);
        }
    }

    /**
     * Registers the specific listener to this plugin
     *
     * @param listener listener to register
     * @param <T1>     plugin to which the listener should be registered
     * @param <T2>     event to which the listener should listen
     */
    public <T1 extends PaperPlugin, T2 extends Event> void registerListener(@NotNull PaperListener<T1, T2> listener) {
        getServer().getPluginManager().registerEvents(listener, this);
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

    @NotNull
    public List<Challenge<? extends PaperPlugin>> activeChallenges() {
        return challenges.stream().filter(Challenge::active).toList();
    }

    @NotNull
    public List<Challenge<? extends PaperPlugin>> challenges() {
        return challenges;
    }

    public CustomConfig customConfig() {
        return config;
    }

    public Timer timer() {
        return timer;
    }

    public Localizer localizer() {
        return localizer;
    }
}
