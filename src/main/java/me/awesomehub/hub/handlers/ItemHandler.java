package me.awesomehub.hub.handlers;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import me.awesomehub.hub.AHPlugin;
import me.awesomehub.hub.utils.VirtualItem;

import org.bukkit.Bukkit;

import com.google.common.collect.Maps;

/**
 * Classe qui permet de gérer les items.
 * 
 * @author Mac'
 */
public class ItemHandler extends SavableHandler {
    private static final File ITEMS_FILE = new File(AHPlugin.get().getDataFolder(), "items.json");

    /**
     * Les items avec leur id pour clé.
     * 
     * @var Map
     */
    private Map<String, VirtualItem> items;

    /**
     * Construit un ItemHandler.
     */
    public ItemHandler() {
        super(ItemHandler.ITEMS_FILE);

        for (Entry<String, VirtualItem> item : items.entrySet()) {
            Bukkit.getPluginManager().registerEvents(item.getValue(), AHPlugin.get());
        }
    }

    /**
    * Charge tous les items.
    */
    @Override
    public void loadAll() {
        items = Maps.newHashMap();

        try {
            VirtualItem[] items = AHPlugin.getGson().fromJson(new FileReader(saveFile), VirtualItem[].class);
            if (items != null) {
                for (VirtualItem item : items) {
                    this.items.put(item.getId(), item);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        AHPlugin.get().getLogger().info("Successfully loaded " + items.size() + " items.");
    }

    /**
     * Sauvegarde tous les items.
     */
    @Override
    public void saveAll() {
        try {
            FileWriter writer = new FileWriter(saveFile);
            AHPlugin.getGson().toJson(items.values().toArray(), VirtualItem[].class, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retourne tous les items
     * 
     * @return Map
     */
    public Map<String, VirtualItem> getItems() {
        return items;
    }
    
    /**
     * Retourne l'item ayant le nom NAME
     * 
     * @param NAME
     * @return si il existe, renvoie @VirtualItem sinon @Null
     */
    public VirtualItem getItem(String name) {
    	return items.get(name);
    }
}
