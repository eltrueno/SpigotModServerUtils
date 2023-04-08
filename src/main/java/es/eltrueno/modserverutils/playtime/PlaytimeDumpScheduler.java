package es.eltrueno.modserverutils.playtime;

import es.eltrueno.modserverutils.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PlaytimeDumpScheduler extends BukkitRunnable {

    @Override
    public void run() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for(Player player : PlaytimeManager.getPlayersCached()){
                    PlaytimeManager.dumpCacheToJson(player);

                }
                Bukkit.getServer().getConsoleSender().sendMessage("§eSe han guardado los tiempos de juego de los jugadores conectados");
            }
        }.runTaskAsynchronously(Main.plugin);
    }

}
