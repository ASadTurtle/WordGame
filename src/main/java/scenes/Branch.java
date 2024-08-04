package scenes;

import java.util.Optional;

import events.Event;
import game.Player;

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

    public Branch(String nextScene, String prompt, Optional<Event> event) {
        this.nextScene = nextScene;
        this.prompt = prompt;
        this.event = event;
    }

    public String nextScene() {
        return nextScene;
    }

    public String prompt() {
        return prompt;
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
