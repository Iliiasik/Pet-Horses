package pethorses.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class TextUtil {
    public static Component colored(NamedTextColor color, String text) {
        return Component.text(text).color(color != null ? color : NamedTextColor.WHITE);
    }

    public static Component plain(String text) {
        return Component.text(text);
    }

    public static String namedTextColorToKey(NamedTextColor named) {
        if (named == null) return "WHITE";
        if (named == NamedTextColor.BLACK) return "BLACK";
        if (named == NamedTextColor.DARK_BLUE) return "DARK_BLUE";
        if (named == NamedTextColor.DARK_GREEN) return "DARK_GREEN";
        if (named == NamedTextColor.DARK_AQUA) return "DARK_AQUA";
        if (named == NamedTextColor.DARK_RED) return "DARK_RED";
        if (named == NamedTextColor.DARK_PURPLE) return "DARK_PURPLE";
        if (named == NamedTextColor.GOLD) return "GOLD";
        if (named == NamedTextColor.GRAY) return "GRAY";
        if (named == NamedTextColor.DARK_GRAY) return "DARK_GRAY";
        if (named == NamedTextColor.BLUE) return "BLUE";
        if (named == NamedTextColor.GREEN) return "GREEN";
        if (named == NamedTextColor.AQUA) return "AQUA";
        if (named == NamedTextColor.RED) return "RED";
        if (named == NamedTextColor.LIGHT_PURPLE) return "LIGHT_PURPLE";
        if (named == NamedTextColor.YELLOW) return "YELLOW";
        if (named == NamedTextColor.WHITE) return "WHITE";
        return "WHITE";
    }

    public static NamedTextColor parseNamedTextColor(String name) {
        if (name == null) return NamedTextColor.WHITE;
        String key = name.trim().toUpperCase();
        switch (key) {
            case "BLACK": return NamedTextColor.BLACK;
            case "DARK_BLUE": return NamedTextColor.DARK_BLUE;
            case "DARK_GREEN": return NamedTextColor.DARK_GREEN;
            case "DARK_AQUA": return NamedTextColor.DARK_AQUA;
            case "DARK_RED": return NamedTextColor.DARK_RED;
            case "DARK_PURPLE": return NamedTextColor.DARK_PURPLE;
            case "GOLD": return NamedTextColor.GOLD;
            case "GRAY": return NamedTextColor.GRAY;
            case "DARK_GRAY": return NamedTextColor.DARK_GRAY;
            case "BLUE": return NamedTextColor.BLUE;
            case "GREEN": return NamedTextColor.GREEN;
            case "AQUA": return NamedTextColor.AQUA;
            case "RED": return NamedTextColor.RED;
            case "LIGHT_PURPLE": return NamedTextColor.LIGHT_PURPLE;
            case "YELLOW": return NamedTextColor.YELLOW;
            case "WHITE": return NamedTextColor.WHITE;
            default: return NamedTextColor.WHITE;
        }
    }
}
