package es.eltrueno.modserverutils;

import org.bukkit.scheduler.BukkitRunnable;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class Utils {

    public static String formatSecondsOmitting(long timeInSeconds)
    {
        long secondsLeft = timeInSeconds % 3600 % 60;
        long minutes = (int) Math.floor(timeInSeconds % 3600 / 60);
        long hours = (int) Math.floor(timeInSeconds / 3600);

        String fullstr = (hours==0 ? "" : hours+"h ") + (minutes==0 ? "" : minutes+"m ") + (secondsLeft==0 ? "" : secondsLeft+"s ");

        return fullstr;
    }

    public static String formatSeconds(long timeInSeconds)
    {
        return String.format("%02d:%02d:%02d", timeInSeconds / 3600, (timeInSeconds / 60) % 60, timeInSeconds % 60);
    }

    public static String calculateTotalTime(long seconds) {
        long sec = seconds % 60;
        long minutes = seconds % 3600 / 60;
        long hours = seconds % 86400 / 3600;
        long days = seconds / 86400;

        String ret = "";
        ret+=days>0 ? days+" Días, " : "";
        ret+=hours>0 ? hours+" Horas, " : "";
        ret+=minutes>0 ? minutes+" Minutos, " : "";
        ret+=sec>0 ? sec+" Segundos, " : "";
        return ret;
    }

    public static void runSync(Runnable runnable) {
        new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTask(Main.plugin);
    }

    public static boolean isSameDay(Date date1){
        Instant savedInst = date1.toInstant().truncatedTo(ChronoUnit.DAYS);
        Instant nowInst = new Date().toInstant().truncatedTo(ChronoUnit.DAYS);

        return (savedInst.equals(nowInst));
    }

    public static long minutesLeft(long todaySeconds){
        long secondsLeft = todaySeconds % 3600 % 60;
        long minutes = (int) Math.floor(todaySeconds % 3600 / 60);
        long hours = (int) Math.floor(todaySeconds / 3600);
        if(hours==0 && secondsLeft==0){
            return minutes;
        }else if(hours==0 && minutes==0 && secondsLeft!=0){
            return 0;
        }else return -1;
    }

    public static long secondsLeft(long todaySeconds){
        long seconds = todaySeconds % 3600 % 60;
        long minutes = (int) Math.floor(todaySeconds % 3600 / 60);
        long hours = (int) Math.floor(todaySeconds / 3600);
        if(hours==0 && minutes==0 && seconds!=0){
            return seconds;
        }else return -1;
    }

    public static double getPercent(long total, long now){
        int iTotal = Math.toIntExact(total);
        int iNow = Math.toIntExact(now);
        return (double) (((double)iNow*100)/iTotal);
    }

}
