package org.ipvp.canvas;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;

/**
 * Information about a click performed by a player in a Menu
 */
public class ClickInformation {

    private final Menu clickedMenu;
    private final InventoryAction action;
    private final ClickType clickType;
    
    ClickInformation(Menu clickedMenu, InventoryAction action, ClickType clickType) {
        this.clickedMenu = clickedMenu;
        this.action = action;
        this.clickType = clickType;
    }

    /**
     * Returns the Menu that is handling the click
     * 
     * @return The handling Menu
     */
    public Menu getClickedMenu() {
        return clickedMenu;
    }

    /**
     * Returns the action performed by a player
     * 
     * @return The action of a player
     */
    public InventoryAction getAction() {
        return action;
    }

    /**
     * Returns the type of click performed by a player
     * 
     * @return The type of click
     */
    public ClickType getClickType() {
        return clickType;
    }
}
