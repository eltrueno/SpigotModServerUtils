package es.eltrueno.modserverutils.playtime;

import es.eltrueno.modserverutils.Main;
import es.eltrueno.modserverutils.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class PlaytimeScheduler extends BukkitRunnable {


    @Override
    public void run() {
        new BukkitRunnable() {
            @Override
            public void run() {
                final Calendar now = Calendar.getInstance();
                now.setTimeZone(TimeZone.getTimeZone("Europe/Madrid"));
                int minsUntilReset = -1;
                //Reset msg
                if(now.get(Calendar.HOUR_OF_DAY)==PlaytimeManager.RESET_HOUR-1 && now.get(Calendar.MINUTE)==0&& now.get(Calendar.SECOND)==0){
                    //queda 1h
                    minsUntilReset = 60;
                }else if(now.get(Calendar.HOUR_OF_DAY)==PlaytimeManager.RESET_HOUR-1 && now.get(Calendar.MINUTE)==30&& now.get(Calendar.SECOND)==0){
                    //queda 30m
                    minsUntilReset = 30;
                }else if(now.get(Calendar.HOUR_OF_DAY)==PlaytimeManager.RESET_HOUR-1 && now.get(Calendar.MINUTE)==50&& now.get(Calendar.SECOND)==0){
                    //queda 10m
                    minsUntilReset = 10;
                }else if(now.get(Calendar.HOUR_OF_DAY)==PlaytimeManager.RESET_HOUR-1 && now.get(Calendar.MINUTE)==55&& now.get(Calendar.SECOND)==0){
                    minsUntilReset = 5;
                }

                for(Player player : PlaytimeManager.getPlayersCached()){
                    Playtime playtime = PlaytimeManager.getPlaytime(player);

                    if(!PlaytimeManager.isVirtualSameDay(playtime.getTodayDate())){
                        playtime.setTodaySeconds(0);
                        PlaytimeManager.savePlaytimeToCache(player,playtime);
                        Utils.runSync(() -> PlaytimeManager.dumpCacheToJson(player));
                        player.sendMessage("§ePor suerte para ti es un nuevo día y se ha reseteado el tiempo de juego. Ala, sigue viciandote perro");
                    }

                    //UNTIL RESET MSG
                    if(minsUntilReset!=-1){
                        String msg = "";
                        if(minsUntilReset==60){
                            msg = "§eQueda §b1 hora §epara el reset del tiempo de juego";
                        }else{
                            msg = "§eQuedan §b"+minsUntilReset+" minutos §epara el reset del tiempo de juego";
                        }
                        player.sendMessage(msg);
                    }

                    playtime.addSecond();

                    if(playtime.getTodaySeconds()<PlaytimeManager.LIMIT_TIME_SECONDS){
                        PlaytimeManager.savePlaytimeToCache(player,playtime);

                        final long rest = PlaytimeManager.LIMIT_TIME_SECONDS - playtime.getTodaySeconds();
                        long minsLeft = Utils.minutesLeft(rest);
                        long secsLeft = Utils.secondsLeft(rest);
                        if(minsLeft==10 || minsLeft==5 || minsLeft==30){
                            Utils.runSync(()->player.sendMessage("§e¡Te quedan §6"+minsLeft+" minutos §ede tiempo de juego!"));
                        }else if(minsLeft==1){
                            Utils.runSync(()->player.sendMessage("§e¡Te queda solo §c1 minuto §ede tiempo de juego!"));
                        }else if(minsLeft==0 && secsLeft<=10){
                            Utils.runSync(()->player.sendMessage("§e¡Te quedan §c"+secsLeft+" segundos §ede tiempo de juego!"));
                        }

                        final double percent = Utils.getPercent(PlaytimeManager.LIMIT_TIME_SECONDS, rest);
                        final double reversepercent = 100-percent;

                        String tiempoFormatted = Utils.formatSeconds(rest);

                        //ACTUALIZAR ACTIONBAR MSG
                        if(PlaytimeManager.actionbarVisible.contains(player.getUniqueId())) {

                            Utils.runSync(() -> {
                                int barChars = 70;
                                String bar = "";
                                int barFill = (int) ((percent / 100) * barChars);
                                int barRest = barChars - barFill;
                                for (int i = 1; i <= barFill; i++) {
                                    bar += "§e|";
                                }
                                for (int i = 1; i <= barRest; i++) {
                                    bar += "§8|";
                                }
                                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§eTiempo restante: §b" + tiempoFormatted + " §e[" + bar + "§e]"));
                            });
                        }

                        //ACTUALIZAR BOSSBAR
                        if(PlaytimeManager.bossbarVisible.contains(player.getUniqueId())){
                            Utils.runSync(() -> {
                                BossBar bar = PlaytimeManager.getBossbar(player);
                                bar.setProgress(percent/100);
                                String bbtit = "§eTiempo restante: §b"+tiempoFormatted;
                                bar.setTitle(bbtit);
                                bar.setVisible(true);
                            });
                        }
                    }else{
                        PlaytimeManager.savePlaytimeToCache(player,playtime);
                        Utils.runSync(() -> PlaytimeManager.dumpCacheToJson(player));
                        //FUERA
                        Utils.runSync(() -> player.kickPlayer("§eEres un puto viciado y has superado el límite de tiempo diário de §b"+ Utils.calculateTotalTime(PlaytimeManager.LIMIT_TIME_SECONDS)+'\n'+'\n'+"§eSe restablecerá a las §b"+PlaytimeManager.RESET_HOUR+":00§e (España)"));
                    }
                }
            }
        }.runTaskAsynchronously(Main.plugin);
    }
}
