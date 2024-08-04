package requirements;

import java.util.ArrayList;

import game.Player;

/**
 * An OR requirement holds a list of requirements, and evaluates to true if ANY
 * of its sub-requirements are also true.
 * 
 * @author Ahmed El-Sayed
 */
public class OrRequirement implements Requirement {
    ArrayList<Requirement> requirements;

    public OrRequirement(ArrayList<Requirement> requirements) {
        this.requirements = requirements;
    }

    public boolean evaluate(Player player) {
        return requirements
                .stream()
                .anyMatch(requirement -> requirement.evaluate(player));
    }
}
