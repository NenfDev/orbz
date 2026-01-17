package dev.nenf.orbz;

import dev.nenf.orbz.axgens.AxGensBridge;
import dev.nenf.orbz.axgens.IslandGeneratorTracker;
import dev.nenf.orbz.cmd.OrbsCommand;
import dev.nenf.orbz.cmd.PickupAllGensCommand;
import dev.nenf.orbz.cmd.UpgradesCommand;
import dev.nenf.orbz.data.IslandDataStore;
import dev.nenf.orbz.gui.PathEditor;
import dev.nenf.orbz.gui.PathGui;
import dev.nenf.orbz.gui.PathGuiListener;
import dev.nenf.orbz.hooks.OrbzExpansion;
import dev.nenf.orbz.hooks.SsbHook;
import dev.nenf.orbz.hooks.VaultHook;
import dev.nenf.orbz.items.VoucherListener;
import dev.nenf.orbz.items.VoucherService;
import dev.nenf.orbz.listeners.AxGensGeneratorHook;
import dev.nenf.orbz.listeners.ExpBoosterListener;
import dev.nenf.orbz.listeners.SsbDisbandHook;
import dev.nenf.orbz.shop.ShopRegistry;
import dev.nenf.orbz.shop.UpgradeService;
import dev.nenf.orbz.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Orbz extends JavaPlugin {
    private static Orbz inst;
    private IslandDataStore ds;
    private Messages msg;
    private SsbHook ssb;
    private VaultHook v;
    private AxGensBridge ax;
    private ShopRegistry shop;
    private IslandGeneratorTracker tracker;
    private VoucherService vs;
    private PathGui gui;
    private PathEditor edit;
    private UpgradeService us;

    @Override
    public void onEnable() {
        inst = this;
        saveDefaultConfig();
        saveResource("path.yml", false);
        saveResource("messages_en.yml", false);

        msg = new Messages(this);
        ds = new IslandDataStore(this);
        ssb = new SsbHook(this);
        v = new VaultHook();
        ax = new AxGensBridge(this);
        shop = new ShopRegistry(this);
        us = new UpgradeService(this);
        tracker = new IslandGeneratorTracker();
        vs = new VoucherService(this);
        gui = new PathGui(this);
        edit = new PathEditor(this);

        dev.nenf.orbz.api.IslandBoosters.init(this);

        var pm = Bukkit.getPluginManager();
        pm.registerEvents(new VoucherListener(this), this);
        pm.registerEvents(new PathGuiListener(this), this);
        pm.registerEvents(edit, this);
        pm.registerEvents(new ExpBoosterListener(this), this);
        new SsbDisbandHook(this).register();
        new AxGensGeneratorHook(this, tracker).register();

        if (pm.isPluginEnabled("PlaceholderAPI")) {
            new OrbzExpansion(this).register();
        }

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> ds.flush(), 6000L, 6000L);
        checkUpdates();

        var orbs = new OrbsCommand(this);
        getCommand("orbs").setExecutor(orbs);
        getCommand("orbs").setTabCompleter(orbs);

        var upg = new UpgradesCommand(this);
        getCommand("upgrades").setExecutor(upg);
        getCommand("upgrades").setTabCompleter(upg);

        var pick = new PickupAllGensCommand(this);
        getCommand("pickupallgens").setExecutor(pick);
        getCommand("pickupallgens").setTabCompleter(pick);
    }

    @Override
    public void onDisable() {
        if (ds != null) ds.close();
    }

    private void checkUpdates() {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> 
            getLogger().info("v" + getDescription().getVersion()));
    }

    public static Orbz get() { return inst; }
    public IslandDataStore ds() { return ds; }
    public Messages msg() { return msg; }
    public SsbHook ssb() { return ssb; }
    public VaultHook v() { return v; }
    public AxGensBridge ax() { return ax; }
    public ShopRegistry shop() { return shop; }
    public dev.nenf.orbz.shop.UpgradeService us() { return us; }
    public dev.nenf.orbz.axgens.IslandGeneratorTracker tracker() { return tracker; }
    public VoucherService vs() { return vs; }
    public PathGui gui() { return gui; }
    public PathEditor edit() { return edit; }

    public void reload() {
        reloadConfig();
        validateConfig();
        msg.reload();
        ds.reload();
        gui.reload();
    }

    private void validateConfig() {
        int size = getConfig().getInt("gui.size", 27);
        if (size % 9 != 0 || size < 9 || size > 54) {
            getConfig().set("gui.size", 27);
        }
    }
}
