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
        savePlaytimeToJson(player, playtime);
    }

    public static Playtime getPlaytime(Player player){
        if(!cachedPlaytime.containsKey(player)) cachePlaytimeFromJson(player);
        return cachedPlaytime.get(player);
    }

    public static Playtime getPlaytimeFromJson(String uuid){
        JsonObject playtimeJsonObj = getPlaytimeJsonObject(uuid);

        long totalSeconds = 0, todaySeconds = 0;
        Date todayDate;

        if(playtimeJsonObj!=null && playtimeJsonObj.has("totalSeconds")){
            totalSeconds = playtimeJsonObj.get("totalSeconds").getAsLong();
            todaySeconds = playtimeJsonObj.get("todaySeconds").getAsLong();
            todayDate = new Date(playtimeJsonObj.get("todayDate").getAsLong());

            if(!Utils.isSameDay(todayDate)){
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
        if(Utils.isSameDay(playtime.getTodayDate()) && playtime.getTodaySeconds()>= PlaytimeManager.LIMIT_TIME_SECONDS){
            return false;
        }else return true;
    }

}
