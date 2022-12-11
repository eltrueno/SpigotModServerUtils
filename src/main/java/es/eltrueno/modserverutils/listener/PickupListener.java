package es.eltrueno.modserverutils.listener;

import es.eltrueno.modserverutils.pickupcancel.PickupCommandHandler;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class PickupListener implements Listener {

    @EventHandler
    public void onPickup(EntityPickupItemEvent ev){
        if(ev.getEntityType()== EntityType.PLAYER){
            Player p = (Player) ev.getEntity();
            if(PickupCommandHandler.toCancelPickup.contains(p.getUniqueId())) ev.setCancelled(true);
        }
    }

}
