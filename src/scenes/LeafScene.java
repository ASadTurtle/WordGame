package scenes;

import java.util.ArrayList;
import java.util.Optional;

import events.Event;

/**
 * A leaf scene is one that does not lead to any other branches. It is the final
 * scene originating from a certain branch, which we record as its root, and
 * will have a nextScene to return to, usually the parent scene of the root.
 * When we return from a Leaf Scene, we usually remove the branch it came from
 * so as to not repeat previously visited scenes.
 * 
 * @author Ahmed El-Sayed
 */
public class LeafScene extends Scene {
    private String nextScene;

    public LeafScene(String index, String lines, ArrayList<String> roots, Optional<Event> event,
            String nextScene) {
        super(index, lines, roots, event);
        this.nextScene = nextScene;
    }

    public String nextScene() {
        return this.nextScene;
    }

}
