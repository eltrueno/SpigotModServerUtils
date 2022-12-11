package es.eltrueno.modserverutils.tpa;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpaCommandHandler implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(label.equalsIgnoreCase("tpa")){
            if(sender instanceof Player){
                Player psender = (Player) sender;
                if(args.length==1){
                    String playername = args[0];
                    if(Bukkit.getPlayer(playername)!=null && Bukkit.getPlayer(playername).isOnline()){
                        Player preciver = Bukkit.getPlayer(playername);
                        if(preciver.getUniqueId().toString().equals(psender.getUniqueId().toString())){
                            psender.sendMessage(ChatColor.YELLOW+"¿Eres tonto? ¿Intentando hacerte tp a ti mismo? Tu sigue... Luego lloramos cuando se escape un /kill "+psender.getName()+" ...");
                        }else{
                            if(TpaManager.canSendTpa(psender, preciver)){
                                TpaManager.createTpaRequest(psender, preciver);
                            }else{
                                TpaRequest.PlayerType senderType =TpaManager.getPlayerType(psender);
                                TpaRequest.PlayerType reciverType =TpaManager.getPlayerType(preciver);
                                if(senderType==TpaRequest.PlayerType.SENDER){
                                    psender.sendMessage(ChatColor.RED+"Ya has enviado una petición de teletransporte. Esperate un rato majo, no seas impaciente...");
                                } else if(senderType==TpaRequest.PlayerType.RECIVER){
                                    psender.sendMessage(ChatColor.RED+"No puedes enviar una peticion de teletransporte cuando tienes una pendiente de aceptar, tonto.");
                                }
                                if(senderType==null) {
                                    psender.sendMessage(ChatColor.RED + "Ese jugador ya tiene otra peticion de teletransporte pendiente. Esperate un rato majo, no seas impaciente...");
                                }
                            }
                        }
                    }else psender.sendMessage(ChatColor.RED+"¿¿¿Tu ves algún jugador conectado que se llame "+playername+" ??? Porque yo no, asique dificilmente puedo tepearte a el. PUTO INUTUL");

                }else psender.sendMessage(ChatColor.RED+"Se usa así hijo: "+ChatColor.YELLOW+"/tpa <jugador>");

            } else sender.sendMessage("Que haces?");
        }
        if(label.equalsIgnoreCase("tpaccept")){
            if(sender instanceof Player){
                Player p = (Player) sender;
                TpaRequest req = TpaManager.getRecieverRequest(p);
                if(req!=null){
                    TpaManager.acceptTpaRequest(req);
                }else p.sendMessage(ChatColor.RED+"No tienes ninguna petición de tp. Puto mono");
                /*if(tparequests.containsKey(p)){
                    Player totp = tparequests.get(p);
                    if(lastLocation.get(totp)!=null){
                        lastLocation.replace(totp, totp.getLocation());
                    }else lastLocation.put(totp, totp.getLocation());
                    p.sendMessage(ChatColor.YELLOW+"Teletransportando a §b"+totp.getName()+"§e hacia ti...");
                    totp.sendMessage(ChatColor.YELLOW+"§b"+p.getName()+" §eHa aceptado tu solicitud de tp. Teletransportando...");
                    totp.teleport(p);
                    tparequests.remove(p);
                }else{
                    p.sendMessage(ChatColor.RED+"No tienes ninguna peticion de tp. Puto mono");
                }*/

            } else sender.sendMessage("Que haces?");
        }
        if(label.equalsIgnoreCase("tpadeny")){
            if(sender instanceof Player){
                Player p = (Player) sender;
                TpaRequest req = TpaManager.getRecieverRequest(p);
                if(req!=null){
                    TpaManager.denyTpaRequest(req);
                }else p.sendMessage(ChatColor.RED+"No tienes ninguna petición de tp. Puto mono");

            } else sender.sendMessage("Que haces?");
        }
        return true;
    }
}
