package dev.nenf.orbz.shop.impl;

import dev.nenf.orbz.Orbz;
import dev.nenf.orbz.shop.ShopAction;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class CommandAction implements ShopAction {
    private final Orbz plugin;
    private final String cmd;
    private final boolean wide;

    @Override
    public void run(Player p, String id) {
        if (wide && id != null) {
            for (var uid : plugin.ssb().getMemberUuids(id)) {
                var m = Bukkit.getPlayer(uid);
                if (m != null && m.isOnline()) dispatch(m, id);
            }
        } else dispatch(p, id);
    }

    private void dispatch(Player p, String id) {
        var s = cmd.replace("{player}", p.getName())
                   .replace("{uuid}", p.getUniqueId().toString())
                   .replace("{island}", id != null ? id : "");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s);
    }
}
