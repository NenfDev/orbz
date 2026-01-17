package dev.nenf.orbz.items;

import dev.nenf.orbz.Orbz;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public final class VoucherListener implements Listener {
    private final Orbz plugin;

    public VoucherListener(Orbz p) {
        this.plugin = p;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent e) {
        if (e.getHand() != EquipmentSlot.HAND || !e.hasItem()) return;

        var p = e.getPlayer();
        var it = e.getItem();
        long amt = plugin.vs().amount(it);
        if (amt <= 0) return;

        var id = plugin.ssb().getIslandId(p);
        if (id == null) {
            plugin.msg().send(p, "no-island");
            return;
        }

        plugin.ds().add(id, amt);
        it.setAmount(it.getAmount() - 1);

        plugin.msg().send(p, "claimed-orbs", java.util.Map.of("amount", String.valueOf(amt)));
        e.setCancelled(true);
    }
}
