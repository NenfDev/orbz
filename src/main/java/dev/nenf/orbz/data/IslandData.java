package dev.nenf.orbz.data;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public final class IslandData {
    private volatile long orbs = 0L;
    private final Map<String, Integer> nodes = new ConcurrentHashMap<>();
    private final Map<BoosterType, Integer> boosters = new ConcurrentHashMap<>();

    public long orbs() {
        return orbs;
    }

    public synchronized void orbs(long orbs) {
        this.orbs = Math.max(0L, orbs);
    }

    public synchronized long add(long amount) {
        if (amount <= 0) return orbs;
        this.orbs = Math.addExact(orbs, amount);
        return orbs;
    }

    public synchronized long take(long amount) {
        if (amount <= 0) return orbs;
        long next = orbs - amount;
        if (next < 0) next = 0;
        this.orbs = next;
        return orbs;
    }

    public int node(String nodeId) {
        return nodes.getOrDefault(nodeId, 0);
    }

    public void node(String nodeId, int level) {
        nodes.put(nodeId, Math.max(0, level));
    }

    public Map<String, Integer> nodes() {
        return nodes;
    }

    public int booster(BoosterType type) {
        return boosters.getOrDefault(type, 0);
    }

    public void booster(BoosterType type, int level) {
        boosters.put(type, Math.max(0, level));
    }

    public Map<BoosterType, Integer> boosters() {
        return boosters;
    }
}
