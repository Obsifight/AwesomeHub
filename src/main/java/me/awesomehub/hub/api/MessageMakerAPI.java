package me.awesomehub.hub.api;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.awesomehub.hub.AHPlugin;

public abstract class MessageMakerAPI {
	
	/**
	 * Is the title and actionBar message available
	 * 
	 * @var boolean
	 */
	public static boolean isTitleAvailable;
	
	/**
	 * Default variable for 
	 */
    private static int DEFAULT_FADE_OUT = 20;
    private static int DEFAULT_STAY = 20;
    private static int DEFAULT_FADE_IN = 20;
    
    
    /**
     * NMS version
     * 
     * @var {@link String}
     */
    public static String VERSION;
	
	static {
		String path = Bukkit.getServer().getClass().getPackage().getName();
		VERSION = path.substring(path.lastIndexOf(".") + 1, path.length());
		
		if (VERSION.startsWith("v1_7")) {
			isTitleAvailable = false;
		}
		else
			isTitleAvailable = true;
	}
	
	/**
	 * Constructeur de la class
	 */
	public MessageMakerAPI () {
		if (isTitleAvailable) {
			AHPlugin.get().getLogger().log(Level.INFO, "Title/ActionBar disabled because of your spigot version, all of your message will go in the chat");
		}
	}
	
	/**
	 * Send title to the player (or a message if he cant receive the title)
	 * @param {@link Player} player who receive will the message
	 * @param String the message
	 */
	public abstract void sendTitle(Player player, String title);
	
	/**
	 * Send title to all player (or a message if a player cant receive the title)
	 * @param String the message
	 */
	public void sendTitleToAllPlayer(String title) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			sendTitle(player, title);
		}
	}
	
	/**
	 * Send header and footer to the player
	 * @param {@link Player} player who will receive them
	 * @param String the footer (can be null)
	 * @param String the header (can be null)
	 */
	public abstract void sendHeaderAndFooterToPlayer(Player player, String footer, String header);
	
	
	/**
	 * Send header and footer to all connected player
	 * 
	 * @param String the footer (can be null)
	 * @param String the header (can be null)
	 */
	public void sendHeaderAndFooterToAllPlayer(String footer, String header) {
		for(Player player : Bukkit.getOnlinePlayers()) {
			sendHeaderAndFooterToPlayer(player, footer, header);
		}
	}
	
	
	
	public enum EnumMessageType {
		TITLE,
		SUBTITLE,
		ACTION_BAR,
		MESSAGE,
		HEADER_FOOTER;
	}
}
