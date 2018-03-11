package me.awesomehub.hub.utils;

import java.lang.reflect.Field;
import java.util.Map;

import me.awesomehub.hub.utils.ReflectionUtils.PackageType;

import org.bukkit.entity.EntityType;

import com.google.common.collect.Maps;

/**
 * Permet d'enregistrer des entitiés customisées.
 * 
 * @author mrgreen33gamer
 * @author Gwennael
 */
public class EntityRegister {
    protected static Field mapStringToClassField, mapClassToStringField,
            mapClassToIdField, mapStringToIdField;
    private static Map<EntityType, Class<?>> customEntities = Maps.newHashMap();

    static {
        try {
            EntityRegister.mapStringToClassField = ReflectionUtils.getField("EntityTypes", PackageType.MINECRAFT_SERVER, true, "c");
            EntityRegister.mapClassToStringField = ReflectionUtils.getField("EntityTypes", PackageType.MINECRAFT_SERVER, true, "d");
            EntityRegister.mapClassToIdField = ReflectionUtils.getField("EntityTypes", PackageType.MINECRAFT_SERVER, true, "f");
            EntityRegister.mapStringToIdField = ReflectionUtils.getField("EntityTypes", PackageType.MINECRAFT_SERVER, true, "g");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Retourne la classe d'une entité customisée.
     * 
     * @param type Le type de l'entité.
     * @return {@link Class}
     */
    public static Class<?> getEntityClass(EntityType type) {
        return EntityRegister.customEntities.get(type);
    }

    /**
     * Ajoute une entité.
     * 
     * @param entityClass La nouvelle classe à utiliser.
     * @param name Le nom de l'entité.
     * @param type Le type de l'entité.
     */
    @SuppressWarnings({ "rawtypes", "unchecked", "deprecation" })
    public static void addCustomEntity(Class entityClass, String name, EntityType type) {
        if (EntityRegister.mapStringToClassField != null && EntityRegister.mapStringToIdField != null && EntityRegister.mapClassToStringField != null && EntityRegister.mapClassToIdField != null) {
            try {
                Map mapStringToClass = (Map) EntityRegister.mapStringToClassField.get(null);
                Map mapStringToId = (Map) EntityRegister.mapStringToIdField.get(null);
                Map mapClasstoString = (Map) EntityRegister.mapClassToStringField.get(null);
                Map mapClassToId = (Map) EntityRegister.mapClassToIdField.get(null);

                mapStringToClass.put(name, entityClass);
                mapStringToId.put(name, Integer.valueOf(type.getTypeId()));
                mapClasstoString.put(entityClass, name);
                mapClassToId.put(entityClass, Integer.valueOf(type.getTypeId()));
                EntityRegister.customEntities.put(type, entityClass);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
