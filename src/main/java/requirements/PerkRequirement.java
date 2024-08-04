package requirements;

import game.Player;

/**
 * TODO
 * 
 * @author Ahmed El-Sayed
 */
public class PerkRequirement implements Requirement {
    String perk;

    public PerkRequirement(String perk) {
        this.perk = perk;
    }

    public boolean evaluate(Player player) {
        return player.hasPerk(perk);
    }
}
