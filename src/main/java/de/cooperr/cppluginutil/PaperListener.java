package de.cooperr.cppluginutil;

import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

/**
 * Custom listener which has some utility methods
 *
 * @param <T1> plugin to which this listener should belong
 * @param <T2> event to which this listener should listen
 */
public abstract class PaperListener<T1 extends PaperPlugin, T2 extends Event> implements Listener {
    
    protected final T1 plugin;

    /**
     * Registers this listener to the plugin
     *
     * @param plugin plugin to which this listener should belong
     */
    public PaperListener(@NotNull T1 plugin) {
        this.plugin = plugin;
        
        plugin.registerListener(this);
    }

    /**
     * Handles the event <b>T</b>. Needs to get annotated with {@link org.bukkit.event.EventHandler EventHandler}!
     *
     * @param event event to handle
     */
    public abstract void onEvent(T2 event);
}
