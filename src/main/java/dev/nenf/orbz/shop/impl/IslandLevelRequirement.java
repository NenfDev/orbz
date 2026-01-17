package dev.nenf.orbz.shop.impl;

import dev.nenf.orbz.Orbz;
import dev.nenf.orbz.shop.ShopRequirement;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class IslandLevelRequirement implements ShopRequirement {
    private final Orbz plugin;
    private final long req;

    @Override
    public boolean meets(Player p, String id) {
        return id != null && plugin.ssb().getIslandLevel(id) >= req;
    }

    @Override
    public String failMsg() {
        return "Requires Island Level " + req;
    }
}
