package me.awesomehub.hub.handlers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.awesomehub.hub.AHPlugin;
import me.awesomehub.hub.config.Locale;
import me.awesomehub.hub.utils.ItemBuilder;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Classe qui gère les différents gadgets.
 * 
 * @author Gwennael
 */
public enum Gadget implements Listener {
    ;

    /**
     * Les différents triggers disponibles pour les gadgets.
     * 
     * @author Gwennael
     */
    public enum GadgetTrigger {
        PLAYER_MOVE,
        PLAYER_INTERACT,
        ON_DAMAGE_BY_PLAYER,
        ON_DAMAGE_WITH_PLAYER,
        INTERACT_WITH_ITEM,
        INTERACT_WITH_BLOCK,
        INTERACT_WITH_ENTITY;
    }

    public static abstract class GadgetAction {
        protected Gadget gadget;

        /**
         * Lorsque le gadget est activé.
         * 
         * @param player Le joueur qui l'active.
         */
        public abstract void onStart(Player player);

        /**
         * Lorsqu'un gadget attrape un évenement.
         * 
         * @param event L'évenement attrapé.
         * @param trigger Le trigger associé.
         * @param player Le joueur.
         */
        public abstract void onEvent(Event event, GadgetTrigger trigger, Player player);

        /**
         * Lorsqu'un gadget arrive à sa fin.
         * 
         * @param player Le joueur.
         */
        public abstract void onFinish(Player player);
    }

    public static Map<Player, Gadget> playerGadgets = new HashMap<>();
    public static final String COOLDOWN_KEY = "GADGET_COOLDOWN";

    private GadgetAction action;
    private int cooldown;
    private List<GadgetTrigger> triggers;
    private ItemStack itemStack;

    /**
     * Permet de construire un nouveau gadget.
     * 
     * @param mat L'icône du gadget.
     * @param name Le nom du gadget.
     * @param action L'action à utiliser.
     * @param cooldown Le cooldown.
     * @param description La description.
     * @param triggers Les triggers utilisés.
     */
    private Gadget(Material mat, String name, GadgetAction action, int cooldown, String description, List<GadgetTrigger> triggers) {
        this.action = action;
        action.gadget = this;
        this.cooldown = cooldown;
        this.triggers = triggers;
        itemStack = new ItemBuilder(mat).setTitle(name).addLores(description.split("\n")).build();
        Bukkit.getPluginManager().registerEvents(this, AHPlugin.get());
    }

    /**
     * Démarre l'utilisation d'un gadget.
     * 
     * @param player Le joueur.
     */
    private void onStart(Player player) {
        Gadget.playerGadgets.put(player, this);
        action.onStart(player);
    }

    /**
     * Active un gadget pour un joueur.
     * 
     * @param player Le joueur.
     * @return boolean
     */
    public boolean enableFor(Player player) {
        if (player.hasMetadata(Gadget.COOLDOWN_KEY)) {
            long diff = (System.currentTimeMillis() - player.getMetadata(Gadget.COOLDOWN_KEY).get(0).asLong()) / 1000;
            if (diff < cooldown) {
                player.sendMessage(Locale.get().cooldownMessage.replace("%seconds%", diff + "").replace("%plural%", diff > 1 ? "s" : ""));
                return false;
            }
        }
        player.removeMetadata(Gadget.COOLDOWN_KEY, AHPlugin.get());
        player.setMetadata(Gadget.COOLDOWN_KEY, new FixedMetadataValue(AHPlugin.get(), System.currentTimeMillis()));
        this.onStart(player);
        return true;
    }

    /**
     * Fait arriver un gadget à sa fin.
     * 
     * @param player Le joueur.
     */
    public void onFinish(Player player) {
        this.onFinish(player, 0);
    }

    /**
     * Fait arriver un gadget à sa fin après x ticks.
     * 
     * @param player Le joueur.
     * @param totalTicks Le nombre de ticks.
     */
    public void onFinish(final Player player, long totalTicks) {
        if (totalTicks <= 0) {
            if (Gadget.playerGadgets.remove(player) == this) {
                action.onFinish(player);
            }
        } else {
            new BukkitRunnable() {

                @Override
                public void run() {
                    Gadget.this.onFinish(player);
                }
            }.runTaskLater(AHPlugin.get(), totalTicks);
        }
    }

    /**
     * Quand un joueur quitte.
     * 
     * @param evt
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent evt) {
        if (Gadget.playerGadgets.containsKey(evt.getPlayer()) && Gadget.playerGadgets.get(evt.getPlayer()) == this) {
            this.onFinish(evt.getPlayer());
        }
    }

    /**
     * Quand un joueur est expulsé.
     * 
     * @param evt
     */
    @EventHandler
    public void onPlayerKick(PlayerKickEvent evt) {
        if (Gadget.playerGadgets.containsKey(evt.getPlayer()) && Gadget.playerGadgets.get(evt.getPlayer()) == this) {
            this.onFinish(evt.getPlayer());
        }
    }

    /**
     * Quand un joueur se met à bouger.
     * 
     * @param evt
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent evt) {
        if (triggers.contains(GadgetTrigger.PLAYER_MOVE)) {
            action.onEvent(evt, GadgetTrigger.PLAYER_MOVE, evt.getPlayer());
        }
    }

    /**
     * Quand un joueur intéragit avec un objet.
     * 
     * @param evt
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent evt) {
        if (evt.hasItem() && itemStack.isSimilar(evt.getItem())) {
            evt.setCancelled(true);
            if (!this.enableFor(evt.getPlayer())) return;
        } else {
            if (evt.hasItem() && triggers.contains(GadgetTrigger.INTERACT_WITH_ITEM)) {
                action.onEvent(evt, GadgetTrigger.INTERACT_WITH_ITEM, evt.getPlayer());
            } else if (evt.getAction().name().contains("BLOCK") && triggers.contains(GadgetTrigger.INTERACT_WITH_BLOCK)) {
                action.onEvent(evt, GadgetTrigger.INTERACT_WITH_BLOCK, evt.getPlayer());
            } else if (triggers.contains(GadgetTrigger.PLAYER_INTERACT)) {
                action.onEvent(evt, GadgetTrigger.PLAYER_INTERACT, evt.getPlayer());
            }
        }
    }

    /**
     * Quand un joueur intéragit avec une entité.
     * 
     * @param evt
     */
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent evt) {
        if (triggers.contains(GadgetTrigger.INTERACT_WITH_ENTITY)) {
            action.onEvent(evt, GadgetTrigger.INTERACT_WITH_ENTITY, evt.getPlayer());
        }
    }

    /**
     * Quand un joueur donne ou prend des dégâts à/par une autre entitié.
     * 
     * @param evt
     */
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent evt) {
        if (evt.getDamager() instanceof Player && triggers.contains(GadgetTrigger.ON_DAMAGE_BY_PLAYER)) {
            action.onEvent(evt, GadgetTrigger.ON_DAMAGE_BY_PLAYER, (Player) evt.getDamager());
        }
        if (evt.getEntity() instanceof Player && triggers.contains(GadgetTrigger.ON_DAMAGE_WITH_PLAYER)) {
            action.onEvent(evt, GadgetTrigger.ON_DAMAGE_WITH_PLAYER, (Player) evt.getEntity());
        }
    }
}
