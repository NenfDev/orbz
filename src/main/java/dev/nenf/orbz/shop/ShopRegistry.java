package dev.nenf.orbz.shop;

import dev.nenf.orbz.Orbz;
import dev.nenf.orbz.shop.impl.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopRegistry {
    private final Orbz plugin;
    private final Map<String, ShopCurrency> currencies = new HashMap<>();

    public ShopRegistry(Orbz p) {
        this.plugin = p;
        add(new OrbsCurrency(p));
    }

    public void add(ShopCurrency sc) {
        currencies.put(sc.getId().toUpperCase(), sc);
    }

    public ShopCurrency get(String id) {
        return currencies.get(id.toUpperCase());
    }

    public List<ShopRequirement> parseReqs(List<Map<?, ?>> list) {
        var out = new ArrayList<ShopRequirement>();
        if (list == null) return out;

        for (var m : list) {
            var type = String.valueOf(m.get("type")).toUpperCase();
            switch (type) {
                case "PREREQUISITE" -> out.add(new PrerequisiteRequirement(plugin, String.valueOf(m.get("id"))));
                case "ISLAND_LEVEL" -> out.add(new IslandLevelRequirement(plugin, Long.parseLong(String.valueOf(m.get("level")))));
                case "CURRENCY" -> {
                    var p = m.get("provider");
                    var provider = p != null ? String.valueOf(p).toUpperCase() : "ORBS";
                    var sc = provider.equals("VAULT") ? new VaultCurrency(plugin) : get("ORBS");
                    out.add(new CurrencyRequirement(sc, Long.parseLong(String.valueOf(m.get("amount")))));
                }
            }
        }
        return out;
    }

    public List<ShopAction> parseActions(List<?> list) {
        var out = new ArrayList<ShopAction>();
        if (list == null) return out;

        for (var o : list) {
            if (o instanceof String s) {
                if (s.startsWith("[MSG] ")) {
                    out.add(new MessageAction(s.substring(6)));
                } else if (s.startsWith("[SSB] ")) {
                    var parts = s.substring(6).split(" ");
                    out.add(new SsbUpgradeAction(plugin, parts[0], parts.length > 1 ? Integer.parseInt(parts[1]) : 1));
                } else if (s.toLowerCase().contains("is admin setupgrade ")) {
                    var stripped = s.startsWith("/") ? s.substring(1) : s;
                    var parts = stripped.split(" ");
                    if (parts.length >= 5) {
                        int lvl = 1;
                        if (parts.length >= 6) {
                            try { lvl = Integer.parseInt(parts[5]); } catch (Exception ignored) {}
                        }
                        out.add(new SsbUpgradeAction(plugin, parts[4], lvl));
                    } else out.add(new CommandAction(plugin, s, false));
                } else out.add(new CommandAction(plugin, s, false));
                continue;
            }
            if (!(o instanceof Map<?, ?> m)) continue;

            var type = String.valueOf(m.get("type")).toUpperCase();
            switch (type) {
                case "COMMAND" -> out.add(new CommandAction(plugin, String.valueOf(m.get("run")), Boolean.parseBoolean(String.valueOf(m.get("island-wide")))));
                case "MESSAGE" -> out.add(new MessageAction(String.valueOf(m.get("message"))));
                case "SSB_UPGRADE" -> out.add(new SsbUpgradeAction(plugin, String.valueOf(m.get("upgrade")), Integer.parseInt(String.valueOf(m.get("level")))));
            }
        }
        return out;
    }
}
