package requirements;

import java.util.ArrayList;

import game.Player;

/**
 * TODO
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
