package dev.nenf.orbz.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public final class Messages {
    private final JavaPlugin plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();
    private YamlConfiguration cfg;
    private final Map<String, String> cache = new HashMap<>();

    public Messages(JavaPlugin p) {
        this.plugin = p;
        reload();
    }

    public void reload() {
        var lang = plugin.getConfig().getString("language", "en");
        var f = new File(plugin.getDataFolder(), "messages_" + lang + ".yml");
        if (!f.exists()) {
            plugin.saveResource("messages_en.yml", false);
            if (!lang.equals("en")) f = new File(plugin.getDataFolder(), "messages_en.yml");
        }
        cfg = YamlConfiguration.loadConfiguration(f);
        cache.clear();
    }

    public void send(CommandSender s, String k, Map<String, String> ph) {
        var raw = raw(k);
        if (raw == null) { s.sendMessage(mm.deserialize("<red>Missing: " + k)); return; }

        var pre = raw(raw.startsWith("[NO_PREFIX]") ? "" : "prefix");
        if (pre != null) raw = raw.startsWith("[NO_PREFIX]") ? raw.substring(11) : pre + raw;

        if (ph != null) for (var e : ph.entrySet()) raw = raw.replace("{" + e.getKey() + "}", e.getValue());
        if (s instanceof org.bukkit.entity.Player p && org.bukkit.Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) raw = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(p, raw);

        s.sendMessage(mm.deserialize(raw));
    }

    public void send(CommandSender s, String k) { send(s, k, null); }
    public String raw(String k) { return k.isEmpty() ? "" : cache.computeIfAbsent(k, key -> cfg.getString(key)); }
}
