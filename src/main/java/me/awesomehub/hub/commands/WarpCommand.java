package me.awesomehub.hub.commands;

import me.awesomehub.hub.AHPlugin;
import me.awesomehub.hub.handlers.Command.AHCommand;
import me.awesomehub.hub.handlers.Command.SubCommand;

import org.bukkit.entity.Player;

public class WarpCommand implements AHCommand {

    @Override
    @SubCommand(label = "warp", mustBePlayer = true)
    public boolean onCommand(Player player, String label, String[] args) {
        if (args.length == 0) return false;

        if (AHPlugin.get().getWarpHandler().listWarps().containsKey(args[0])) {
            player.teleport(AHPlugin.get().getWarpHandler().getWarpLocation(args[0]));
        }
        return true;
    }
}
