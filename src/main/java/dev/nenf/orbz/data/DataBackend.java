package dev.nenf.orbz.data;

import java.util.Map;

public interface DataBackend {
    void load(Map<String, IslandData> cache);
    void save(String islandId, IslandData data);
    void remove(String islandId);
    default void close() {}
}
