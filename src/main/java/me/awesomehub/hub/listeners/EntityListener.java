package me.awesomehub.hub.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTargetEvent;

/**
 * Classe qui gère les évenements liés aux entitiés.
 * 
 * @author Gwennael
 */
public class EntityListener implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageEvent evt) {
        if (evt.getCause() != DamageCause.CUSTOM) {
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent evt) {
        evt.setCancelled(true);
    }

    @EventHandler
    public void onEntityBreakDoor(EntityBreakDoorEvent evt) {
        evt.setCancelled(true);
    }
}
