package es.eltrueno.modserverutils.home;

import com.google.gson.*;
import es.eltrueno.modserverutils.JsonDataManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class HomeManager {
    public static final int MAX_HOMES = 3;

    private static HashMap<String, List<Home>> homeCache = new HashMap<>();

    private static void saveHomesData(String uuid, JsonObject homesJsonObject){
        try {
            JsonObject parentObj = JsonDataManager.getUserObject(uuid);
            if(parentObj!=null){
                if(parentObj.has("homes")) parentObj.remove(uuid);
                parentObj.add("homes", homesJsonObject);
                JsonDataManager.saveUserData(uuid, parentObj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static JsonObject getHomesJsonObject(String uuid){
        JsonObject homesObj = null;
        try {
            JsonObject parentObj = JsonDataManager.getUserObject(uuid);
            if(parentObj!=null){
                if(parentObj.has("homes")){
                    homesObj = parentObj.getAsJsonObject("homes");
                }else{
                    JsonObject newHomesObj = new JsonObject();
                    parentObj.add("homes", newHomesObj);
                    JsonDataManager.saveUserData(uuid, parentObj);
                    homesObj = newHomesObj;

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return homesObj;
    }

    private static void cacheHomesFromJson(Player player){
        homeCache.remove(player.getUniqueId().toString());
        List<Home> jsonHomes = getHomesFromJson(player.getUniqueId().toString());
        homeCache.put(player.getUniqueId().toString(), jsonHomes==null ? new ArrayList<>() : jsonHomes);
    }

    private static void saveHomes(Player player, List<Home> homes){
        homeCache.replace(player.getUniqueId().toString(), homes);

        JsonObject homesJsonObj = new JsonObject();
        for(Home home : homes){
            JsonObject newHomeObj = new JsonObject();
            newHomeObj.addProperty("name", home.getName());
            newHomeObj.addProperty("world", home.getLocation().getWorld().getName());
            newHomeObj.addProperty("x", home.getLocation().getX());
            newHomeObj.addProperty("y", home.getLocation().getY());
            newHomeObj.addProperty("z", home.getLocation().getZ());
            newHomeObj.addProperty("pitch", home.getLocation().getPitch());
            newHomeObj.addProperty("yaw", home.getLocation().getYaw());

            homesJsonObj.add(home.getName(), newHomeObj);
        }
        saveHomesData(player.getUniqueId().toString(), homesJsonObj);
    }

    public static int addHome(Player player, String homename, Location loc){
        if(!homeCache.containsKey(player.getUniqueId().toString()))cacheHomesFromJson(player);
        List<Home> homesCached = homeCache.get(player.getUniqueId().toString());
        if(homesCached.size()<MAX_HOMES){
            boolean containsHomeName = getHomeByName(player, homename)!=null;

            if(!containsHomeName){
                homesCached.add(new Home(homename, loc));
                homeCache.replace(player.getUniqueId().toString(), homesCached);
                saveHomes(player, homesCached);
                return 0;
            }else return 1;
        }else return 2;
    }

    public static boolean delHome(Player player, String homename){
        if(!homeCache.containsKey(player.getUniqueId().toString()))cacheHomesFromJson(player);
        List<Home> homesCached = homeCache.get(player.getUniqueId().toString());

        Home home = getHomeByName(player, homename);
        if(homesCached.isEmpty() || (!homesCached.isEmpty() && home==null)) return false;
        else{
            homesCached.remove(home);
            saveHomes(player, homesCached);
            return true;
        }
    }

    private static List<Home> getHomesFromJson(String uuid){
        JsonObject homesJsonObject = getHomesJsonObject(uuid);

        if(homesJsonObject == null) return null;

        Set<Map.Entry<String, JsonElement>> entries = homesJsonObject.entrySet();
        ArrayList<Home> pHomesArray = new ArrayList<Home>();
        for (Map.Entry<String, JsonElement> entry: entries) {
            String world = entry.getValue().getAsJsonObject().get("world").getAsString();
            double x = entry.getValue().getAsJsonObject().get("x").getAsDouble();
            double y = entry.getValue().getAsJsonObject().get("y").getAsDouble();
            double z = entry.getValue().getAsJsonObject().get("z").getAsDouble();
            float yaw = entry.getValue().getAsJsonObject().get("yaw").getAsFloat();
            float pitch = entry.getValue().getAsJsonObject().get("pitch").getAsFloat();
            pHomesArray.add(new Home(entry.getKey(), new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch)));
        }
        return pHomesArray;
    }

    public static List<Home> getHomes(Player player){
        List<Home> retHomes = new ArrayList<>();
        if(homeCache.containsKey(player.getUniqueId().toString())){
            retHomes = homeCache.get(player.getUniqueId().toString());
        }else{
            retHomes = getHomesFromJson(player.getUniqueId().toString());
            homeCache.put(player.getUniqueId().toString(), retHomes);
        }
        return retHomes;
    }

    public static Home getHomeByName(Player player, String homename){
        try{
            Home home = null;
            for(Home h : homeCache.get(player.getUniqueId().toString())){
                if(h.getName().equals(homename)){
                    home = h;
                    break;
                }
            }
            return home;
        }catch(Exception ex){
            return null;
        }
    }

    public static Home getHomeByNameFromJson(String uuid, String homename){
        JsonObject homesJsonObject = getHomesJsonObject(uuid);

        if(homesJsonObject == null || !homesJsonObject.has(homename)) return null;

        String world = homesJsonObject.get(homename).getAsJsonObject().get("world").getAsString();
        double x = homesJsonObject.get(homename).getAsJsonObject().get("x").getAsDouble();
        double y = homesJsonObject.get(homename).getAsJsonObject().get("y").getAsDouble();
        double z = homesJsonObject.get(homename).getAsJsonObject().get("z").getAsDouble();
        float yaw = homesJsonObject.get(homename).getAsJsonObject().get("yaw").getAsFloat();
        float pitch = homesJsonObject.get(homename).getAsJsonObject().get("pitch").getAsFloat();
        return new Home(homesJsonObject.get(homename).getAsJsonObject().get("name").getAsString()
                , new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch));
    }
}
