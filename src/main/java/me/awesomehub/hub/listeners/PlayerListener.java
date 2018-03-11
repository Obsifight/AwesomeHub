package me.awesomehub.hub.listeners;

import me.awesomehub.hub.AHPlugin;
import me.awesomehub.hub.config.Configuration;
import me.awesomehub.hub.utils.PlayerUtils;
import me.awesomehub.hub.utils.VirtualMenu;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Classe qui gère les évenements liés au joueur.
 * 
 * @author Mac'
 */
public class PlayerListener implements Listener {

    @EventHandler
    public void onJoin(final PlayerJoinEvent e) {
        // On le clear parce qu'on ne sait jamais.
        PlayerUtils.clearInventory(e.getPlayer());

        // Affichage de la hotbar dans l'inventaire du joueur.
        VirtualMenu hotbar = AHPlugin.get().getMenuHandler().getMenus().get("hotbar");
        if (hotbar != null) {
            hotbar.applyHotbar(e.getPlayer());
        }

        // Pour ne pas qu'il puisse faire n'importe quoi.
        e.getPlayer().setGameMode(GameMode.ADVENTURE);
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String format = Configuration.get().chatFormat;
        format = format.replace("%prefix%", AHPlugin.get().getPermissionsHandler().getPrefix(p));
        format = format.replace("%suffix%", AHPlugin.get().getPermissionsHandler().getSuffix(p));
        format = format.replace("%dname%", p.getDisplayName());
        format = format.replace("%name%", p.getName());
        e.setFormat(ChatColor.translateAlternateColorCodes('&', format));
        format = format.replace("%msg%", e.getMessage());
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent e) {
        if (!e.isCancelled() && !WorldListener.isBuilder(e.getPlayer().getName())) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteractEntity(PlayerInteractEntityEvent e) {
        if (!e.isCancelled() && !WorldListener.isBuilder(e.getPlayer().getName())) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if (!e.isCancelled() && e.getDamager() instanceof Player && !WorldListener.isBuilder(((Player) e.getDamager()).getName())) {
            e.setCancelled(true);
        }
    }
}
