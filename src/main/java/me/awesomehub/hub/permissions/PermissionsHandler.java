package me.awesomehub.hub.permissions;

import org.bukkit.entity.Player;

/**
 * Gère la compatibilité avec les plugins de permissions.
 * 
 * @author Gwennael
 */
public interface PermissionsHandler {

    /**
     * Retourne le préfixe du joueur.
     * 
     * @param player Le joueur.
     * @return string
     */
    public String getPrefix(Player player);

    /**
     * Retourne le suffixe du joueur.
     * 
     * @param player Le joueur.
     * @return string
     */
    public String getSuffix(Player player);
}
