package me.awesomehub.hub.utils;

public class InventoryUtils {

    /**
     * Récupère un nombre de colonnes pour un inventaire.
     * 
     * @param size Le nombre de slots minimum.
     * @return int Le nombre de colonnes nécessaires.
     */
    public static int getPerfectColumnNumber(int size) {
        return (int) Math.ceil(size / 9);
    }
}
