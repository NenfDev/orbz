package dev.nenf.orbz.items;

import dev.nenf.orbz.Orbz;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.UUID;

public final class VoucherService {
    private final Orbz plugin;
    private final NamespacedKey kAmt, kTok;
    private final java.security.SecureRandom rng = new java.security.SecureRandom();

    public VoucherService(Orbz p) {
        this.plugin = p;
        kAmt = new NamespacedKey(p, "amt");
        kTok = new NamespacedKey(p, "tok");
    }

    public ItemStack create(long amt, int model) {
        var s = plugin.getConfig().getConfigurationSection("orb-voucher");
        var m = s.getString("material", "AMETHYST_SHARD");
        var it = m.startsWith("BASE64:") ? dev.nenf.orbz.util.SkullUtils.getCustomSkull(m.substring(7)) : new ItemStack(Optional.ofNullable(org.bukkit.Material.matchMaterial(m)).orElse(org.bukkit.Material.AMETHYST_SHARD));
        var meta = it.getItemMeta();

        int cmd = model >= 0 ? model : s.getInt("custom-model-data", 0);
        if (cmd > 0) meta.setCustomModelData(cmd);

        if (s.getBoolean("glow", true)) {
            meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
            it.addUnsafeEnchantment(org.bukkit.enchantments.Enchantment.LUCK_OF_THE_SEA, 1);
        }

        var mm = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage();
        meta.displayName(mm.deserialize(s.getString("name", "Orbs").replace("{amount}", String.valueOf(amt))));
        meta.lore(s.getStringList("lore").stream().map(l -> mm.deserialize(l.replace("{amount}", String.valueOf(amt)))).toList());

        var pdc = meta.getPersistentDataContainer();
        pdc.set(kAmt, org.bukkit.persistence.PersistentDataType.LONG, amt);
        pdc.set(kTok, org.bukkit.persistence.PersistentDataType.STRING, UUID.randomUUID() + "-" + rng.nextInt(999999));

        it.setItemMeta(meta);
        return it;
    }

    public long amount(ItemStack it) {
        if (it == null || !it.hasItemMeta()) return -1;
        var amt = it.getItemMeta().getPersistentDataContainer().get(kAmt, org.bukkit.persistence.PersistentDataType.LONG);
        return amt == null ? -1 : amt;
    }

    public String token(ItemStack it) {
        if (it == null || !it.hasItemMeta()) return null;
        return it.getItemMeta().getPersistentDataContainer().get(kTok, org.bukkit.persistence.PersistentDataType.STRING);
    }
}
