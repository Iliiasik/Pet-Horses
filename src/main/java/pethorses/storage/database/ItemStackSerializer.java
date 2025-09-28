package pethorses.storage.database;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.logging.Logger;

public class ItemStackSerializer {
    public static byte[] serialize(ItemStack[] items, Logger logger) {
        if (items == null || items.length == 0) return null;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {
            dataOutput.writeInt(items.length);
            for (ItemStack item : items) {
                dataOutput.writeObject(item);
            }
            return outputStream.toByteArray();
        } catch (Exception e) {
            logger.warning("Error serializing item stacks: " + e.getMessage());
            return null;
        }
    }

    public static ItemStack[] deserialize(byte[] data, Logger logger) {
        if (data == null || data.length == 0) return new ItemStack[0];
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
             BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {
            int size = dataInput.readInt();
            ItemStack[] items = new ItemStack[size];
            for (int i = 0; i < size; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }
            return items;
        } catch (Exception e) {
            logger.warning("Error deserializing item stacks: " + e.getMessage());
            return new ItemStack[0];
        }
    }
}
