package me.awesomehub.hub.handlers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import me.awesomehub.hub.commands.BuildCommand;
import me.awesomehub.hub.commands.GiveCommand;
import me.awesomehub.hub.commands.MenuCommand;
import me.awesomehub.hub.commands.ReloadCommand;
import me.awesomehub.hub.commands.SetWarpCommand;
import me.awesomehub.hub.commands.WarpCommand;
import me.awesomehub.hub.config.Configuration;
import me.awesomehub.hub.config.Locale;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.Sets;

/**
 * Classe qui permet de gérer les commandes d'AwesomeHub.
 * 
 * @author Gwennael
 */
public class Command implements CommandExecutor {

    /**
     * Annotation permettant la création de sous commandes.
     * 
     * @author Gwennael
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public static @interface SubCommand {

        /**
         * Le label de le commande.
         * 
         * @return String
         */
        String label();

        /**
         * Les alias de la commande.
         * 
         * @return String[]
         */
        String[] aliases() default {};

        /**
         * La permission de la commande.
         * 
         * @return string
         */
        String permission() default "awesomehub.admin";
        
        /**
         * Si le sender soit être un joueur
         * 
         * @return boolean
         */
        boolean mustBePlayer() default false;
    }

    /**
     * Interface permettant la création de sous commandes.
     * 
     * @author Gwennael
     */
    public static interface AHCommand {

        /**
         * Quand la commande est exécutée.
         * 
         * @param player Le joueur.
         * @param label La commande.
         * @param args Les arguments.
         * 
         * @return boolean
         */
        public boolean onCommand(Player player, String label, String[] args);
    }

    /**
     * Les sous commandes enregistrées.
     * 
     * @var {@link List}
     */
    private Set<AHCommand> commands;

    /**
     * Construit la commande de base d'AwesomeHub.
     */
    public Command() {
        commands = Sets.newHashSet();
        this.registerCommands();
    }

    /**
     * Enregistre les commandes.
     */
    private void registerCommands() {
        commands.add(new GiveCommand());
        commands.add(new MenuCommand());
        commands.add(new ReloadCommand());
        commands.add(new SetWarpCommand());
        commands.add(new WarpCommand());
        commands.add(new BuildCommand());
    }

    /**
     * A l'appel de la commande principale d'AwesomeHub.
     */
    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.YELLOW + "Plugin AwesomeHub v1.0.0 by ThisIsMac & Rellynn");
        } else {
            String subLabel = args[0].toLowerCase();
            for (AHCommand subCommand : commands) {
                try {
                    Method onCommand = subCommand.getClass().getMethod("onCommand", Player.class, String.class, String[].class);
                    if (onCommand.isAnnotationPresent(SubCommand.class)) {
                        SubCommand annotation = onCommand.getAnnotation(SubCommand.class);
                        if(annotation.mustBePlayer() && !(sender instanceof Player)) {
                        	sender.sendMessage(ChatColor.RED + " You must be a player to use this command");
                        	return true ;
                        }
                        if (annotation.label().equals(subLabel) || Arrays.asList(annotation.aliases()).contains(subLabel)) {
                            if (!sender.hasPermission(annotation.permission())) {
                                sender.sendMessage(Locale.get().dontHavePermission.replace("%permission%", annotation.permission()));
                                return true;
                            }
                            onCommand.invoke(subCommand, sender, args[0], Arrays.copyOfRange(args, 1, args.length));
                            return true;
                        }
                    }
                } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}
