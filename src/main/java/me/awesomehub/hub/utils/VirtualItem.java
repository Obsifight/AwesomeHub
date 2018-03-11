package me.awesomehub.hub.utils;

import me.awesomehub.hub.AHPlugin;
import me.awesomehub.hub.config.Configuration;
import me.awesomehub.hub.config.Locale;
import me.awesomehub.hub.handlers.EffectHandler;
import me.awesomehub.hub.utils.MyDisguise.EntityDisguise;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Objet VirtuelItem qui permet de sauvegarder
 * des items et leur associer des actions.
 * 
 * @author Mac'
 */
public class VirtualItem implements Listener {

    /**
     * Les différentes actions disponibles.
     */
    public static enum ActionType {
        OPEN_MENU,
        PRINT_MESSAGE,
        CONNECT,
        TOGGLE_PET,
        TOGGLE_PLAYER,
        NOTHING,
        COMMAND,
        TOGGLE_DISGUISE,
        TOGGLE_EFFECT,
        TELEPORTATION;
    }

    private ItemStack item;
    private String id;
    private ActionType actionType;
    private String target;

    /**
     * Construit un VirtualItem.
     * 
     * @param id L'id qui sera utilisé pour appeler l'item.
     * @param item L'ItemStack.
     * @param actionType Le type de l'action.
     * @param target Le paramètre de l'action.
     */
    public VirtualItem(String id, ItemStack item, ActionType actionType, String target) {
        this.id = id;
        this.item = item;
        this.actionType = actionType;
        this.target = target;
    }

    /**
     * Retourne l'id.
     * 
     * @return string
     */
    public String getId() {
        return id;
    }

    /**
     * Retourne l'ItemStack.
     * 
     * @return ItemStack
     */
    public ItemStack getItem() {
        return item;
    }

    /**
     * Retourne le type d'action
     * 
     * @return ActionType
     */
    public ActionType getAction() {
        return actionType;
    }

    /**
     * Retourne la target de l'action
     * 
     * @return String
     */
    public String getTarget() {
        return target;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent evt) {
        if (evt.getAction() != Action.RIGHT_CLICK_AIR && evt.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!evt.hasItem() || !item.isSimilar(evt.getItem())) return;

        this.performAction(actionType, evt.getPlayer());
        evt.setCancelled(true);
        evt.getPlayer().updateInventory();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null || e.getClickedInventory().getType() != InventoryType.PLAYER) return;
        if (e.getCurrentItem() == null || !item.isSimilar(e.getCurrentItem())) return;

        this.performAction(actionType, (Player) e.getWhoClicked());
        e.setCancelled(true);
    }

    public void performAction(ActionType type, Player player) {
        if (type != null) {
            switch (type) {
            case OPEN_MENU: {
                VirtualMenu menu = AHPlugin.get().getMenuHandler().getMenus().get(target);
                if (menu != null) {
                    player.openInventory(menu.createInventory());
                } else {
                    player.sendMessage(Locale.get().menuDoesntExist.replaceAll("%menu%", target));
                }
                break;
            }
            case PRINT_MESSAGE: {
                for (String element : target.split("\n")) 
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', element));
                break;
            }
            case COMMAND: {
            	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), target.replaceAll("%player%", player.getName()));
            	break;
            }
            case CONNECT: {
                if (Configuration.get().bungeeSupport && AHPlugin.get().getNetworkHandler().getServerList().contains(target)) {
                    AHPlugin.get().getNetworkHandler().sendPlayerTo(player, target);
                } else {
                    player.sendMessage(Locale.get().serverDoesntExist.replaceAll("%server%", target));
                }
                break;
            }
            case NOTHING: {
                break;
            }
            case TOGGLE_PLAYER: {
                AHPlugin.get().getVisibilityHandler().togglePlayer(player);
                break;
            }
            case TOGGLE_DISGUISE: {
                if (EntityDisguise.getDisguises().containsKey(target.toUpperCase())) {
                    AHPlugin.get().getDisguiseHandler().togglePlayer(player, EntityDisguise.valueOf(target.toUpperCase()));
                } else {
                    String disguises = StringUtils.join(EntityDisguise.getDisguises().keySet().toArray(), ", ");
                    player.sendMessage(Locale.get().disguiseDoesntExist.replaceAll("%disguises%", disguises).replaceAll("%disguise%", target));
                }
                break;
            }
            case TOGGLE_EFFECT: {
                if (EffectHandler.getEffects().containsKey(target)) {
                    AHPlugin.get().getEffectHandler().togglePlayer(player, target);
                } else {
                    String effects = StringUtils.join(EffectHandler.getEffects().keySet().toArray(), ", ");
                    player.sendMessage(Locale.get().disguiseDoesntExist.replaceAll("%effects%", effects).replaceAll("%effect%", target));
                }
                break;
            }
            case TOGGLE_PET: {
                if (AHPlugin.get().getPetHandler() != null) {
                    try {
                        EntityType petType = EntityType.valueOf(target.toUpperCase());
                        AHPlugin.get().getPetHandler().togglePlayer(player, petType);
                    } catch (Exception e) {
                        String pets = "";
                        for (EntityType entityType : EntityType.values()) {
                            if (entityType.isAlive()) {
                                pets = new StringBuilder().append(pets).append(entityType.name().toLowerCase()).append(", ").toString();
                            }
                        }
                        if (!pets.isEmpty()) {
                            pets = pets.substring(0, pets.length() - 2);
                        }
                        player.sendMessage(Locale.get().petDoesntExist.replaceAll("%pets%", pets).replaceAll("%pet%", target));
                    }
                }
                break;
            }
            case TELEPORTATION: {
                Location location = null;
                if (target.startsWith("{")) {
                    location = AHPlugin.getGson().fromJson(target, Location.class);
                } else if (AHPlugin.get().getWarpHandler().listWarps().containsKey(target)) {
                    location = AHPlugin.get().getWarpHandler().getWarpLocation(target);
                } else if (target.contains("_")) {
                    location = MiscUtils.stringToLocation(target);
                }
                if (location != null) {
                    player.teleport(location);
                    player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1F, 1F);
                }
                break;
            }
            default: {
                break;
            }
            }
        }
    }
}
