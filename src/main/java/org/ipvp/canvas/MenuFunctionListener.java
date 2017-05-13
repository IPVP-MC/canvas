package org.ipvp.canvas;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.ipvp.canvas.slot.Slot;

/**
 * A listener that maintains the required functions of Menus.
 * <p>
 * Effectively handles all required events and passes through necessary data
 * to Menu instances that are being interacted with by players.
 */
public final class MenuFunctionListener implements Listener {

    @EventHandler
    public void handleGuiClick(InventoryClickEvent event) {
        Inventory clicked = event.getClickedInventory();

        if (clicked.getHolder() instanceof Menu) {
            Menu menu = (Menu) clicked.getHolder();
            Slot slot = menu.getSlot(event.getSlot()); // TODO: Clicking outside etc
            ClickInformation clickInformation = new ClickInformation(menu, slot, event.getAction(), event.getClick());

            Event.Result result = Event.Result.DEFAULT;
            if (!slot.getClickOptions().test(clickInformation)) {
                result = Event.Result.DENY;
            }
            
            clickInformation.setResult(result);
            
            if (slot.getClickHandler().isPresent()) {
                slot.getClickHandler().get().click((Player) event.getWhoClicked(), clickInformation);
            }
            
            event.setResult(clickInformation.getResult());
        }
    }
}
