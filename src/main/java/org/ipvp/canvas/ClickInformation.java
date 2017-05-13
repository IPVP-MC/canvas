package org.ipvp.canvas;

import org.bukkit.event.Event;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.ipvp.canvas.slot.Slot;

/**
 * Information about a click performed by a player in a Menu
 */
public class ClickInformation {

    private final Menu clickedMenu;
    private final Slot clickedSlot;
    private final InventoryAction action;
    private final ClickType clickType;
    private Event.Result result;
    
    ClickInformation(Menu clickedMenu, Slot clickedSlot, InventoryAction action, ClickType clickType) {
        this.clickedMenu = clickedMenu;
        this.clickedSlot = clickedSlot;
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
     * Returns the Slot that was clicked
     * 
     * @return The clicked Slot
     */
    public Slot getClickedSlot() {
        return clickedSlot;
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

    /**
     * Returns the result that the click will have in the inventory
     * 
     * @return The click result
     */
    public Event.Result getResult() {
        return result;
    }

    /**
     * Sets the result that the click will have in the inventory
     * 
     * @param result The new result
     */
    public void setResult(Event.Result result) {
        this.result = result;
    }
}
