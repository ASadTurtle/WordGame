package scenes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.Scanner;

import events.Event;
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

    private static final String ESC = "\033[0m";
    private static final String BLUE = "\033[94m";

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

    @Override
    public String run(Scanner sc, HashMap<String, Scene> scenes, Player player) {
        // Print lines for this scene (Optionally has player name)
        System.out.printf(this.lines() + "\n", player.getName());

        // Run event if it exists
        runEvent(player);

        // Print branches
        int i = 0;
        for (Branch branch : branches) {
            if (!branch.evaluateRequirement(player))
                continue;
            i++;
            // Print options that have requirements blue
            if (branch.hasRequirement())
                System.out.print(BLUE);

            System.out.printf("%d. %s\n", i, branch.prompt());
            System.out.print(ESC);
        }

        // TODO - make this in a menu class
        while (true) {
            String input = sc.nextLine();

            // Quit
            if (input.matches("[qQ](uit)?")) {
                sc.close();
                System.exit(0);
            }

            // Attempt to select branch from user input
            try {
                int bOption = Integer.parseInt(input) - 1;
                try {
                    Branch branch = branches.get(bOption);

                    // Run event if it exists in this branch
                    branch.runEvent(player);
                    branches.remove(branch);
                    return branch.nextScene();

                } catch (Exception e) {

                    // TODO
                    System.out.println("TODO - ERROR MESSAGE");
                    continue;
                }
            } catch (NumberFormatException e) {
                // TODO
                System.out.println("TODO - ERROR MESSAGE");
                continue;
            }
        }
    }
}
