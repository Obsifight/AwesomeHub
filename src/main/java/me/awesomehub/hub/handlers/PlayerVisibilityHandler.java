package me.awesomehub.hub.handlers;

import java.util.List;

import me.awesomehub.hub.AHPlugin;
import me.awesomehub.hub.config.Locale;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.google.common.collect.Lists;

/**
 * Classe qui gère la visibilité des joueurs par rapport aux autres.
 * 
 * @author Mac'
 */
public class PlayerVisibilityHandler implements Listener {

    /**
     * Le pseudo des joueurs.
     * 
     * @var {@link List}
     */
    private List<String> playerToggled;

    /**
     * Construit le PlayerVisibilityHandler.
     */
    public PlayerVisibilityHandler() {
        playerToggled = Lists.newArrayList();
        Bukkit.getPluginManager().registerEvents(this, AHPlugin.get());
    }

    /**
     * Ajoute à la liste des playerToggled si il n'y est pas.
     * Le supprime si il y est.
     * 
     * @param p Le joueur.
     */
    public void togglePlayer(Player p) {
        if (playerToggled.contains(p.getName())) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', Locale.get().togglePlayerOff));
            this.removePlayerToggled(p);
        } else {
            this.addPlayerToggled(p);
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', Locale.get().togglePlayerOn));
        }
    }

    /**
     * Cache tous les joueurs et le rajoute dans la liste.
     * 
     * @param p Le joueur.
     */
    @SuppressWarnings("deprecation")
    public void addPlayerToggled(Player p) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isOp()) {
                continue; // On force l'affichage des opérateurs.
            }
            p.hidePlayer(player);
        }
        playerToggled.add(p.getName());
    }

    /**
     * Affiche tous les joueurs et le supprime de la liste.
     * 
     * @param p Le joueur.
     */
    @SuppressWarnings("deprecation")
    public void removePlayerToggled(Player p) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            p.showPlayer(player);
        }
        playerToggled.remove(p.getName());
    }

    /**
     * Quand un joueur rejoint le serveur.
     * 
     * @param e
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent e) {
        if (e.getPlayer().isOp()) return; // On force l'affichage des opérateurs.

        for (String player : playerToggled) {
            Bukkit.getPlayer(player).hidePlayer(e.getPlayer());
        }
    }
}
