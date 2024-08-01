package game;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
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
    /**
     * Parses a JSONObject from a player record into a Player object for the game
     * to use. The argument is the entire JSON object from the loaded file.
     * 
     * @param jFile
     */
    public Player parsePlayer(JSONObject jFile) throws Exception {

        JSONObject jPlayer = (JSONObject) jFile.get("player");
        String name = (String) jPlayer.get("name");
        ArrayList<String> perks = parseListStr(jPlayer, "perks");
        ArrayList<String> items = parseListStr(jPlayer, "items");
        ArrayList<String> statuses = parseListStr(jPlayer, "statuses");

        return new Player(name, perks, items, statuses);
    }

    /**
     * Parses a JSONObject containing all scenes in the current game, into a
     * Hashmap of scenes for the game to store and use over the games runtime.
     * 
     * @param jFile
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public HashMap<String, Scene> parseScenes(JSONObject jFile) throws Exception {
        JSONObject jScenes = (JSONObject) jFile.get("scenes");
        HashMap<String, Scene> scenes = new HashMap<>();
        jScenes.forEach((key, jScene) -> {
            Scene scene = parseScene((String) key, (JSONObject) jScene);
            scenes.put((String) key, scene);
        });

        return scenes;
    }

    /**
     * Parses a single JSONObject scene into a Scene java object. Called by
     * parseScenes for each JSON scene in the file it reads.
     * 
     * @param jScene
     */
    public Scene parseScene(String index, JSONObject jScene) {
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
    public Optional<Event> parseEvent(JSONObject j) {
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
    public ArrayList<Branch> parseBranches(JSONObject jScene) {
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
     * Parses a JSON file to get the current scene the player is in.
     * 
     * @param jFile
     * @throws Exception
     */
    public String parseCurrScene(JSONObject jFile) throws Exception {
        return (String) jFile.get("currScene");
    }

    /**
     * Parses a JSON file to get the next chapter the player will start when
     * this one has concluded.
     * 
     * @param jFile
     * @throws Exception
     */
    public Optional<String> parseNextChapter(JSONObject jFile) throws Exception {
        return Optional.ofNullable((String) jFile.get("nextChapter"));
    }

    /**
     * Parses the game name from the save file.
     * 
     * @param jFile
     * @throws Exception
     */
    public String parseGameName(JSONObject jFile) throws Exception {
        return (String) jFile.get("gameName");
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
    public ArrayList<String> parseListStr(JSONObject j, String key) {
        JSONArray jObject = (JSONArray) j.get(key);
        ArrayList<String> list = new ArrayList<>();
        jObject.forEach(object -> {
            list.add((String) object);
        });
        return list;
    }
}
