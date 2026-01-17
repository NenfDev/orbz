package dev.nenf.orbz.api;

import dev.nenf.orbz.Orbz;
import dev.nenf.orbz.data.BoosterType;
import org.bukkit.entity.Player;

public final class IslandBoosters {

    private static Orbz plugin;

    private IslandBoosters() {}

    public static void init(Orbz pl) {
        plugin = pl;
    }

    public static double getMultiplier(Player p, BoosterType t) {
        if (plugin == null || p == null) return 1.0;
        var id = plugin.ssb().getIslandId(p);
        return id == null ? 1.0 : plugin.ds().multiplier(id, t);
    }
}
