package me.awesomehub.hub.handlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import me.awesomehub.hub.AHPlugin;

import org.bukkit.craftbukkit.libs.com.google.gson.JsonIOException;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonSyntaxException;

/**
 * Permet de créer une configuration.
 * 
 * @author Gwennael
 */
public abstract class Config {

    /**
     * Récupère le fichier chargé de contenir la config.
     * 
     * @return {@link File}
     */
    public abstract File getFile();

    /**
     * Charge la configuration.
     * 
     * @return {@link Config}
     */
    public Config load() {
        File file = this.getFile();
        if (!file.exists()) {
            this.save();
        } else {
            try {
                return AHPlugin.getGson().fromJson(new FileReader(file), this.getClass());
            } catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    /**
     * Sauvegarde la configuration.
     * 
     * @return {@link Config}
     */
    public Config save() {
        try {
            File file = this.getFile();
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            FileWriter writer = new FileWriter(file);
            AHPlugin.getGson().toJson(this, this.getClass(), writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }
}
