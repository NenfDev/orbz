package dev.nenf.orbz.data;

import dev.nenf.orbz.Orbz;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public final class IslandDataStore {
    private final Orbz plugin;
    private DataBackend backend;

    private final Map<String, IslandData> cache = new ConcurrentHashMap<>();

    public IslandDataStore(Orbz plugin) {
        this.plugin = plugin;
        setupBackend();
        reload();
    }

    private void setupBackend() {
        var type = plugin.getConfig().getString("storage.type", "YAML").toUpperCase();
        backend = type.equals("YAML") ? new YamlBackend(plugin) : new SqlBackend(plugin);
    }

    public void reload() {
        cache.clear();
        backend.load(cache);
    }

    public void flush() {
        cache.forEach((id, d) -> backend.save(id, d));
    }

    public void close() {
        flush();
        backend.close();
    }

    public void saveAsync(String id) {
        var d = cache.get(id);
        if (d == null) return;
        org.bukkit.Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> backend.save(id, d));
    }

    public boolean hasData(String id) {
        return cache.containsKey(id);
    }

    public IslandData get(String id) {
        return cache.computeIfAbsent(id, k -> new IslandData());
    }

    public void remove(String id) {
        cache.remove(id);
        org.bukkit.Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> backend.remove(id));
    }

    public long orbs(String id) {
        return get(id).orbs();
    }

    public void orbs(String id, long amt) {
        get(id).orbs(amt);
        saveAsync(id);
    }

    public void add(String id, long amt) {
        get(id).add(amt);
        saveAsync(id);
    }

    public boolean take(String id, long amt) {
        var d = get(id);
        if (d.orbs() < amt) return false;
        d.take(amt);
        saveAsync(id);
        return true;
    }

    public int node(String id, String nodeId) {
        return get(id).node(nodeId);
    }

    public void node(String id, String nodeId, int lvl) {
        get(id).node(nodeId, lvl);
        saveAsync(id);
    }

    public int booster(String id, BoosterType t) {
        return get(id).booster(t);
    }

    public void booster(String id, BoosterType t, int lvl) {
        get(id).booster(t, lvl);
        saveAsync(id);
    }

    public double multiplier(String id, BoosterType t) {
        int lvl = booster(id, t);
        double per = plugin.getConfig().getDouble("boosters." + t.name() + ".per-level", 0.0);
        return 1.0 + (lvl * per);
    }
}
