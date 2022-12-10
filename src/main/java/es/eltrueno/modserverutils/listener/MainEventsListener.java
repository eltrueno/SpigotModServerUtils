package es.eltrueno.modserverutils.listener;

import es.eltrueno.modserverutils.Main;
import es.eltrueno.modserverutils.home.HomeManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class MainEventsListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onCommandPreProcess(PlayerCommandPreprocessEvent ev) {
        Player player = ev.getPlayer();
        String[] msg = ev.getMessage().split(" ");
        if(!player.getName().equalsIgnoreCase("el_trueno")){
            if(!msg[0].equalsIgnoreCase("/sethome")
                    && !msg[0].equalsIgnoreCase("/delhome")
                    && !msg[0].equalsIgnoreCase("/home")
                    && !msg[0].equalsIgnoreCase("/homes")
                    && !msg[0].equalsIgnoreCase("/tpa")
                    && !msg[0].equalsIgnoreCase("/tpaccept")
                    && !msg[0].equalsIgnoreCase("/tpadeny")
                    && !msg[0].equalsIgnoreCase("/msg")
                    && !msg[0].equalsIgnoreCase("/tell")
                    && !msg[0].equalsIgnoreCase("/help")
                    && !msg[0].equalsIgnoreCase("/back")
                    && !msg[0].equalsIgnoreCase("/pickup")
            ){
                ev.setCancelled(true);
                player.sendMessage("¿¿A ver, desde cuando existe ese comando en minecraft?? Imbecil");
            }else if(msg[0].equalsIgnoreCase("/msg")){
                Bukkit.getLogger().info("[MSG_LOG] "+ player.getName()+" escribio a "+msg[1]+" | "+ev.getMessage());
            }else if(msg[0].equalsIgnoreCase("/help")){
                player.sendMessage("§a======================================");
                player.sendMessage("");
                player.sendMessage("§e/pickup: §fActiva o desactiva la recolección de items del suelo");
                player.sendMessage("§e/sethome <nombre>: §fEstablece un home ("+ HomeManager.MAX_HOMES +"§f max)");
                player.sendMessage("§e/delhome <nombre>: §fBorra un home");
                player.sendMessage("§e/home <nombre>: §fVe a tu home");
                player.sendMessage("§e/tpa <jugador>: §fEnvia petición de tp a jugador");
                player.sendMessage("§e/tpaccept: §fAcepta petición de tp (o click en el chat)");
                player.sendMessage("§e/tpadeny: §fDeniega petición de tp (o click en el chat)");
                player.sendMessage("");
                player.sendMessage("§cNOTA: El sistema de TPA lo hice yo en 5min, asique seguramente este bugeadisimo :D. Si mandais peticion a la vez a alguien seguramente pete, asique no lo hagais :*");
                player.sendMessage("");
                player.sendMessage("§a======================================");
                ev.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent ev){
        Player p = ev.getEntity().getPlayer();
        if(Main.lastLocation.get(p)!=null){
            Main.lastLocation.replace(p, p.getLocation());
        }else Main.lastLocation.put(p, p.getLocation());
    }

}
