package dev.nenf.orbz.shop;

import org.bukkit.entity.Player;

public interface ShopRequirement {
    boolean meets(Player player, String islandId);
    String failMsg();
}
