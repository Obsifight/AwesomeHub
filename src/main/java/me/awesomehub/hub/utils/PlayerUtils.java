package me.awesomehub.hub.utils;

import org.bukkit.entity.Player;

public class PlayerUtils {

    /**
     * Permet de vider l'inventaire d'un joueur.
     * 
     * @param player Le joueur.
     */
    public static void clearInventory(Player player) {
        // Clear all
        player.getInventory().clear();
        player.getActivePotionEffects().clear();
        player.getInventory().setBoots(null);
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);

        // Health & exhaustion
        player.setExhaustion(0.0F);
        player.setHealth(20.0D);
        player.setMaxHealth(20.0D);
    }
}
