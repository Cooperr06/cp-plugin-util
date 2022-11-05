package de.cooperr.cppluginutil;

import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

/**
 * Custom listener which has some utility methods
 */
public abstract class PaperListener<T1 extends PaperPlugin, T2 extends Event> implements Listener {
    
    protected final T1 plugin;
    
    /**
     * Registers the listener to the plugin
     *
     * @param plugin plugin to register the listener to
     */
    public PaperListener(@NotNull T1 plugin) {
        this.plugin = plugin;
        
        plugin.registerListener(this);
    }
    
    /**
     * Handles the T event
     *
     * @param event event to handle
     */
    @EventHandler
    public abstract void onEvent(T2 event);
}
