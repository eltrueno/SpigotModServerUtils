package es.eltrueno.modserverutils.pickupcancel;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PickupCommandHandler implements CommandExecutor {

    public static List<UUID> toCancelPickup = new ArrayList<UUID>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(label.equalsIgnoreCase("pickup")){
            if(sender instanceof Player){
                Player p = (Player) sender;
                if(toCancelPickup.contains(p.getUniqueId())){
                    if(p.getUniqueId().toString().equals("3c54db02-dbc9-4080-a7d4-22a4d6194606")){
                        p.sendMessage("§aAhora coges items del suelo (Lo que te gustaría hacerla a Paulita)");
                    }
                    else p.sendMessage("§aActivada §ela recolección de items del suelo");
                    toCancelPickup.remove(p.getUniqueId());
                }else{
                    toCancelPickup.add(p.getUniqueId());
                    if(p.getUniqueId().toString().equals("3c54db02-dbc9-4080-a7d4-22a4d6194606")){
                        p.sendMessage("§cAhora no coges items del suelo (igual que con Paulita)");
                    }
                    else p.sendMessage("§cCancelada §ela recolección de items del suelo");
                }
            } else sender.sendMessage("Que haces?");
        }
        return true;
    }
}
