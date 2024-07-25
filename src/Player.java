import java.util.ArrayList;

/**
 * The Player class stores all data pertinent to the player character.
 * Their name is stored here, as well as any items they carry, perks they have
 * gained, and statuses they are affected by.
 * 
 * TODO - Expand
 */
public class Player {
    private String name;
    private ArrayList<String> perks;
    private ArrayList<String> items;
    private ArrayList<String> statuses;

    /**
     * Construct a new player, with the given name they have chosen. The Game
     * will default the name to `Aurellion` if one was not entered by the
     * player.
     * 
     * @param name
     */
    public Player(String name) {
        this.name = name;
        this.perks = new ArrayList<>();
        this.items = new ArrayList<>();
        this.statuses = new ArrayList<>();
    }

    /**
     * This constructer instantiates a Player that exists from a save file.
     * This should ONLY be called when loading a player state from a save.
     * 
     * @param name
     * @param perks
     * @param items
     * @param statuses
     */
    public Player(String name, ArrayList<String> perks, ArrayList<String> items, ArrayList<String> statuses) {
        this.name = name;
        this.perks = perks;
        this.items = items;
        this.statuses = statuses;
    }

    public String getName() {
        return name;
    }

    /**
     * Grants the player a new perk. If the player already has it, then nothing
     * else happens
     * 
     * @param perk
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
     * @param item
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
     * @param status
     */
    public void gainStatus(String status) {
        if (!hasStatus(status)) {
            statuses.add(status);
        }
    }

    /**
     * Does the player have this perk?
     * 
     * @param perk
     * @return
     */
    public boolean hasPerk(String perk) {
        return perks.contains(perk);
    }

    /**
     * Does the player have this item?
     * 
     * @param item
     * @return
     */
    public boolean hasItem(String item) {
        return items.contains(item);
    }

    /**
     * Does the player have this status?
     * 
     * @param status
     * @return
     */
    public boolean hasStatus(String status) {
        return statuses.contains(status);
    }

    /**
     * The player loses the given perk. If they never had it, then nothing
     * changes.
     * 
     * @param perk
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
     * @param item
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
     * @param status
     */
    public void loseStatus(String status) {
        if (hasStatus(status)) {
            statuses.remove(status);
        }
    }
}
