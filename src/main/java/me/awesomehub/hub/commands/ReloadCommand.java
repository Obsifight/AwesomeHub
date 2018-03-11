package me.awesomehub.hub.commands;

import me.awesomehub.hub.AHPlugin;
import me.awesomehub.hub.config.Configuration;
import me.awesomehub.hub.config.Locale;
import me.awesomehub.hub.handlers.Command.AHCommand;
import me.awesomehub.hub.handlers.Command.SubCommand;

import org.bukkit.entity.Player;

public class ReloadCommand implements AHCommand {

    @Override
    @SubCommand(label = "reload")
    public boolean onCommand(Player player, String label, String[] args) {
        // Recharge les handlers
        AHPlugin.get().getItemHandler().loadAll();
        AHPlugin.get().getMenuHandler().loadAll();

        // Recharge les configs
        Configuration config = (Configuration) Configuration.get().load();
        AHPlugin.get().setConfiguration(config);
        AHPlugin.get().setLocale(AHPlugin.get().loadLocale(config.locale));
        player.sendMessage(Locale.get().pluginReloaded);
        return true;
    }
}
