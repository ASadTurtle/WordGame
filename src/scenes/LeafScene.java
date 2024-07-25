package scenes;

import java.util.ArrayList;
import java.util.Optional;

import events.Event;

/**
 * A leaf scene is one that does not lead to any other branches. It is the final
 * scene originating from a certain branch, which we record as its root, and
 * will have a nextScene to return to, usually the parent scene of the root.
 * 
 * When we return from a Leaf Scene, we usually remove the branch it came from
 * so as to not repeat previously visited scenes.
 */
public class LeafScene extends Scene {
    private String root;
    private String nextScene;

    public LeafScene(String index, ArrayList<String> lines, Optional<Event> event, String root, String nextScene) {
        super(index, lines, event);
        this.root = root;
        this.nextScene = nextScene;
    }

    public String root() {
        return this.root;
    }

    public String nextScene() {
        return this.nextScene;
    }

}
