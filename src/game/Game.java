package game;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import events.Event;
import events.GetPerkEvent;
import events.GetStatusEvent;
import scenes.Branch;
import scenes.LeafScene;
import scenes.NodeScene;
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
    private static HashMap<String, Scene> scenes = new HashMap<>();
    private static String currScene;

    public static void main(String[] args) throws Exception {
        // Initialise the game
        initGame();

        // Debug message for game state
        System.out.println(player.getName());
        System.out.println(scenes.get(currScene).lines());
        System.out.println(currScene);
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
     * Print init options to player on game init.
     */
    private static void printMainMenu() {
        System.out.println("WELCOME TO THE WORDGAME PROJECT!");
        System.out.println("1. New Game");
        System.out.println("2. Load");
        System.out.println("3. Quit\n");
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
        clearTerminal();
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

                    // Get save file
                    BufferedReader br = new BufferedReader(new FileReader(save));
                    JSONObject jGame = (JSONObject) (new JSONParser().parse(br));

                    // Load player data
                    parsePlayer(jGame);
                    parseScenes(jGame);
                    parseCurrScene(jGame);

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
    private static void parseScenes(JSONObject jFile) throws Exception {
        JSONObject jScenes = (JSONObject) jFile.get("scenes");
        jScenes.forEach((key, jScene) -> {
            Scene scene = parseScene((String) key, (JSONObject) jScene);
            scenes.put((String) key, scene);
        });
    }

    /**
     * Parses a single JSONObject scene into a Scene java object. Called by
     * parseScenes for each JSON scene in the file it reads.
     * 
     * @param jScene
     */
    private static Scene parseScene(String index, JSONObject jScene) {
        // Get the scene type
        String sceneType = (String) jScene.get("sceneType");

        // Get common scene fields
        ArrayList<String> lines = parseListStr(jScene, "lines");
        Optional<Event> event = parseEvent(jScene);
        ArrayList<String> roots = parseListStr(jScene, "roots");

        // Get additional fields based on type
        if (sceneType.matches("leaf")) {
            String nextScene = (String) jScene.get("nextScene");
            return new LeafScene(index, lines, roots, event, nextScene);
        }

        if (sceneType.matches("node")) {
            ArrayList<Branch> branches = parseBranches(jScene);
            return new NodeScene(index, lines, roots, event, branches);
        }
        return null;
    }

    /**
     * Parses an Event from a JSON scene or branch.
     * 
     * @return
     */
    private static Optional<Event> parseEvent(JSONObject j) {
        Optional<JSONObject> jEvent = Optional.ofNullable((JSONObject) j.get("event"));

        if (jEvent.isEmpty()) {
            return Optional.empty();
        }

        String eventType = (String) jEvent.get().get("type");
        String arg = (String) jEvent.get().get("arg");

        // Return appropriate Event type
        switch (eventType) {
            case "getPerk":
                return Optional.of(new GetPerkEvent(arg));
            case "getStatus":
                return Optional.of(new GetStatusEvent(arg));
            default:
                return Optional.empty();
        }
    }

    /**
     * Parses all branches from a JSON scene.
     * 
     * @param jScene
     * @return
     */
    @SuppressWarnings("unchecked")
    private static ArrayList<Branch> parseBranches(JSONObject jScene) {
        ArrayList<Branch> branches = new ArrayList<>();
        JSONArray jBranches = (JSONArray) jScene.get("branches");
        jBranches.forEach(rawBranch -> {
            JSONObject jBranch = (JSONObject) rawBranch;
            String bIndex = (String) jBranch.get("bIndex");
            String bScene = (String) jBranch.get("bScene");
            String prompt = (String) jBranch.get("prompt");
            Optional<Event> event = parseEvent(jBranch);
            branches.add(new Branch(bIndex, bScene, prompt, event));
        });

        return branches;
    }

    /**
     * Parses a JSON object to get the current scene the player is in from
     * a JSON file, either a save or a template chapter file.
     * 
     * @param jFile
     * @throws Exception
     */
    private static void parseCurrScene(JSONObject jFile) throws Exception {
        currScene = (String) jFile.get("currScene");
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
    private static ArrayList<String> parseListStr(JSONObject j, String key) {
        JSONArray jObject = (JSONArray) j.get(key);
        ArrayList<String> list = new ArrayList<>();
        jObject.forEach(object -> {
            list.add((String) object);
        });
        return list;
    }
}
