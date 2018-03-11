package me.awesomehub.hub.utils;

import org.bukkit.Location;

/**
 * Classe défini un Warp.
 * 
 * @author Gwennael
 */
public class Warp {

    /**
     * L'id du warp.
     * 
     * @var string
     */
    private String id;

    /**
     * La position du warp.
     * 
     * @var {@link Location}
     */
    private Location where;

    /**
     * Construit un warp.
     * 
     * @param id L'id du warp.
     * @param where La position.
     */
    public Warp(String id, Location where) {
        this.id = id;
        this.where = where;
    }

    /**
     * Retourne l'id du warp.
     * 
     * @return string
     */
    public String getId() {
        return id;
    }

    /**
     * Retourne la position du warp.
     * 
     * @return {@link Location}
     */
    public Location getWhere() {
        return where;
    }
}
