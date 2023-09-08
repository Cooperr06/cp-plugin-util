package de.cooperr.cppluginutil.base;

import de.cooperr.cppluginutil.challenge.Challenge;
import de.cooperr.cppluginutil.command.PaperCommand;
import de.cooperr.cppluginutil.util.CustomConfig;
import de.cooperr.cppluginutil.util.Localizer;
import de.cooperr.cppluginutil.util.Timer;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.management.openmbean.InvalidKeyException;
import javax.management.openmbean.KeyAlreadyExistsException;
import java.io.Reader;
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
     * Registers all challenges by instantiating every {@link Challenge}<br>
     * Register a challenge by calling its constructor.
     *
     * @see Challenge#Challenge(PaperPlugin)
     */
    protected void challengeRegistration() {
    }

    /**
     * Registers all commands by instantiating every {@link PaperCommand}<br>
     * Register a command by calling its constructor.
     *
     * @see PaperCommand#PaperCommand(PaperPlugin)
     */
    protected void commandRegistration() {
    }

    /**
     * Registers all listeners by instantiating every {@link PaperListener}<br>
     * Register a listener by calling its constructor.
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
     */
    public <T extends PaperPlugin> void registerChallenge(@NotNull Challenge<T> challenge) {
        if (challenges.stream().anyMatch(registeredChallenge -> registeredChallenge.name().equals(challenge.name()))) {
            throw new KeyAlreadyExistsException("Challenge \"%s\" is already registered".formatted(challenge.name()));
        }
        challenges.add(challenge);
    }

    /**
     * Registers the specific command to this plugin
     *
     * @param command command to register
     * @param <T>     plugin to which the command should be registered
     */
    public <T extends PaperPlugin> void registerCommand(@NotNull PaperCommand<T> command) {
        var pluginCommand = getCommand(command.name());
        if (pluginCommand == null) {
            throw new IllegalArgumentException("Failed to register command \"%s\"".formatted(command.name()));
        }
        pluginCommand.setExecutor(command);
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

    /**
     * Returns the reader for the resource file
     *
     * @param fileName file name of the resource (optionally with parent directories as well,<br>
     *                 without {@code '.'} and {@code '/resources'})
     * @return reader for the resource file
     * @see JavaPlugin#getTextResource(String)
     */
    @NotNull
    public Reader resourceReader(@NotNull String fileName) {
        return getTextResource(fileName);
    }

    /**
     * Gets a challenge by its unique name
     *
     * @param name name of the challenge
     * @return challenge with the given name or null if there is no challenge with the given name
     */
    @NotNull
    public Challenge<? extends PaperPlugin> challengeByName(@NotNull String name) {
        var challengeOptional = challenges.stream().filter(challenge -> challenge.name().equals(name)).findFirst();
        if (challengeOptional.isEmpty()) {
            throw new InvalidKeyException("Challenge \"%s\" does not exist".formatted(name));
        }
        return challengeOptional.get();
    }

    @NotNull
    public List<Challenge<? extends PaperPlugin>> activeChallenges() {
        return challenges.stream().filter(Challenge::active).toList();
    }

    @NotNull
    public List<Challenge<? extends PaperPlugin>> challenges() {
        return challenges;
    }

    @Nullable
    public CustomConfig customConfig() {
        return config;
    }

    @Nullable
    public Timer timer() {
        return timer;
    }

    @Nullable
    public Localizer localizer() {
        return localizer;
    }
}
