package pethorses.util;

import net.kyori.adventure.text.format.NamedTextColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TextUtilTest {

    @Test
    public void coloredReturnsComponentWithColor() {
        var comp = TextUtil.colored(NamedTextColor.RED, "hello");
        assertNotNull(comp);
        assertEquals("hello", comp.toString().contains("hello") ? "hello" : comp.toString());
    }

    @Test
    public void plainReturnsComponent() {
        var comp = TextUtil.plain("test");
        assertNotNull(comp);
    }

    @Test
    public void namedTextColorToKeyAndParseRoundTrip() {
        assertEquals("RED", TextUtil.namedTextColorToKey(NamedTextColor.RED));
        assertEquals(NamedTextColor.RED, TextUtil.parseNamedTextColor("RED"));
        assertEquals(NamedTextColor.WHITE, TextUtil.parseNamedTextColor(null));
        assertEquals("WHITE", TextUtil.namedTextColorToKey(null));
    }
}

