package de.cooperr.cppluginutil.challenge;

import de.cooperr.cppluginutil.base.PaperPlugin;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Challenge with a goal and a possible fail
 *
 * @param <T> plugin to which this challenge should belong
 */
public abstract class Challenge<T extends PaperPlugin> {

    protected final T plugin;

    protected boolean active = false;

    /**
     * Registers the goal and fail listeners to the plugin
     *
     * @param plugin plugin to which this challenge should belong
     */
    public Challenge(@NotNull T plugin) {
        this.plugin = plugin;

        plugin.registerChallenge(this);
        plugin.getServer().getPluginManager().registerEvents(goalListener(), plugin);
        if (failListener() != null) {
            plugin.getServer().getPluginManager().registerEvents(failListener(), plugin);
        }
    }

    /**
     * Creates the listener which checks for the players reaching the goal
     *
     * @return listener which checks for the players reaching the goal
     */
    @NotNull
    public abstract Listener goalListener();

    /**
     * Creates the listener which checks for the players failing the challenge
     *
     * @return listener which checks for the players failing the challenge, may be null if this challenge cannot fail
     */
    @Nullable
    public Listener failListener() {
        return null;
    }

    /**
     * Performs the actions if the challenge goal was reached
     */
    public abstract void reachedGoal();

    /**
     * Performs the actions if the challenge failed
     */
    public abstract void failed();

    /**
     * @return challenge name
     */
    public abstract String name();

    public boolean active() {
        return active;
    }

    public void active(boolean active) {
        this.active = active;
    }
}
