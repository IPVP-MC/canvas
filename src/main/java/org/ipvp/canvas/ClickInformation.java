package org.ipvp.canvas;

import java.util.Objects;

import org.bukkit.event.Event;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.ipvp.canvas.slot.Slot;

/**
 * Information about a click performed by a player in a Menu
 */
public class ClickInformation {
    
    private final InventoryClickEvent handle;
    private final Menu clickedMenu;
    private final Slot clickedSlot;
    private Event.Result result;
    
    ClickInformation(InventoryClickEvent handle, Menu clickedMenu, Slot clickedSlot, Event.Result result) {
        this.handle = handle;
        this.clickedMenu = clickedMenu;
        this.clickedSlot = clickedSlot;
        this.result = result;
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
        return handle.getAction();
    }

    /**
     * Returns the type of click performed by a player
     * 
     * @return The type of click
     */
    public ClickType getClickType() {
        return handle.getClick();
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
        Objects.requireNonNull(result);
        this.result = result;
    }

    /**
     * @return Whether the Player is adding an item into the slot
     */
    public boolean isAddingItem() {
        switch (getAction()) {
            case PLACE_ALL:
            case PLACE_SOME:
            case PLACE_ONE:
            case SWAP_WITH_CURSOR:
            case HOTBAR_SWAP:
                return true;
            case MOVE_TO_OTHER_INVENTORY:
                return handle.getView().getBottomInventory() == handle.getClickedInventory();
            default:
                return false;
        }
    }

    /**
     * @return Whether the Player is removing an item from the slot
     */
    public boolean isTakingItem() {
        switch (getAction()) {
            case PICKUP_ALL:
            case PICKUP_HALF:
            case PICKUP_ONE:
            case PICKUP_SOME:
            case SWAP_WITH_CURSOR:
                return true;
            case MOVE_TO_OTHER_INVENTORY:
            case HOTBAR_MOVE_AND_READD:
            case HOTBAR_SWAP:
                return handle.getView().getTopInventory() == handle.getClickedInventory();
            default:
                return isDroppingItem();
        }
    }

    /**
     * @return Whether the Player is dropping the item in the slot
     */
    public boolean isDroppingItem() {
        switch (getAction()) {
            case DROP_ALL_SLOT:
            case DROP_ONE_SLOT:
                return true;
            default:
                return false;
        }
    }
}
