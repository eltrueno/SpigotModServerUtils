package es.eltrueno.modserverutils.playtime;

import es.eltrueno.modserverutils.Main;
import es.eltrueno.modserverutils.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Date;

public class PlaytimeScheduler extends BukkitRunnable {


    @Override
    public void run() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for(Player player : PlaytimeManager.getPlayersCached()){
                    Playtime playtime = PlaytimeManager.getPlaytime(player);

                    if(!Utils.isSameDay(playtime.getTodayDate())){
                        playtime.setTodayDate(new Date());
                        playtime.setTodaySeconds(0);
                        PlaytimeManager.savePlaytimeToCache(player,playtime);
                        Utils.runSync(() -> PlaytimeManager.dumpCacheToJson(player));
                        player.sendMessage("§ePor suerte para ti es un nuevo día y se ha reseteado el tiempo de juego. Ala, sigue viciandote perro");
                    }

                    playtime.addSecond();

                    if(playtime.getTodaySeconds()<PlaytimeManager.LIMIT_TIME_SECONDS){
                        PlaytimeManager.savePlaytimeToCache(player,playtime);
                        //ACTUALIZAR ACTIONBAR MSG
                        final long rest = PlaytimeManager.LIMIT_TIME_SECONDS - playtime.getTodaySeconds();
                        long minsLeft = Utils.minutesLeft(rest);
                        long secsLeft = Utils.secondsLeft(rest);
                        if(minsLeft==10 || minsLeft==5){
                            Utils.runSync(()->player.sendMessage("§e¡Te quedan §6"+minsLeft+" minutos §ede tiempo de juego!"));
                        }else if(minsLeft==1){
                            Utils.runSync(()->player.sendMessage("§e¡Te queda solo §c1 minuto §ede tiempo de juego!"));
                        }else if(minsLeft==0 && secsLeft<=10){
                            Utils.runSync(()->player.sendMessage("§e¡Te quedan §c"+secsLeft+" segundos §ede tiempo de juego!"));
                        }

                        String tiempoFormatted = Utils.formatSeconds(rest);
                        final double percent = Utils.getPercent(PlaytimeManager.LIMIT_TIME_SECONDS, rest);
                        Utils.runSync(()->{
                            int barChars = 70;
                            String bar = "";
                            int barFill = (int)((percent/100)*barChars);
                            System.out.println("barFill: "+barFill);
                            System.out.println("percent: "+percent/100);
                            int barRest = barChars-barFill;
                            for(int i=1; i<=barFill; i++){
                                bar+="§e|";
                            }
                            for(int i=1; i<=barRest; i++){
                                bar+="§8|";
                            }
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§eTiempo restante: §b"+tiempoFormatted+" §e["+bar+"§e]"));
                        });
                    }else{
                        PlaytimeManager.savePlaytimeToCache(player,playtime);
                        Utils.runSync(() -> PlaytimeManager.dumpCacheToJson(player));
                        //FUERA
                        Utils.runSync(() -> player.kickPlayer("§eEres un puto viciado y has superado el límite de tiempo diário de §b"+ Utils.formatSecondsOmitting(PlaytimeManager.LIMIT_TIME_SECONDS)+'\n'+'\n'+"§eSe restablecerá a las §b00:00§e (España)"));
                    }
                }
            }
        }.runTaskAsynchronously(Main.plugin);
    }
}
