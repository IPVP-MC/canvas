/*
 * Copyright (C) Matthew Steglinski (SainttX) <matt@ipvp.org>
 * Copyright (C) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.sainttx.canvas.test;

import java.util.Arrays;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.MenuFunctionListener;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;
import org.ipvp.canvas.slot.ClickOptions;
import org.ipvp.canvas.slot.Slot;
import org.ipvp.canvas.type.ChestMenu;

public class CanvasExamplePlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new MenuFunctionListener(), this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Menu baseMenu = getBaseMenu();

        // Add an item to move to the movable items menu
        ItemStack nextMenuItem = new ItemStack(Material.ARROW);
        ItemMeta nextMenuMeta = nextMenuItem.getItemMeta();
        nextMenuMeta.setDisplayName("Next Menu");
        nextMenuMeta.setLore(Arrays.asList("Click me to go to a menu where", "you can move items around"));
        nextMenuItem.setItemMeta(nextMenuMeta);

        Slot nextMenuSlot = baseMenu.getSlot(2, 8);
        nextMenuSlot.setItem(nextMenuItem);
        nextMenuSlot.setClickHandler((p, m) -> getMovableItemsMenu().open(p));

        baseMenu.open((Player) sender);
        return true;
    }

    /**
     * Creates the entry example menu that shows a players basic stats
     * using item templates.
     *
     * @return base menu
     */
    private Menu getBaseMenu() {
        Menu menu = ChestMenu.builder(3)
                .title("Canvas Inventory")
                .build();
        menu.setCloseHandler((p, m) -> {
            getServer().getLogger().info("Player " + p.getName() + " closed a menu");
        });
        Mask mask = BinaryMask.builder(menu)
                .item(new ItemStack(Material.STAINED_GLASS_PANE))
                .pattern("111111111")
                .pattern("100000001")
                .pattern("111111111")
                .build();
        mask.apply(menu);


        menu.getSlot(2, 2).setItemTemplate(p -> {
            ItemStack playerNameItem = new ItemStack(Material.PAPER);
            ItemMeta nameMeta = playerNameItem.getItemMeta();
            nameMeta.setDisplayName("Name: " + p.getName());
            playerNameItem.setItemMeta(nameMeta);
            return playerNameItem;
        });

        menu.getSlot(2, 3).setItemTemplate(p -> {
            ItemStack playerLevelItem = new ItemStack(Material.EXP_BOTTLE);
            ItemMeta levelMeta = playerLevelItem.getItemMeta();
            levelMeta.setDisplayName("Level: " + p.getLevel());
            playerLevelItem.setItemMeta(levelMeta);
            return playerLevelItem;
        });

        Slot debugSlot = menu.getSlot(2, 7);
        debugSlot.setItemTemplate(p -> {
            ItemStack playerLevelItem = new ItemStack(Material.REDSTONE);
            ItemMeta levelMeta = playerLevelItem.getItemMeta();
            levelMeta.setDisplayName("Debug Item");
            levelMeta.setLore(Arrays.asList("Clicking and moving this item around will",
                    "print debug output in the console"));
            playerLevelItem.setItemMeta(levelMeta);
            return playerLevelItem;
        });
        debugSlot.setClickOptions(ClickOptions.ALLOW_ALL);
        debugSlot.setClickHandler(getDebugClickHandler());

        return menu;
    }

    // Creates a debugging click handler
    private Slot.ClickHandler getDebugClickHandler() {
        return (player, click) -> {
            Slot slot = click.getClickedSlot();
            getServer().getLogger().info("Player " + player.getName() + " is clicking slot index " + slot.getIndex());
            getServer().getLogger().info("- Inventory Action: " + click.getAction());
            getServer().getLogger().info("- Click Type: " + click.getClickType());
            getServer().getLogger().info("- Current Event Result: " + click.getResult());
            if (click.isAddingItem() || click.isTakingItem()) {
                getServer().getLogger().info("- Item Amount: " + click.getItemAmount());
            }
            getServer().getLogger().info("- isAddingItem: " + click.isAddingItem());
            if (click.isAddingItem()) {
                ItemStack addingItem = click.getAddingItem();
                getServer().getLogger().info("- Adding Item: " + addingItem.getType());
            }
            getServer().getLogger().info("- isTakingItem: " + click.isTakingItem());
            getServer().getLogger().info("- isDroppingItem: " + click.isDroppingItem());

            ItemStack slotItem = slot.getItem(player);
            if (slotItem != null) {
                getServer().getLogger().info("- Slot is configured with item of type " + slotItem.getType());
            } else {
                getServer().getLogger().info("- Slot is not configured with any item");
            }

            ItemStack rawItem = slot.getRawItem(player);
            if (rawItem != null) {
                getServer().getLogger().info("- Raw item in slot is of type " + rawItem.getType());
            } else {
                getServer().getLogger().info("- No raw item in slot");
            }
        };
    }

    /**
     * Creates and returns a menu with some items that any player can
     * move around freely.
     *
     * <p>Players can also drop the item outside the menu, because the
     * default behavior is overriden.
     *
     * @return movable items menu
     */
    private Menu getMovableItemsMenu() {
        Menu moveableItemsMenu = ChestMenu.builder(3)
                .title("Move Items Around")
                .build();
        moveableItemsMenu.getSlot(0).setItem(new ItemStack(Material.STONE));
        moveableItemsMenu.getSlot(1).setItem(new ItemStack(Material.STONE));
        moveableItemsMenu.getSlot(2).setItem(new ItemStack(Material.STONE));
        for (Slot slot : moveableItemsMenu) {
            slot.setClickOptions(ClickOptions.ALLOW_ALL);
        }
        moveableItemsMenu.setCursorDropHandler((player, click) -> {
            getServer().getLogger().info("Player " + player.getName() + " dropped an item with type: "
                    + click.getCursorItem().getType());
            click.setResult(Event.Result.ALLOW);
        });
        return moveableItemsMenu;
    }
}
