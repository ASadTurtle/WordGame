package events;

import game.Player;

/**
 * A GetStatusEvent is an event that gives the player a status. Need I say more?
 * 
 * @author Ahmed El-Sayed
 */
public class GetStatusEvent implements Event {
    private String status;

    public GetStatusEvent(String status) {
        this.status = status;
    }

    /**
     * This event means the player gains a status.
     */
    public void runEvent(Player player) {
        player.gainStatus(status);
    }

    @Override
    public void logEvent() {
        System.out.printf("\033[93mNew Status: %s\n\n\033[0m", status);
    }
}
