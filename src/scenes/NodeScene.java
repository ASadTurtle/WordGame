package scenes;

import java.util.ArrayList;
import java.util.Optional;

import events.Event;

/**
 * A NodeScene is a scene that branches to other scenes, depending on the
 * players input.
 * 
 * It should list all available choices for the player to make,
 * and if the player ever returns to this scene from a branch, that branch
 * should not be available to select again.
 * 
 * The branches of a node scene is a list of Branch objects, the details of
 * which are specified in the respective class.
 */
public class NodeScene extends Scene {
    private ArrayList<Branch> branches;

    public NodeScene(String index, String lines, ArrayList<String> roots, Optional<Event> event,
            ArrayList<Branch> branches) {
        super(index, lines, roots, event);
        this.branches = branches;
    }

    public ArrayList<Branch> branches() {
        return branches;
    }
}
