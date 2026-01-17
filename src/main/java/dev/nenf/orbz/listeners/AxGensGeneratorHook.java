package dev.nenf.orbz.listeners;

import dev.nenf.orbz.Orbz;
import dev.nenf.orbz.axgens.IslandGeneratorTracker;
import dev.nenf.orbz.hooks.Reflect;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

public final class AxGensGeneratorHook {
    private final Orbz plugin;
    private final IslandGeneratorTracker tracker;

    public AxGensGeneratorHook(Orbz p, IslandGeneratorTracker t) {
        this.plugin = p;
        this.tracker = t;
    }

    public void register() {
        if (!plugin.ax().present()) return;

        var pm = Bukkit.getPluginManager();
        reg(pm, "com.artillexstudios.axgens.api.events.GeneratorPostPlaceEvent", this::place);
        reg(pm, "com.artillexstudios.axgens.api.events.GeneratorPickupEvent", this::pick);
    }

    @SuppressWarnings("unchecked")
    private void reg(PluginManager pm, String cls, java.util.function.Consumer<Event> h) {
        try { pm.registerEvent((Class<? extends Event>) Class.forName(cls), new Listener() {}, EventPriority.MONITOR, (l, e) -> h.accept(e), plugin, true); }
        catch (Throwable ignored) {}
    }

    private void place(Event e) {
        var p = Reflect.callAny(e, new String[]{"getPlayer"}, new Class<?>[]{}, new Object[]{});
        var g = Reflect.callAny(e, new String[]{"getGenerator"}, new Class<?>[]{}, new Object[]{});
        if (p instanceof Player pl && g != null) {
            var id = plugin.ssb().getIslandId(pl);
            if (id != null) tracker.add(id, g);
        }
    }

    private void pick(Event e) {
        var p = Reflect.callAny(e, new String[]{"getPlayer"}, new Class<?>[]{}, new Object[]{});
        var g = Reflect.callAny(e, new String[]{"getGenerator"}, new Class<?>[]{}, new Object[]{});
        if (p instanceof Player pl && g != null) {
            var id = plugin.ssb().getIslandId(pl);
            if (id != null) tracker.remove(id, g);
        }
    }
}
