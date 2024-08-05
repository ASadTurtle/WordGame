package game;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import events.Event;
import events.GetPerkEvent;
import events.GetStatusEvent;
import requirements.Requirement;
import requirements.RequirementFactory;
import scenes.Branch;
import scenes.LeafScene;
import scenes.NodeScene;
import scenes.Scene;

/**
 * This class is responsible for parsing the JSON file with the given filename,
 * and returning the appropriate Game Objects for the TextGame to use.
 * 
 * @author Ahmed El-Sayed
 */
public class GameParser {

    // File parsed to JSON Object
    JSONObject jFile;

    /**
     * Given the filename of a JSON data file or JSON save file, creates a
     * GameParser to parse game data from that file.
     * 
     * @param fileName The JSON file we are parsing for game data
     * @throws FileNotFoundException
     */
    public GameParser(File fileName) throws FileNotFoundException {
        jFile = new JSONObject(new JSONTokener(new FileReader(fileName)));
    }

    /**
     * Parses the JSON representation of the player into a Player object.
     * 
     * @return The Player object
     */
    public Player parsePlayer() {

        JSONObject jPlayer = jFile.getJSONObject("player");
        String name = jPlayer.getString("name");
        ArrayList<String> perks;
        ArrayList<String> items;
        ArrayList<String> statuses;

        perks = parseListStr(jPlayer.optJSONArray("perks"));
        items = parseListStr(jPlayer.optJSONArray("items"));
        statuses = parseListStr(jPlayer.optJSONArray("statuses"));

        return new Player(name, perks, items, statuses);
    }

    /**
     * Parses a JSONObject containing all scenes in the current game, into a
     * Hashmap of scenes for the game to store and use over the games runtime.
     * 
     * @return A Hashmap of Scenes, keyed by their index from the JSON file
     */
    public HashMap<String, Scene> parseScenes() {
        JSONObject jScenes = jFile.getJSONObject("scenes");
        HashMap<String, Scene> scenes = new HashMap<>();
        jScenes.keySet().forEach(key -> {
            JSONObject jScene = jScenes.getJSONObject(key);
            Scene scene = parseScene(key, jScene);
            scenes.put(key, scene);
        });

        return scenes;
    }

    /**
     * Parses a single JSONObject scene into a Scene java object. Called by
     * parseScenes for each JSON scene in the file it reads.
     * 
     * @param index  The index of this JSON scene
     * @param jScene The JSON scene
     * @return A Scene object
     */
    public Scene parseScene(String index, JSONObject jScene) {
        // Get the scene type
        String sceneType = jScene.getString("sceneType");

        // Get common scene fields
        String lines = jScene.getString("lines");
        Optional<Event> event = parseEvent(jScene);

        // Get additional fields based on type
        if (sceneType.matches("leaf")) {
            String nextScene = jScene.getString("nextScene");
            return new LeafScene(index, lines, event, nextScene);
        }

        if (sceneType.matches("node")) {
            ArrayList<Branch> branches = parseBranches(jScene);
            return new NodeScene(index, lines, event, branches);
        }
        return null;
    }

    /**
     * Parses an Event from a JSON scene or branch.
     * 
     * @param j The scene or branch JSONObject
     * @return Optionally, an Event object
     */
    public Optional<Event> parseEvent(JSONObject j) {
        Optional<JSONObject> jEvent = Optional.ofNullable(j.optJSONObject("event"));

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
     * @param jScene The scene JSONObject
     * @return A list of Branch objects this scene contains
     */
    public ArrayList<Branch> parseBranches(JSONObject jScene) {
        ArrayList<Branch> branches = new ArrayList<>();
        JSONArray jBranches = jScene.getJSONArray("branches");
        jBranches.forEach(rawBranch -> {
            JSONObject jBranch = (JSONObject) rawBranch;
            String bScene = jBranch.getString("bScene");
            String prompt = jBranch.getString("prompt");
            Optional<Event> event = parseEvent(jBranch);
            JSONObject jReq = jBranch.optJSONObject("requirement");
            Optional<Requirement> requirement = Optional.ofNullable(RequirementFactory.buildRequirement(jReq));
            branches.add(new Branch(bScene, prompt, event, requirement));
        });

        return branches;
    }

    /**
     * Parses the current scene from the JSON file.
     * 
     * @return The current scene index
     */
    public String parseCurrScene() {
        return jFile.getString("currScene");
    }

    /**
     * Parses the next chapter, if it exists, from the JSON file.
     * 
     * @return Optionally, the identity of the next chapter
     */
    public Optional<String> parseNextChapter() {
        String nextChapter = jFile.optString("nextChapter");
        if (!nextChapter.isEmpty())
            return Optional.of(nextChapter);
        return Optional.empty();
    }

    /**
     * Parses the game name from JSON.
     * 
     * @return The name of the Game
     */
    public String parseGameName() {
        return jFile.getString("gameName");
    }

    /**
     * Parses a JSONArray of strings into an ArrayList of strings.
     * 
     * @param jList A JSONArray containing strings
     * @return A List of strings from the JSONArray
     */
    public ArrayList<String> parseListStr(JSONArray jList) {
        ArrayList<String> list = new ArrayList<>();
        if (jList == null) {
            return list;
        }
        jList.forEach(object -> {
            list.add((String) object);
        });
        return list;
    }
}
