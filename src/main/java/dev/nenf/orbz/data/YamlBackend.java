package dev.nenf.orbz.data;

import dev.nenf.orbz.Orbz;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class YamlBackend implements DataBackend {
    private final Orbz plugin;
    private final File file;
    private final YamlConfiguration cfg;

    public YamlBackend(Orbz p) {
        this.plugin = p;
        this.file = new File(p.getDataFolder(), p.getConfig().getString("storage.file", "data.yml"));
        this.cfg = YamlConfiguration.loadConfiguration(file);
    }

    @Override
    public void load(Map<String, IslandData> cache) {
        var root = cfg.getConfigurationSection("islands");
        if (root == null) return;

        for (var id : root.getKeys(false)) {
            var s = root.getConfigurationSection(id);
            if (s == null) continue;

            var d = new IslandData();
            d.orbs(s.getLong("orbs", 0L));

            var nodes = s.getConfigurationSection("nodes");
            if (nodes != null) nodes.getKeys(false).forEach(k -> d.node(k, nodes.getInt(k)));

            var boosters = s.getConfigurationSection("boosters");
            if (boosters != null) boosters.getKeys(false).forEach(k -> {
                var t = BoosterType.parse(k);
                if (t != null) d.booster(t, boosters.getInt(k));
            });
            cache.put(id, d);
        }
    }

    @Override
    public void save(String id, IslandData d) {
        var root = cfg.getConfigurationSection("islands");
        if (root == null) root = cfg.createSection("islands");
        var s = root.getConfigurationSection(id);
        if (s == null) s = root.createSection(id);

        s.set("orbs", d.orbs());
        var nodes = s.createSection("nodes");
        d.nodes().forEach(nodes::set);

        var boosters = s.createSection("boosters");
        d.boosters().forEach((t, lvl) -> boosters.set(t.name(), lvl));

        try {
            cfg.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Save failed: " + e.getMessage());
        }
    }

    @Override
    public void remove(String id) {
        var root = cfg.getConfigurationSection("islands");
        if (root == null) return;
        root.set(id, null);
        try {
            cfg.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Save failed: " + e.getMessage());
        }
    }
}
