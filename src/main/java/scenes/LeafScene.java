package scenes;

import java.util.HashMap;
import java.util.Optional;
import java.util.Scanner;

import events.Event;
import game.Player;

/**
 * A leaf scene is one that does not lead to any other branches. It is the final
 * originating from a certain branch, and will have a nextScene to return to.
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
        System.out.printf(this.lines() + "\n", player.getName());
        runEvent(player);
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
