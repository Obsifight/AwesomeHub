package me.awesomehub.hub.commands;

import me.awesomehub.hub.AHPlugin;
import me.awesomehub.hub.handlers.Command.AHCommand;
import me.awesomehub.hub.handlers.Command.SubCommand;

import org.bukkit.entity.Player;

public class GiveCommand implements AHCommand {

    @Override
    @SubCommand(label = "item")
    public boolean onCommand(Player player, String label, String[] args) {
        if (args.length == 0) return false;

        if (AHPlugin.get().getItemHandler().getItems().containsKey(args[0])) {
            player.getInventory().addItem(AHPlugin.get().getItemHandler().getItems().get(args[0]).getItem());
        }
        return true;
    }
}
