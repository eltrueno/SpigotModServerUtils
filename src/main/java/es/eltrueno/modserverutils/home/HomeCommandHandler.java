package es.eltrueno.modserverutils.home;

import es.eltrueno.modserverutils.Main;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class HomeCommandHandler implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(label.equalsIgnoreCase("homes")||label.equalsIgnoreCase("homelist")||label.equalsIgnoreCase("listhomes")){
            if(sender instanceof Player){
                Player p = (Player) sender;
                if(!HomeManager.getHomes(p).isEmpty()){
                    List<Home> homes = HomeManager.getHomes(p);
                    p.sendMessage("§e======================================");
                    p.sendMessage("");
                    for(Home home : homes){
                        TextComponent msg = new TextComponent("§e- ");
                        TextComponent homeMsg = new TextComponent("§b"+home.getName());
                        homeMsg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§aClick para Teletransportarse a §b"+home.getName()).create()));
                        homeMsg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/home "+home.getName()));
                        p.spigot().sendMessage(msg,homeMsg);
                    }
                    p.sendMessage("");
                    p.sendMessage("§e======================================");
                }else sender.sendMessage(ChatColor.RED+"No tienes homes hijo :c");

            } else sender.sendMessage("Que haces?");
        }
        if(label.equalsIgnoreCase("sethome")){
            if(sender instanceof Player){
                Player p = (Player) sender;
                if(args.length==1){
                    String homename = args[0];
                    int ret = HomeManager.addHome(p, homename.toLowerCase(), p.getLocation().clone());
                    if(ret==-1){
                        p.sendMessage(ChatColor.RED+"ERROR INTERNO. F EN EL CHAT");
                    }else if(ret==0){
                        p.sendMessage(ChatColor.GREEN+"Home guardada correctamente bro");
                    }else if(ret==1){
                        p.sendMessage(ChatColor.RED+"Te das cuenta de que ya tienes una home con ese nombre?? Tienes que borrarla antes. IMBÉCIL");
                    }else if(ret==2){
                        p.sendMessage(ChatColor.RED+"Has superado el limite de homes ("+HomeManager.MAX_HOMES+ChatColor.RED+"). Te jodes y vas andando o borras otra home. Puto simio");
                    }
                }else sender.sendMessage(ChatColor.RED+"Se usa así hijo: "+ChatColor.YELLOW+"/sethome <nombre>");

            } else sender.sendMessage("Que haces?");
        }
        if(label.equalsIgnoreCase("home")){
            if(sender instanceof Player){
                Player p = (Player) sender;
                if(args.length==1){
                    String homename = args[0];
                    if(HomeManager.getHomeByName(p, homename.toLowerCase())!=null){
                        Home home = HomeManager.getHomeByName(p, homename.toLowerCase());
                        if(Main.lastLocation.get(p)!=null){
                            Main.lastLocation.replace(p, p.getLocation());
                        }else Main.lastLocation.put(p, p.getLocation());
                        p.sendMessage(ChatColor.YELLOW+"Teletransportando....");
                        p.teleport(home.getLocation());
                    }else{
                        p.sendMessage(ChatColor.RED+"No tienes ninguna home con ese nombre. Lo vas a escribir bien, o te tengo que ayudar a base de ostias??");
                    }
                }else sender.sendMessage(ChatColor.RED+"Se usa así hijo: "+ChatColor.YELLOW+"/home <nombre>");

            } else sender.sendMessage("Que haces?");
        }
        if(label.equalsIgnoreCase("delhome")){
            if(sender instanceof Player){
                Player p = (Player) sender;
                if(args.length==1){
                    String homename = args[0];
                    if(HomeManager.delHome(p, homename.toLowerCase())){
                        p.sendMessage(ChatColor.GREEN+"Home borrada. Luego lloraremos....");
                    }else p.sendMessage(ChatColor.RED+"No tienes ninguna home con ese nombre. Lo vas a escribir bien, o te tengo que ayudar a base de ostias??");

                }else sender.sendMessage(ChatColor.RED+"Se usa así hijo: "+ChatColor.YELLOW+"/delhome <nombre>");

            } else sender.sendMessage("Que haces?");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player){
            Player p = (Player) sender;
            String arg = args.length>=1 ? args[args.length-1]: "";
            if(label.equalsIgnoreCase("home") && args.length == 1){
                ArrayList<String> homeNames = new ArrayList<String>();
                HomeManager.getHomes(p).forEach(home -> homeNames.add(home.getName()));

                return StringUtil.copyPartialMatches(arg, homeNames, new ArrayList<>());
            }
            if(label.equalsIgnoreCase("delhome") && args.length == 1){
                ArrayList<String> homeNames = new ArrayList<String>();
                HomeManager.getHomes(p).forEach(home -> homeNames.add(home.getName()));

                return StringUtil.copyPartialMatches(arg, homeNames, new ArrayList<>());
            }

        } else sender.sendMessage("Que haces?");
        return new ArrayList<>();
    }
}
