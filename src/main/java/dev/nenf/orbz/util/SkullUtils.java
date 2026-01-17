package dev.nenf.orbz.util;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public class SkullUtils {
    public static ItemStack getCustomSkull(String base64) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        if (base64 == null || base64.isEmpty()) return item;
        
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        if (meta == null) return item;

        try {
            PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes(base64.getBytes()));
            profile.setProperty(new ProfileProperty("textures", base64));
            meta.setPlayerProfile(profile);
            item.setItemMeta(meta);
        } catch (Exception e) {}
        
        return item;
    }
}
