package me.awesomehub.hub.permissions;

import org.anjocaido.groupmanager.GroupManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Gère la compatibilité avec GroupManager.
 * 
 * @author Gwennael
 */
public class GroupManagerHandler implements PermissionsHandler {

    /**
     * L'instance du plugin GroupManager.
     * 
     * @var {@link GroupManager}
     */
    private GroupManager groupManager;

    /**
     * Construit l'handler GroupManager.
     */
    public GroupManagerHandler() {
        groupManager = (GroupManager) Bukkit.getPluginManager().getPlugin("GroupManager");
    }

    /**
     * Retourne le préfixe du joueur.
     * 
     * @param player Le joueur.
     * @return string
     */
    @Override
    public String getPrefix(Player player) {
        return groupManager.getWorldsHolder().getWorldPermissions(player).getUserPrefix(player.getName());
    }

    /**
     * Retourne le suffixe du joueur.
     * 
     * @param player Le joueur.
     * @return string
     */
    @Override
    public String getSuffix(Player player) {
        return groupManager.getWorldsHolder().getWorldPermissions(player).getUserSuffix(player.getName());
    }
}
