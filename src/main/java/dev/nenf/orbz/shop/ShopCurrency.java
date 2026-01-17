package dev.nenf.orbz.shop;

import org.bukkit.entity.Player;

public interface ShopCurrency {
    String getId();
    boolean has(Player player, long amount);
    void withdraw(Player player, long amount);
    void deposit(Player player, long amount);
}
