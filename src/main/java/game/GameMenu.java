package game;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import scenes.Branch;

public class GameMenu {
    // ANSI characters
    private static final String RED = "\033[91m";
    private static final String GOLD = "\033[93m";
    private static final String BLUE = "\033[94m";
    private static final String ESC = "\033[0m";

    /**
     * Print Main Menu options to player.
     */
    public static void printMainMenu() {
        System.out.print(BLUE);
        System.out.println("WELCOME TO THE WORDGAME PROJECT!");
        System.out.println("1. New Game");
        System.out.println("2. Load");
        System.out.println("3. Quit");
        System.out.println(ESC);
    }

    /**
     * Prints all valid WordGames in the {@code data} directory.
     * 
     * @param games List of valid directories in {@code data}
     */
    public static void printGames(ArrayList<String> games) {
        if (games.size() == 0) {
            logError("There are no valid games :(");
            return;
        }
        System.out.print(BLUE);
        System.out.println("SELECT A GAME:");
        int i = 1;
        for (String game : games) {
            String gameName = game.replace("_", " ").replace("data\\", "");
            System.out.printf("%d. %s\n", i, gameName);
            i++;
        }
        System.out.println(ESC);
    }

    /**
     * Prints all valid save files in the {@code saves} directory.
     * 
     * @param saves List of valid save files in {@code saves}
     * @throws IOException
     */
    public static void printSaves(ArrayList<File> saves) throws IOException {
        if (saves.size() == 0) {
            logError("There are no valid saves :(");
            return;
        }
        System.out.print(BLUE);
        System.out.println("SAVES:");
        int i = 1;
        for (File save : saves) {
            String saveName = save.toString().replace(".json", "").replace("saves\\", "");
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            System.out.printf("%d. %-12s - %s\n", i, saveName, sdf.format(save.lastModified()));
            i++;
        }
        System.out.println(ESC);
    }

    public static void printScene(String lines, Player player) {
        System.out.printf(lines + "\n", player.name());
    }

    /**
     * Prints all branches the player can choose. We dont take any inelligble
     * branch in the {@code branches} parameter, so we dont check here.
     * 
     * @param branches Elligble branches the player may select
     * @param player   The player character
     */
    public static void printBranches(ArrayList<Branch> branches, Player player) {
        // Print branches
        int i = 0;
        for (Branch branch : branches) {
            i++;
            // Print options that have requirements blue
            if (branch.hasRequirement())
                System.out.print(BLUE);

            System.out.printf("%d. %s\n", i, branch.prompt());
            System.out.print(ESC);
        }
        System.out.println();
    }

    /**
     * Prints commands for main menu
     */
    public static void logHelpMainMenu() {
        System.out.print(GOLD);
        System.out.println("<[n]ew game>      - start a new game");
        System.out.println("<[l]oad>          - load an existing save");
        logHelpCommon();
    }

    /**
     * Print commands for new game menu
     */
    public static void logHelpNewGame() {
        System.out.print(GOLD);
        System.out.println("<game_number>     - start a new game");
        System.out.println("<[b]ack>          - return to main menu");
        logHelpCommon();
    }

    /**
     * Prints commands for load menu
     */
    public static void logHelpLoad() {
        System.out.print(GOLD);
        System.out.println("<save_number>     - load an existing save");
        System.out.println("<[b]ack>          - return to main menu");
        logHelpCommon();
    }

    public static void logHelpScene() {
        System.out.print(GOLD);
        System.out.println("<branch_number>   - select branch");
        System.out.println("<[p]layer>        - print player info");
        logHelpCommon();
    }

    /**
     * Prints commands common to all menus
     */
    public static void logHelpCommon() {
        System.out.print(GOLD);
        System.out.println("<[q]uit>          - quit the game");
        System.out.println("<[h]elp>          - print this message");
        System.out.println(ESC);
    }

    /**
     * Prints error message to player
     * 
     * @param error
     */
    public static void logError(String error) {
        System.out.print(RED);
        System.out.println(error);
        System.out.println(ESC);
    }

    /**
     * Prints information about the player to terminal
     */
    public static void logPlayer(Player player) {
        System.out.print(BLUE);
        System.out.printf("%s\n", player.name());
        System.out.printf("Perks:      %s\n", player.perks());
        System.out.printf("Inventory:  %s\n", player.items());
        System.out.printf("Statuses:   %s\n", player.statuses());
        System.out.println(ESC);
    }

    /**
     * Helper function, clears the terminal.
     */
    public static void clearTerminal() {
        System.out.print("\033[H\033[2J");
    }
}
