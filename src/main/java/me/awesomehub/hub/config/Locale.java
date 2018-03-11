package me.awesomehub.hub.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import me.awesomehub.hub.AHPlugin;
import me.awesomehub.hub.handlers.Config;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonIOException;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonSyntaxException;

/**
 * Configuration de la langue d'AwesomeHub.
 * 
 * @author Gwennael
 */
public class Locale extends Config {

    /**
     * Contient le fichier de langue.
     * 
     * @var {@link File}
     */
    private transient File file;

    /**
     * Retourne la langue utilisée.
     * 
     * @return {@link Locale}
     */
    public static Locale get() {
        return AHPlugin.get().getLocale();
    }

    public String name = "en";
    public String cooldownMessage = ChatColor.RED + "You must wait %seconds% second%plural% before use that.";
    public String menuDoesntExist = ChatColor.RED + "The menu \"%menu%\" does not exist.";
    public String serverDoesntExist = ChatColor.RED + "The server \"%server%\" does not exist or \"bungeesupport\" is not enable in config.";
    public String mustBePlayer = ChatColor.RED + "You must be a player!";
    public String togglePlayerOff = ChatColor.GREEN + "You can see everyone now.";
    public String togglePlayerOn = ChatColor.GREEN + "All player are now invisible (except op's).";
    public String disguiseDoesntExist = ChatColor.RED + "The disguise \"%disguise%\" does not exist. Allowed disguise: %disguises%";
    public String effectDoesntExist = ChatColor.RED + "The effect \"%effect%\" does not exist. Allowed effects: %effects%";
    public String petDoesntExist = ChatColor.RED + "The pet \"%disguise%\" does not exist. Allowed pets: %disguises%";
    public String dontHavePermission = ChatColor.RED + "You don't have \"%permission%\" permission!";
    public String pluginReloaded = ChatColor.GREEN + "AwesomeHub has been reloaded with success!";
    public String warpDefined = ChatColor.GREEN + "Warp \"%warp%\" has been defined at your feet!";
    public String buildOn = ChatColor.GREEN + "You can now build and edit this map.";
    public String buildOff = ChatColor.RED + "You can't edit on this map now.";
    public String bossbarPluginMissing = ChatColor.RED + "BossBarAPI missing, disabling option...";
    public String bossbarPluginPresent = ChatColor.RED + "BossBarAPI found, enabling option...";

    /**
     * Construit une langue.
     * 
     * @param file Le fichier vers la langue.
     */
    public Locale(File file) {
        this.file = file;
    }

    /**
     * Charge la langue.
     * 
     * @return {@link Locale}
     */
    @Override
    public Locale load() {
        if (!file.exists()) {
            this.save();
        } else {
            try {
                Locale locale = AHPlugin.getGson().fromJson(new FileReader(file), this.getClass());
                locale.file = file;
                return locale;
            } catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    /*
     * (non-Javadoc)
     * @see me.awesomehub.plugins.handlers.Config#getFile()
     */
    @Override
    public File getFile() {
        return file;
    }
}
