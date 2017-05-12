package org.ipvp.canvas;

import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.ipvp.canvas.button.Button;
import org.ipvp.canvas.button.ClickableButton;

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
            Optional<Button> obutton = menu.getButton(event.getSlot());

            if (obutton.isPresent() && obutton.get() instanceof ClickableButton) {
                ClickableButton button = (ClickableButton) obutton.get();
                ClickInformation clickInformation = new ClickInformation(menu, event.getAction(), event.getClick());
                
                if (button.options().test(clickInformation)) {
                    button.click((Player) event.getWhoClicked(), clickInformation);
                    event.setResult(Event.Result.DENY);
                }
            }
        }
    }
}
