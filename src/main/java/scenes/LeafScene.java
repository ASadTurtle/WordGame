package scenes;

import java.util.HashMap;
import java.util.Optional;
import java.util.Scanner;

import events.Event;
import game.Player;

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

    public LeafScene(String index, String lines, Optional<Event> event,
            String nextScene) {
        super(index, lines, event);
        this.nextScene = nextScene;
    }

    public String nextScene() {
        return this.nextScene;
    }

    @Override
    public String run(Scanner sc, HashMap<String, Scene> scenes, Player player) {
        System.out.printf(this.lines() + "\n\n", player.getName());
        System.out.println("Press ENTER to continue...");

        // TODO - make this in a menu class
        String input = sc.nextLine();
        // Quit
        if (input.matches("[qQ](uit)?")) {
            sc.close();
            System.exit(0);
        }
        return nextScene;
    }
}
