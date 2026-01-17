package dev.nenf.orbz.hooks;

import dev.nenf.orbz.Orbz;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class SsbHook {
    private final Orbz plugin;
    private final boolean present;
    private final Class<?> api;

    public SsbHook(Orbz p) {
        this.plugin = p;
        var pl = Bukkit.getPluginManager().getPlugin("SuperiorSkyblock2");
        this.present = pl != null && pl.isEnabled();
        this.api = present ? load("com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI") : null;
    }

    private Class<?> load(String n) { try { return Class.forName(n); } catch (Throwable t) { return null; } }
    public boolean present() { return present && api != null; }

    public String getIslandId(Player p) {
        var isl = getIsland(p);
        if (isl == null) return null;
        var uid = Reflect.callAny(isl, new String[]{"getUniqueId", "getUUID", "getIslandUUID", "getId"}, new Class<?>[]{}, new Object[]{});
        return uid == null ? null : uid.toString();
    }

    public Object getIsland(Player p) {
        if (!present() || p == null) return null;
        var sp = Reflect.callStaticAny(api, new String[]{"getPlayer"}, new Class<?>[]{Player.class}, new Object[]{p});
        if (sp == null) sp = Reflect.callStaticAny(api, new String[]{"getPlayer"}, new Class<?>[]{UUID.class}, new Object[]{p.getUniqueId()});
        if (sp == null) return null;
        var isl = Reflect.callAny(sp, new String[]{"getIsland", "getIslandWrapper"}, new Class<?>[]{}, new Object[]{});
        return isl != null ? isl : Reflect.callStaticAny(api, new String[]{"getIslandAt"}, new Class<?>[]{org.bukkit.Location.class}, new Object[]{p.getLocation()});
    }

    public Object getIsland(String id) {
        if (!present() || id == null) return null;
        try { return Reflect.callStaticAny(api, new String[]{"getIslandByUUID"}, new Class<?>[]{UUID.class}, new Object[]{UUID.fromString(id)}); }
        catch (Exception e) { return null; }
    }

    public long getIslandLevel(String id) {
        var isl = getIsland(id);
        if (isl == null) return 0;
        var lvl = Reflect.callAny(isl, new String[]{"getIslandLevel"}, new Class<?>[]{}, new Object[]{});
        return lvl instanceof Number n ? n.longValue() : 0;
    }

    public int getUpgradeLevel(String id, String upg) {
        var isl = getIsland(id);
        if (isl == null) return 0;
        var lvl = Reflect.callAny(isl, new String[]{"getUpgradeLevel"}, new Class<?>[]{String.class}, new Object[]{upg});
        return lvl instanceof Number n ? n.intValue() : 0;
    }

    public Set<UUID> getMemberUuids(String id) {
        var isl = getIsland(id);
        if (isl == null) return Collections.emptySet();
        var mems = Reflect.callAny(isl, new String[]{"getIslandMembers", "getMembers"}, new Class<?>[]{boolean.class}, new Object[]{true});
        if (mems == null) mems = Reflect.callAny(isl, new String[]{"getIslandMembers", "getMembers"}, new Class<?>[]{}, new Object[]{});
        if (!(mems instanceof Iterable<?> it)) return Collections.emptySet();
        var out = new HashSet<UUID>();
        for (var m : it) {
            if (m instanceof UUID u) out.add(u);
            else {
                var uid = Reflect.callAny(m, new String[]{"getUniqueId", "getUUID"}, new Class<?>[]{}, new Object[]{});
                if (uid instanceof UUID u) out.add(u);
            }
        }
        return out;
    }
}
