package scenes;

import java.util.HashMap;
import java.util.Optional;
import java.util.Scanner;

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
    private String lines;
    private Optional<Event> event;

    public Scene(String index, String lines, Optional<Event> event) {
        this.index = index;
        this.lines = lines;
        this.event = event;
    }

    public String index() {
        return this.index;
    }

    public String lines() {
        return this.lines;
    }

    public void logEvent() {
        if (event.isPresent()) {
            event.get().logEvent();
        }
    }

    /**
     * The run behaviour of the scene. Outputs the index of the next scene.
     * 
     * @return The index of the next scene
     */
    public abstract String run(Scanner sc, HashMap<String, Scene> scenes, Player player);

    /**
     * Modify the players state by resolving the event in this scene, if one
     * exists.
     * 
     * @param player The player character
     */
    public void runEvent(Player player) {
        if (event.isPresent()) {
            event.get().runEvent(player);
            event.get().logEvent();
        }
    }
}
