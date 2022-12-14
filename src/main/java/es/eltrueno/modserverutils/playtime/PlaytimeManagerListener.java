package es.eltrueno.modserverutils.playtime;

import es.eltrueno.modserverutils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlaytimeManagerListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        System.out.println(player.getName()+" seconds: "+PlaytimeManager.getPlaytime(player).getTodaySeconds());
        PlaytimeManager.dumpCacheToJson(player);
        PlaytimeManager.deleteCachedPlayer(player);
    }

    @EventHandler
    public void onJoin(PlayerLoginEvent event){
        Player player = event.getPlayer();
        PlaytimeManager.cachePlaytimeFromJson(player);

        Playtime playtime = PlaytimeManager.getPlaytime(player);
        if(Utils.isSameDay(playtime.getTodayDate()) && playtime.getTodaySeconds()>= PlaytimeManager.LIMIT_TIME_SECONDS){
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "§eEres un puto viciado y has superado el límite de tiempo diário de §b"+ Utils.formatSecondsOmitting(PlaytimeManager.LIMIT_TIME_SECONDS)+'\n'+'\n'+"§eSe restablecerá a las §b00:00§e (España)");
            PlaytimeManager.deleteCachedPlayer(player);
        }
    }

}
