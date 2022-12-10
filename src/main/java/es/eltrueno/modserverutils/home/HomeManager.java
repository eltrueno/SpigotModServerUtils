package es.eltrueno.modserverutils.home;

import com.google.gson.*;
import es.eltrueno.modserverutils.JsonDataManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class HomeManager {
    public static final int MAX_HOMES = 3;

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

    public static int addHome(String uuid, String homename, Location loc){
        JsonObject homesJsonObject = getHomesJsonObject(uuid);
        if(homesJsonObject!=null){
            if(homesJsonObject.size()<MAX_HOMES){
                if(!homesJsonObject.has(homename)){
                    JsonObject newHomeObj = new JsonObject();
                    newHomeObj.addProperty("name", homename);
                    newHomeObj.addProperty("world", loc.getWorld().getName());
                    newHomeObj.addProperty("x", loc.getX());
                    newHomeObj.addProperty("y", loc.getY());
                    newHomeObj.addProperty("z", loc.getZ());
                    newHomeObj.addProperty("pitch", loc.getPitch());
                    newHomeObj.addProperty("yaw", loc.getYaw());

                    homesJsonObject.add(homename, newHomeObj);
                    saveHomesData(uuid, homesJsonObject);
                    return 0;
                }else return 1;
            }else return 2;
        }else return -1;
    }

    public static boolean delHome(String uuid, String homename){
        JsonObject homesJsonObject = getHomesJsonObject(uuid);
        if(homesJsonObject!=null){
            if(homesJsonObject.has(homename)){
                homesJsonObject.remove(homename);
                saveHomesData(uuid, homesJsonObject);
                return true;
            }else return false;
        }else return false;
    }

    public static ArrayList<Home> getHomes(String uuid){
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

    public static Home getHomeByName(String uuid, String homename){
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
