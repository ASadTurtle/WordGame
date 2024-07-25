package game;

import java.util.HashMap;

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
    private Player player;
    private HashMap<String, Scene> scenes;

    public static void main(String[] args) {
        // Create a new player and scenes instance by parsing appropriate JSON files
    }
}
