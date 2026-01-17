package dev.nenf.orbz.hooks;

import dev.nenf.orbz.Orbz;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class OrbzExpansion extends PlaceholderExpansion {
    private final Orbz plugin;

    public OrbzExpansion(Orbz p) { this.plugin = p; }

    @Override public @NotNull String getIdentifier() { return "orbz"; }
    @Override public @NotNull String getAuthor() { return "Nenf"; }
    @Override public @NotNull String getVersion() { return plugin.getDescription().getVersion(); }
    @Override public boolean persist() { return true; }

    @Override
    public String onPlaceholderRequest(Player p, @NotNull String params) {
        if (p == null) return "";
        var id = plugin.ssb().getIslandId(p);
        if (id == null) return "0";

        if (params.equals("balance")) return String.valueOf(plugin.ds().orbs(id));
        if (params.startsWith("node_level_")) return String.valueOf(plugin.ds().node(id, params.substring(11)));
        if (params.startsWith("booster_")) {
            var t = dev.nenf.orbz.data.BoosterType.parse(params.substring(8));
            if (t != null) return String.format("%.2f", plugin.ds().multiplier(id, t));
        }
        return null;
    }
}
