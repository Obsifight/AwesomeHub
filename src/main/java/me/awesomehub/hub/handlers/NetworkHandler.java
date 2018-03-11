package me.awesomehub.hub.handlers;

import java.util.List;
import java.util.Map;

import me.awesomehub.hub.AHPlugin;
import me.awesomehub.hub.config.Configuration;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

/**
 * Classe qui gère la communication avec BungeeCord.
 * 
 * @author Mac'
 */
public class NetworkHandler implements PluginMessageListener {
    private List<String> servers;
    private Map<String, Integer> serversCount;

    /**
     * L'id de la tâche pour l'annuler.
     * 
     * @var int
     */
    private int taskID = 0;

    /**
     * Construit l'objet Network.
     */
    public NetworkHandler() {
        servers = Lists.newArrayList();
        serversCount = Maps.newHashMap();

        if (!Configuration.get().bungeeSupport) return;

        Bukkit.getMessenger().registerOutgoingPluginChannel(AHPlugin.get(), "BungeeCord");
        Bukkit.getMessenger().registerIncomingPluginChannel(AHPlugin.get(), "BungeeCord", this);

        this.startQueryingData();
    }

    /**
     * Envoie un joueur vers le serveur désigné.
     * 
     * @param player Le joueur qui va être envoyé.
     * @param server Le serveur cible.
     */
    public void sendPlayerTo(Player player, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);
        player.sendPluginMessage(AHPlugin.get(), "BungeeCord", out.toByteArray());
    }

    /**
     * Envoie une requête à Bungee pour connaître
     * le nom de tous les serveurs du network.
     */
    @SuppressWarnings("deprecation")
    public void getAllServers() {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GetServers");
        Bukkit.getOnlinePlayers()[0].sendPluginMessage(AHPlugin.get(), "BungeeCord", out.toByteArray());
    }

    /**
     * Envoie un requête à Bungee pour connaître
     * le nombre de connectés pour chaque serveur du network.
     */
    @SuppressWarnings("deprecation")
    private void getAllServerCount() {
        for (String server : servers) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("PlayerCount");
            out.writeUTF(server);
            Bukkit.getOnlinePlayers()[0].sendPluginMessage(AHPlugin.get(), "BungeeCord", out.toByteArray());
        }
    }

    /**
     * Lance une tâche qui va faire les requêtes toutes les 30 secondes.
     */
    public void startQueryingData() {
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(AHPlugin.get(), new Runnable() {

            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                if (Bukkit.getOnlinePlayers().length > 0) {
                    NetworkHandler.this.getAllServers();
                    NetworkHandler.this.getAllServerCount();
                }

            }
        }, 0, 600);
    }

    /**
     * Arrêt du runnable qui permet de faire les requêtes.
     */
    public void stopQueryingData() {
        Bukkit.getScheduler().cancelTask(taskID);
        taskID = 0;
    }

    @Override
    public void onPluginMessageReceived(String channel, Player p, byte[] data) {
        if (!channel.equals("BungeeCord")) return;

        ByteArrayDataInput in = ByteStreams.newDataInput(data);
        String channels = in.readUTF();

        if (channels.equals("GetServers")) {
            String[] tempservers = in.readUTF().split(", ");
            for (String s : tempservers) {
                servers.add(s);
            }
        } else if (channels.equals("PlayerCount")) {
            String name = in.readUTF();
            int count = in.readInt();
            serversCount.put(name, count);
        }

    }

    /**
     * Récupère la HashMap qui stocke le nombre de joueurs pour chaque serveur.
     * 
     * @return {@link Map}
     */
    public Map<String, Integer> getBungeeServerCount() {
        return serversCount;
    }

    /**
     * Recupère la liste qui stocke les noms des serveurs.
     * 
     * @return {@link List}
     */
    public List<String> getServerList() {
        return servers;
    }
}
