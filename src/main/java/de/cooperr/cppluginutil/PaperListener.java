package de.cooperr.cppluginutil;

import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public abstract class PaperListener<T extends Event> implements Listener {
    
    protected final PaperPlugin plugin;
    
    public PaperListener(@NotNull PaperPlugin plugin) {
        this.plugin = plugin;
        
        plugin.registerListener(this);
    }
    
    @EventHandler
    public abstract void onEvent(T event);
}
