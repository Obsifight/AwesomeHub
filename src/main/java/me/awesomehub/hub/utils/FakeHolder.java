package me.awesomehub.hub.utils;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * Permet de gérer les inventaires des menus.
 * 
 * @author Mac'
 */
public class FakeHolder implements InventoryHolder {

    /**
     * L'id du menu.
     */
    private String id;

    /**
     * Construit un nouveau FakeHolder.
     * 
     * @param id L'id du menu.
     */
    public FakeHolder(String id) {
        this.id = id;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }

    /**
     * Retourne l'id du menu.
     * 
     * @return string
     */
    public String getId() {
        return id;
    }
}
