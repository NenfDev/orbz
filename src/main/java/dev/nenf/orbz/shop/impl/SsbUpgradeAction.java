package dev.nenf.orbz.shop.impl;

import dev.nenf.orbz.Orbz;
import dev.nenf.orbz.shop.ShopAction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@lombok.RequiredArgsConstructor
public class SsbUpgradeAction implements ShopAction {
    private final Orbz plugin;
    private final String upg;
    private final int lvl;

    @Override
    public void run(Player p, String id) {
        if (id != null) org.bukkit.Bukkit.dispatchCommand(org.bukkit.Bukkit.getConsoleSender(), "is admin setupgrade " + p.getName() + " " + upg + " " + lvl);
    }
}
