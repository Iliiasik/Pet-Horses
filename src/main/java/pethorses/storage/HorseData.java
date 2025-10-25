package pethorses.storage;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Horse;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class HorseData {
    private UUID horseId;
    private UUID ownerId;
    private int level = 1;
    private int experience = 0;
    private Horse.Color color = Horse.Color.BROWN;
    private Horse.Style style = Horse.Style.NONE;
    private String horseName = null;
    private NamedTextColor horseNameColor = NamedTextColor.WHITE;
    private long deathTime = 0;
    private boolean isFollowing = true;
    private int jumps = 0;
    private double blocksTraveled = 0.0;
    private int totalJumps = 0;
    private double totalBlocksTraveled = 0.0;
    private ItemStack[] backpackItems = new ItemStack[0];
    private ItemStack armorItem = null;

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
    public NamedTextColor getHorseNameColor() { return horseNameColor; }
    public void setHorseNameColor(NamedTextColor horseNameColor) { this.horseNameColor = horseNameColor; }
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
    public ItemStack[] getBackpackItems() { return backpackItems; }
    public void setBackpackItems(ItemStack[] backpackItems) { this.backpackItems = backpackItems; }
    public ItemStack getArmorItem() { return armorItem; }
    public void setArmorItem(ItemStack armorItem) { this.armorItem = armorItem; }

    public int getBackpackSize(int baseSize, int sizePerLevel, int maxSize) {
        int calculatedSize = baseSize + (level * sizePerLevel);
        return Math.min(calculatedSize, maxSize);
    }
}