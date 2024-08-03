package events;

import game.Player;

/**
 * A GetPerkEvent is an event that gives the player a perk. Need I say more?
 * 
 * @author Ahmed El-Sayed
 */
public class GetPerkEvent implements Event {
    private String perk;

    public GetPerkEvent(String perk) {
        this.perk = perk;
    }

    /**
     * This Event means the player gains a perk.
     */
    public void runEvent(Player player) {
        player.gainPerk(perk);
    }
}
