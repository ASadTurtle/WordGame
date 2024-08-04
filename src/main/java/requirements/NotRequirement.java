package requirements;

import game.Player;

/**
 * A NOT requirement holds another requirement, and evaluates to true if its
 * sub-requirement evaluates to false.
 * 
 * @author Ahmed El-Sayed
 */
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
