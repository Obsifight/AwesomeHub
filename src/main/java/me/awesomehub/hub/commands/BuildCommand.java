package me.awesomehub.hub.commands;

import me.awesomehub.hub.config.Locale;
import me.awesomehub.hub.handlers.Command.AHCommand;
import me.awesomehub.hub.handlers.Command.SubCommand;
import me.awesomehub.hub.listeners.WorldListener;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class BuildCommand implements AHCommand {

    @Override
    @SubCommand(label = "build", permission = "awesomehub.build", mustBePlayer = true)
    public boolean onCommand(Player player, String label, String[] args) {
        if (WorldListener.isBuilder(player.getName())) {
            WorldListener.removeBuilder(player.getName());
            player.sendMessage(Locale.get().buildOff);
            player.setGameMode(GameMode.ADVENTURE);
        } else {
            WorldListener.addBuilder(player.getName());
            player.sendMessage(Locale.get().buildOn);
            player.setGameMode(GameMode.CREATIVE);
        }
        return true;
    }
}
