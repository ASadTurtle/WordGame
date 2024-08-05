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

    private static final String RED = "\033[91m";
    private static final String GOLD = "\033[93m";
    private static final String BLUE = "\033[94m";
    private static final String ESC = "\033[0m";

    public static void main(String[] args) throws IOException {
        // Loop until player quits
        while (true) {
            Scanner inputScanner = new Scanner(System.in);

            // Initialise the game
            if (nextChapter == null) {
                mainMenu(inputScanner);
            }

            // Run the game
            while (!currScene.isBlank()) {
                GameMenu.clearTerminal();

                // Run scene logic
                Scene scene = scenes.get(currScene);
                currScene = scene.run(inputScanner, scenes, player);
            }

            // If we were given a next chapter, load it now
            if (nextChapter.isPresent()) {
                // TODO - load the next chapter
            }
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
        GameMenu.clearTerminal();
        GameMenu.printMainMenu();

        while (true) {
            String input = sc.nextLine().toLowerCase();

            // Start a new game
            if (input.matches("[n](ew game)?|1")) {
                GameMenu.clearTerminal();
                if (newGame(sc)) {
                    break;
                }
                GameMenu.printMainMenu();
                continue;
            }

            // Load save
            if (input.matches("[l](oad)?|2")) {
                GameMenu.clearTerminal();
                if (loadSave(sc)) {
                    break;
                }
                GameMenu.printMainMenu();
                continue;
            }

            // Quit
            if (input.matches("[q](uit)?|3")) {
                GameMenu.clearTerminal();
                sc.close();
                System.exit(0);
            }

            // Print commands
            if (input.matches("[h](elp)?")) {
                GameMenu.clearTerminal();
                GameMenu.printMainMenu();
                GameMenu.logHelpMainMenu();
                continue;
            }

            GameMenu.clearTerminal();
            GameMenu.printMainMenu();
            GameMenu.logError("Invalid option, use [h]elp for a list of commands");
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
        GameMenu.printGames(games);
        while (true) {
            String input = sc.nextLine().toLowerCase();

            // Quit
            if (input.matches("[q](uit)?")) {
                GameMenu.clearTerminal();
                // Leave immediately
                sc.close();
                System.exit(0);
            }

            // Back to main menu
            if (input.matches("[b](ack)?")) {
                GameMenu.clearTerminal();
                return false;
            }

            // Print commands
            if (input.matches("[h](elp)?")) {
                GameMenu.clearTerminal();
                GameMenu.printGames(games);
                GameMenu.logHelpNewGame();
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
                    gameName = game;

                    GameMenu.clearTerminal();
                    break;

                } catch (Exception e) {
                    GameMenu.clearTerminal();
                    GameMenu.printGames(games);
                    GameMenu.logError(e.getMessage());
                    continue;
                }
            } catch (NumberFormatException e) {
                GameMenu.clearTerminal();
                GameMenu.printGames(games);
                GameMenu.logError("Invalid option, use [h]elp for a list of commands");
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

        GameMenu.printSaves(saves);
        while (true) {
            String input = sc.nextLine().toLowerCase();

            // Quit
            if (input.matches("[q](uit)?")) {
                GameMenu.clearTerminal();
                // Leave immediately
                sc.close();
                System.exit(0);
            }

            // Back to main menu
            if (input.matches("[b](ack)?")) {
                GameMenu.clearTerminal();
                return false;
            }

            // Print commands
            if (input.matches("[h](elp)?")) {
                GameMenu.clearTerminal();
                GameMenu.printSaves(saves);
                GameMenu.logHelpLoad();
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

                    GameMenu.clearTerminal();
                    break;

                } catch (Exception e) {
                    GameMenu.clearTerminal();
                    GameMenu.printSaves(saves);
                    GameMenu.logError(e.getMessage());
                    continue;
                }
            } catch (NumberFormatException e) {
                GameMenu.clearTerminal();
                GameMenu.printSaves(saves);
                GameMenu.logError("Invalid option. Use [h]elp for a list of commands");
                continue;
            }
        }
        return true;
    }
}
