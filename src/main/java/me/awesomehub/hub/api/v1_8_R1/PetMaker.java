package me.awesomehub.hub.api.v1_8_R1;

import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import me.awesomehub.hub.api.PetMakerAPI;

import org.bukkit.entity.EntityType;

/**
 * Gestions des animaux de compagnie pour la v1_8_R1.
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
            "public void g(float sideMot, float forMot) { " +
                "if (this.passenger == null || !(this.passenger instanceof net.minecraft.server." + PetMakerAPI.VERSION + ".EntityHuman)) { " +
                    "super.g(sideMot, forMot); " +
                    "this.S = 0.5F; " +
                    "return; " +
                 "} " +
                 "net.minecraft.server." + PetMakerAPI.VERSION + ".EntityHuman human = (net.minecraft.server." + PetMakerAPI.VERSION + ".EntityHuman) this.passenger; " +
                 "this.lastYaw = this.yaw = this.passenger.yaw; " +
                 "this.pitch = this.passenger.pitch * 0.5F; " +
                 "this.setYawPitch(this.yaw, this.pitch); " +
                 "this.aI = this.aG = this.yaw; " +
                 "this.S = 1.0F; " +
                 "sideMot = ((net.minecraft.server." + PetMakerAPI.VERSION + ".EntityLiving) this.passenger).aX * 0.5F; " +
                 "forMot = ((net.minecraft.server." + PetMakerAPI.VERSION + ".EntityLiving) this.passenger).aY; " +
                 "if (forMot <= 0.0F) { " +
                     "forMot *= 0.25F; " +
                 "} " +
                 "sideMot *= 0.75F; " +
                 "float speed = 0.2F; " +
                 "this.j(speed); " +
                 "super.g(sideMot, forMot); " +
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
                "net.minecraft.server." + PetMakerAPI.VERSION + ".Navigation localNavigation = (net.minecraft.server." + PetMakerAPI.VERSION + ".Navigation) this.entity.getNavigation();" +
                "boolean flag = localNavigation.g(); " +
                "localNavigation.b(false); " +
                "this.path = this.entity.getNavigation().a(location.getX() + 1, location.getY(), location.getZ() + 1); " +
                "localNavigation.b(flag); " +
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
