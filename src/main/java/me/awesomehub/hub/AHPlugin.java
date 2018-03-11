package me.awesomehub.hub;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Map.Entry;

import me.awesomehub.hub.adapters.ItemStackAdapter;
import me.awesomehub.hub.adapters.LocationAdapter;
import me.awesomehub.hub.api.PetMakerAPI;
import me.awesomehub.hub.config.Configuration;
import me.awesomehub.hub.config.Locale;
import me.awesomehub.hub.handlers.Command;
import me.awesomehub.hub.handlers.DisguiseHandler;
import me.awesomehub.hub.handlers.EffectHandler;
import me.awesomehub.hub.handlers.ItemHandler;
import me.awesomehub.hub.handlers.MenuHandler;
import me.awesomehub.hub.handlers.NetworkHandler;
import me.awesomehub.hub.handlers.PetHandler;
import me.awesomehub.hub.handlers.PlayerVisibilityHandler;
import me.awesomehub.hub.handlers.WarpHandler;
import me.awesomehub.hub.listeners.EntityListener;
import me.awesomehub.hub.listeners.PlayerListener;
import me.awesomehub.hub.listeners.WorldListener;
import me.awesomehub.hub.permissions.GroupManagerHandler;
import me.awesomehub.hub.permissions.NullPermissionsHandler;
import me.awesomehub.hub.permissions.PermissionsExHandler;
import me.awesomehub.hub.permissions.PermissionsHandler;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.Maps;

/**
 * La classe principale du plugin.
 * 
 * @author Gwennael
 * @author Mac'
 */
public class AHPlugin extends JavaPlugin {

    /**
     * L'instance de cette classe.
     * 
     * @var {@link AHPlugin}
     */
    private static AHPlugin instance;

    /**
     * Retourne l'instance du plugin.
     * 
     * @return {@link AHPlugin}
     */
    public static AHPlugin get() {
        return AHPlugin.instance;
    }

    /**
     * L'instance locale de gson.
     * 
     * @var {@link Gson}
     */
    private static Gson gson;

    /**
     * Permet de récupérer une instance de Gson.
     * 
     * @return {@link Gson}
     */
    public static Gson getGson() {
        if (AHPlugin.gson == null) {
            AHPlugin.gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.VOLATILE)
            		.registerTypeAdapter(ItemStack.class, new ItemStackAdapter())
            		.registerTypeAdapter(Location.class, new LocationAdapter()).create();
        }
        return AHPlugin.gson;
    }

    /**
     * L'instance de la configuration.
     * 
     * @var {@link Configuration}
     */
    private Configuration configuration;

    /**
     * L'instance de la langue.
     * 
     * @var {@link Locale}
     */
    private Locale locale;

    /**
     * L'instance du MenuHandler.
     * 
     * @var {@link MenuHandler}
     */
    private MenuHandler menuHandler;

    /**
     * L'instance du NetworkHandler.
     * 
     * @var {@link NetworkHandler}
     */
    private NetworkHandler networkHandler;

    /**
     * L'instance du ItemHandler.
     * 
     * @var {@link ItemHandler}
     */
    private ItemHandler itemHandler;

    /**
     * L'instance du PlayerVisibilityHandler.
     * 
     * @var {@link PlayerVisibilityHandler}
     */
    private PlayerVisibilityHandler visibilityHandler;

    /**
     * L'instance du DisguiseHandler.
     * 
     * @var {@link DisguiseHandler}
     */
    private DisguiseHandler disguiseHandler;

    /**
     * L'instance du EffectHandler.
     * 
     * @var {@link EffectHandler}
     */
    private EffectHandler effectHandler;

    /**
     * L'instance du WarpHandler.
     * 
     * @var {@link WarpHandler}
     */
    private WarpHandler warpHandler;

    /**
     * L'instance du gestionnaire de permissions.
     * 
     * @var {@link PermissionsHandler}
     */
    private PermissionsHandler permissionsHandler;
    
    /**
     * L'instance du PetHandler.
     * 
     * @var {@link PetHandler}
     */
    private PetHandler petHandler;

    /**
     * A l'activation du plugin.
     */
    @Override
    public void onEnable() {
        AHPlugin.instance = this;
        configuration = (Configuration) new Configuration().load();
        locale = this.loadLocale(configuration.locale);
        itemHandler = new ItemHandler();
        menuHandler = new MenuHandler();
        networkHandler = new NetworkHandler();
        visibilityHandler = new PlayerVisibilityHandler();
        disguiseHandler = new DisguiseHandler();
        effectHandler = new EffectHandler();
        warpHandler = new WarpHandler();
        permissionsHandler = this.hookPermissions();

        try {
            PetMakerAPI petMaker = (PetMakerAPI) Class.forName("me.awesomehub.hub.api." + PetMakerAPI.VERSION + ".PetMaker").getConstructor(EntityType[].class).newInstance(new Object[] { new EntityType[] { EntityType.COW, EntityType.PIG, EntityType.CHICKEN, EntityType.SHEEP, EntityType.ZOMBIE, EntityType.SKELETON, EntityType.WITCH, EntityType.BLAZE, EntityType.WOLF, EntityType.CAVE_SPIDER, EntityType.SPIDER, EntityType.CREEPER, EntityType.ENDERMAN } });
            petHandler = new PetHandler(petMaker);
        } catch (Exception e) {
            this.getLogger().severe("Pets aren't compatible with \"" + Bukkit.getServer().getVersion() + "\" version!");
            e.printStackTrace();
        }

        this.getCommand("awesomehub").setExecutor(new Command());
        this.getServer().getPluginManager().registerEvents(new EntityListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        this.getServer().getPluginManager().registerEvents(new WorldListener(), this);

        WorldListener.prepareWorld();
    }

    /**
     * A la désactivation du plugin.
     */
    @Override
    public void onDisable() {
        warpHandler.saveAll();
    }

    /**
     * Hook le plugin de permissions utilisé.
     * 
     * @return {@link PermissionsHandler}
     */
    private PermissionsHandler hookPermissions() {
        Map<String, Class<? extends PermissionsHandler>> handlers = Maps.newHashMap();
        handlers.put("PermissionsEx", PermissionsExHandler.class);
        handlers.put("GroupManager", GroupManagerHandler.class);
        for (Entry<String, Class<? extends PermissionsHandler>> entry : handlers.entrySet()) {
            if (this.getServer().getPluginManager().getPlugin(entry.getKey()) != null) {
                this.getLogger().info("Hooked into \"" + entry.getKey() + "\" permissions plugin!");
                try {
                    return entry.getValue().newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        this.getLogger().warning("No permissions plugin found!");
        return new NullPermissionsHandler();
    }

    /**
     * Charge la langue demandée.
     * 
     * @param name Le nom de la langue.
     * @return {@link Locale}
     */
    public Locale loadLocale(String name) {
        File locales = new File(this.getDataFolder(), "locales");
        if (!locales.exists()) {
            locales.mkdirs();
        } else {
            for (File localeF : locales.listFiles()) {
                if (localeF.isFile() && localeF.getName().endsWith(".json")) {
                    Locale locale = new Locale(localeF).load();
                    if (locale.name.equalsIgnoreCase(name)) return locale;
                }
            }
        }
        return new Locale(new File(locales, "en.json")).load();
    }

    /**
     * Défini la configuration.
     * 
     * @param configuration
     */
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * Défini la langue.
     * 
     * @param locale
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    /**
     * Retourne une instance de la configuration.
     * 
     * @return {@link Configuration}
     */
    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     * Retourne une instance de la langue.
     * 
     * @return {@link Locale}
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Retourne une instance du MenuHandler.
     * 
     * @return {@link MenuHandler}
     */
    public MenuHandler getMenuHandler() {
        return menuHandler;
    }

    /**
     * Retourne une instance du NetworkHandler.
     * 
     * @return {@link NetworkHandler}
     */
    public NetworkHandler getNetworkHandler() {
        return networkHandler;
    }

    /**
     * Retourne une instance du ItemHandler.
     * 
     * @return {@link MenuHandler}
     */
    public ItemHandler getItemHandler() {
        return itemHandler;
    }

    /**
     * Retourne une instance du PlayerVisibilityHandler.
     * 
     * @return {@link PlayerVisibilityHandler}
     */
    public PlayerVisibilityHandler getVisibilityHandler() {
        return visibilityHandler;
    }

    /**
     * Retourne une instance du DisguiseHandler.
     * 
     * @return {@link DisguiseHandler}
     */
    public DisguiseHandler getDisguiseHandler() {
        return disguiseHandler;
    }

    /**
     * Retourne une instance du EffectHandler.
     * 
     * @return {@link EffectHandler}
     */
    public EffectHandler getEffectHandler() {
        return effectHandler;
    }

    /**
     * Retourne une instance du WarpHandler.
     * 
     * @return {@link WarpHandler}
     */
    public WarpHandler getWarpHandler() {
        return warpHandler;
    }

    /**
     * Retourne une instance du PermissionHandler.
     * 
     * @return {@link PermissionsHandler}
     */
    public PermissionsHandler getPermissionsHandler() {
        return permissionsHandler;
    }

    /**
     * Retourne une instance du PetHandler.
     * 
     * @return {@link PetHandler}
     */
    public PetHandler getPetHandler() {
        return petHandler;
    }
}
