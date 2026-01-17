package dev.nenf.orbz.listeners;

import dev.nenf.orbz.Orbz;
import dev.nenf.orbz.data.BoosterType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;

public final class ExpBoosterListener implements Listener {
    private final Orbz plugin;

    public ExpBoosterListener(Orbz p) {
        this.plugin = p;
    }

    @EventHandler(ignoreCancelled = true)
    public void onExp(PlayerExpChangeEvent e) {
        var p = e.getPlayer();
        var id = plugin.ssb().getIslandId(p);
        if (id == null) return;

        double m = plugin.ds().multiplier(id, BoosterType.EXP);
        if (m <= 1.00001) return;

        e.setAmount(Math.max(0, (int) Math.floor(e.getAmount() * m)));
    }
}
