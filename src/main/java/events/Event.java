package events;

import game.Player;

/**
 * <p>
 * An Event is something that occurs during a scene, and alters the state of the
 * player. Examples include:
 * </p>
 * <ul>
 * <li>Gaining a perk</li>
 * <li>Finding an item</li>
 * <li>Losing a status</li>
 * </ul>
 * 
 * An Event has a type and an argument. The argument defines the change to
 * be made.
 * 
 * @author Ahmed El-Sayed
 */
public interface Event {
    /**
     * Run the event to modify the player state.
     * 
     * @param player The player character
     */
    public void runEvent(Player player);

    /**
     * Prints the result of the event to the player.
     */
    public void logEvent();
}
