package requirements;

import game.Player;

/**
 * A item requirement checks if the player has the required item, by calling
 * the {@code player.hasItem(item)} method.
 * 
 * @author Ahmed El-Sayed
 */
public class ItemRequirement implements Requirement {
    String item;

    public ItemRequirement(String item) {
        this.item = item;
    }

    public boolean evaluate(Player player) {
        return player.hasItem(item);
    }
}
