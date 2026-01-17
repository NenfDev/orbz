package dev.nenf.orbz.listeners;

import dev.nenf.orbz.Orbz;
import dev.nenf.orbz.hooks.Reflect;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class SsbDisbandHook {
    private final Orbz plugin;

    public SsbDisbandHook(Orbz p) {
        this.plugin = p;
    }

    public void register() {
        if (!plugin.ssb().present()) return;
        try {
            var cls = (Class<? extends Event>) Class.forName("com.bgsoftware.superiorskyblock.api.events.IslandDisbandEvent");
            Bukkit.getPluginManager().registerEvent(cls, new Listener() {}, EventPriority.NORMAL, (l, e) -> handle(e), plugin, true);
        } catch (Throwable ignored) {}
    }

    private void handle(Event e) {
        var isl = Reflect.callAny(e, new String[]{"getIsland"}, new Class<?>[]{}, new Object[]{});
        if (isl == null) return;

        var uid = Reflect.callAny(isl, new String[]{"getUniqueId", "getUUID", "getIslandUUID", "getId"}, new Class<?>[]{}, new Object[]{});
        var id = uid == null ? String.valueOf(isl) : uid.toString();

        if (!plugin.ds().hasData(id) && !plugin.tracker().hasGenerators(id)) return;

        var gens = plugin.tracker().get(id);
        if (gens != null && !gens.isEmpty()) {
            var pObj = Reflect.callAny(e, new String[]{"getPlayer"}, new Class<?>[]{}, new Object[]{});
            var p = pObj instanceof Player pl ? pl : null;
            
            var list = new ArrayList<>(gens);
            var items = new ArrayList<org.bukkit.inventory.ItemStack>();
            for (var g : list) {
                var it = plugin.ax().item(g);
                if (it != null) items.add(it);
                plugin.ax().delete(g);
            }

            if (p != null && p.isOnline() && !items.isEmpty()) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (!p.isOnline()) return;
                    for (var it : items) {
                        p.getInventory().addItem(it).values().forEach(rem -> p.getWorld().dropItem(p.getLocation(), rem));
                    }
                    plugin.msg().send(p, "picked-up-gens", java.util.Map.of("count", String.valueOf(items.size())));
                }, 1L);
            }
        }

        plugin.ds().remove(id);
        plugin.tracker().clearIsland(id);
    }
}
