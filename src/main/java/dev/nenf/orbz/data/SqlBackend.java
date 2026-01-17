package dev.nenf.orbz.data;

import dev.nenf.orbz.Orbz;

import java.util.Map;

public class SqlBackend implements DataBackend {
    private final Orbz plugin;

    public SqlBackend(Orbz p) {
        this.plugin = p;
        setup();
    }

    private void setup() {
        plugin.getLogger().severe("SQL disabled. Use YAML.");
    }

    @Override public void load(Map<String, IslandData> cache) {}
    @Override public void save(String id, IslandData d) {}
    @Override public void remove(String id) {}
    @Override public void close() {}
}
