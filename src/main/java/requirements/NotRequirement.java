package requirements;

import game.Player;

public class NotRequirement implements Requirement {
    Requirement requirement;

    public NotRequirement(Requirement requirement) {
        this.requirement = requirement;
    }

    @Override
    public boolean evaluate(Player player) {
        return !requirement.evaluate(player);
    }

}
