package game;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import events.Event;
import events.GetPerkEvent;
import events.GetStatusEvent;
import scenes.Branch;
import scenes.LeafScene;
import scenes.NodeScene;
import scenes.Scene;

public class GameParser {

    JSONObject jFile;

    public GameParser(String fileName) throws FileNotFoundException {
        jFile = new JSONObject(new JSONTokener(new FileReader(fileName)));
    }

    /**
     * Parses a JSONObject from a player record into a Player object for the game
     * to use. The argument is the entire JSON object from the loaded file.
     * 
     * @param jFile
     */
    public Player parsePlayer() {

        JSONObject jPlayer = jFile.getJSONObject("player");
        String name = jPlayer.getString("name");
        ArrayList<String> perks = parseListStr(jPlayer, "perks");
        ArrayList<String> items = parseListStr(jPlayer, "items");
        ArrayList<String> statuses = parseListStr(jPlayer, "statuses");

        return new Player(name, perks, items, statuses);
    }

    /**
     * Parses a JSONObject containing all scenes in the current game, into a
     * Hashmap of scenes for the game to store and use over the games runtime.
     * 
     */
    public HashMap<String, Scene> parseScenes() {
        JSONObject jScenes = jFile.getJSONObject("scenes");
        HashMap<String, Scene> scenes = new HashMap<>();
        jScenes.toMap().forEach((key, jScene) -> {
            Scene scene = parseScene(key, (JSONObject) jScene);
            scenes.put(key, scene);
        });

        return scenes;
    }

    /**
     * Parses a single JSONObject scene into a Scene java object. Called by
     * parseScenes for each JSON scene in the file it reads.
     * 
     * @param index
     * @param jScene
     */
    public Scene parseScene(String index, JSONObject jScene) {
        // Get the scene type
        String sceneType = jScene.getString("sceneType");

        // Get common scene fields
        ArrayList<String> lines = parseListStr(jScene, "lines");
        Optional<Event> event = parseEvent(jScene);
        ArrayList<String> roots = parseListStr(jScene, "roots");

        // Get additional fields based on type
        if (sceneType.matches("leaf")) {
            String nextScene = jScene.getString("nextScene");
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
     * @param j
     * @return
     */
    public Optional<Event> parseEvent(JSONObject j) {
        Optional<JSONObject> jEvent = Optional.ofNullable(j.getJSONObject("event"));

        if (jEvent.isEmpty()) {
            return Optional.empty();
        }

        String eventType = jEvent.get().getString("type");
        String arg = jEvent.get().getString("arg");

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
    public ArrayList<Branch> parseBranches(JSONObject jScene) {
        ArrayList<Branch> branches = new ArrayList<>();
        JSONArray jBranches = jScene.getJSONArray("branches");
        jBranches.forEach(rawBranch -> {
            JSONObject jBranch = (JSONObject) rawBranch;
            String bScene = jBranch.getString("bScene");
            String prompt = jBranch.getString("prompt");
            Optional<Event> event = parseEvent(jBranch);
            branches.add(new Branch(bScene, prompt, event));
        });

        return branches;
    }

    /**
     * Parses a JSON file to get the current scene the player is in.
     * 
     */
    public String parseCurrScene() {
        return jFile.getString("currScene");
    }

    /**
     * Parses a JSON file to get the next chapter the player will start when
     * this one has concluded.
     * 
     */
    public Optional<String> parseNextChapter() {
        return Optional.ofNullable(jFile.getString("nextChapter"));
    }

    /**
     * Parses the game name from the save file.
     * 
     */
    public String parseGameName() {
        return jFile.getString("gameName");
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
    public ArrayList<String> parseListStr(JSONObject j, String key) {
        JSONArray jObject = (JSONArray) j.get(key);
        ArrayList<String> list = new ArrayList<>();
        jObject.forEach(object -> {
            list.add((String) object);
        });
        return list;
    }
}
