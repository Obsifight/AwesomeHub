package me.awesomehub.hub.commands;

import me.awesomehub.hub.AHPlugin;
import me.awesomehub.hub.config.Locale;
import me.awesomehub.hub.handlers.Command.AHCommand;
import me.awesomehub.hub.handlers.Command.SubCommand;

import org.bukkit.entity.Player;

public class SetWarpCommand implements AHCommand {

    @Override
    @SubCommand(label = "setwarp", mustBePlayer = true)
    public boolean onCommand(Player player, String label, String[] args) {
        if (args.length == 0) return false;

        AHPlugin.get().getWarpHandler().setWarp(args[0], player.getLocation());
        player.sendMessage(Locale.get().warpDefined.replace("%warp%", args[0]));
        return true;
    }
}
