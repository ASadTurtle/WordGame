package requirements;

import java.util.ArrayList;

import game.Player;

/**
 * TODO
 * 
 * @author Ahmed El-Sayed
 */
public class AndRequirement implements Requirement {
    ArrayList<Requirement> requirements;

    public AndRequirement(ArrayList<Requirement> requirements) {
        this.requirements = requirements;
    }

    public boolean evaluate(Player player) {
        return requirements
                .stream()
                .allMatch(requirement -> requirement.evaluate(player));
    }
}
