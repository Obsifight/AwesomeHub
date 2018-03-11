package me.awesomehub.hub.permissions;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Gère la compatibilité si aucun plugin de permissions n'est installé.
 * 
 * @author Gwennael
 */
public class NullPermissionsHandler implements PermissionsHandler {

    /**
     * Retourne le préfixe du joueur.
     * 
     * @param player Le joueur.
     * @return string
     */
    @Override
    public String getPrefix(Player player) {
        return player.isOp() ? ChatColor.DARK_RED + "[Admin]": "";
    }

    /**
     * Retourne le suffixe du joueur.
     * 
     * @param player Le joueur.
     * @return string
     */
    @Override
    public String getSuffix(Player player) {
        return "";
    }
}
