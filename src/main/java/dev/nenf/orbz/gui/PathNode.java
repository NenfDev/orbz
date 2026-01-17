package dev.nenf.orbz.gui;

import dev.nenf.orbz.data.BoosterType;
import dev.nenf.orbz.shop.ShopAction;
import dev.nenf.orbz.shop.ShopRequirement;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter @Builder @Accessors(fluent = true)
public final class PathNode {
    public final String id;
    public final int slot;
    public final long cost;
    public final int max;
    public final List<String> pre;
    public final BoosterType type;
    public final ItemStack item;
    @Builder.Default public final List<ShopRequirement> reqs = new ArrayList<>();
    @Builder.Default public final List<ShopAction> acts = new ArrayList<>();

    public String ssb() {
        if (type == null) return null;
        return switch (type) {
            case EXP -> "is-exp-booster";
            case SELL -> "is-sell-booster";
            default -> null;
        };
    }
}
