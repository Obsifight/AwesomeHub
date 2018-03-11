package me.awesomehub.hub.api.v1_7_R4;

import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import me.awesomehub.hub.api.PetMakerAPI;

import org.bukkit.entity.EntityType;

/**
 * Gestions des animaux de compagnie pour la v1_7_R4.
 * 
 * @author BtoBastian
 * @author Gwennael
 */
public class PetMaker extends PetMakerAPI {

    /**
     * Construit une instance du PetMaker.
     * 
     * @param types Les types d'entités à créer.
     */
    public PetMaker(EntityType[] types) {
        super(types);
    }

    /*
     * (non-Javadoc)
     * @see me.awesomehub.hub.api.PetMakerAPI#makeCustomEntity(org.bukkit.entity.EntityType, javassist.CtClass, javassist.CtClass)
     */
    @Override
    protected void makeCustomEntity(EntityType type, CtClass clazz, CtClass subClass) throws Exception {
        // @formatter:off
        CtConstructor constructor = CtNewConstructor.make(
           "public " + subClass.getName() + "(net.minecraft.server." + PetMakerAPI.VERSION + ".World world) { " +
                "super(world); " +
            "} ", subClass);
        subClass.addConstructor(constructor);

        CtMethod eMethod = CtMethod.make(
            "public void e(float sideMot, float forMot) { " +
                "if (this.passenger == null || !(this.passenger instanceof net.minecraft.server." + PetMakerAPI.VERSION + ".EntityHuman)) { " +
                    "super.e(sideMot, forMot); " +
                    "this.W = 0.5F; " +
                    "return; " +
                 "} " +
                 "net.minecraft.server." + PetMakerAPI.VERSION + ".EntityHuman human = (net.minecraft.server." + PetMakerAPI.VERSION + ".EntityHuman) this.passenger; " +
                 "this.lastYaw = this.yaw = this.passenger.yaw; " +
                 "this.pitch = this.passenger.pitch * 0.5F; " +
                 "this.b(this.yaw, this.pitch); " +
                 "this.aO = this.aM = this.yaw; " +
                 "this.W = 1.0F; " +
                 "sideMot = ((net.minecraft.server." + PetMakerAPI.VERSION + ".EntityLiving) this.passenger).bd * 0.5F; " +
                 "forMot = ((net.minecraft.server." + PetMakerAPI.VERSION + ".EntityLiving) this.passenger).be; " +
                 "if (forMot <= 0.0F) { " +
                     "forMot *= 0.25F; " +
                 "} " +
                 "sideMot *= 0.75F; " +
                 "float speed = 0.2F; " +
                 "this.i(speed); " +
                 "super.e(sideMot, forMot); " +
             "} ", subClass);
        subClass.addMethod(eMethod);
        // @formatter:on
    }

    @Override
    protected void makePathfinderClass(CtClass clazz, CtClass subClass) throws Exception {
        // @formatter:off
        CtConstructor constructor = CtNewConstructor.make(
            "public PathfinderGoalWalktoTile(Object entitycreature, org.bukkit.entity.Player player) { " +
                "this.entity = (net.minecraft.server." + PetMakerAPI.VERSION + ".EntityInsentient) entitycreature; " +
                "this.player = player; " +
            "} "
        , subClass);
        subClass.addConstructor(constructor);

        CtMethod aMethod = CtNewMethod.make(
            "public boolean a() { " +
                "if (player == null || !player.isOnline()) { " +
                    "return this.path != null; " +
                "} " +
                "org.bukkit.Location location = player.getLocation(); " +
                "if (location.distanceSquared(this.entity.getBukkitEntity().getLocation()) >= 50) { " +
                    "this.entity.getBukkitEntity().teleport(location.add(1D, 0D, 1D)); " +
                    "return false; " +
                "} " +
                "boolean flag = this.entity.getNavigation().c(); " +
                "this.entity.getNavigation().b(false); " +
                "this.path = this.entity.getNavigation().a(location.getX() + 1, location.getY(), location.getZ() + 1); " +
                "this.entity.getNavigation().b(flag); " +
                "if (this.path != null) { " +
                    "this.c(); " +
                "} " +
                "return this.path != null; " +
            "} ", subClass);
        subClass.addMethod(aMethod);

        CtMethod cMethod = CtNewMethod.make(
            "public void c() { " +
                "this.entity.getNavigation().a(this.path, 1.35D); " +
            "} "
        , subClass);
        subClass.addMethod(cMethod);
        // @formatter:on
    }
}
