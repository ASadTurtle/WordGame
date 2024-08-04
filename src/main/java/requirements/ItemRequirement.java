package requirements;

import game.Player;

/**
 * TODO
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
