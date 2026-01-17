package dev.nenf.orbz.shop.impl;

import dev.nenf.orbz.shop.ShopCurrency;
import dev.nenf.orbz.shop.ShopRequirement;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class CurrencyRequirement implements ShopRequirement {
    private final ShopCurrency cur;
    private final long amt;

    @Override
    public boolean meets(Player p, String id) {
        return cur.has(p, amt);
    }

    @Override
    public String failMsg() {
        return "Not enough " + cur.getId();
    }

    public void consume(Player p) {
        cur.withdraw(p, amt);
    }
}
