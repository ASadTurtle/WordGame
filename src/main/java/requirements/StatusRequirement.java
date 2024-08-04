package requirements;

import game.Player;

public class StatusRequirement implements Requirement {
    String status;

    public StatusRequirement(String status) {
        this.status = status;
    }

    public boolean evaluate(Player player) {
        return player.hasStatus(status);
    }
}
