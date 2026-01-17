package dev.nenf.orbz.cmd;

import dev.nenf.orbz.Orbz;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class OrbsCommand implements CommandExecutor, TabCompleter {
    private final Orbz plugin;

    public OrbsCommand(Orbz p) {
        this.plugin = p;
    }

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String l, String[] args) {
        if (args.length == 0) { help(s); return true; }

        var sub = args[0].toLowerCase();
        if (sub.equals("reload")) {
            if (!s.hasPermission("orbz.admin.reload")) return fail(s);
            plugin.reload();
            s.sendMessage("§aReloaded.");
            return true;
        }

        if (sub.equals("balance")) {
            if (!s.hasPermission("orbz.command.orbs")) return fail(s);
            if (!(s instanceof Player p)) return fail(s, "Players only.");
            var id = plugin.ssb().getIslandId(p);
            if (id == null) { plugin.msg().send(p, "no-island"); return true; }
            plugin.msg().send(p, "orb-balance", Map.of("amount", String.valueOf(plugin.ds().orbs(id))));
            return true;
        }

        if (sub.equals("item")) {
            if (!s.hasPermission("orbz.admin.item")) return fail(s);
            if (args.length < 3) return fail(s, "§c/orbs item <p> <amt> [model]");
            var target = Bukkit.getPlayerExact(args[1]);
            if (target == null) return fail(s, "§cOffline.");
            long amt = val(args[2]);
            int model = args.length >= 4 ? (int) val(args[3]) : -1;
            target.getInventory().addItem(plugin.vs().create(amt, model));
            s.sendMessage("§aGave " + amt + " to " + target.getName());
            return true;
        }

        if (sub.equals("give") || sub.equals("take") || sub.equals("set")) {
            if (!s.hasPermission(sub.equals("give") ? "orbz.admin.give" : "orbz.admin.set")) return fail(s);
            if (args.length < 3) return fail(s, "§c/orbs " + sub + " <p> <amt>");
            var target = Bukkit.getPlayerExact(args[1]);
            if (target == null) return fail(s, "§cOffline.");
            var id = plugin.ssb().getIslandId(target);
            if (id == null) return fail(s, "§cNo island.");
            long amt = val(args[2]);

            if (sub.equals("give")) plugin.ds().add(id, amt);
            else if (sub.equals("take")) plugin.ds().orbs(id, Math.max(0, plugin.ds().orbs(id) - amt));
            else plugin.ds().orbs(id, amt);

            s.sendMessage("§aUpdated " + target.getName() + " balance to " + plugin.ds().orbs(id));
            return true;
        }

        if (sub.equals("admin")) {
            if (!s.hasPermission("orbz.admin.edit")) return fail(s);
            if (args.length < 2) return fail(s, "§c/orbs admin <setlevel|setnode|setgui>");
            var asub = args[1].toLowerCase();
            switch (asub) {
                case "setlevel" -> {
                    if (args.length < 5) return fail(s, "§c/orbs admin setlevel <p> <nid> <lvl>");
                    var target = Bukkit.getPlayerExact(args[2]);
                    if (target == null) return fail(s, "§cOffline.");
                    var id = plugin.ssb().getIslandId(target);
                    if (id == null) return fail(s, "§cNo island.");
                    var nid = args[3];
                    int lvl = (int) val(args[4]);
                    var n = plugin.gui().config().nodes().get(nid);
                    if (n != null && n.type() != null) plugin.ds().booster(id, n.type(), lvl);
                    else plugin.ds().node(id, nid, lvl);
                    s.sendMessage("§aSet " + nid + " to " + lvl);
                }
                case "setnode" -> {
                    if (args.length < 5) return fail(s, "§c/orbs admin setnode <nid> <f> <v>");
                    var nid = args[2];
                    var f = args[3];
                    var v = String.join(" ", Arrays.copyOfRange(args, 4, args.length));
                    plugin.edit().update(nid, f, v);
                    plugin.reload();
                    s.sendMessage("§aUpdated " + nid);
                }
                case "setgui" -> {
                    if (args.length < 4) return fail(s, "§c/orbs admin setgui <title|size> <v>");
                    var f = args[2].toLowerCase();
                    var v = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
                    var file = new java.io.File(plugin.getDataFolder(), "config.yml");
                    var cfg = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(file);
                    if (f.equals("title")) cfg.set("gui.title", v);
                    else if (f.equals("size")) cfg.set("gui.size", Integer.parseInt(v));
                    else return fail(s, "§cInvalid.");
                    try { cfg.save(file); } catch (Exception ignored) {}
                    plugin.reload();
                    s.sendMessage("§aUpdated GUI.");
                }
            }
            return true;
        }
        return fail(s, "Unknown.");
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command cmd, String a, String[] args) {
        var out = new ArrayList<String>();
        if (args.length == 1) {
            out.add("balance");
            if (s.hasPermission("orbz.admin.reload")) out.addAll(List.of("give", "take", "set", "item", "reload", "admin"));
            return filter(out, args[0]);
        }
        if (args.length == 2 && args[0].equals("admin")) {
            out.addAll(List.of("setlevel", "setnode", "setgui"));
            return filter(out, args[1]);
        }
        if (args.length == 3 && args[0].equals("admin") && args[1].equals("setnode")) {
            out.addAll(plugin.gui().config().nodes().keySet());
            return filter(out, args[2]);
        }
        return out;
    }

    private void help(CommandSender s) {
        s.sendMessage("§b--- Orbs Help ---\n§f/orbs balance §7- Balance");
        if (s.hasPermission("orbz.admin.give")) s.sendMessage("§f/orbs give/take/set <p> <amt>\n§f/orbs item <p> <amt>\n§f/orbs reload\n§f/orbs admin <setlevel|setnode|setgui>");
    }

    private boolean fail(CommandSender s) { plugin.msg().send(s, "no-permission"); return true; }
    private boolean fail(CommandSender s, String m) { s.sendMessage(m); return true; }
    private long val(String s) { try { return Long.parseLong(s); } catch (Exception e) { return 0; } }
    private List<String> filter(List<String> l, String in) { return l.stream().filter(s -> s.toLowerCase().startsWith(in.toLowerCase())).toList(); }
}
