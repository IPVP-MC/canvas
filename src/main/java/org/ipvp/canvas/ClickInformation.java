package org.ipvp.canvas;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;

public class ClickInformation {

    private final Menu clickedMenu;
    private final InventoryAction action;
    private final ClickType clickType;
    
    ClickInformation(Menu clickedMenu, InventoryAction action, ClickType clickType) {
        this.clickedMenu = clickedMenu;
        this.action = action;
        this.clickType = clickType;
    }
    
    public Menu getClickedMenu() {
        return clickedMenu;
    }
    
    public InventoryAction getAction() {
        return action;
    }
    
    public ClickType getClickType() {
        return clickType;
    }
}
