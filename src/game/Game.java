package game;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.Scanner;

import scenes.Scene;

/**
 * The Game class represents the current game state.
 * 
 * It holds the scenes loaded from a save, or from the default library for a
 * new game. These scenes can be modified as the player progresses through
 * the story, such as when a player goes through a branch of a scene,
 * that branch will not be available should they return to that scene.
 * 
 * It also holds the players state, with all the info stored in the Player
 * class.
 * 
 * It loops continuously until the player quits the game. The player may choose
 * to save the game state to a file, or load a previous game state from an
 * existing save.
 * 
 */
public class Game {
    private static Player player;
    private static HashMap<String, Scene> scenes;
    private static String currScene;
    private static Optional<String> nextChapter;
    private static String gameName;

    public static void main(String[] args) throws Exception {
        // Initialise the game
        initGame();

        // Debug message for game state
        System.out.println("Player name: " + player.getName());
        System.out.println("Lines in this scene: " + scenes.get(currScene).lines());
        System.out.println("The current scene is: " + currScene);
        System.out.println("The next chapter is: " + nextChapter);
        System.out.println("Game name: " + gameName);
    }

    /**
     * Called on program startup, promtps the player to initialise a new game
     */
    private static void initGame() throws Exception {
        Scanner initScanner = new Scanner(System.in);
        // Prompt player to select a game, or load a save.
        clearTerminal();
        printMainMenu();

        while (true) {
            String input = initScanner.nextLine();

            // Start a new game
            if (input.matches("[nN](ew [gG]ame)?") || input.matches("1")) {
                clearTerminal();
                if (newGame()) {
                    initScanner.close();
                    break;
                }
                printMainMenu();
                continue;
            }

            // Load save
            if (input.matches("[lL](oad)?") || input.matches("2")) {
                clearTerminal();
                if (loadSave()) {
                    initScanner.close();
                    break;
                }
                printMainMenu();
                continue;
            }

            // Quit
            if (input.matches("[qQ](uit)?") || input.matches("3")) {
                clearTerminal();
                initScanner.close();
                System.exit(0);
            }

            // Print commands
            if (input.matches("[hH](elp)?")) {
                clearTerminal();
                printMainMenu();
                logHelpInit();
                continue;
            }

            clearTerminal();
            printMainMenu();
            System.out.println("Invalid option. Use [h]elp for a list of commands\n");
        }
    }

    /**
     * Initialises a new game from user input. Returns if a new game was
     * successfully initialised.
     * 
     * @return
     */
    private static boolean newGame() throws Exception {
        File gamesDir = new File("data");

        if (gamesDir.listFiles().length == 0) {
            System.out.println("There are no games available :(");
            return false;
        }

        // Add each available game directory to array
        ArrayList<String> games = new ArrayList<>();
        for (File file : gamesDir.listFiles()) {
            if (file.isDirectory()) {
                games.add(file.getPath());
            }
        }

        // Loop until user selects a game to play.
        Scanner newGameScanner = new Scanner(System.in);
        printGames(games);
        while (true) {
            String input = newGameScanner.nextLine();

            // Quit
            if (input.matches("[qQ](uit)?")) {
                clearTerminal();
                // Leave immediately
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

                    // Load game data from default player file
                    GameParser playerParser = new GameParser(game + "\\playerDefault.json");
                    player = playerParser.parsePlayer();

                    // Load game data from chapter 1
                    GameParser chapterParser = new GameParser(game + "\\chapter1.json");
                    scenes = chapterParser.parseScenes();
                    currScene = chapterParser.parseCurrScene();
                    nextChapter = chapterParser.parseNextChapter();
                    gameName = game.replace("data\\", "");

                    clearTerminal();
                    newGameScanner.close();
                    break;

                } catch (Exception e) {
                    clearTerminal();
                    printGames(games);
                    System.err.println(e);
                    continue;
                }
            } catch (Exception e) {
                clearTerminal();
                printGames(games);
                System.out.println("Invalid option. Use [h]elp for a list of commands\n");
                continue;
            }
        }

        return true;

    }

    /**
     * Takes user input to load from a selection of savefiles. Returns boolean
     * for successful load.
     * 
     * @throws InterruptedException
     * @throws IOException
     */
    private static boolean loadSave() throws Exception {
        File savesDir = new File("saves");

        if (savesDir.listFiles().length == 0) {
            System.out.println("There are no saved games");
            return false;
        }

        // Add each valid saves filepath to array
        ArrayList<String> saves = new ArrayList<>();
        for (File file : savesDir.listFiles()) {
            if (file.getName().contains(".json")) {
                saves.add(file.getPath());
            }
        }

        // Loop until user selects save to load from
        Scanner loadScanner = new Scanner(System.in);
        printSaves(saves);
        while (true) {
            String input = loadScanner.nextLine();

            // Quit
            if (input.matches("[qQ](uit)?")) {
                clearTerminal();
                // Leave immediately
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
                    String save = saves.get(saveOption);

                    // Load Game data from save file
                    GameParser gp = new GameParser(save);
                    player = gp.parsePlayer();
                    scenes = gp.parseScenes();
                    currScene = gp.parseCurrScene();
                    nextChapter = gp.parseNextChapter();
                    gameName = gp.parseGameName();

                    clearTerminal();
                    loadScanner.close();
                    break;

                } catch (Exception e) {
                    clearTerminal();
                    printSaves(saves);
                    System.err.println(e);
                    continue;
                }
            } catch (Exception e) {
                clearTerminal();
                printSaves(saves);
                System.out.println("Invalid option. Use [h]elp for a list of commands\n");
                continue;
            }
        }

        return true;
    }

    /**
     * Print init options to player on game init.
     */
    private static void printMainMenu() {
        System.out.println("WELCOME TO THE WORDGAME PROJECT!");
        System.out.println("1. New Game");
        System.out.println("2. Load");
        System.out.println("3. Quit\n");
    }

    /**
     * Prints commands for main menu
     */
    private static void logHelpInit() {
        System.out.println("<[n]ew game>    - start a new game");
        System.out.println("<[l]oad>        - load an existing save");
        System.out.println("<[q]uit>        - quit the game");
        System.out.println("<[h]elp>        - print this message");
        System.out.println();
    }

    /**
     * Prints commands for load menu
     */
    private static void logHelpLoad() {
        System.out.println("<save_number>   - load save");
        System.out.println("<[b]ack>        - return to main menu");
        System.out.println("<[q]uit>        - quit the game");
        System.out.println("<[h]elp>        - print this message");
        System.out.println();
    }

    /**
     * Print commands for new game menu
     */
    private static void logHelpNewGame() {
        System.out.println("<game_number>   - start game");
        System.out.println("<[b]ack>        - return to main menu");
        System.out.println("<[q]uit>        - quit the game");
        System.out.println("<[h]elp>        - print this message");
        System.out.println();
    }

    /**
     * Helper function, clears the terminal.
     * 
     * @throws IOException
     * @throws InterruptedException
     */
    @SuppressWarnings("deprecation")
    private static void clearTerminal() throws IOException, InterruptedException {
        final String os = System.getProperty("os.name");
        if (os.contains("Windows"))
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        else
            Runtime.getRuntime().exec("clear");
    }

    /**
     * Prints all valid save files in the `saves` directory.
     * 
     * @param saves
     */
    private static void printSaves(ArrayList<String> saves) {
        System.out.println("SAVES:");
        int i = 1;
        for (String save : saves) {
            String saveName = save.replace(".json", "").replace("saves\\", "");
            System.out.printf("%d. " + saveName + "\n", i);
            i++;
        }
        System.out.println();
    }

    private static void printGames(ArrayList<String> games) {
        System.out.println("SELECT A GAME:");
        int i = 1;
        for (String game : games) {
            String gameName = game.replace("_", " ").replace("data\\", "");
            System.out.printf("%d. " + gameName + "\n", i);
            i++;
        }
        System.out.println();
    }
}
