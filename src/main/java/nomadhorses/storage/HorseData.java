package nomadhorses.storage;

import org.bukkit.entity.Horse;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class HorseData {
    private UUID horseId;
    private UUID ownerId;
    private int level = 1;
    private int experience = 0;
    private Horse.Color color = Horse.Color.BROWN;
    private Horse.Style style = Horse.Style.NONE;
    private String horseName = null;
    private long deathTime = 0;
    private boolean isFollowing = true;
    private int jumps = 0;
    private double blocksTraveled = 0.0;
    private int totalJumps = 0;
    private double totalBlocksTraveled = 0.0;

    public UUID getHorseId() { return horseId; }
    public void setHorseId(UUID horseId) { this.horseId = horseId; }
    public UUID getOwnerId() { return ownerId; }
    public void setOwnerId(UUID ownerId) { this.ownerId = ownerId; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    public int getExperience() { return experience; }
    public void setExperience(int experience) { this.experience = experience; }
    public Horse.Color getColor() { return color; }
    public void setColor(Horse.Color color) { this.color = color; }
    public Horse.Style getStyle() { return style; }
    public void setStyle(Horse.Style style) { this.style = style; }
    public String getHorseName() { return horseName; }
    public void setHorseName(String horseName) { this.horseName = horseName; }
    public long getDeathTime() { return deathTime; }
    public void setDeathTime(long deathTime) { this.deathTime = deathTime; }
    public boolean isFollowing() { return isFollowing; }
    public void setFollowing(boolean following) { isFollowing = following; }
    public int getJumps() { return jumps; }
    public void setJumps(int jumps) { this.jumps = jumps; }
    public double getBlocksTraveled() { return blocksTraveled; }
    public void setBlocksTraveled(double blocksTraveled) { this.blocksTraveled = blocksTraveled; }
    public int getTotalJumps() { return totalJumps; }
    public void setTotalJumps(int totalJumps) { this.totalJumps = totalJumps; }
    public double getTotalBlocksTraveled() { return totalBlocksTraveled; }
    public void setTotalBlocksTraveled(double totalBlocksTraveled) { this.totalBlocksTraveled = totalBlocksTraveled; }
}