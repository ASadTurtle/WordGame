package game;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Optional;
import java.util.Scanner;

import scenes.Scene;

/**
 * <p>
 * The Game class represents the current game state.
 * </p>
 * 
 * <p>
 * It holds the scenes loaded from a save, or from the default library for a
 * new game. These scenes can be modified as the player progresses through
 * the story, such as when a player goes through a branch of a scene,
 * that branch will not be available should they return to that scene.
 * </p>
 * 
 * <p>
 * It also holds the players state, with all the info stored in the Player
 * class.
 * </p>
 * 
 * <p>
 * It loops continuously until the player quits the game. The player may choose
 * to save the game state to a file, or load a previous game state from an
 * existing save.
 * </p>
 * 
 * @author Ahmed El-Sayed
 */
public class Game {
    private static Player player;
    private static HashMap<String, Scene> scenes;
    private static String currScene;
    private static Optional<String> nextChapter;
    private static String gameName;

    private static final String RED = "\033[31m";
    private static final String GOLD = "\033[33m";
    private static final String BLUE = "\033[34m";
    private static final String ESC = "\033[0m";

    public static void main(String[] args) throws IOException {
        // Loop until player quits
        while (true) {
            // Initialise the game
            Scanner inputScanner = new Scanner(System.in);
            mainMenu(inputScanner);

            // Debug message for game state
            System.out.printf(
                    "Player name: %s\nLines in this scene: %s\nThe current scene is: %s\nThe next chapter is: %s\nGame name: %s\n\n",
                    player.getName(), scenes.get(currScene).lines(), currScene, nextChapter.orElse("0"), gameName);
        }
    }

    /**
     * Called on program startup, prompts the player to initialise a new game.
     * They may start a new game, or choose to load from an existing save.
     * 
     * @throws IOException
     */
    private static void mainMenu(Scanner sc) throws IOException {
        // Prompt player to select a game, or load a save.
        clearTerminal();
        printMainMenu();

        while (true) {
            String input = sc.nextLine();

            // Start a new game
            if (input.matches("[nN](ew [gG]ame)?|1")) {
                clearTerminal();
                if (newGame(sc)) {
                    break;
                }
                printMainMenu();
                continue;
            }

            // Load save
            if (input.matches("[lL](oad)?|2")) {
                clearTerminal();
                if (loadSave(sc)) {
                    break;
                }
                printMainMenu();
                continue;
            }

            // Quit
            if (input.matches("[qQ](uit)?|3")) {
                clearTerminal();
                sc.close();
                System.exit(0);
            }

            // Print commands
            if (input.matches("[hH](elp)?")) {
                clearTerminal();
                printMainMenu();
                logHelpMainMenu();
                continue;
            }

            clearTerminal();
            printMainMenu();
            logError("Invalid option, use [h]elp for a list of commands");
        }
    }

    /**
     * The New Game menu. Initialises a new game from user input.
     * 
     * @return {@code true} if a new game was successfully created, {@code false} if
     *         we go back to the main menu
     */
    private static boolean newGame(Scanner sc) {
        File gamesDir = new File("data");

        // Add each available game directory to array
        ArrayList<String> games = new ArrayList<>();
        for (File gameDir : gamesDir.listFiles()) {
            // Check file is directory and non-empty
            if (gameDir.isDirectory() && gameDir.list().length > 0) {
                games.add(gameDir.getPath());
            }
        }

        // Loop until user selects a game to play.
        printGames(games);
        while (true) {
            String input = sc.nextLine();

            // Quit
            if (input.matches("[qQ](uit)?")) {
                clearTerminal();
                // Leave immediately
                sc.close();
                System.exit(0);
            }

            // Back to main menu
            if (input.matches("[bB](ack)?")) {
                clearTerminal();
                return false;
            }

            // Print commands
            if (input.matches("[hH](elp)?")) {
                clearTerminal();
                printGames(games);
                logHelpNewGame();
                continue;
            }

            // Attempt to start a game from user input
            try {
                int gameOption = Integer.parseInt(input) - 1;
                try {
                    String game = games.get(gameOption);
                    File playerDefault = new File(game + "\\playerDefault.json");
                    File chapter = new File(game + "\\chapter1.json");

                    // Load game data from default player file
                    GameParser playerParser = new GameParser(playerDefault);
                    player = playerParser.parsePlayer();

                    // Load game data from chapter 1
                    GameParser chapterParser = new GameParser(chapter);
                    scenes = chapterParser.parseScenes();
                    currScene = chapterParser.parseCurrScene();
                    nextChapter = chapterParser.parseNextChapter();
                    gameName = game.replace("data\\", "");

                    clearTerminal();
                    break;

                } catch (Exception e) {
                    clearTerminal();
                    printGames(games);
                    logError(e.getMessage());
                    continue;
                }
            } catch (NumberFormatException e) {
                clearTerminal();
                printGames(games);
                logError("Invalid option, use [h]elp for a list of commands");
                continue;
            }
        }

        return true;
    }

    /**
     * The Load game menu. Takes user input to load from a selection of savefiles.
     * 
     * @throws IOException
     * @return {@code true} if save was successfully loaded, {@code false} if we go
     *         back to main menu
     */
    private static boolean loadSave(Scanner sc) throws IOException {
        File savesDir = new File("saves");

        // Add each valid saves filepath to array
        ArrayList<File> saves = new ArrayList<>();
        for (File file : savesDir.listFiles())
            if (file.getName().contains(".json"))
                saves.add(file);

        // Sort saves by last modified time (Descending)
        saves.sort(Comparator.comparingLong(File::lastModified).reversed());

        printSaves(saves);
        while (true) {
            String input = sc.nextLine();

            // Quit
            if (input.matches("[qQ](uit)?")) {
                clearTerminal();
                // Leave immediately
                sc.close();
                System.exit(0);
            }

            // Back to main menu
            if (input.matches("[bB](ack)?")) {
                clearTerminal();
                return false;
            }

            // Print commands
            if (input.matches("[hH](elp)?")) {
                clearTerminal();
                printSaves(saves);
                logHelpLoad();
                continue;
            }

            // Attempt to load save from user input
            try {
                int saveOption = Integer.parseInt(input) - 1;
                try {
                    File save = saves.get(saveOption);

                    // Load Game data from save file
                    GameParser gp = new GameParser(save);
                    player = gp.parsePlayer();
                    scenes = gp.parseScenes();
                    currScene = gp.parseCurrScene();
                    nextChapter = gp.parseNextChapter();
                    gameName = gp.parseGameName();

                    clearTerminal();
                    break;

                } catch (Exception e) {
                    clearTerminal();
                    printSaves(saves);
                    logError(e.getMessage());
                    continue;
                }
            } catch (NumberFormatException e) {
                clearTerminal();
                printSaves(saves);
                logError("Invalid option. Use [h]elp for a list of commands");
                continue;
            }
        }

        return true;
    }

    /**
     * Print Main Menu options to player.
     */
    private static void printMainMenu() {
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
    private static void printGames(ArrayList<String> games) {
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
        System.out.println();
    }

    /**
     * Prints all valid save files in the {@code saves} directory.
     * 
     * @param saves List of valid save files in {@code saves}
     * @throws IOException
     */
    private static void printSaves(ArrayList<File> saves) throws IOException {
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

    /**
     * Prints commands for main menu
     */
    private static void logHelpMainMenu() {
        System.out.print(GOLD);
        System.out.println("<[n]ew game>    - start a new game");
        System.out.println("<[l]oad>        - load an existing save");
        logHelpCommon();
    }

    /**
     * Print commands for new game menu
     */
    private static void logHelpNewGame() {
        System.out.print(GOLD);
        System.out.println("<game_number>   - start a new game");
        System.out.println("<[b]ack>        - return to main menu");
        logHelpCommon();
    }

    /**
     * Prints commands for load menu
     */
    private static void logHelpLoad() {
        System.out.print(GOLD);
        System.out.println("<save_number>   - load an existing save");
        System.out.println("<[b]ack>        - return to main menu");
        logHelpCommon();
    }

    /**
     * Prints commands common to all menus
     */
    private static void logHelpCommon() {
        System.out.println("<[q]uit>        - quit the game");
        System.out.println("<[h]elp>        - print this message");
        System.out.println(ESC);
    }

    /**
     * Prints error message to player
     * 
     * @param error
     */
    private static void logError(String error) {
        System.out.print(RED);
        System.out.println(error);
        System.out.println(ESC);
    }

    /**
     * Helper function, clears the terminal.
     */
    private static void clearTerminal() {
        System.out.print("\033[H\033[2J");
    }
}
