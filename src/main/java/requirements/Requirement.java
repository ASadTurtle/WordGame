package requirements;

import game.Player;

/**
 * <p>
 * A requirement is some boolean expression attached to a branch. If the player
 * meets the requirement of a branch, that branch will be available for them
 * to select.
 * </p>
 * 
 * <p>
 * Requirements will check what perks, items, and/or statuses the player has,
 * and will evaluate the requirements logic to determine if the player can
 * select the branch.
 * </p>
 * 
 * <p>
 * Since requirements are boolean expressions, they can be modularly combined
 * to make more complex expressions.
 * </p>
 * 
 * <p>
 * For example, a branch may have a single
 * requirement specifying the player must have the "Axe" item to attack a
 * creature. We could also have a branch where we require the "Haft" and
 * "Axehead" items, AND the "Blacksmith" perk in order to make an "Axe" item.
 * Since requirements are modular, we can make them as simple or as complex as
 * we like.
 * </p>
 * 
 * @author Ahmed El-Sayed
 */
public interface Requirement {
    /**
     * Evaluates the requirement to determine if the player meets the conditions
     * to select the branch.
     * 
     * @param player The player character
     * @return {@code true} if the player meets the requirements
     */
    public boolean evaluate(Player player);
}
