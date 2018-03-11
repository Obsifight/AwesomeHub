package me.awesomehub.hub.handlers;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import me.awesomehub.hub.AHPlugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.google.common.collect.Maps;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.effect.BleedEffect;
import de.slikey.effectlib.effect.CircleEffect;
import de.slikey.effectlib.effect.CloudEffect;
import de.slikey.effectlib.effect.DiscoBallEffect;
import de.slikey.effectlib.effect.DonutEffect;
import de.slikey.effectlib.effect.DragonEffect;
import de.slikey.effectlib.effect.FlameEffect;
import de.slikey.effectlib.effect.HeartEffect;
import de.slikey.effectlib.effect.IconEffect;
import de.slikey.effectlib.effect.LineEffect;
import de.slikey.effectlib.effect.LoveEffect;
import de.slikey.effectlib.effect.MusicEffect;
import de.slikey.effectlib.effect.ShieldEffect;
import de.slikey.effectlib.effect.SmokeEffect;
import de.slikey.effectlib.effect.SphereEffect;
import de.slikey.effectlib.effect.StarEffect;
import de.slikey.effectlib.effect.TornadoEffect;
import de.slikey.effectlib.effect.TraceEffect;
import de.slikey.effectlib.effect.TurnEffect;
import de.slikey.effectlib.effect.VortexEffect;
import de.slikey.effectlib.effect.WarpEffect;

/**
 * Classe qui gère les effets des joueurs.
 * 
 * @author Mac'
 * @author Gwennael
 */
public class EffectHandler implements Listener {

    /**
     * Associe les classes d'effets à un nom.
     * 
     * @var {@link Map}
     */
    private static final Map<String, Class<? extends Effect>> BY_NAME = Maps.newHashMap();

    static {
        EffectHandler.BY_NAME.put("bleed", BleedEffect.class);
        EffectHandler.BY_NAME.put("circle", CircleEffect.class);
        EffectHandler.BY_NAME.put("cloud", CloudEffect.class);
        EffectHandler.BY_NAME.put("discoball", DiscoBallEffect.class);
        EffectHandler.BY_NAME.put("donut", DonutEffect.class);
        EffectHandler.BY_NAME.put("dragon", DragonEffect.class);
        EffectHandler.BY_NAME.put("flame", FlameEffect.class);
        EffectHandler.BY_NAME.put("heart", HeartEffect.class);
        EffectHandler.BY_NAME.put("icon", IconEffect.class);
        EffectHandler.BY_NAME.put("line", LineEffect.class);
        EffectHandler.BY_NAME.put("love", LoveEffect.class);
        EffectHandler.BY_NAME.put("music", MusicEffect.class);
        EffectHandler.BY_NAME.put("shield", ShieldEffect.class);
        EffectHandler.BY_NAME.put("smoke", SmokeEffect.class);
        EffectHandler.BY_NAME.put("sphere", SphereEffect.class);
        EffectHandler.BY_NAME.put("star", StarEffect.class);
        EffectHandler.BY_NAME.put("tornado", TornadoEffect.class);
        EffectHandler.BY_NAME.put("trace", TraceEffect.class);
        EffectHandler.BY_NAME.put("turn", TurnEffect.class);
        EffectHandler.BY_NAME.put("vortex", VortexEffect.class);
        EffectHandler.BY_NAME.put("warp", WarpEffect.class);
    }

    public static Map<String, Class<? extends Effect>> getEffects() {
        return EffectHandler.BY_NAME;
    }

    /**
     * Le manager des effets.
     * 
     * @var {@link EffectManager}
     */
    private EffectManager effectManager;

    /**
     * Les effets par nom de joueur.
     * 
     * @var {@link Map}
     */
    private Map<String, Effect> playerEffects;

    /**
     * Construit le {@link PlayerVisibilityHandler}.
     */
    public EffectHandler() {
        effectManager = new EffectManager(AHPlugin.get());
        playerEffects = Maps.newHashMap();
        EffectManager.initialize();
        Bukkit.getPluginManager().registerEvents(this, AHPlugin.get());
    }

    /**
     * Ajoute à la liste des playerToggled si il n'y est pas.
     * Le supprime si il y est.
     * 
     * @param p Le joueur.
     * @param effectName Le nom de l'effet.
     */
    public void togglePlayer(Player p, String effectName) {
        if (!playerEffects.containsKey(p.getName())) {
            Effect effect = this.addPlayerEffect(p, effectName);
            playerEffects.put(p.getName(), effect);
        } else {
            if (EffectHandler.BY_NAME.get(effectName) != this.removeEffect(p).getClass()) {
                this.togglePlayer(p, effectName);
            }
        }
    }

    /**
     * Retire l'effet d'un joueur.
     * 
     * @param p Le joueur.
     * @return {@link Effect}
     */
    public Effect removeEffect(Player p) {
        Effect effect = null;
        if (playerEffects.containsKey(p.getName())) {
            effect = playerEffects.remove(p.getName());
            effectManager.done(effect);
        }
        return effect;
    }

    /**
     * Ajoute un effect à un joueur.
     * HIPSTER METHOD.
     * 
     * @param p Le joueur.
     * @param effectName Le nom de l'effet.
     * @return {@link Effect}
     */
    private Effect addPlayerEffect(Player p, String effectName) {
        Class<? extends Effect> clazz = EffectHandler.BY_NAME.get(effectName);
        if (clazz != null) {
            try {
                Effect effect = clazz.getConstructor(EffectManager.class).newInstance(effectManager);
                effect.setEntity(p);
                effect.infinite();
                effect.start();
                return effect;
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Quand un joueur se déconnecte.
     * 
     * @param evt
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent evt) {
        this.removeEffect(evt.getPlayer());
    }

    /**
     * Quand un joueur est expulsé du serveur.
     * 
     * @param evt
     */
    @EventHandler
    public void onPlayerKick(PlayerKickEvent evt) {
        this.removeEffect(evt.getPlayer());
    }
}
