package scenes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.Scanner;

import events.Event;
import game.GameMenu;
import game.Player;

/**
 * <p>
 * A NodeScene is a scene that branches to other scenes, depending on the
 * players input.
 * </p>
 * 
 * <p>
 * It should list all available choices for the player to make,
 * and if the player ever returns to this scene from a branch, that branch
 * should not be available to select again.
 * </p>
 * 
 * <p>
 * The branches of a node scene is a list of Branch objects, the details of
 * which are specified in the respective class.
 * </p>
 */
public class NodeScene extends Scene {
    private ArrayList<Branch> branches;

    public NodeScene(String index, String lines, Optional<Event> event,
            ArrayList<Branch> branches) {
        super(index, lines, event);
        this.branches = branches;
    }

    public void pruneBranch(String sceneIndex) {
        branches
                .stream()
                .filter(branch -> branch.nextScene().equals(sceneIndex));
    }

    public ArrayList<Branch> branches() {
        return branches;
    }

    public String run(Scanner sc, HashMap<String, Scene> scenes, Player player) {
        // Print lines for this scene (Optionally has player name)
        GameMenu.printScene(lines(), player);

        // Run event if it exists
        runEvent(player);

        // Find elligble branches
        ArrayList<Branch> elligbleBranches = new ArrayList<>();
        for (Branch branch : branches) {
            if (branch.evaluateRequirement(player))
                elligbleBranches.add(branch);
        }

        // Print elligble branches
        GameMenu.printBranches(elligbleBranches, player);

        // Loop until player selects an elligble branch
        while (true) {
            String input = sc.nextLine().toLowerCase();

            // Quit
            if (input.matches("[q](uit)?")) {
                GameMenu.clearTerminal();
                sc.close();
                System.exit(0);
            }

            // Print player info
            if (input.matches("[p](layer)?")) {
                GameMenu.clearTerminal();
                GameMenu.printScene(lines(), player);
                logEvent();
                GameMenu.printBranches(elligbleBranches, player);
                player.logPlayer();
                continue;
            }

            // Print commands
            if (input.matches("[h](elp)?")) {
                GameMenu.clearTerminal();
                GameMenu.printScene(lines(), player);
                logEvent();
                GameMenu.printBranches(elligbleBranches, player);
                GameMenu.logHelpScene();
                continue;
            }

            // Attempt to select branch from user input
            try {
                int bOption = Integer.parseInt(input) - 1;
                try {
                    Branch branch = elligbleBranches.get(bOption);

                    // Run event if it exists in this branch
                    branch.runEvent(player);
                    branches.remove(branch);
                    return branch.nextScene();

                } catch (IndexOutOfBoundsException e) {
                    GameMenu.clearTerminal();
                    GameMenu.printScene(lines(), player);
                    logEvent();
                    GameMenu.printBranches(elligbleBranches, player);
                    GameMenu.logError("Invalid branch");
                    continue;
                }
            } catch (NumberFormatException e) {
                GameMenu.clearTerminal();
                GameMenu.printScene(lines(), player);
                logEvent();
                GameMenu.printBranches(elligbleBranches, player);
                GameMenu.logError("Invalid option. Use [h]elp for a list of commands");
                continue;
            }
        }
    }
}
