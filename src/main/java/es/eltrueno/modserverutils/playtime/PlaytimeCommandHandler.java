package es.eltrueno.modserverutils.playtime;

import es.eltrueno.modserverutils.Main;
import es.eltrueno.modserverutils.Utils;
import es.eltrueno.modserverutils.home.HomeManager;
import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlaytimeCommandHandler implements TabExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(label.equalsIgnoreCase("playtime")||label.equalsIgnoreCase("tiempo")
                ||label.equalsIgnoreCase("tiempojuego")||label.equalsIgnoreCase("tiempodejuego")){
            if(sender instanceof Player){
                Player p = (Player) sender;
                Playtime playerPlaytime = PlaytimeManager.getPlaytime(p);
                final long rest = PlaytimeManager.LIMIT_TIME_SECONDS - playerPlaytime.getTodaySeconds();
                String tiempoFormatted = Utils.calculateTotalTime(playerPlaytime.getTodaySeconds());
                String resFormatted = Utils.calculateTotalTime(rest);
                String totalFormatted = Utils.calculateTotalTime(playerPlaytime.getTotalSeconds());
                if(args.length==0){
                    p.sendMessage("§e======================================");
                    p.sendMessage("");
                    p.sendMessage("§eHoy llevas jugado: §b"+tiempoFormatted);
                    p.sendMessage("§eTiempo diario restante: §b"+resFormatted);
                    p.sendMessage("§eTiempo total jugado: §b"+totalFormatted);
                    p.sendMessage("");
                    p.sendMessage("§e======================================");
                }else if (args.length==1){
                    String subcmd = args[0];
                    if(subcmd.equalsIgnoreCase("hudarriba")||subcmd.equalsIgnoreCase("tophud")){
                        if(PlaytimeManager.bossbarVisible.contains(p.getUniqueId())){
                            //bossbar invisible
                            PlaytimeManager.bossbarVisible.remove(p.getUniqueId());
                            final BossBar bb = PlaytimeManager.getBossbar(p);
                            PlaytimeManager.deleteCachedBossbar(p);
                            Bukkit.getScheduler().runTaskLater(Main.plugin, new Runnable() {
                                @Override
                                public void run() {
                                    bb.setVisible(false);
                                    bb.removeAll();
                                }
                            },10);
                            p.sendMessage("§eHUD superior §cdesctivado");
                        }else{
                            //bossbar visible
                            PlaytimeManager.bossbarVisible.add(p.getUniqueId());
                            p.sendMessage("§eHUD superior §aactivado");
                        }
                    }
                    else if(subcmd.equalsIgnoreCase("hudabajo")||subcmd.equalsIgnoreCase("bottomhud")){
                        if(PlaytimeManager.actionbarVisible.contains(p.getUniqueId())){
                            //actionbar invisible
                            PlaytimeManager.actionbarVisible.remove(p.getUniqueId());
                            p.sendMessage("§eHUD inferior §cdesctivado");
                        }else{
                            //actionbar visible
                            PlaytimeManager.actionbarVisible.add(p.getUniqueId());
                            p.sendMessage("§eHUD inferior §aactivado");
                        }
                    }
                }
            } else sender.sendMessage("¿Qué haces?");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player){
            Player p = (Player) sender;
            String arg = args.length>=1 ? args[args.length-1]: "";
            if((label.equalsIgnoreCase("playtime")||label.equalsIgnoreCase("tiempo")
                    ||label.equalsIgnoreCase("tiempojuego")||label.equalsIgnoreCase("tiempodejuego")) && args.length == 1){
                ArrayList<String> validArgs = new ArrayList<String>();
                validArgs.add("tophud");
                validArgs.add("bottomhud");
                validArgs.add("hudarriba");
                validArgs.add("hudabajo");
                return StringUtil.copyPartialMatches(arg, validArgs, new ArrayList<>());
            }

        } else sender.sendMessage("Que haces?");
        return new ArrayList<>();
    }
}
