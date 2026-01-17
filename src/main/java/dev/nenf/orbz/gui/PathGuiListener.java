package dev.nenf.orbz.gui;

import dev.nenf.orbz.Orbz;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public final class PathGuiListener implements Listener {
    private final Orbz plugin;

    public PathGuiListener(Orbz p) {
        this.plugin = p;
    }

    @EventHandler(ignoreCancelled = true)
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        if (e.getClickedInventory() == null || e.getClickedInventory() != e.getView().getTopInventory()) return;

        var title = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(e.getView().title());
        var base = plugin.getConfig().getString("gui.title", "Upgrades");
        var expected = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(MiniMessage.miniMessage().deserialize(base));

        if (!title.startsWith(expected)) return;

        e.setCancelled(true);
        var edit = title.contains("(EDITOR)");
        var slot = e.getRawSlot();
        var n = plugin.gui().get(slot);

        if (edit) {
            if (n != null) {
                if (e.getClick().isRightClick()) plugin.edit().edit(p, n.id());
            } else if (slot < e.getInventory().getSize()) plugin.edit().create(p, slot);
        } else if (n != null) plugin.gui().buy(p, n);
    }
}
