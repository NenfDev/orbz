package dev.nenf.orbz.shop;

import dev.nenf.orbz.Orbz;
import dev.nenf.orbz.gui.PathNode;
import dev.nenf.orbz.shop.impl.CurrencyRequirement;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.Map;

public final class UpgradeService {
    private final Orbz plugin;

    public UpgradeService(Orbz p) {
        this.plugin = p;
    }

    public boolean met(Player p, String id, PathNode n) {
        for (var r : n.reqs) {
            if (r instanceof CurrencyRequirement) continue;
            if (!r.meets(p, id)) return false;
        }
        return true;
    }

    public int level(String id, PathNode n) {
        var ssb = n.ssb();
        return ssb != null ? plugin.ssb().getUpgradeLevel(id, ssb) : plugin.ds().node(id, n.id);
    }

    public boolean bought(String id, PathNode n) {
        return level(id, n) >= n.max;
    }

    public boolean buy(Player p, PathNode n) {
        var id = plugin.ssb().getIslandId(p);
        if (id == null) {
            plugin.msg().send(p, "no-island");
            return false;
        }

        for (var r : n.reqs) {
            if (!r.meets(p, id)) {
                if (r.failMsg() != null) {
                    p.sendMessage(MiniMessage.miniMessage().deserialize("<red>" + r.failMsg() + "</red>"));
                }
                return false;
            }
        }

        int lvl = level(id, n);
        if (lvl >= n.max) return false;

        for (var r : n.reqs) {
            if (r instanceof CurrencyRequirement cr) cr.consume(p);
        }

        for (var a : n.acts) a.run(p, id);

        p.playSound(p.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
        p.spawnParticle(org.bukkit.Particle.HAPPY_VILLAGER, p.getLocation().add(0, 1, 0), 20, 0.5, 0.5, 0.5, 0.1);

        if (n.ssb() == null) plugin.ds().node(id, n.id, lvl + 1);

        plugin.msg().send(p, "node-purchased", Map.of("node", n.id));
        return true;
    }
}
