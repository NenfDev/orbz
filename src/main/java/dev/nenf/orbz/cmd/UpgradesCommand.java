package dev.nenf.orbz.cmd;

import dev.nenf.orbz.Orbz;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class UpgradesCommand implements CommandExecutor, TabCompleter {
    private final Orbz plugin;

    public UpgradesCommand(Orbz p) {
        this.plugin = p;
    }

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String l, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("help")) {
            help(s);
            return true;
        }

        if (!(s instanceof Player p)) {
            s.sendMessage("Players only.");
            return true;
        }

        var edit = args.length > 0 && args[0].equalsIgnoreCase("edit");
        var perm = edit ? "orbz.upgrades.edit" : "orbz.command.upgrades";
        if (!s.hasPermission(perm)) {
            plugin.msg().send(s, "no-permission");
            return true;
        }

        plugin.gui().open(p, edit);
        return true;
    }

    private void help(CommandSender s) {
        s.sendMessage("§b--- Upgrades Help ---");
        s.sendMessage("§f/upgrades §7- Open upgrades");
        if (s.hasPermission("orbz.upgrades.edit")) s.sendMessage("§f/upgrades edit §7- Editor mode");
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command cmd, String a, String[] args) {
        if (args.length != 1) return List.of();
        var out = new ArrayList<String>();
        var in = args[0].toLowerCase();
        if (s.hasPermission("orbz.upgrades.edit") && "edit".startsWith(in)) out.add("edit");
        if ("help".startsWith(in)) out.add("help");
        return out;
    }
}
