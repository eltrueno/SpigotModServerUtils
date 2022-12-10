package es.eltrueno.modserverutils.pickupcancel;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PickupCommandExecutor implements CommandExecutor {

    public static List<UUID> toCancelPickup = new ArrayList<UUID>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(label.equalsIgnoreCase("pickup")){
            if(sender instanceof Player){
                Player p = (Player) sender;
                if(toCancelPickup.contains(p.getUniqueId())){
                    p.sendMessage(ChatColor.YELLOW+"§aActivada §ela recolección de items del suelo");
                    toCancelPickup.remove(p.getUniqueId());
                }else{
                    toCancelPickup.add(p.getUniqueId());
                    p.sendMessage(ChatColor.YELLOW+"§cCancelada §ela recolección de items del suelo");
                }
            } else sender.sendMessage("Que haces?");
        }
        return true;
    }
}
