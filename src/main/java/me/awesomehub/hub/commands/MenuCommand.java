package me.awesomehub.hub.commands;

import me.awesomehub.hub.AHPlugin;
import me.awesomehub.hub.handlers.Command.AHCommand;
import me.awesomehub.hub.handlers.Command.SubCommand;

import org.bukkit.entity.Player;

public class MenuCommand implements AHCommand {

    @Override
    @SubCommand(label = "menu", mustBePlayer = true)
    public boolean onCommand(Player player, String label, String[] args) {
        if (args.length == 0) return false;

        if (AHPlugin.get().getMenuHandler().getMenus().containsKey(args[0])) {
            player.openInventory(AHPlugin.get().getMenuHandler().getMenus().get(args[0]).createInventory());
        }
        return true;
    }
}
