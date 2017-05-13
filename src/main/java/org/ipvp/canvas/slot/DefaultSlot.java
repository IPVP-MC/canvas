package org.ipvp.canvas.slot;

import java.util.Objects;
import java.util.Optional;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * A slot defined for default use by all Menus defined by this library.
 */
public class DefaultSlot implements Slot {

    private final Inventory handle;
    private final int index;
    private ClickOptions options;
    private ClickHandler handler;
    
    public DefaultSlot(Inventory handle, int index) {
        this(handle, index, ClickOptions.DENY_ALL);
    }

    public DefaultSlot(Inventory handle, int index, ClickOptions options) {
        Objects.requireNonNull(handle);
        this.handle = handle;
        this.index = index;
        setClickOptions(options);
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public ClickOptions getClickOptions() {
        return options;
    }

    @Override
    public void setClickOptions(ClickOptions options) {
        Objects.requireNonNull(options);
        this.options = options;
    }

    @Override
    public ItemStack getItem() {
        return handle.getItem(index);
    }

    @Override
    public void setItem(ItemStack item) {
        handle.setItem(index, item);
    }

    @Override
    public Optional<ClickHandler> getClickHandler() {
        return Optional.ofNullable(handler);
    }

    @Override
    public void setClickHandler(ClickHandler handler) {
        this.handler = handler;
    }
}
