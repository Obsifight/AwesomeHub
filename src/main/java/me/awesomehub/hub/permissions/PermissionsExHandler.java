package me.awesomehub.hub.permissions;

import org.bukkit.entity.Player;

import ru.tehkode.permissions.bukkit.PermissionsEx;

/**
 * Gère la compatibilité avec PermissionsEx.
 * 
 * @author Gwennael
 */
public class PermissionsExHandler implements PermissionsHandler {

    /**
     * Retourne le préfixe du joueur.
     * 
     * @param player Le joueur.
     * @return string
     */
    @Override
    public String getPrefix(Player player) {
        return PermissionsEx.getUser(player).getPrefix();
    }

    /**
     * Retourne le suffixe du joueur.
     * 
     * @param player Le joueur.
     * @return string
     */
    @Override
    public String getSuffix(Player player) {
        return PermissionsEx.getUser(player).getSuffix();
    }
}
