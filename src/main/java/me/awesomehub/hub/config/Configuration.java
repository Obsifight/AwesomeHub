package me.awesomehub.hub.config;

import java.io.File;

import me.awesomehub.hub.AHPlugin;
import me.awesomehub.hub.handlers.Config;

/**
 * Configuration de base d'AwesomeHub.
 * 
 * @author Gwennael
 */
public class Configuration extends Config {

    /**
     * Retourne l'instance de la configuration.
     * 
     * @return {@link Configuration}
     */
    public static Configuration get() {
        return AHPlugin.get().getConfiguration();
    }

    public String locale = "en";
    public boolean bungeeSupport = false;
    public boolean showPlayerCountOnItem = false;
    public String chatFormat = "<%dname%&f> %msg%";
    public boolean bossbarSupport = false;

    /*
     * (non-Javadoc)
     * @see me.awesomehub.plugins.handlers.Config#getFile()
     */
    @Override
    public File getFile() {
        return new File(AHPlugin.get().getDataFolder(), "config.json");
    }
}
