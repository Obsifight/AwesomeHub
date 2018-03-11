package me.awesomehub.hub.handlers;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import me.awesomehub.hub.AHPlugin;
import me.awesomehub.hub.utils.Warp;

import org.bukkit.Location;

import com.google.common.collect.Maps;

/**
 * Classe qui gère des points de téléportation.
 * 
 * @author Gwennael
 */
public class WarpHandler extends SavableHandler {

    /**
     * Le fichier qui contient les warps sauvegardés.
     * 
     * @var {@link File}
     */
    private static final File WARPS_FILE = new File(AHPlugin.get().getDataFolder(), "warps.json");

    /**
     * Les points de téléportation.
     * 
     * @var {@link Map}
     */
    private Map<String, Warp> warps;

    /**
     * Construit le WarpHandler.
     */
    public WarpHandler() {
        super(WarpHandler.WARPS_FILE);
    }

    /**
     * Défini ou redéfini un warp.
     * 
     * @param id L'id du warp.
     * @param where La position.
     * @return {@link Warp}
     */
    public Warp setWarp(String id, Location where) {
        return warps.put(id, new Warp(id, where));
    }

    /**
     * Retourne la position d'un warp.
     * 
     * @param id L'id du warp.
     * @return {@link Location}
     */
    public Location getWarpLocation(String id) {
        Warp warp = warps.get(id);
        if (warp != null) return warp.getWhere();
        return null;
    }

    /**
     * Supprime un warp.
     * 
     * @param id L'id du warp.
     * @return {@link Warp}
     */
    public Warp removeWarp(String id) {
        return warps.remove(id);
    }

    /**
     * Charge tous les warps.
     */
    @Override
    public void loadAll() {
        warps = Maps.newHashMap();

        try {
            Warp[] warps = AHPlugin.getGson().fromJson(new FileReader(saveFile), Warp[].class);
            if (warps != null) {
                for (Warp warp : warps) {
                    this.warps.put(warp.getId(), warp);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        AHPlugin.get().getLogger().info("Sucessfuly loaded " + warps.size() + " warps.");
    }

    /**
     * Sauvegarde tous les warps.
     */
    @Override
    public void saveAll() {
        try {
            FileWriter writer = new FileWriter(saveFile);
            Warp[] warps = this.warps.values().toArray(new Warp[0]);
            AHPlugin.getGson().toJson(warps, Warp[].class, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retourne la liste des warps.
     * 
     * @return {@link Map}
     */
    public Map<String, Warp> listWarps() {
        return warps;
    }
}
