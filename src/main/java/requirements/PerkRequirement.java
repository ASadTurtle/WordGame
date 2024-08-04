package requirements;

import game.Player;

/**
 * A perk requirement checks if the player has the required perk, by calling
 * the {@code player.hasPerk(perk)} method.
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
