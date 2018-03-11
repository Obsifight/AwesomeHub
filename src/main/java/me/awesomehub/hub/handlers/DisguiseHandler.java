package me.awesomehub.hub.handlers;

import java.util.Map;

import me.awesomehub.hub.AHPlugin;
import me.awesomehub.hub.utils.MyDisguise;
import me.awesomehub.hub.utils.MyDisguise.EntityDisguise;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.google.common.collect.Maps;

/**
 * Classe qui gère les déguisements.
 * 
 * @author Gwennael
 */
public class DisguiseHandler implements Listener {

    /**
     * Les déguisements par joueur.
     */
    private Map<Player, MyDisguise> playerDisguises;

    /**
     * Construit le DisguiseHandler.
     */
    public DisguiseHandler() {
        playerDisguises = Maps.newHashMap();
        Bukkit.getPluginManager().registerEvents(this, AHPlugin.get());
    }

    /**
     * Déguise un joueur.
     * 
     * @param player Le joueur.
     * @param type Le déguisement
     * @return boolean
     */
    @SuppressWarnings("deprecation")
    public boolean disguisePlayer(Player player, EntityDisguise type) {
        if (!playerDisguises.containsKey(player)) {
            MyDisguise disguise = new MyDisguise(player, type);
            disguise.updateDisguise(Bukkit.getOnlinePlayers());
            playerDisguises.put(player, disguise);
        } else {
            try {
                playerDisguises.get(player).changePlayerDisguise(type, Bukkit.getOnlinePlayers());
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /**
     * Déguise ou enlève le déguisement d'un joueur.
     * 
     * @param player Le joueur.
     * @param type Le déguisement.
     */
    public void togglePlayer(Player player, EntityDisguise type) {
        if (!playerDisguises.containsKey(player) || playerDisguises.get(player).getType() != type) {
            this.disguisePlayer(player, type);
        } else {
            this.removeDisguise(player);
        }
    }

    /**
     * Retire le déguisement d'un joueur.
     * 
     * @param player Le joueur.
     * @return {@link MyDisguise}
     */
    public MyDisguise removeDisguise(Player player) {
        MyDisguise disguise = null;
        if (playerDisguises.containsKey(player)) {
            disguise = playerDisguises.remove(player);
            try {
                disguise.removeDisguise();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return disguise;
    }

    /**
     * Quand un joueur se déconnecte.
     * 
     * @param evt
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent evt) {
        this.removeDisguise(evt.getPlayer());
    }

    /**
     * Quand un joueur est expulsé du serveur.
     * 
     * @param evt
     */
    @EventHandler
    public void onPlayerKick(PlayerKickEvent evt) {
        this.removeDisguise(evt.getPlayer());
    }
}
