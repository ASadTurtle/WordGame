package scenes;

import java.util.ArrayList;
import java.util.Optional;

import events.Event;
import game.Player;

/**
 * The Scene class is an abstract class that should store the following
 * information:
 * - index
 * - lines
 * - event
 * 
 * The concrete Scene type will determine how the scene is structured, and what
 * behaviour it should follow. See other scene classes for details on how this
 * can differ.
 */
public abstract class Scene {
    private String index;
    private ArrayList<String> lines;
    private Optional<Event> event;

    /**
     * The scene constructor should fill all fields, and should not leave any
     * uninitialised.
     * 
     * @param index
     * @param lines
     * @param event
     */
    public Scene(String index, ArrayList<String> lines, Optional<Event> event) {
        this.index = index;
        this.lines = lines;
        this.event = event;
    }

    /**
     * Get the index of this scene in the library.
     */
    public String index() {
        return this.index;
    }

    /**
     * Play the lines in this scene before moving to the next.
     */
    public ArrayList<String> lines() {
        return this.lines;
    }

    /**
     * Modify the players state by resolving the event in this scene, if one
     * exists.
     */
    public void runEvent(Player player) {
        if (event.isPresent()) {
            event.get().runEvent(player);
        }
    }
}
