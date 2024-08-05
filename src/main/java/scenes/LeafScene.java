package scenes;

import java.util.HashMap;
import java.util.Optional;
import java.util.Scanner;

import events.Event;
import game.GameMenu;
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

    public String run(Scanner sc, HashMap<String, Scene> scenes, Player player) {
        GameMenu.printScene(lines(), player);
        runEvent(player);
        System.out.println("Press ENTER to continue...\n");

        while (true) {
            String input = sc.nextLine();

            // Quit
            if (input.matches("[qQ](uit)?")) {
                GameMenu.clearTerminal();
                sc.close();
                System.exit(0);
            }

            // Print player info
            if (input.matches("[pP](layer)?")) {
                GameMenu.clearTerminal();
                GameMenu.printScene(lines(), player);
                logEvent();
                player.logPlayer();
                System.out.println("Press ENTER to continue...\n");
                player.logPlayer();
                continue;
            }

            // Print commands
            if (input.matches("[h](elp)?")) {
                GameMenu.clearTerminal();
                GameMenu.printScene(lines(), player);
                logEvent();
                System.out.println("Press ENTER to continue...\n");
                GameMenu.logHelpCommon();
                continue;
            }

            if (input.isBlank())
                return nextScene;

            GameMenu.clearTerminal();
            GameMenu.printScene(lines(), player);
            logEvent();
            System.out.println("Press ENTER to continue...\n");
        }
    }
}
