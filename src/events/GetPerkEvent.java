package events;

import game.Player;

/**
 * A GetPerkEvent is an event that gives the player a perk. Need I say more?
 */
public class GetPerkEvent implements Event {
    private String perk;

    public GetPerkEvent(String perk) {
        this.perk = perk;
    }

    public void runEvent(Player player) {
        player.gainPerk(perk);
    }
}
