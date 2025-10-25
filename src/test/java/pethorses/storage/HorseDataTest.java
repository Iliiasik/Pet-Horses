package pethorses.storage;

import org.junit.jupiter.api.Test;
import org.bukkit.entity.Horse;

import static org.junit.jupiter.api.Assertions.*;

public class HorseDataTest {

    @Test
    public void testBackpackSizeCalculation() {
        HorseData data = new HorseData();
        data.setLevel(1);
        int size = data.getBackpackSize(9, 9, 54);
        assertEquals(18, size);
    }

    @Test
    public void testBackpackSizeLevel0() {
        HorseData data = new HorseData();
        data.setLevel(0);
        int size = data.getBackpackSize(9, 9, 54);
        assertEquals(9, size);
    }

    @Test
    public void testBackpackSizeRespectsMax() {
        HorseData data = new HorseData();
        data.setLevel(100);
        int size = data.getBackpackSize(9, 9, 54);
        assertEquals(54, size);
    }

    @Test
    public void testBackpackSizeHighLevel() {
        HorseData data = new HorseData();
        data.setLevel(5);
        int size = data.getBackpackSize(9, 9, 100);
        assertEquals(54, size);
    }

    @Test
    public void testDefaultLevel() {
        HorseData data = new HorseData();
        assertEquals(1, data.getLevel());
    }

    @Test
    public void testDefaultExperience() {
        HorseData data = new HorseData();
        assertEquals(0, data.getExperience());
    }

    @Test
    public void testDefaultColor() {
        HorseData data = new HorseData();
        assertEquals(Horse.Color.BROWN, data.getColor());
    }

    @Test
    public void testDefaultStyle() {
        HorseData data = new HorseData();
        assertEquals(Horse.Style.NONE, data.getStyle());
    }

    @Test
    public void testDefaultFollowing() {
        HorseData data = new HorseData();
        assertTrue(data.isFollowing());
    }

    @Test
    public void testSetAndGetHorseId() {
        HorseData data = new HorseData();
        java.util.UUID id = java.util.UUID.randomUUID();
        data.setHorseId(id);
        assertEquals(id, data.getHorseId());
    }

    @Test
    public void testSetAndGetOwnerId() {
        HorseData data = new HorseData();
        java.util.UUID id = java.util.UUID.randomUUID();
        data.setOwnerId(id);
        assertEquals(id, data.getOwnerId());
    }

    @Test
    public void testSetAndGetLevel() {
        HorseData data = new HorseData();
        data.setLevel(10);
        assertEquals(10, data.getLevel());
    }

    @Test
    public void testSetAndGetExperience() {
        HorseData data = new HorseData();
        data.setExperience(500);
        assertEquals(500, data.getExperience());
    }

    @Test
    public void testSetAndGetColor() {
        HorseData data = new HorseData();
        data.setColor(Horse.Color.BLACK);
        assertEquals(Horse.Color.BLACK, data.getColor());
    }

    @Test
    public void testSetAndGetStyle() {
        HorseData data = new HorseData();
        data.setStyle(Horse.Style.WHITE);
        assertEquals(Horse.Style.WHITE, data.getStyle());
    }

    @Test
    public void testSetAndGetHorseName() {
        HorseData data = new HorseData();
        data.setHorseName("Shadow");
        assertEquals("Shadow", data.getHorseName());
    }

    @Test
    public void testSetAndGetDeathTime() {
        HorseData data = new HorseData();
        long time = System.currentTimeMillis();
        data.setDeathTime(time);
        assertEquals(time, data.getDeathTime());
    }

    @Test
    public void testSetAndGetFollowing() {
        HorseData data = new HorseData();
        data.setFollowing(true);
        assertTrue(data.isFollowing());
        data.setFollowing(false);
        assertFalse(data.isFollowing());
    }

    @Test
    public void testSetAndGetJumps() {
        HorseData data = new HorseData();
        data.setJumps(25);
        assertEquals(25, data.getJumps());
    }

    @Test
    public void testSetAndGetBlocksTraveled() {
        HorseData data = new HorseData();
        data.setBlocksTraveled(100.5);
        assertEquals(100.5, data.getBlocksTraveled(), 0.01);
    }

    @Test
    public void testSetAndGetTotalJumps() {
        HorseData data = new HorseData();
        data.setTotalJumps(150);
        assertEquals(150, data.getTotalJumps());
    }

    @Test
    public void testSetAndGetTotalBlocksTraveled() {
        HorseData data = new HorseData();
        data.setTotalBlocksTraveled(5000.75);
        assertEquals(5000.75, data.getTotalBlocksTraveled(), 0.01);
    }

    @Test
    public void testBackpackSizeWithDifferentParams() {
        HorseData data = new HorseData();
        data.setLevel(2);
        int size = data.getBackpackSize(5, 3, 20);
        assertEquals(11, size);
    }

    @Test
    public void testBackpackSizeZeroBase() {
        HorseData data = new HorseData();
        data.setLevel(3);
        int size = data.getBackpackSize(0, 5, 50);
        assertEquals(15, size);
    }
}
