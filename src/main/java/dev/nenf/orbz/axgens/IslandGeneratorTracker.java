package dev.nenf.orbz.axgens;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class IslandGeneratorTracker {
    private final ConcurrentMap<String, Set<Object>> cache = new ConcurrentHashMap<>();

    public void add(String id, Object g) {
        if (id != null && g != null) cache.computeIfAbsent(id, k -> ConcurrentHashMap.newKeySet()).add(g);
    }

    public boolean hasGenerators(String id) {
        var s = id == null ? null : cache.get(id);
        return s != null && !s.isEmpty();
    }

    public void remove(String id, Object g) {
        if (id == null || g == null) return;
        var s = cache.get(id);
        if (s != null && s.remove(g) && s.isEmpty()) cache.remove(id);
    }

    public Collection<Object> get(String id) {
        var s = id == null ? null : cache.get(id);
        return s == null ? Collections.emptyList() : Set.copyOf(s);
    }

    public void clearIsland(String id) {
        if (id != null) cache.remove(id);
    }
}
