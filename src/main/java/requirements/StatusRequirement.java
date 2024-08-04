package requirements;

import game.Player;

/**
 * A status requirement checks if the player has the required status, by calling
 * the {@code player.hasStatus(status)} method.
 * 
 * @author Ahmed El-Sayed
 */
public class StatusRequirement implements Requirement {
    String status;

    public StatusRequirement(String status) {
        this.status = status;
    }

    public boolean evaluate(Player player) {
        return player.hasStatus(status);
    }
}
