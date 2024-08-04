package scenes;

import java.util.Optional;

import events.Event;
import game.Player;
import requirements.Requirement;

/**
 * The Branch class represents a possible branch from a scene. The branch
 * should have a prompt, as well as a scene it leads to, an index, and
 * optionally an event.
 * 
 * @author Ahmed El-Sayed
 */
public class Branch {
    private String nextScene;
    private String prompt;
    private Optional<Event> event;
    private Optional<Requirement> requirement;

    public Branch(String nextScene, String prompt, Optional<Event> event, Optional<Requirement> requirement) {
        this.nextScene = nextScene;
        this.prompt = prompt;
        this.event = event;
        this.requirement = requirement;
    }

    public String nextScene() {
        return nextScene;
    }

    public String prompt() {
        return prompt;
    }

    /**
     * Run the requirement for this branch, if it has one. If it does not then
     * it is always true.
     * 
     * @param player The player character
     * @return If the player meets the requirements to select this branch
     */
    public boolean evaluateRequirement(Player player) {
        if (requirement.isPresent())
            return requirement.get().evaluate(player);
        return true;
    }

    /**
     * Modify the players state by resolving the event in this branch, if one
     * exists.
     * 
     * @param player The player character
     */
    public void runEvent(Player player) {
        if (event.isPresent()) {
            event.get().runEvent(player);
        }
    }
}
