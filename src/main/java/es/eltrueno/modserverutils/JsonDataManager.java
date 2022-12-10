package es.eltrueno.modserverutils;

import com.google.gson.*;

import java.io.*;

public class JsonDataManager {

    public static JsonObject getRootJson(){
        JsonObject rootobj = null;
        try {
            JsonParser parser = new JsonParser();
            File f = new File(Main.plugin.getDataFolder()+"/userdata.json");
            if(!f.exists()) {
                f.createNewFile();
                try (Writer writer = new FileWriter(Main.plugin.getDataFolder()+"/userdata.json")) {
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    JsonObject json = new JsonObject();
                    gson.toJson(json, writer);
                }
            }
            JsonElement jsonElement = parser.parse(new FileReader(Main.plugin.getDataFolder()+"/userdata.json"));
            rootobj = jsonElement.getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rootobj;
    }

    public static JsonObject getUserObject(String uuid){
        JsonObject userObj = null;
        try {
            JsonObject rootObj = getRootJson();
            if(rootObj!=null){
                if(rootObj.has(uuid)){
                    userObj = rootObj.getAsJsonObject(uuid);
                }else{
                    JsonObject newUserObj = new JsonObject();
                    rootObj.add(uuid, newUserObj);
                    saveData(rootObj);
                    userObj = newUserObj;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userObj;
    }

    public static void saveData(JsonObject rootJsonObject){
        try (Writer writer = new FileWriter(Main.plugin.getDataFolder()+"/userdata.json")) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(rootJsonObject, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveUserData(String uuid, JsonObject userJsonObject){
        try {
            JsonObject parentObj = JsonDataManager.getRootJson();
            if(parentObj!=null){
                if(parentObj.has(uuid)) parentObj.remove(uuid);
                parentObj.add(uuid, userJsonObject);
                saveData(parentObj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
