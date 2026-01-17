package dev.nenf.orbz.shop.impl;

import dev.nenf.orbz.Orbz;
import dev.nenf.orbz.shop.ShopCurrency;
import org.bukkit.entity.Player;

@lombok.RequiredArgsConstructor
public class OrbsCurrency implements ShopCurrency {
    private final Orbz plugin;

    @Override public String getId() { return "ORBS"; }

    @Override
    public boolean has(Player p, long amt) {
        var id = plugin.ssb().getIslandId(p);
        return id != null && plugin.ds().orbs(id) >= amt;
    }

    @Override
    public void withdraw(Player p, long amt) {
        var id = plugin.ssb().getIslandId(p);
        if (id != null) plugin.ds().take(id, amt);
    }

    @Override
    public void deposit(Player p, long amt) {
        var id = plugin.ssb().getIslandId(p);
        if (id != null) plugin.ds().add(id, amt);
    }
}
