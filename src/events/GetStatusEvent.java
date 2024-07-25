package events;

import game.Player;

/**
 * A GetStatusEvent is an event that gives the player a status. Need I say more?
 */
public class GetStatusEvent implements Event {
    private String status;

    public GetStatusEvent(String status) {
        this.status = status;
    }

    public void runEvent(Player player) {
        player.gainStatus(status);
    }
}
