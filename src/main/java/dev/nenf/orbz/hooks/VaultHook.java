package dev.nenf.orbz.hooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook {
    private final boolean present;
    private Object econ;

    public VaultHook() {
        this.present = Bukkit.getPluginManager().getPlugin("Vault") != null;
        if (present) {
            try {
                var rsp = Bukkit.getServicesManager().getRegistration(Class.forName("net.milkbowl.vault.economy.Economy"));
                if (rsp != null) this.econ = rsp.getProvider();
            } catch (Throwable ignored) {}
        }
    }

    public boolean present() { return present && econ != null; }

    public boolean has(Player p, double amt) {
        if (!present()) return false;
        var res = Reflect.call(econ, "has", new Class<?>[]{org.bukkit.OfflinePlayer.class, double.class}, new Object[]{p, amt});
        return res instanceof Boolean b && b;
    }

    public void withdraw(Player p, double amt) {
        if (present()) Reflect.call(econ, "withdrawPlayer", new Class<?>[]{org.bukkit.OfflinePlayer.class, double.class}, new Object[]{p, amt});
    }

    public void deposit(Player p, double amt) {
        if (present()) Reflect.call(econ, "depositPlayer", new Class<?>[]{org.bukkit.OfflinePlayer.class, double.class}, new Object[]{p, amt});
    }
}
