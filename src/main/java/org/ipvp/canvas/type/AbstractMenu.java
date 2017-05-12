package org.ipvp.canvas.type;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.ipvp.canvas.ImmutableInventory;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.button.Button;

/**
 * An abstract class that provides a skeletal implementation of the Menu 
 * interface.
 */
public abstract class AbstractMenu implements Menu  {

    private final Inventory inventory;
    private Menu parent;
    private Map<Integer, Button> buttons = new HashMap<>();

    protected AbstractMenu(String title, int slots, Menu parent) {
        if (title == null) {
            title = InventoryType.CHEST.getDefaultTitle();
        }
        this.inventory = Bukkit.createInventory(this, slots, title);
        this.parent = parent;
    }
    
    protected AbstractMenu(String title, InventoryType type, Menu parent) {
        Objects.requireNonNull(type, "type cannot be null");
        if (title == null) {
            title = type.getDefaultTitle();
        }
        this.inventory = Bukkit.createInventory(this, type, title);
        this.parent = parent;
    }

    @Override
    public Optional<Menu> getParent() {
        return Optional.ofNullable(parent);
    }

    @Override
    public void open(Player viewer) {
        viewer.openInventory(getInventory());
    }

    @Override
    public void close(Player viewer) {
        Inventory inv = getInventory();
        if (!inv.getViewers().contains(viewer)) {
            throw new IllegalStateException("menu not open for player");
        }
        viewer.closeInventory();
    }

    @Override
    public Optional<Button> getButton(int index) {
        return Optional.ofNullable(buttons.get(index));
    }

    @Override
    public void setButton(int index, Button button) {
        if (buttons.containsKey(index)) {
            clear(index);
        }
        buttons.put(index, button);
        inventory.setItem(index, button.getIcon());
    }

    @Override
    public void clear() {
        buttons.clear();
    }

    @Override
    public void clear(int index) {
        Button removed = buttons.remove(index);
        if (removed != null) {
            inventory.setItem(index, null);
        }
    }

    @Override
    public ImmutableInventory getInventory() {
        return new ImmutableInventory(inventory);
    }

    /**
     * Abstract base class for builders of {@link Menu} types.
     * <p>
     * Builder instances are reusable; calling {@link #build()} will
     * generate a new Menu with identical features to the ones created before it.
     */
    public static abstract class Builder implements Menu.Builder {
        
        private String title;
        private Menu parent;

        @Override
        public Menu.Builder title(String title) {
            this.title = title;
            return this;
        }

        @Override
        public Menu.Builder parent(Menu parent) {
            this.parent = parent;
            return this;
        }

        public String getTitle() {
            return title;
        }

        public Menu getParent() {
            return parent;
        }
    }
}
