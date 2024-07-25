package events;

import game.Player;

/**
 * An Event is something that occurs during a scene, and alters the state of the
 * player. Examples include:
 * - Gaining a perk
 * - Finding an item
 * - Losing a status
 * 
 * An event has a type and an argument. The argument defines the change to
 * be made
 */
public interface Event {
    public void runEvent(Player player);
}
