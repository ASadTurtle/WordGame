package game;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
        // Create a new player from JSON file.
        BufferedReader br = new BufferedReader(new FileReader("data\\playerDefault.json"));
        JSONObject jPlayer = (JSONObject) (new JSONParser().parse(br));
        parsePlayer(jPlayer);

        // Create a new scene library
    }

    /**
     * Parses a JSONObject from a player record into a Player object for the game
     * to use.
     * 
     * @param jPlayer
     */
    private static void parsePlayer(JSONObject jPlayer) {
        String name = (String) jPlayer.get("name");
        List<String> perks = parseList(jPlayer, "perks");
        List<String> items = parseList(jPlayer, "items");
        List<String> statuses = parseList(jPlayer, "statuses");
        player = new Player(name, perks, items, statuses);
    }

    private static List<String> parseList(JSONObject j, String key) {
        return Arrays.asList((String[]) ((JSONArray) j.get(key)).toArray());
    }
}
