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

package org.ipvp.canvas.paginate;

import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.mask.Mask;
import org.ipvp.canvas.slot.Slot;
import org.ipvp.canvas.slot.SlotSettings;
import org.ipvp.canvas.template.ItemStackTemplate;
import org.ipvp.canvas.template.StaticItemTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Fluent builder to assist with creating series of Menus.
 */
public class PaginatedMenuBuilder extends AbstractPaginatedMenuBuilder<PaginatedMenuBuilder> {

    private Mask slots;
    private List<SlotSettings> items = new ArrayList<>();

    private PaginatedMenuBuilder(Menu.Builder<?> pageBuilder) {
        super(pageBuilder);
    }

    /**
     * Sets the slots where items can be inserted in each created Menu.
     *
     * @param slots slot mask
     * @return fluent pattern
     */
    public PaginatedMenuBuilder slots(Mask slots) {
        this.slots = slots;
        return this;
    }

    /**
     * Adds an item to be added for pagination.
     *
     * @param item item
     * @return fluent pattern
     */
    public PaginatedMenuBuilder addItem(ItemStack item) {
        return addItem(new StaticItemTemplate(item));
    }

    /**
     * Adds an item to be added for pagination.
     *
     * @param item item template
     * @return fluent pattern
     */
    public PaginatedMenuBuilder addItem(ItemStackTemplate item) {
        return addItem(SlotSettings.builder().itemTemplate(item).build());
    }

    /**
     * Adds an item to be added for pagination.
     *
     * @param item item slot details
     * @return fluent pattern
     */
    public PaginatedMenuBuilder addItem(SlotSettings item) {
        items.add(item);
        return this;
    }

    /**
     * Adds a collection of items for pagination.
     *
     * @param items items
     * @return fluent pattern
     */
    public PaginatedMenuBuilder addItems(Collection<ItemStack> items) {
        items.forEach(this::addItem);
        return this;
    }

    /**
     * Adds a collection of items for pagination.
     *
     * @param items items
     * @return fluent pattern
     */
    public PaginatedMenuBuilder addItemTemplates(Collection<ItemStackTemplate> items) {
        items.forEach(this::addItem);
        return this;
    }

    /**
     * Adds a collection of slot settings for pagination.
     *
     * @param slotSettings slotSettings
     * @return fluent pattern
     */
    public PaginatedMenuBuilder addSlotSettings(Collection<SlotSettings> slotSettings) {
        slotSettings.forEach(this::addItem);
        return this;
    }

    /**
     * Builds the pages of the menu.
     *
     * @return menu pages
     */
    public List<Menu> build() {
        List<Menu> pages = new ArrayList<>();
        List<SlotSettings> items = new ArrayList<>(this.items);

        do {
            Menu page = getPageBuilder().build();
            if (getNewMenuModifier() != null) {
                getNewMenuModifier().accept(page);
            }
            List<Integer> validSlots = getValidSlots(page);
            setPaginationIcon(page, getPreviousButtonSlot(), getPreviousButtonEmpty());
            setPaginationIcon(page, getNextButtonSlot(), getNextButtonEmpty());
            Iterator<Integer> slotIterator = validSlots.iterator();
            while (!items.isEmpty() && slotIterator.hasNext()) {
                int slotIndex = slotIterator.next();
                if (page.getDimensions().getArea() > slotIndex) {
                    SlotSettings item = items.remove(0);
                    Slot slot = page.getSlot(slotIndex);
                    slot.setSettings(item);
                }
            }

            pages.add(page);
        } while (!items.isEmpty());

        linkPages(pages);
        return pages;
    }

    /* Helper method to check if a menu can fit an item into a slot masks slots */
    private List<Integer> getValidSlots(Menu menu) {
        List<Integer> valid = new ArrayList<>();
        if (slots != null) {
            for (int slot : slots) {
                if (slot >= 0 && slot < menu.getDimensions().getArea()) {
                    valid.add(slot);
                }
            }
        }
        return valid;
    }

    /**
     * Returns a new pagination builder.
     *
     * @param pageBuilder menu page builder
     * @return builder instance
     */
    public static PaginatedMenuBuilder builder(Menu.Builder<?> pageBuilder) {
        if (pageBuilder == null) {
            throw new IllegalArgumentException("Menu builder cannot be null");
        }
        return new PaginatedMenuBuilder(pageBuilder);
    }
}
