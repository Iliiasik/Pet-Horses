package pethorses.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TextUtil {
    public static Component colored(NamedTextColor color, String text) {
        return Component.text(text).color(color != null ? color : NamedTextColor.WHITE);
    }

    public static Component plain(String text) {
        return Component.text(text);
    }

    private static final Map<NamedTextColor, String> COLOR_TO_KEY;
    private static final Map<String, NamedTextColor> KEY_TO_COLOR;

    static {
        Map<NamedTextColor, String> c2k = new HashMap<>();
        Map<String, NamedTextColor> k2c = new HashMap<>();
        try {
            for (Field field : NamedTextColor.class.getFields()) {
                if (Modifier.isStatic(field.getModifiers()) && field.getType() == NamedTextColor.class) {
                    Object val = field.get(null);
                    if (val instanceof NamedTextColor) {
                        NamedTextColor color = (NamedTextColor) val;
                        String name = field.getName();
                        c2k.put(color, name);
                        k2c.put(name.toUpperCase(), color);
                    }
                }
            }
        } catch (IllegalAccessException ignored) {
        }
        k2c.putIfAbsent("WHITE", NamedTextColor.WHITE);
        c2k.putIfAbsent(NamedTextColor.WHITE, "WHITE");

        COLOR_TO_KEY = Collections.unmodifiableMap(c2k);
        KEY_TO_COLOR = Collections.unmodifiableMap(k2c);
    }

    public static String namedTextColorToKey(NamedTextColor named) {
        if (named == null) return COLOR_TO_KEY.getOrDefault(NamedTextColor.WHITE, "WHITE");
        return COLOR_TO_KEY.getOrDefault(named, "WHITE");
    }

    public static NamedTextColor parseNamedTextColor(String name) {
        if (name == null) return NamedTextColor.WHITE;
        String key = name.trim().toUpperCase();
        return KEY_TO_COLOR.getOrDefault(key, NamedTextColor.WHITE);
    }
}
