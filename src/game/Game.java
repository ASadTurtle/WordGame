package game;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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
    private static HashMap<String, Scene> gameScenes;
    private static String currScene;

    public static void main(String[] args) throws Exception {
        // Initialise the game
        initGame();

        // System.out.println(player.getName());
        // System.out.println(gameScenes.values());
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
                printMainMenu();
                initScanner.close();
                break;
            }

            // Load save
            if (input.matches("[lL](oad)?") || input.matches("2")) {
                clearTerminal();
                printMainMenu();
                boolean loadedSave = loadSave();
                if (loadedSave) {
                    initScanner.close();
                    break;
                }
                printMainMenu();
                continue;
            }

            // User quits
            if (input.matches("[qQ](uit)?") || input.matches("3")) {
                clearTerminal();
                initScanner.close();
                break;
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
        System.out.println("<save_number> - load save");
        System.out.println("<[b]ack>      - return to main menu");
        System.out.println("<[q]uit>      - quit the game");
        System.out.println("<[h]elp>      - print this message");
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
     * Print init options to player on game init.
     */
    private static void printMainMenu() {
        System.out.println("WELCOME TO THE WORDGAME PROJECT!");
        System.out.println("1. New Game");
        System.out.println("2. Load");
        System.out.println("3. Quit\n");
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

        // Print each valid save in save directory and add its filepath to array
        ArrayList<String> saves = new ArrayList<>();
        for (File file : savesDir.listFiles()) {
            if (file.getName().contains(".json")) {
                saves.add(file.getPath());
            }
        }

        // Loop until user selects save to load from
        Scanner loadScanner = new Scanner(System.in);
        clearTerminal();
        printSaves(saves);
        while (true) {
            String input = loadScanner.nextLine();

            // Quit
            if (input.matches("[qQ](uit)?")) {
                clearTerminal();
                break;
            }

            // Make Back command
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
                    System.out.println("Loading...\n");

                    // Get save file
                    BufferedReader br = new BufferedReader(new FileReader(save));
                    JSONObject jGame = (JSONObject) (new JSONParser().parse(br));

                    // Load player data
                    parsePlayer(jGame);
                    parseScene(jGame);
                    clearTerminal();
                    loadScanner.close();
                    break;

                } catch (IndexOutOfBoundsException e) {
                    throw e;
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

    /**
     * Parses a JSONObject from a player record into a Player object for the game
     * to use. The argument is the entire JSON object from the loaded file.
     * 
     * @param jFile
     */
    private static void parsePlayer(JSONObject jFile) throws Exception {

        JSONObject jPlayer = (JSONObject) jFile.get("player");
        String name = (String) jPlayer.get("name");
        List<String> perks = parseListStr(jPlayer, "perks");
        List<String> items = parseListStr(jPlayer, "items");
        List<String> statuses = parseListStr(jPlayer, "statuses");

        player = new Player(name, perks, items, statuses);
    }

    /**
     * Parses a JSONObject containing all scenes in the current game, into a
     * Hashmap of scenes for the game to store and use over the games runtime.
     * 
     * @param jFile
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private static void parseScene(JSONObject jFile) throws Exception {
        JSONObject jScenes = (JSONObject) jFile.get("scenes");
        HashMap<String, Scene> scenes = new HashMap<>();
        scenes.putAll(jScenes);
        gameScenes = scenes;
    }

    /**
     * Takes a JSONObject that has an array, the key of that array, and returns
     * the list as a java ArrayList. The elements are all strings, this is used
     * for parsing list of player perks/items/statuses.
     * 
     * @param j
     * @param key
     * @return
     */
    @SuppressWarnings("unchecked")
    private static List<String> parseListStr(JSONObject j, String key) {
        JSONArray jObject = (JSONArray) j.get(key);
        List<String> list = new ArrayList<>();
        jObject.forEach(object -> {
            list.add((String) object);
        });
        return list;
    }
}
