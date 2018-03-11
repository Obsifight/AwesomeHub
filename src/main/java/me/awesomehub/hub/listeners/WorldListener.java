package me.awesomehub.hub.listeners;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.world.StructureGrowEvent;

import com.google.common.collect.Lists;

/**
 * Classe qui gère les évenements liés au monde.
 * 
 * @author Mac'
 */
public class WorldListener implements Listener {

    /**
     * Liste des joueurs pouvant construire.
     * 
     * @var {@link List}
     */
    private static List<String> currentBuilder;

    /**
     * Permet de savoir si le joueur a activé le mode "constructeur".
     * 
     * @param name Le nom du joueur
     * @return boolean
     */
    public static boolean isBuilder(String name) {
        return WorldListener.currentBuilder.contains(name);
    }

    /**
     * Permet d'ajouter un joueur dans la liste des constructeurs.
     * 
     * @param name Le nom du joueur.
     */
    public static void addBuilder(String name) {
        WorldListener.currentBuilder.add(name);
    }

    /**
     * Permet de retirer un joueur de la liste des constructeurs.
     * 
     * @param name Le nom du joueur.
     */
    public static void removeBuilder(String name) {
        WorldListener.currentBuilder.remove(name);
    }

    /**
     * Construit le WorldListener.
     */
    public WorldListener() {
        WorldListener.currentBuilder = Lists.newArrayList();
    }

    @EventHandler
    public void onBlockGrow(BlockGrowEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockRedstone(BlockRedstoneEvent event) {
        event.setNewCurrent(event.getOldCurrent());
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockForm(BlockFormEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onStructureGrow(StructureGrowEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() != SpawnReason.CUSTOM) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!WorldListener.currentBuilder.contains(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!WorldListener.currentBuilder.contains(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    /**
     * Prépare le monde pour éviter le spawn de mob, le cycle jour/nuit etc.
     */
    public static void prepareWorld() {
        World world = Bukkit.getWorlds().get(0);
        world.getEntities().clear();
        world.setStorm(false);
        world.setThundering(false);
        world.setWeatherDuration(Integer.MAX_VALUE);
        world.setMonsterSpawnLimit(0);
        world.setAnimalSpawnLimit(0);
        world.setAmbientSpawnLimit(0);
        world.setTicksPerAnimalSpawns(Integer.MAX_VALUE);
        world.setTicksPerMonsterSpawns(Integer.MAX_VALUE);
        world.setGameRuleValue("doDaylightCycle", "false");
        world.setFullTime(12000);
        world.setDifficulty(Difficulty.PEACEFUL);
        world.setPVP(false);
    }
}
