package me.awesomehub.hub.adapters;

import java.lang.reflect.Type;
import java.util.logging.Level;

import me.awesomehub.hub.AHPlugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonDeserializationContext;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonDeserializer;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonElement;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonObject;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParseException;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonSerializationContext;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonSerializer;

/**
 * Permet de sérializer et désérializer une Location en JSON.
 * 
 * @author Gwennael
 */
public class LocationAdapter implements JsonDeserializer<Location>, JsonSerializer<Location> {
    private static final String WORLD = "world";
    private static final String X = "x";
    private static final String Y = "y";
    private static final String Z = "z";
    private static final String YAW = "yaw";
    private static final String PITCH = "pitch";

    @Override
    public Location deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            JsonObject obj = json.getAsJsonObject();
            World world = Bukkit.getWorld(obj.get(LocationAdapter.WORLD).getAsString());
            if (world != null) {
                JsonElement yaw = obj.get(LocationAdapter.YAW), pitch = obj.get(LocationAdapter.PITCH);
                Location location = new Location(world, obj.get(LocationAdapter.X).getAsDouble(), obj.get(LocationAdapter.Y).getAsDouble(), obj.get(LocationAdapter.Z).getAsDouble(), yaw == null ? 0.0F : yaw.getAsFloat(), pitch == null ? 0.0F : pitch.getAsFloat());
                return location;
            }
        } catch (Exception ex) {
            // ex.printStackTrace();
            AHPlugin.get().getLogger().log(Level.WARNING, "Error encountered while deserializing a Location.");
        }
        return null;
    }

    @Override
    public JsonElement serialize(Location src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty(LocationAdapter.WORLD, src.getWorld().getName());
            obj.addProperty(LocationAdapter.X, src.getX());
            obj.addProperty(LocationAdapter.Y, src.getY());
            obj.addProperty(LocationAdapter.Z, src.getZ());
            if (src.getYaw() != 0.0F) {
                obj.addProperty(LocationAdapter.YAW, src.getYaw());
            }
            if (src.getPitch() != 0.0F) {
                obj.addProperty(LocationAdapter.PITCH, src.getPitch());
            }
        } catch (Exception ex) {
            // ex.printStackTrace();
            AHPlugin.get().getLogger().log(Level.WARNING, "Error encountered while serializing a Location.");
        }
        return obj;
    }
}
