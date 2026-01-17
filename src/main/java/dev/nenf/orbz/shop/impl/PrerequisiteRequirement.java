package dev.nenf.orbz.shop.impl;

import dev.nenf.orbz.Orbz;
import dev.nenf.orbz.shop.ShopRequirement;
import org.bukkit.entity.Player;

@lombok.RequiredArgsConstructor
public class PrerequisiteRequirement implements ShopRequirement {
    private final Orbz plugin;
    private final String nid;

    @Override
    public boolean meets(Player p, String id) {
        if (id == null) return false;
        var n = plugin.gui().config().nodes().get(nid);
        return n == null || plugin.us().level(id, n) >= n.max();
    }

    @Override public String failMsg() { return "Requires previous nodes"; }
}
