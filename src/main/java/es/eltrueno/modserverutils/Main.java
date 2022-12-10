package es.eltrueno.modserverutils;

import es.eltrueno.modserverutils.home.HomeCommandExecutor;
import es.eltrueno.modserverutils.listener.MainEventsListener;
import es.eltrueno.modserverutils.listener.PickupListener;
import es.eltrueno.modserverutils.pickupcancel.PickupCommandExecutor;
import es.eltrueno.modserverutils.tpa.TpaCommandExecutor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class Main extends JavaPlugin implements CommandExecutor {

    public static Main plugin;

    public static HashMap<Player, Location> lastLocation = new HashMap<Player, Location>();

    @Override
    public void onEnable(){
        plugin = this;
        try {
            plugin.getDataFolder().mkdir();
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonDataManager.getRootJson();
        registerCommands();

        Bukkit.getPluginManager().registerEvents(new MainEventsListener(), this);
        Bukkit.getPluginManager().registerEvents(new PickupListener(), this);
    }

    private static void registerCommands(){
        Bukkit.getPluginCommand("home").setExecutor(new HomeCommandExecutor());
        Bukkit.getPluginCommand("homes").setExecutor(new HomeCommandExecutor());
        Bukkit.getPluginCommand("sethome").setExecutor(new HomeCommandExecutor());
        Bukkit.getPluginCommand("delhome").setExecutor(new HomeCommandExecutor());

        Bukkit.getPluginCommand("pickup").setExecutor(new PickupCommandExecutor());

        Bukkit.getPluginCommand("tpa").setExecutor(new TpaCommandExecutor());
        Bukkit.getPluginCommand("tpaccept").setExecutor(new TpaCommandExecutor());
        Bukkit.getPluginCommand("tpadeny").setExecutor(new TpaCommandExecutor());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if(label.equalsIgnoreCase("back")){
            if(sender instanceof Player){
                Player p = (Player) sender;
                if(lastLocation.get(p)!=null){
                    Location loc = lastLocation.get(p);
                    p.sendMessage(ChatColor.YELLOW+"Teletransportandote a la anterior localización...");
                    if(lastLocation.get(p)!=null){
                        lastLocation.replace(p, p.getLocation());
                    }else lastLocation.put(p, p.getLocation());
                    p.teleport(loc);
                }else p.sendMessage(ChatColor.RED+"A ver, imbécil. ¿Cómo vas a ir para atrás si no has ido a ningun lado aún, puto calvo?");
            } else sender.sendMessage("Que haces?");
        }
        return true;
    }

}
