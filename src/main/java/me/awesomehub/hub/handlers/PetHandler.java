package me.awesomehub.hub.handlers;

import java.util.Map;

import me.awesomehub.hub.AHPlugin;
import me.awesomehub.hub.api.PetMakerAPI;
import me.awesomehub.hub.utils.EntityRegister;
import me.awesomehub.hub.utils.ReflectionUtils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Maps;

/**
 * Classe qui gère les animaux de compagnie des joueurs.
 * 
 * @author Gwennael
 */
public class PetHandler implements Listener {

    /**
     * Les animaux de compagnie avec leur propriétaire pour clé.
     * 
     * @var {@link Map}
     */
    private Map<Player, LivingEntity> playerPets;

    /**
     * L'instance du PetMakerAPI.
     * 
     * @var {@link me.awesomehub.hub.api.PetMakerAPI}
     */
    private PetMakerAPI petMaker;

    /**
     * Construit le PetHandler.
     * 
     * @var petMaker L'instance du PetMakerAPI.
     */
    public PetHandler(PetMakerAPI petMaker) {
        playerPets = Maps.newHashMap();
        this.petMaker = petMaker;

        Bukkit.getPluginManager().registerEvents(this, AHPlugin.get());
    }

    /**
     * Créé ou supprime un animal de compagnie.
     * 
     * @param player Un joueur.
     * @param type Le type d'animal de compagnie.
     */
    public void togglePlayer(Player player, EntityType type) {
        if (!playerPets.containsKey(player)) {
            this.makePet(player, type);
        } else {
            if (this.removePet(player).getType() != type) {
                this.togglePlayer(player, type);
            }
        }
    }

    /**
     * Créé un animal de compagnie.
     * 
     * @param owner Le joueur.
     * @param type Le type d'animal de compagnie.
     */
    private void makePet(Player owner, EntityType type) {
        try {
            Location location = owner.getLocation();
            Class<?> customClass = EntityRegister.getEntityClass(type);
            if (customClass != null) {
                Object world = ReflectionUtils.invokeMethod(owner.getWorld(), "getHandle");
                Object entity = ReflectionUtils.instantiateObject(customClass, world);
                ReflectionUtils.invokeMethod(entity, "setPosition", location.getX(), location.getY(), location.getZ());
                ReflectionUtils.invokeMethod(world, "addEntity", entity, SpawnReason.CUSTOM);
                LivingEntity livingEntity = (LivingEntity) ReflectionUtils.invokeMethod(entity, "getBukkitEntity");
                petMaker.setOwner((LivingEntity) ReflectionUtils.invokeMethod(entity, "getBukkitEntity"), owner);
                livingEntity.setCustomName(owner.getDisplayName() + " pet's");
                playerPets.put(owner, livingEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Supprime l'animal de compagnie d'un joueur.
     * 
     * @param player Le joueur
     * @return {@link LivingEntity}
     */
    public LivingEntity removePet(Player player) {
        LivingEntity entity = null;
        if (playerPets.containsKey(player)) {
            entity = playerPets.remove(player);
            entity.remove();
        }
        return entity;
    }

    @EventHandler
    public void onPlayerChat(final AsyncPlayerChatEvent evt) {
        try {
            final EntityType type = EntityType.valueOf(evt.getMessage().toUpperCase());
            new BukkitRunnable() {

                @Override
                public void run() {
                    PetHandler.this.togglePlayer(evt.getPlayer(), type);
                }
            }.runTask(AHPlugin.get());
        } catch (Exception e) {}
    }

    /**
     * Quand le plugin est désactivé.
     * 
     * @param evt
     */
    @EventHandler
    public void onPluginDisable(PluginDisableEvent evt) {
        if (evt.getPlugin() == AHPlugin.get()) {
            for (Player player : playerPets.keySet()) {
                this.removePet(player);
            }
        }
    }

    /**
     * Quand un joueur clique sur son animal de compagnie.
     * 
     * @param evt
     */
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent evt) {
        Entity ent = evt.getRightClicked();
        if (ent == playerPets.get(evt.getPlayer())) {
            ent.setPassenger(evt.getPlayer());
        }
    }

    /**
     * Quand un joueur se déconnecte.
     * 
     * @param evt
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent evt) {
        this.removePet(evt.getPlayer());
    }

    /**
     * Quand un joueur est expulsé du serveur.
     * 
     * @param evt
     */
    @EventHandler
    public void onPlayerKick(PlayerKickEvent evt) {
        this.removePet(evt.getPlayer());
    }
}
