package es.eltrueno.modserverutils.playtime;

import com.google.gson.JsonObject;
import es.eltrueno.modserverutils.JsonDataManager;
import es.eltrueno.modserverutils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class PlaytimeManager {

    public static final long LIMIT_TIME_SECONDS = 18000; //5H

    public static final int RESET_HOUR = 4;

    private static final HashMap<Player, Playtime> cachedPlaytime = new HashMap<Player, Playtime>();

    private static final HashMap<Player, BossBar> cachedBossbar = new HashMap<Player, BossBar>();

    public static List<UUID> actionbarVisible = new ArrayList<UUID>();
    public static List<UUID> bossbarVisible = new ArrayList<UUID>();

    private static void savePlaytimeData(String uuid, JsonObject playtimeJsonObject){
        try {
            JsonObject parentObj = JsonDataManager.getUserObject(uuid);
            if(parentObj!=null){
                if(parentObj.has("playtime")) parentObj.remove(uuid);
                parentObj.add("playtime", playtimeJsonObject);
                JsonDataManager.saveUserData(uuid, parentObj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static JsonObject getPlaytimeJsonObject(String uuid){
        JsonObject playtimeObj = null;
        try {
            JsonObject parentObj = JsonDataManager.getUserObject(uuid);
            if(parentObj!=null){
                if(parentObj.has("playtime")){
                    playtimeObj = parentObj.getAsJsonObject("playtime");
                }else{
                    JsonObject newObj = new JsonObject();
                    parentObj.add("playtime", newObj);
                    JsonDataManager.saveUserData(uuid, parentObj);
                    playtimeObj = newObj;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return playtimeObj;
    }

    public static void deleteCachedPlayer(Player player){
        if(cachedPlaytime.containsKey(player))cachedPlaytime.remove(player);
    }

    public static void deleteCachedBossbar(Player player){
        if(cachedBossbar.containsKey(player))cachedBossbar.remove(player);
    }

    public static BossBar getBossbar(Player player){
        BossBar bar;
        if(cachedBossbar.containsKey(player)) bar = cachedBossbar.get(player);
        else{
            bar = Bukkit.createBossBar("", BarColor.YELLOW, BarStyle.SOLID);
            bar.addPlayer(player);
            bar.setVisible(false);
            bar.setProgress(1.0);
            cachedBossbar.put(player, bar);
        }
        return bar;
    }

    public static void cachePlaytimeFromJson(Player player){
        cachedPlaytime.remove(player);
        Playtime playtime = getPlaytimeFromJson(player.getUniqueId().toString());
        cachedPlaytime.put(player, playtime==null ? new Playtime() : playtime);
    }

    public static void savePlaytimeToCache(Player player, Playtime playtime){
        if(cachedPlaytime.containsKey(player)) cachedPlaytime.replace(player, playtime);
        else cachedPlaytime.put(player, playtime);
    }

    private static void savePlaytimeToJson(Player player, Playtime playtime){
        JsonObject playtimeJsonObj = new JsonObject();
        playtimeJsonObj.addProperty("totalSeconds", playtime.getTotalSeconds());
        playtimeJsonObj.addProperty("todaySeconds", playtime.getTodaySeconds());
        playtimeJsonObj.addProperty("todayDate", playtime.getTodayDate().getTime());

        savePlaytimeData(player.getUniqueId().toString(), playtimeJsonObj);
    }

    public static void dumpCacheToJson(Player player){
        Playtime playtime = getPlaytime(player);
        playtime.setTodayDate(new Date());
        savePlaytimeToJson(player, playtime);
    }

    public static Playtime getPlaytime(Player player){
        if(!cachedPlaytime.containsKey(player)) cachePlaytimeFromJson(player);
        return cachedPlaytime.get(player);
    }

    public static boolean isVirtualSameDay(Date date1){
        Calendar now = Calendar.getInstance();
        now.setTimeZone(TimeZone.getTimeZone("Europe/Madrid"));
        now.add(Calendar.HOUR_OF_DAY, -RESET_HOUR);

        Calendar datec = Calendar.getInstance();
        datec.setTimeZone(TimeZone.getTimeZone("Europe/Madrid"));
        datec.setTime(date1);
        datec.add(Calendar.HOUR_OF_DAY, -RESET_HOUR);

        //Instant savedInst = datec.toInstant().truncatedTo(ChronoUnit.DAYS);
        //Instant nowInst = now.toInstant().truncatedTo(ChronoUnit.DAYS);


        final boolean sd = now.get(Calendar.DAY_OF_MONTH)==datec.get(Calendar.DAY_OF_MONTH);


        /*if(now.get(Calendar.HOUR_OF_DAY)>=RESET_HOUR){
            nowInst = new Date().toInstant().truncatedTo(ChronoUnit.DAYS);
        }else{
            Calendar now2 = Calendar.getInstance();
            now2.add(Calendar.DAY_OF_YEAR, -1);
            nowInst = now2.toInstant().truncatedTo(ChronoUnit.DAYS);
        }*/

        return sd;
    }

    public static Playtime getPlaytimeFromJson(String uuid){
        JsonObject playtimeJsonObj = getPlaytimeJsonObject(uuid);

        long totalSeconds = 0, todaySeconds = 0;
        Date todayDate;

        if(playtimeJsonObj!=null && playtimeJsonObj.has("totalSeconds")){
            totalSeconds = playtimeJsonObj.get("totalSeconds").getAsLong();
            todaySeconds = playtimeJsonObj.get("todaySeconds").getAsLong();
            todayDate = new Date(playtimeJsonObj.get("todayDate").getAsLong());

            if(!isVirtualSameDay(todayDate)){
                todaySeconds = 0;
                todayDate = new Date();
            }
        }else todayDate = new Date();

        return new Playtime(totalSeconds, todayDate, todaySeconds);
    }

    public static Set<Player> getPlayersCached(){
        return cachedPlaytime.keySet();
    }

    public static boolean checkPlaytime(Player player){
        Playtime playtime = PlaytimeManager.getPlaytime(player);
        if(playtime.getTodaySeconds()>= PlaytimeManager.LIMIT_TIME_SECONDS
                && isVirtualSameDay(playtime.getTodayDate())){
            return false;
        }else return true;
    }

}
