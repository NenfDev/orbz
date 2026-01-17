package dev.nenf.orbz.axgens;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import dev.nenf.orbz.Orbz;

public final class AxGensBridge {
    private final Orbz plugin;
    private final Plugin axgens;

    public AxGensBridge(Orbz p) {
        this.plugin = p;
        this.axgens = Bukkit.getPluginManager().getPlugin("AxGens");
    }

    public boolean present() {
        return axgens != null && axgens.isEnabled();
    }

    public void remove(org.bukkit.entity.Player p, Object g) {
        if (g == null || !present()) return;
        try {
            var utils = Class.forName("com.artillexstudios.axgens.utils.GeneratorUtils");
            var genCls = Class.forName("com.artillexstudios.axgens.generators.Generator");
            var m = utils.getMethod("removeGenerator", org.bukkit.entity.Player.class, genCls);
            m.setAccessible(true);
            m.invoke(null, p, g);
        } catch (Throwable t) {
            plugin.getLogger().warning("Remove failed: " + t.getMessage());
        }
    }

    public void delete(Object g) {
        if (g == null || !present()) return;
        try {
            var utils = Class.forName("com.artillexstudios.axgens.utils.GeneratorUtils");
            var genCls = Class.forName("com.artillexstudios.axgens.generators.Generator");
            var m = utils.getMethod("deleteGenerator", genCls);
            m.setAccessible(true);
            m.invoke(null, g);
        } catch (Throwable t) {
            plugin.getLogger().warning("Delete failed: " + t.getMessage());
        }
    }

    public org.bukkit.inventory.ItemStack item(Object g) {
        if (g == null || !present()) return null;
        try {
            var genCls = Class.forName("com.artillexstudios.axgens.generators.Generator");
            var tiersCls = Class.forName("com.artillexstudios.axgens.tiers.Tiers");
            var tierCls = Class.forName("com.artillexstudios.axgens.tiers.Tier");

            var tierId = (int) genCls.getMethod("getTier").invoke(g);
            var tier = tiersCls.getMethod("getTier", int.class).invoke(null, tierId);
            var it = (org.bukkit.inventory.ItemStack) tierCls.getMethod("getGenItem").invoke(tier);

            if (it == null) return null;
            it = it.clone();

            try {
                boolean broken = (boolean) genCls.getMethod("isBroken").invoke(g);
                var nbtCls = Class.forName("com.artillexstudios.axgens.libs.axapi.items.NBTWrapper");
                var nbt = nbtCls.getConstructor(org.bukkit.inventory.ItemStack.class).newInstance(it);
                nbtCls.getMethod("set", String.class, Object.class).invoke(nbt, "AxGens-Broken", broken);
                nbtCls.getMethod("build").invoke(nbt);
            } catch (Throwable ignored) {}

            return it;
        } catch (Throwable t) {
            plugin.getLogger().warning("Item fetch failed: " + t.getMessage());
            return null;
        }
    }
}
