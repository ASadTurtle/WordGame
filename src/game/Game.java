package game;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
    private HashMap<String, Scene> scenes;

    public static void main(String[] args) throws Exception {
        // Initialise the game
        initGame();

        // System.out.println("Player name: " + player.getName());
    }

    /**
     * Called on program startup, promtps the player to initialise a new game
     */
    private static void initGame() throws Exception {
        Scanner initScanner = new Scanner(System.in);
        // Prompt player to select a game, or load a save.
        System.out.println("WELCOME TO THE WORDGAME PROJECT!");
        printOptionsInit();

        while (true) {
            String input = initScanner.nextLine();

            // Start a new game
            if (input.matches("[nN](ew [gG]ame)?") || input.matches("1")) {
                System.out.println("Starting new game...\n");
                initScanner.close();
                break;
            }

            // Load save
            if (input.matches("[lL](oad)?") || input.matches("2")) {
                loadSave();
                initScanner.close();
                break;
            }

            // User quits
            if (input.matches("[qQ](uit)?") || input.matches("3")) {
                System.out.println("Quitting...\n");
                initScanner.close();
                break;
            }

            // Print commands
            if (input.matches("[hH](elp)?")) {
                printOptionsInit();
                continue;
            }

            System.out.println("\nInvalid option. Use [h]elp for a list of commands\n");
        }
    }

    /**
     * Print init options to player on game init.
     */
    private static void printOptionsInit() {
        System.out.println("1. New Game");
        System.out.println("2. Load");
        System.out.println("3. Quit\n");
    }

    /**
     * Takes user input to load from a selection of savefiles
     */
    private static void loadSave() {
        File savesDir = new File("saves");

        if (savesDir.listFiles().length == 0) {
            System.out.println("There are no saved games");
            return;
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
        System.out.println("\nSAVES:");
        printSaves(saves);
        while (true) {
            String input = loadScanner.nextLine();

            if (input.matches("[qQ](uit)?")) {
                System.out.println("Quiting...\n");
                break;
            }

            if (input.matches("[sS](aves)?")) {
                printSaves(saves);
                continue;
            }

            if (input.matches("[hH](elp)?")) {
                System.out.println("<save_number> - load save");
                System.out.println("<[s]aves>     - show available saves");
                System.out.println("<[h]elp>      - print this message");
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
                    loadScanner.close();
                    break;

                } catch (IndexOutOfBoundsException e) {
                    throw e;
                }
            } catch (Exception e) {
                System.out.println("Invalid option. Use [h]elp for a list of commands\n");
                continue;
            }
        }
    }

    private static void printSaves(ArrayList<String> saves) {
        int i = 1;
        for (String save : saves) {
            String saveName = save.replace(".json", "");
            System.out.printf("%d. " + saveName + "\n", i);
            i++;
        }
    }

    /**
     * Parses a JSONObject from a player record into a Player object for the game
     * to use.
     * 
     * @param jPlayer
     */
    private static void parsePlayer(JSONObject jFile) throws Exception {

        JSONObject jPlayer = (JSONObject) jFile.get("player");
        String name = (String) jPlayer.get("name");
        List<String> perks = parseList(jPlayer, "perks");
        List<String> items = parseList(jPlayer, "items");
        List<String> statuses = parseList(jPlayer, "statuses");

        player = new Player(name, perks, items, statuses);
    }

    /**
     * Takes a JSONObject that has an array, the key of that array, and returns
     * the list as a java ArrayList.
     * 
     * @param j
     * @param key
     * @return
     */
    @SuppressWarnings("unchecked")
    private static List<String> parseList(JSONObject j, String key) {
        JSONArray jStatuses = (JSONArray) j.get(key);
        List<String> statuses = new ArrayList<>();
        jStatuses.forEach(status -> {
            statuses.add((String) status);
        });
        return statuses;
    }
}
