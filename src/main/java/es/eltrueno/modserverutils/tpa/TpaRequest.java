package es.eltrueno.modserverutils.tpa;

import org.bukkit.entity.Player;

public class TpaRequest {

    private Player sender;
    private Player reciver;

    public TpaRequest(Player request, Player recived) {
        this.sender = request;
        this.reciver = recived;
    }

    public Player getSender() {
        return sender;
    }

    public void setSender(Player sender) {
        this.sender = sender;
    }

    public Player getReciver() {
        return reciver;
    }

    public void setReciver(Player reciver) {
        this.reciver = reciver;
    }

    public enum PlayerType {
        SENDER,
        RECIVER
    }
}
