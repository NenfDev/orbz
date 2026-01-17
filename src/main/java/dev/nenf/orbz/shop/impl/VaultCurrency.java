package dev.nenf.orbz.shop.impl;

import dev.nenf.orbz.Orbz;
import dev.nenf.orbz.shop.ShopCurrency;
import org.bukkit.entity.Player;

@lombok.RequiredArgsConstructor
public class VaultCurrency implements ShopCurrency {
    private final Orbz plugin;

    @Override public String getId() { return "VAULT"; }
    @Override public boolean has(Player p, long amt) { return plugin.v().has(p, amt); }
    @Override public void withdraw(Player p, long amt) { plugin.v().withdraw(p, amt); }
    @Override public void deposit(Player p, long amt) { plugin.v().deposit(p, amt); }
}
