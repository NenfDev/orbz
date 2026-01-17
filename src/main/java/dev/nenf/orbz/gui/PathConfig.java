package dev.nenf.orbz.gui;

import dev.nenf.orbz.Orbz;
import dev.nenf.orbz.data.BoosterType;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import dev.nenf.orbz.util.SkullUtils;
import java.io.File;
import java.util.*;

public final class PathConfig {
    private final Map<String, PathNode> nodes = new LinkedHashMap<>();
    private final ItemStack locked;
    private final boolean glow;

    public PathConfig(Orbz p) {
        var cfg = YamlConfiguration.loadConfiguration(new File(p.getDataFolder(), "path.yml"));
        glow = cfg.getBoolean("purchased-item-glow", true);
        locked = buildItem(cfg.getConfigurationSection("locked-item"), "<red>Locked</red>", List.of("<gray>Locked</gray>"));

        var sec = cfg.getConfigurationSection("nodes");
        if (sec == null) return;

        for (var id : sec.getKeys(false)) {
            var n = sec.getConfigurationSection(id);
            if (n == null) continue;

            var node = PathNode.builder()
                .id(id)
                .slot(n.getInt("slot"))
                .cost(n.getLong("cost-orbs"))
                .max(n.getInt("max-level", 1))
                .pre(n.getStringList("pre"))
                .type(BoosterType.parse(n.getString("booster-type")))
                .item(buildItem(n.getConfigurationSection("display"), "<gray>" + id + "</gray>", List.of()))
                .build();

            node.reqs.addAll(p.shop().parseReqs(n.getMapList("reqs")));
            node.acts.addAll(p.shop().parseActions(n.getList("acts")));
            nodes.put(id, node);
        }
    }

    public Map<String, PathNode> nodes() { return nodes; }
    public ItemStack locked() { return locked; }
    public boolean glow() { return glow; }

    private ItemStack buildItem(ConfigurationSection s, String defName, List<String> defLore) {
        if (s == null) {
            var it = new ItemStack(Material.PAPER);
            var meta = it.getItemMeta();
            meta.displayName(MiniMessage.miniMessage().deserialize(defName));
            it.setItemMeta(meta);
            return it;
        }

        var matStr = s.getString("material", "PAPER");
        var it = matStr.startsWith("BASE64:") ? SkullUtils.getCustomSkull(matStr.substring(7)) : new ItemStack(Optional.ofNullable(Material.matchMaterial(matStr)).orElse(Material.PAPER));
        var meta = it.getItemMeta();

        int cmd = s.getInt("custom-model-data", 0);
        if (cmd > 0) meta.setCustomModelData(cmd);
        meta.displayName(MiniMessage.miniMessage().deserialize(s.getString("name", defName)));

        var lore = s.getStringList("lore");
        if (lore.isEmpty()) lore = defLore;
        meta.lore(lore.stream().map(l -> MiniMessage.miniMessage().deserialize(l)).toList());

        if (s.getBoolean("glow", false)) {
            it.addUnsafeEnchantment(Enchantment.LUCK_OF_THE_SEA, 1);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        it.setItemMeta(meta);
        return it;
    }
}
