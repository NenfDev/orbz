package dev.nenf.orbz.shop.impl;

import dev.nenf.orbz.shop.ShopAction;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class MessageAction implements ShopAction {
    private final String msg;

    @Override
    public void run(Player p, String id) {
        p.sendMessage(MiniMessage.miniMessage().deserialize(msg));
    }
}
