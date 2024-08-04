package requirements;

import game.Player;

/**
 * TODO
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
