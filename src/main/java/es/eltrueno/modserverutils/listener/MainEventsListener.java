package es.eltrueno.modserverutils.listener;

import es.eltrueno.modserverutils.Main;
import es.eltrueno.modserverutils.home.HomeManager;
import es.eltrueno.modserverutils.playtime.PlaytimeManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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
                    && !msg[0].equalsIgnoreCase("/playtime")
                    && !msg[0].equalsIgnoreCase("/tiempo")
                    && !msg[0].equalsIgnoreCase("/tiempojuego")
                    && !msg[0].equalsIgnoreCase("/tiempodejuego")
                    && !msg[0].equalsIgnoreCase("/listhomes")
                    && !msg[0].equalsIgnoreCase("/homeslist")
                    && !msg[0].equalsIgnoreCase("/homelist")
            ){
                ev.setCancelled(true);
                player.sendMessage("¿¿A ver, desde cuando existe ese comando en minecraft?? Imbécil");
            }else if(msg[0].equalsIgnoreCase("/msg")){
                Bukkit.getLogger().info("[MSG_LOG] "+ player.getName()+" escribió a "+msg[1]+" | "+ev.getMessage());
            }else if(msg[0].equalsIgnoreCase("/help")){
                player.sendMessage("§a======================================");
                player.sendMessage("");
                player.sendMessage("§e/pickup: §fActiva o desactiva la recolección de items del suelo");
                player.sendMessage("§e/sethome §b<nombre>§e: §fEstablece un home ("+ HomeManager.MAX_HOMES +"§f max)");
                player.sendMessage("§e/delhome §b<nombre>§e: §fBorra un home");
                player.sendMessage("§e/home §b<nombre>§e: §fVe a tu home");
                player.sendMessage("§e/tpa §b<jugador>§e: §fEnvia petición de tp a jugador");
                player.sendMessage("§e/tpaccept: §fAcepta petición de tp (o click en el chat)");
                player.sendMessage("§e/tpadeny: §fDeniega petición de tp (o click en el chat)");
                player.sendMessage("§e/tiempo: §fMuestra información del tiempo de juego");
                player.sendMessage("§e/tiempo §b[tophud,hudarriba]§e: §fMuestra/oculta el tiempo restante en la parte superior");
                player.sendMessage("§e/tiempo §b[bottomhud,hudabajo]§e: §fMuestra/oculta el tiempo restante en la parte inferior");
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

    @EventHandler
    public void onQuit(PlayerQuitEvent ev){
        Player p = ev.getPlayer();
        PlaytimeManager.deleteCachedBossbar(p);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent ev){
        Player p = ev.getPlayer();
        if(!PlaytimeManager.actionbarVisible.contains(p.getUniqueId()))PlaytimeManager.actionbarVisible.add(p.getUniqueId());
        if(!PlaytimeManager.bossbarVisible.contains(p.getUniqueId()))PlaytimeManager.bossbarVisible.add(p.getUniqueId());
    }

}
