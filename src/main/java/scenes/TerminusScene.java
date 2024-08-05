package scenes;

import java.util.HashMap;
import java.util.Optional;
import java.util.Scanner;

import game.GameMenu;
import game.Player;

public class TerminusScene extends Scene {

    public TerminusScene(String index, String lines) {
        super(index, lines, Optional.empty());
    }

    public String run(Scanner sc, HashMap<String, Scene> scenes, Player player) {
        GameMenu.printScene(lines(), player);
        System.out.println("Press ENTER to continue...\n");

        while (true) {
            String input = sc.nextLine();

            if (input.length() == 0)
                return "";
        }
    }

}
