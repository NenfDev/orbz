package dev.nenf.orbz.cmd;

import dev.nenf.orbz.Orbz;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class PickupAllGensCommand implements CommandExecutor, TabCompleter {
    private final Orbz plugin;

    public PickupAllGensCommand(Orbz p) {
        this.plugin = p;
    }

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String l, String[] args) {
        if (!(s instanceof Player p)) {
            s.sendMessage("Players only.");
            return true;
        }

        if (!p.hasPermission("orbz.pickupallgens")) {
            plugin.msg().send(p, "no-permission");
            return true;
        }

        var id = plugin.ssb().getIslandId(p);
        if (id == null) {
            plugin.msg().send(p, "no-island");
            return true;
        }

        var gens = plugin.tracker().get(id);
        if (gens.isEmpty()) {
            plugin.msg().send(p, "no-gens");
            return true;
        }

        int count = 0;
        for (var g : new ArrayList<>(gens)) {
            plugin.ax().remove(p, g);
            count++;
        }

        plugin.msg().send(p, "picked-up-gens", java.util.Map.of("count", String.valueOf(count)));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command cmd, String a, String[] args) {
        return List.of();
    }
}
