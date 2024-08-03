package scenes;

import java.util.ArrayList;
import java.util.Optional;

import events.Event;
import game.Player;

/**
 * <p>
 * The Scene class is an abstract class that should store the following
 * information:
 * </p>
 * 
 * <ul>
 * <li>index</li>
 * <li>lines</li>
 * <li>event</li>
 * </ul>
 * 
 * The concrete Scene type will determine how the scene is structured, and what
 * behaviour it should follow. See other scene classes for details on how this
 * can differ.
 * 
 * @author Ahmed El-Sayed
 */
public abstract class Scene {
    private String index;
    private ArrayList<String> lines;
    private ArrayList<String> roots;
    private Optional<Event> event;

    public Scene(String index, ArrayList<String> lines, ArrayList<String> roots, Optional<Event> event) {
        this.index = index;
        this.lines = lines;
        this.roots = roots;
        this.event = event;
    }

    public String index() {
        return this.index;
    }

    public ArrayList<String> lines() {
        return this.lines;
    }

    public ArrayList<String> roots() {
        return this.roots;
    }

    /**
     * Modify the players state by resolving the event in this scene, if one
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
