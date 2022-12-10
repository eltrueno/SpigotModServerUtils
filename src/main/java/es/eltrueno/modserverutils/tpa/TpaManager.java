package es.eltrueno.modserverutils.tpa;

import es.eltrueno.modserverutils.Main;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TpaManager {

    //public static HashMap<TpaRequest, LocalTime> tpaRequests = new HashMap<>();
    private static List<TpaRequest> tpaRequests = new ArrayList<TpaRequest>();

    private static boolean containsPlayer(Player player){
        boolean contains = false;
        for(TpaRequest req : tpaRequests){
            if (req.getReciver().equals(player) || req.getSender().equals(player)){
                contains = true;
                break;
            }
        }
        return contains;
    }

    public static TpaRequest.PlayerType getPlayerType(Player player){
        TpaRequest.PlayerType playerType = null;
        for(TpaRequest req : tpaRequests){
            if (req.getReciver().equals(player)){
                playerType = TpaRequest.PlayerType.RECIVER;
                break;
            }
            if (req.getSender().equals(player)){
                playerType = TpaRequest.PlayerType.SENDER;
                break;
            }
        }
        return playerType;
    }

    public static boolean canSendTpa(Player sender, Player reciever){
        return (!containsPlayer(sender) && !containsPlayer(reciever));
    }

    public static TpaRequest getSenderRequest(Player sender){
        TpaRequest request = null;
        for(TpaRequest req : tpaRequests){
            if(req.getSender().equals(sender)){
                request = req;
                break;
            }
        }
        return request;
    }

    public static TpaRequest getRecieverRequest(Player reciever){
        TpaRequest request = null;
        for(TpaRequest req : tpaRequests){
            if(req.getReciver().equals(reciever)){
                request = req;
                break;
            }
        }
        return request;
    }

    public static void acceptTpaRequest(TpaRequest request){
        Player playerSender = request.getSender();

        if(Main.lastLocation.get(playerSender)!=null){
            Main.lastLocation.replace(playerSender, playerSender.getLocation());
        }else Main.lastLocation.put(playerSender, playerSender.getLocation());

        request.getReciver().sendMessage("§eTeletransportando a §b"+playerSender.getName()+"§e hacia ti...");
        playerSender.sendMessage("§b"+request.getReciver().getName()+" §eHa aceptado tu solicitud de tp. Teletransportando...");
        playerSender.teleport(request.getReciver());

        tpaRequests.remove(request);
    }

    public static void denyTpaRequest(TpaRequest request){
        Player playerSender = request.getSender();

        request.getReciver().sendMessage("§cHas rechazado la petición de teletransporte de §b"+playerSender.getName()+"§c hacia ti. Esta feo eso eh.");
        playerSender.sendMessage("§b"+request.getReciver().getName()+" §cHa rechazado tu solicitud de tp");

        tpaRequests.remove(request);
    }


    public static void createTpaRequest(Player sender, Player reciever){
        final TpaRequest request = new TpaRequest(sender, reciever);
        tpaRequests.add(request);

        sender.sendMessage("§eSe ha enviado una petición de teletransporte a §b"+ reciever.getName()+" §eTiene 10 segundos para aceptar tu solicitud");

        reciever.sendMessage("§eTienes una petición de teletransporte de §b"+sender.getName()+"§e Se eliminará en 10 segundos");
        TextComponent acceptComponent = new TextComponent("      §a[ACEPTAR]      ");
        acceptComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§aClick para aceptar").create()));
        acceptComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept"));
        TextComponent denyComponent = new TextComponent("      §c[RECHAZAR]     ");
        denyComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§cClick para rechazar").create()));
        denyComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpadeny"));
        reciever.spigot().sendMessage(acceptComponent, denyComponent);

        Bukkit.getScheduler().runTaskLaterAsynchronously(Main.plugin, new Runnable() {
            @Override
            public void run() {
                if(tpaRequests.contains(request)) {
                    tpaRequests.remove(request);
                    sender.sendMessage("§cSe ha agotado el tiempo de tu peticion de teletransporte con §b" + reciever.getName());
                    reciever.sendMessage("§cSe ha agotado el tiempo de tu peticion de teletransporte de §b" + sender.getName());
                }
            }
        },20*10);
    }


}
