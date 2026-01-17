package dev.nenf.orbz.gui;

import dev.nenf.orbz.Orbz;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public final class PathGui {
    private final Orbz plugin;
    private PathConfig cfg;
    private String title;
    private int size;
    private ItemStack filler;

    public PathGui(Orbz p) {
        this.plugin = p;
        reload();
    }

    public void reload() {
        var c = plugin.getConfig();
        title = c.getString("gui.title", "Upgrades");
        size = c.getInt("gui.size", 54);
        filler = buildFiller();
        cfg = new PathConfig(plugin);
    }

    public PathConfig config() { return cfg; }

    public void open(Player p) { open(p, false); }

    public void open(Player p, boolean edit) {
        var id = plugin.ssb().getIslandId(p);
        if (id == null && !edit) {
            plugin.msg().send(p, "no-island");
            return;
        }

        var inv = Bukkit.createInventory(p, size, MiniMessage.miniMessage().deserialize(title + (edit ? " (EDITOR)" : "")));
        if (plugin.getConfig().getBoolean("gui.filler.enabled", true)) {
            for (int i = 0; i < inv.getSize(); i++) inv.setItem(i, filler);
        }

        long orbs = id != null ? plugin.ds().orbs(id) : 0;

        for (var n : cfg.nodes().values()) {
            boolean met = edit || plugin.us().met(null, id, n);
            boolean bought = !edit && plugin.us().bought(id, n);
            inv.setItem(n.slot(), met ? decorate(p, id, n, bought, orbs) : locked(p, n));
        }
        p.openInventory(inv);
    }

    public PathNode get(int slot) {
        return cfg.nodes().values().stream().filter(n -> n.slot() == slot).findFirst().orElse(null);
    }

    public void buy(Player p, PathNode n) {
        if (plugin.us().buy(p, n)) open(p);
    }

    private ItemStack locked(Player p, PathNode n) {
        var it = cfg.locked().clone();
        var meta = it.getItemMeta();
        if (meta.lore() != null) {
            List<net.kyori.adventure.text.Component> lore = new ArrayList<>();
            for (var c : meta.lore()) {
                var s = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(c);
                s = s.replace("{reason}", "Requires previous nodes");
                if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) s = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(p, s);
                lore.add(MiniMessage.miniMessage().deserialize(s));
            }
            meta.lore(lore);
            it.setItemMeta(meta);
        }
        return it;
    }

    private ItemStack decorate(Player p, String id, PathNode n, boolean bought, long orbs) {
        var it = n.item().clone();
        var meta = it.getItemMeta();
        var status = bought ? "<green>Purchased</green>" : (orbs >= n.cost() ? "<aqua>Available</aqua>" : "<yellow>Not enough Orbs</yellow>");

        if (meta.hasDisplayName()) {
            var s = MiniMessage.miniMessage().serialize(meta.displayName())
                       .replace("{cost}", String.valueOf(n.cost())).replace("{status}", status);
            if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) s = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(p, s);
            meta.displayName(MiniMessage.miniMessage().deserialize(s));
        }

        if (meta.hasLore()) {
            List<net.kyori.adventure.text.Component> lore = new ArrayList<>();
            for (var c : meta.lore()) {
                var s = MiniMessage.miniMessage().serialize(c).replace("{cost}", String.valueOf(n.cost())).replace("{status}", status);
                if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) s = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(p, s);
                lore.add(MiniMessage.miniMessage().deserialize(s));
            }
            meta.lore(lore);
        }

        if (bought && cfg.glow()) {
            it.addUnsafeEnchantment(Enchantment.LUCK_OF_THE_SEA, 1);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        it.setItemMeta(meta);
        return it;
    }

    private ItemStack buildFiller() {
        var s = plugin.getConfig().getConfigurationSection("gui.filler");
        if (s == null) return new ItemStack(Material.BLACK_STAINED_GLASS_PANE);

        var mat = Material.matchMaterial(s.getString("material", "BLACK_STAINED_GLASS_PANE"));
        if (mat == null) mat = Material.BLACK_STAINED_GLASS_PANE;

        var it = new ItemStack(mat);
        var meta = it.getItemMeta();
        int cmd = s.getInt("custom-model-data", 0);
        if (cmd > 0) meta.setCustomModelData(cmd);
        meta.displayName(MiniMessage.miniMessage().deserialize(s.getString("name", " ")));
        it.setItemMeta(meta);
        return it;
    }
}
