package pethorses.storage.yaml;

import org.bukkit.entity.Horse;
import org.bukkit.inventory.ItemStack;
import org.bukkit.configuration.file.FileConfiguration;
import pethorses.storage.HorseData;
import pethorses.util.TextUtil;

import java.util.*;
import net.kyori.adventure.text.format.NamedTextColor;

public class YamlHorseDataRepository {
    private final FileConfiguration config;

    public YamlHorseDataRepository(FileConfiguration config) {
        this.config = config;
    }

    public HorseData load(UUID playerId) {
        String key = playerId.toString();
        HorseData data = new HorseData();
        data.setOwnerId(playerId);
        data.setLevel(config.getInt(key + ".level", 1));
        data.setExperience(config.getInt(key + ".experience", 0));
        data.setColor(parseColor(config.getString(key + ".color", "BROWN")));
        data.setStyle(parseStyle(config.getString(key + ".style", "NONE")));
        data.setHorseName(config.getString(key + ".name"));
        data.setHorseNameColor(parseNamedTextColor(config.getString(key + ".horseNameColor", null)));
        data.setDeathTime(config.getLong(key + ".deathTime", 0));
        data.setJumps(config.getInt(key + ".jumps", 0));
        data.setBlocksTraveled(config.getDouble(key + ".blocksTraveled", 0.0));
        data.setTotalJumps(config.getInt(key + ".totalJumps", 0));
        data.setTotalBlocksTraveled(config.getDouble(key + ".totalBlocksTraveled", 0.0));

        if (config.contains(key + ".backpack")) {
            List<?> backpackList = config.getList(key + ".backpack");
            if (backpackList != null) {
                ItemStack[] backpackItems = new ItemStack[backpackList.size()];
                for (int i = 0; i < backpackList.size(); i++) {
                    Object o = backpackList.get(i);
                    if (o instanceof ItemStack) {
                        backpackItems[i] = (ItemStack) o;
                    } else {
                        backpackItems[i] = null;
                    }
                }
                data.setBackpackItems(backpackItems);
            }
        }
        if (config.contains(key + ".armor")) {
            data.setArmorItem(config.getItemStack(key + ".armor"));
        }
        return data;
    }

    public void save(HorseData data) {
        if (data == null || data.getOwnerId() == null) {
            return;
        }
        String key = data.getOwnerId().toString();
        config.set(key + ".level", data.getLevel());
        config.set(key + ".experience", data.getExperience());
        config.set(key + ".color", (data.getColor() != null) ? data.getColor().name() : Horse.Color.BROWN.name());
        config.set(key + ".style", (data.getStyle() != null) ? data.getStyle().name() : Horse.Style.NONE.name());
        config.set(key + ".name", data.getHorseName());
        NamedTextColor ntc = data.getHorseNameColor();
        String saveColor = TextUtil.namedTextColorToKey(ntc);
        config.set(key + ".horseNameColor", saveColor);
        config.set(key + ".deathTime", data.getDeathTime());
        config.set(key + ".jumps", data.getJumps());
        config.set(key + ".blocksTraveled", data.getBlocksTraveled());
        config.set(key + ".totalJumps", data.getTotalJumps());
        config.set(key + ".totalBlocksTraveled", data.getTotalBlocksTraveled());
        if (data.getBackpackItems() != null && data.getBackpackItems().length > 0) {
            config.set(key + ".backpack", Arrays.asList(data.getBackpackItems()));
        } else {
            config.set(key + ".backpack", null);
        }
        config.set(key + ".armor", data.getArmorItem());
    }

    private Horse.Color parseColor(String colorStr) {
        try { return Horse.Color.valueOf(colorStr); }
        catch (Exception e) { return Horse.Color.BROWN; }
    }
    private Horse.Style parseStyle(String styleStr) {
        try { return Horse.Style.valueOf(styleStr); }
        catch (Exception e) { return Horse.Style.NONE; }
    }
    private NamedTextColor parseNamedTextColor(String colorStr) {
        if (colorStr == null) return net.kyori.adventure.text.format.NamedTextColor.WHITE;
        return TextUtil.parseNamedTextColor(colorStr);
    }
}
