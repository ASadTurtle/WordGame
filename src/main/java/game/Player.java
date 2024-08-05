package game;

import java.util.ArrayList;

/**
 * The Player class stores all data pertinent to the player character.
 * Their name is stored here, as well as any items they carry, perks they have
 * gained, and statuses they are affected by.
 * 
 * @author Ahmed El-Sayed
 */
public class Player {

    private String name;
    private ArrayList<String> perks;
    private ArrayList<String> items;
    private ArrayList<String> statuses;

    /**
     * This constructor instantiates a Player that exists from a file.
     * This should ONLY be called when loading a player state from a save or
     * from a playerDefault file.
     * 
     * @param name     The players name
     * @param perks    The players perks
     * @param items    The players items
     * @param statuses The players statuses
     */
    public Player(String name, ArrayList<String> perks, ArrayList<String> items, ArrayList<String> statuses) {
        this.name = name;
        this.perks = perks;
        this.items = items;
        this.statuses = statuses;
    }

    /**
     * Prints information about the player to terminal
     */
    public void logPlayer() {
        GameMenu.logPlayer(this);
    }

    /**
     * @return The players name
     */
    public String name() {
        return name;
    }

    public ArrayList<String> perks() {
        return perks;
    }

    public ArrayList<String> items() {
        return items;
    }

    public ArrayList<String> statuses() {
        return statuses;
    }

    /**
     * Grants the player a new perk. If the player already has it, then nothing
     * else happens
     * 
     * @param perk Perk we want to give to the player
     */
    public void gainPerk(String perk) {
        if (!hasPerk(perk)) {
            perks.add(perk);
        }
    }

    /**
     * Grants the player a new item. If the player already has it, then nothing
     * else happens
     * 
     * @param item Item we want to give to the player
     */
    public void gainItem(String item) {
        if (!hasItem(item)) {
            items.add(item);
        }
    }

    /**
     * Grants the player a new status. If the player already has it, then nothing
     * else happens
     * 
     * @param status Status we want to give to the player
     */
    public void gainStatus(String status) {
        if (!hasStatus(status)) {
            statuses.add(status);
        }
    }

    /**
     * Does the player have this perk?
     * 
     * @param perk Perk we are checking for
     * @return {@code true} if player has this perk
     */
    public boolean hasPerk(String perk) {
        return perks.contains(perk);
    }

    /**
     * Does the player have this item?
     * 
     * @param item Item we are checking for
     * @return {@code true} if player has this item
     */
    public boolean hasItem(String item) {
        return items.contains(item);
    }

    /**
     * Does the player have this status?
     * 
     * @param status Status we are checking for
     * @return {@code true} if player has this status
     */
    public boolean hasStatus(String status) {
        return statuses.contains(status);
    }

    /**
     * The player loses the given perk. If they never had it, then nothing
     * changes.
     * 
     * @param perk Perk we want to remove from player
     */
    public void losePerk(String perk) {
        if (hasPerk(perk)) {
            perks.remove(perk);
        }
    }

    /**
     * The player loses the given item. If they never had it, then nothing
     * changes.
     * 
     * @param item Item we want to remove from player
     */
    public void loseItem(String item) {
        if (hasItem(item)) {
            items.remove(item);
        }
    }

    /**
     * The player loses the given status. If they never had it, then nothing
     * changes.
     * 
     * @param status Status we want to remove from player
     */
    public void loseStatus(String status) {
        if (hasStatus(status)) {
            statuses.remove(status);
        }
    }
}
