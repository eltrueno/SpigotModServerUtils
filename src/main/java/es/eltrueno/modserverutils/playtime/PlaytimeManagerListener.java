package es.eltrueno.modserverutils.playtime;

import es.eltrueno.modserverutils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlaytimeManagerListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        PlaytimeManager.dumpCacheToJson(player);
        Bukkit.getServer().getConsoleSender().sendMessage("§eSe han guardado los tiempos de juego del jugador §b"+player.getName());
        PlaytimeManager.deleteCachedPlayer(player);
    }

    @EventHandler
    public void onJoin(PlayerLoginEvent event){
        Player player = event.getPlayer();
        PlaytimeManager.cachePlaytimeFromJson(player);

        if(!PlaytimeManager.checkPlaytime(player)){
            String msg = "§eEres un puto viciado y has superado el límite de tiempo diário de §b"+ Utils.formatSecondsOmitting(PlaytimeManager.LIMIT_TIME_SECONDS)+"§e.Se restablecerá a las §b"+PlaytimeManager.RESET_HOUR+":00§e (España)";
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, msg);
            PlaytimeManager.deleteCachedPlayer(player);
        }
    }

}
