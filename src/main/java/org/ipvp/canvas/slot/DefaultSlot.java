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

package org.ipvp.canvas.slot;

import java.util.Objects;
import java.util.Optional;

import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.type.AbstractMenu;
import org.ipvp.canvas.type.MenuHolder;

/**
 * A slot defined for default use by all Menus defined by this library.
 */
public class DefaultSlot implements Slot {

    private final AbstractMenu handle;
    private final int index;
    private ItemStack item;
    private ClickOptions options;
    private ClickHandler handler;
    
    public DefaultSlot(AbstractMenu handle, int index) {
        this(handle, index, ClickOptions.DENY_ALL);
    }

    public DefaultSlot(AbstractMenu handle, int index, ClickOptions options) {
        this(handle, index, options, null);
    }

    private DefaultSlot(AbstractMenu handle, int index, ClickOptions options, ClickHandler handler) {
        Objects.requireNonNull(handle);
        this.handle = handle;
        this.index = index;
        setClickOptions(options);
        this.handler = handler;
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
        return this.item;
    }

    @Override
    public void setItem(ItemStack item) {
        this.item = item == null ? null : new ItemStack(item);
        handle.getViewers().stream().map(MenuHolder::getInventory)
                .forEach(i -> i.setItem(index, item));
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
