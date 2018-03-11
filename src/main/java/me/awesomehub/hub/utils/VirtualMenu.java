package me.awesomehub.hub.utils;

import java.util.Map;
import java.util.Map.Entry;

import me.awesomehub.hub.AHPlugin;
import me.awesomehub.hub.config.Configuration;
import me.awesomehub.hub.utils.VirtualItem.ActionType;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import com.google.common.collect.Maps;

/**
 * Classe qui permet de créer des menus.
 * 
 * @author Mac'
 */
public class VirtualMenu implements Listener {

    /**
     * L'interface.
     * 
     * @var Map
     */
    private Map<Integer, String> display;
    private int size;
    private String id;
    private String name;

    /**
     * Construit un VirtualMenu.
     * 
     * @param name Le nom du menu.
     * @param size La taille du menu.
     * @param id L'id du menu.
     */
    public VirtualMenu(String name, int size, String id) {
        this.size = size;
        this.name = name;
        this.id = id;
        display = Maps.newHashMap();
    }

    /**
     * Ajoute un item à l'interface.
     * 
     * @param slot Le slot.
     * @param item L'item.
     */
    public void addItemToDisplay(int slot, VirtualItem item) {
        display.put(slot, item.getId());
    }

    /**
     * Créé l'inventaire.
     * 
     * @return Inventory
     */
    public Inventory createInventory() {
        Inventory inv = Bukkit.createInventory(new FakeHolder(id), InventoryUtils.getPerfectColumnNumber(size) * 9, name);
        for (Entry<Integer, String> entry : display.entrySet()) {
            VirtualItem temp = AHPlugin.get().getItemHandler().getItems().get(entry.getValue());
            if (temp.getAction() == ActionType.CONNECT && Configuration.get().bungeeSupport && Configuration.get().showPlayerCountOnItem && AHPlugin.get().getNetworkHandler().getBungeeServerCount().containsKey(temp.getTarget())) {
                temp.getItem().setAmount(AHPlugin.get().getNetworkHandler().getBungeeServerCount().get(temp.getTarget()));
            }
            inv.setItem(entry.getKey(), temp.getItem());
        }
        return inv;
    }

    /**
     * Applique la barre d'actions.
     * 
     * @param p Le joueur qui va recevoir la barre d'actions.
     */
    public void applyHotbar(Player p) {
        Inventory inv = p.getInventory();
        for (Entry<Integer, String> entry : display.entrySet()) {
            inv.setItem(entry.getKey(), AHPlugin.get().getItemHandler().getItems().get(entry.getValue()).getItem());
        }
    }

    /**
     * Retourne l'id.
     * 
     * @return int
     */
    public String getId() {
        return id;
    }

    /**
     * Retourne le nom du menu.
     * 
     * @return string
     */
    public String getName() {
        return name;
    }

    /**
     * Retourne la taille du menu.
     * 
     * @return int
     */
    public int getSize() {
        return size;
    }

    /**
     * Retourne l'interface.
     * 
     * @return {@link Map}
     */
    public Map<Integer, String> getCurrentDisplay() {
        return display;
    }

    /**
     * Vide l'interface.
     */
    public void resetCurrentDisplay() {
        display.clear();
    }

    /**
     * Quand un joueur clique sur un item.
     * 
     * @param e
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null || e.getClickedInventory().getType() != InventoryType.CHEST) return;
        if (!(e.getClickedInventory().getHolder() instanceof FakeHolder)) return;
        FakeHolder holder = (FakeHolder) e.getClickedInventory().getHolder();
        if (!holder.getId().equals(id)) return;

        e.setCancelled(true);

        if (display.containsKey(e.getSlot())) {
            VirtualItem item = AHPlugin.get().getItemHandler().getItems().get(display.get(e.getSlot()));
            if (item != null)
            	item.performAction(item.getAction(), (Player) e.getWhoClicked());
        }
    }
}
