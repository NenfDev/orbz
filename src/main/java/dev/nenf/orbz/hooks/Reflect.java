package dev.nenf.orbz.hooks;

import java.lang.reflect.Method;
import java.util.Arrays;

public final class Reflect {
    private Reflect() {}

    public static Object call(Object t, String m, Class<?>[] pt, Object[] args) {
        if (t == null) return null;
        try {
            var meth = t.getClass().getMethod(m, pt);
            meth.setAccessible(true);
            return meth.invoke(t, args);
        } catch (Throwable ignored) { return null; }
    }

    public static Object callAny(Object t, String[] ms, Class<?>[] pt, Object[] args) {
        if (t == null) return null;
        for (var m : ms) {
            var out = call(t, m, pt, args);
            if (out != null) return out;
        }
        return null;
    }

    public static Object callStatic(Class<?> c, String m, Class<?>[] pt, Object[] args) {
        if (c == null) return null;
        try {
            var meth = c.getMethod(m, pt);
            meth.setAccessible(true);
            return meth.invoke(null, args);
        } catch (Throwable ignored) { return null; }
    }

    public static Object callStaticAny(Class<?> c, String[] ms, Class<?>[] pt, Object[] args) {
        if (c == null) return null;
        for (var m : ms) {
            var out = callStatic(c, m, pt, args);
            if (out != null) return out;
        }
        return null;
    }
}
