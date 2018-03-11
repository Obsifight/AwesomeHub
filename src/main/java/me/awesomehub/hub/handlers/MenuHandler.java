package me.awesomehub.hub.handlers;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import me.awesomehub.hub.AHPlugin;
import me.awesomehub.hub.utils.VirtualMenu;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonIOException;

import com.google.common.collect.Maps;

/**
 * Classe qui permet de gérer les menus.
 * 
 * @author Mac'
 */
public class MenuHandler extends SavableHandler {
    private static final File MENUS_FOLDER = new File(AHPlugin.get().getDataFolder() + File.separator + "menus");

    /**
     * Les menus crées avec leur id pour clé.
     * 
     * @var Map
     */
    private Map<String, VirtualMenu> menus;

    /**
     * Construit le MenuHandler.
     */
    public MenuHandler() {
        super(MenuHandler.MENUS_FOLDER, true);

        for (Entry<String, VirtualMenu> menu : menus.entrySet()) {
            Bukkit.getPluginManager().registerEvents(menu.getValue(), AHPlugin.get());
        }
    }

    /**
     * Charge tous les menus.
     */
    @Override
    public void loadAll() {
        menus = Maps.newHashMap();

        for (File file : saveFile.listFiles()) {
            if (!file.isDirectory() && file.getName().endsWith(".json")) {
                VirtualMenu menu = this.load(file.getName().substring(0, file.getName().length() - 4), file);
                menus.put(menu.getId(), menu);
            }
        }

        AHPlugin.get().getLogger().info("Successfully loaded " + menus.size() + " menus.");
    }

    /**
     * Charge un menu à partir de son id et son fichier.
     * 
     * @param id L'id du menu.
     * @param file Le fichier de sauvegarde.
     * @return mixed
     */
    public VirtualMenu load(String id, File file) {
        try {
            VirtualMenu menu = AHPlugin.getGson().fromJson(new FileReader(file), VirtualMenu.class);
            return menu;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Sauvegarde tous les menus.
     */
    @Override
    public void saveAll() {
        for (Entry<String, VirtualMenu> entry : menus.entrySet()) {
            if (entry.getValue() != null && entry.getKey() != null) {
                this.save(entry.getValue());
            }
        }
    }

    /**
     * Sauvegarde un menu.
     * 
     * @param menu Le menu à sauvegarder.
     */
    public void save(VirtualMenu menu) {
        File file = new File(saveFile, menu.getId() + ".json");
        try {
            FileWriter writer = new FileWriter(file);
            AHPlugin.getGson().toJson(menu, VirtualMenu.class, writer);
            writer.flush();
            writer.close();
        } catch (JsonIOException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retourne les menus.
     * 
     * @return Map
     */
    public Map<String, VirtualMenu> getMenus() {
        return menus;
    }
}
