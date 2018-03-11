package me.awesomehub.hub.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class MiscUtils {

    /**
     * Transforme une chaîne de caractères en position.
     * 
     * @param loc La chaîne de caractère.
     * @return {@link Location} La position.
     */
    public static Location stringToLocation(String loc) {
        if (loc == null) return null;
        String[] loca = loc.split("_");
        if (loca.length != 5) return null;

        return new Location(Bukkit.getWorld(loca[0]), Double.parseDouble(loca[1]), Double.parseDouble(loca[2]), Double.parseDouble(loca[3]), Float.parseFloat(loca[4]), Float.parseFloat(loca[5]));
    }

    /**
     * Transforme une position en chaîne de caractères.
     * 
     * @param loc La position.
     * @return string La chaîne de caractères.
     */
    public static String locationToString(Location loc) {
        return String.format("%s_%d_%d_%d_%f_%f", loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
    }
}
