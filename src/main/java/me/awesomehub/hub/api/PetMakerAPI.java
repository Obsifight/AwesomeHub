package me.awesomehub.hub.api;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import me.awesomehub.hub.utils.EntityRegister;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * Permet de créer des animaux de compagnie.
 * 
 * @author BtoBastian
 * @author Gwennael
 */
public abstract class PetMakerAPI {

    /**
     * Si le NMS a échoué ou non.
     * 
     * @var boolean
     */
    protected static boolean nmsFailed = false;

    /**
     * Les classes NMS à utiliser.
     * 
     * @var {@link Class}
     */
    protected static Class<?> CLASS_ENTITY_INSENTIENT;
    protected static Class<?> CLASS_PATHFINDER_GOAL;
    protected static Class<?> CLASS_PATHFINDER_GOAL_FLOAT;
    protected static Class<?> CLASS_PATHFINDER_GOAL_SELECTOR;
    protected static Class<?> CLASS_CRAFT_LIVING_ENTITY;
    protected static Class<?> CLASS_UNSAFE_LIST;
    protected static Class<?> CLASS_PATHFINDER_GOAL_WALK_TO_TILE;

    /**
     * Les champs NMS à utiliser.
     */
    protected static Field gsa;
    protected static Field goalSelector;
    protected static Field targetSelector;

    /**
     * La version de NMS.
     * 
     * @var {@link String}
     */
    public static String VERSION;

    /**
     * Défini le variables de base.
     */
    static {
        String path = Bukkit.getServer().getClass().getPackage().getName();
        PetMakerAPI.VERSION = path.substring(path.lastIndexOf(".") + 1, path.length());

        try {
            PetMakerAPI.CLASS_ENTITY_INSENTIENT = Class.forName("net.minecraft.server." + PetMakerAPI.VERSION + ".EntityInsentient");
            PetMakerAPI.CLASS_PATHFINDER_GOAL = Class.forName("net.minecraft.server." + PetMakerAPI.VERSION + ".PathfinderGoal");
            PetMakerAPI.CLASS_PATHFINDER_GOAL_FLOAT = Class.forName("net.minecraft.server." + PetMakerAPI.VERSION + ".PathfinderGoalFloat");
            PetMakerAPI.CLASS_PATHFINDER_GOAL_SELECTOR = Class.forName("net.minecraft.server." + PetMakerAPI.VERSION + ".PathfinderGoalSelector");
            PetMakerAPI.CLASS_CRAFT_LIVING_ENTITY = Class.forName("org.bukkit.craftbukkit." + PetMakerAPI.VERSION + ".entity.CraftLivingEntity");
            PetMakerAPI.CLASS_UNSAFE_LIST = Class.forName("org.bukkit.craftbukkit." + PetMakerAPI.VERSION + ".util.UnsafeList");

            PetMakerAPI.gsa = PetMakerAPI.CLASS_PATHFINDER_GOAL_SELECTOR.getDeclaredField("b");
            PetMakerAPI.gsa.setAccessible(true);

            PetMakerAPI.goalSelector = PetMakerAPI.CLASS_ENTITY_INSENTIENT.getDeclaredField("goalSelector");
            PetMakerAPI.goalSelector.setAccessible(true);

            PetMakerAPI.targetSelector = PetMakerAPI.CLASS_ENTITY_INSENTIENT.getDeclaredField("targetSelector");
            PetMakerAPI.targetSelector.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
            PetMakerAPI.nmsFailed = true;
        }
    }

    /**
     * Construit une instance de PetMakerAPI.
     * 
     * @param types Les types d'entités à créer.
     */
    public PetMakerAPI(EntityType[] types) {
        try {
            this.loadPathfinderClass();

            for (EntityType type : types) {
                String className = "Entity" + type.getEntityClass().getName().replace("org.bukkit.entity.", "");
                String customName = "Custom" + className;
                CtClass clazz = ClassPool.getDefault().get("net.minecraft.server." + PetMakerAPI.VERSION + "." + className);
                CtClass subClass = ClassPool.getDefault().makeClass(customName, clazz);

                this.makeCustomEntity(type, clazz, subClass);

                EntityRegister.addCustomEntity(subClass.toClass(), subClass.getName(), type);
            }
        } catch (Exception e) {
            e.printStackTrace();
            PetMakerAPI.nmsFailed = true;
        }
    }

    /**
     * Créé les classes des entités.
     * 
     * @param type Le type d'entité à créer.
     * @param clazz La classe de l'entité.
     * @param subClass La nouvelle classe à créer.
     * @throws Exception
     */
    protected abstract void makeCustomEntity(EntityType type, CtClass clazz, CtClass subClass) throws Exception;

    /**
     * Créé la classe pour le pathfinding.
     * 
     * @param clazz La classe du PathfinderGoal..
     * @param subClass Le nouveau Pathfinde à créer.
     * @throws Exception
     */
    protected abstract void makePathfinderClass(CtClass clazz, CtClass subClass) throws Exception;

    /**
     * Charge la classe du Pathfinder.
     * 
     * @throws Exception
     */
    private void loadPathfinderClass() throws Exception {
        CtClass clazz = ClassPool.getDefault().get("net.minecraft.server." + PetMakerAPI.VERSION + ".PathfinderGoal");
        CtClass subClass = ClassPool.getDefault().makeClass("PathfinderGoalWalktoTile", clazz);

        CtField entityField = CtField.make("private net.minecraft.server." + PetMakerAPI.VERSION + ".EntityInsentient entity;", subClass);
        CtField pathField = CtField.make("private net.minecraft.server." + PetMakerAPI.VERSION + ".PathEntity path;", subClass);
        CtField playerField = CtField.make("private org.bukkit.entity.Player player;", subClass);

        subClass.addField(entityField);
        subClass.addField(pathField);
        subClass.addField(playerField);

        this.makePathfinderClass(clazz, subClass);

        PetMakerAPI.CLASS_PATHFINDER_GOAL_WALK_TO_TILE = subClass.toClass();
    }

    /**
     * Défini un animal de compagnie.
     * 
     * @param entity L'entité - l'animal de compagnie.
     * @param owner Le propriétaire.
     * @return boolean
     */
    public boolean setOwner(LivingEntity entity, Player owner) {
        if (PetMakerAPI.nmsFailed) return false;
        try {
            Object nmsEntity = PetMakerAPI.CLASS_CRAFT_LIVING_ENTITY.getMethod("getHandle").invoke(entity);

            Object goal = PetMakerAPI.goalSelector.get(nmsEntity);
            Object target = PetMakerAPI.targetSelector.get(nmsEntity);

            PetMakerAPI.gsa.set(goal, PetMakerAPI.CLASS_UNSAFE_LIST.newInstance());
            PetMakerAPI.gsa.set(target, PetMakerAPI.CLASS_UNSAFE_LIST.newInstance());

            goal.getClass().getMethod("a", int.class, PetMakerAPI.CLASS_PATHFINDER_GOAL).invoke(goal, 0, PetMakerAPI.CLASS_PATHFINDER_GOAL_FLOAT.getConstructor(PetMakerAPI.CLASS_ENTITY_INSENTIENT).newInstance(nmsEntity));
            goal.getClass().getMethod("a", int.class, PetMakerAPI.CLASS_PATHFINDER_GOAL).invoke(goal, 1, PetMakerAPI.CLASS_PATHFINDER_GOAL_WALK_TO_TILE.getConstructor(Object.class, Player.class).newInstance(nmsEntity, owner));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException | InstantiationException e) {
            e.printStackTrace();
            PetMakerAPI.nmsFailed = true;
            return false;
        }
        return true;
    }
}
