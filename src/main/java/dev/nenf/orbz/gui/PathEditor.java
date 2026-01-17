package dev.nenf.orbz.gui;

import dev.nenf.orbz.Orbz;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class PathEditor implements Listener {
    private final Orbz plugin;
    private final Map<UUID, Session> sessions = new HashMap<>();

    public PathEditor(Orbz p) {
        this.plugin = p;
    }

    public void edit(Player p, String nid) {
        sessions.put(p.getUniqueId(), new Session(nid, "select", -1));
        p.sendMessage("Editing node: " + nid + "\nFields: name, lore, material, acts, reqs, maxlevel\nType a field or 'cancel'.");
        p.closeInventory();
    }

    public void create(Player p, int slot) {
        sessions.put(p.getUniqueId(), new Session(null, "create", slot));
        p.sendMessage("Creating node at " + slot + ". Type ID or 'cancel'.");
        p.closeInventory();
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onChat(AsyncChatEvent e) {
        var p = e.getPlayer();
        var s = sessions.remove(p.getUniqueId());
        if (s == null) return;

        e.setCancelled(true);
        var in = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(e.message());

        if (in.equalsIgnoreCase("cancel")) {
            p.sendMessage("Cancelled.");
            Bukkit.getScheduler().runTask(plugin, () -> plugin.gui().open(p, true));
            return;
        }

        if (s.field.equals("select")) {
            var f = in.toLowerCase();
            if (!List.of("name", "lore", "material", "acts", "reqs", "maxlevel").contains(f)) {
                p.sendMessage("Invalid. name, lore, material, acts, reqs, maxlevel");
                sessions.put(p.getUniqueId(), s);
                return;
            }
            sessions.put(p.getUniqueId(), new Session(s.nid, f, -1));
            p.sendMessage("Enter value for " + f + ":");
            return;
        }

        try {
            if (s.field.equals("create")) createNode(p, in, s.slot);
            else update(s.nid, s.field, in);
        } catch (Exception ex) {
            p.sendMessage("Error: " + ex.getMessage());
            sessions.put(p.getUniqueId(), s);
            return;
        }
        
        Bukkit.getScheduler().runTask(plugin, () -> {
            plugin.reload();
            plugin.gui().open(p, true);
        });
    }

    private void createNode(Player p, String id, int slot) {
        var file = new File(plugin.getDataFolder(), "path.yml");
        var cfg = YamlConfiguration.loadConfiguration(file);
        var root = "nodes." + id + ".";
        cfg.set(root + "slot", slot);
        cfg.set(root + "display.material", "PAPER");
        cfg.set(root + "display.name", "<white>" + id + "</white>");
        cfg.set(root + "acts", List.of("say New node " + id + " purchased by {player}"));
        try { cfg.save(file); } catch (IOException ignored) {}
    }

    public void update(String nid, String f, String val) {
        var file = new File(plugin.getDataFolder(), "path.yml");
        var cfg = YamlConfiguration.loadConfiguration(file);
        var root = "nodes." + nid + ".";
        switch (f.toLowerCase()) {
            case "name" -> cfg.set(root + "display.name", val);
            case "lore" -> cfg.set(root + "display.lore", List.of(val.split("\\|")));
            case "material" -> cfg.set(root + "display.material", val.toUpperCase());
            case "acts" -> cfg.set(root + "acts", List.of(val.split("\\|")));
            case "maxlevel" -> cfg.set(root + "max-level", Integer.parseInt(val));
            case "cost" -> cfg.set(root + "cost-orbs", Long.parseLong(val));
            case "slot" -> cfg.set(root + "slot", Integer.parseInt(val));
            case "reqs" -> {
                var list = new ArrayList<Map<String, Object>>();
                for (var p : val.split("\\|")) {
                    var split = p.split(":");
                    if (split.length < 2) continue;
                    var map = new HashMap<String, Object>();
                    var type = split[0].toUpperCase();
                    map.put("type", type);
                    switch (type) {
                        case "CURRENCY" -> {
                            if (split.length >= 3) {
                                map.put("provider", split[1].toUpperCase());
                                map.put("amount", Long.parseLong(split[2]));
                            } else map.put("amount", Long.parseLong(split[1]));
                        }
                        case "PREREQUISITE" -> map.put("id", split[1]);
                        case "ISLAND_LEVEL" -> map.put("level", Long.parseLong(split[1]));
                    }
                    list.add(map);
                }
                cfg.set(root + "reqs", list);
            }
        }
        try { cfg.save(file); } catch (IOException ignored) {}
    }

    private record Session(String nid, String field, int slot) {}
}
