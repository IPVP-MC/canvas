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
import org.ipvp.canvas.mask.Mask2D;
import org.ipvp.canvas.slot.Slot;
import org.ipvp.canvas.slot.SlotSettings;
import org.ipvp.canvas.template.ItemStackTemplate;
import org.ipvp.canvas.template.StaticItemTemplate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

/**
 * Fluent builder to assist with creating series of Menus.
 */
public class PaginatedMenuBuilder {

    private final Menu.Builder pageBuilder;
    private Mask2D slots;
    private Consumer<Menu> newMenuModifier;
    private int previousButtonSlot = -1;
    private int nextButtonSlot = -1;
    private ItemStackTemplate previousButton;
    private ItemStackTemplate previousButtonEmpty;
    private ItemStackTemplate nextButton;
    private ItemStackTemplate nextButtonEmpty;
    private List<SlotSettings> items = new ArrayList<>();

    private PaginatedMenuBuilder(Menu.Builder pageBuilder) {
        this.pageBuilder = pageBuilder;
    }

    /**
     * Sets the slots where items can be inserted in each created Menu.
     *
     * @param slots slot mask
     * @return fluent pattern
     */
    public PaginatedMenuBuilder slots(Mask2D slots) {
        this.slots = slots;
        return this;
    }

    /**
     * Sets the modifier for when a new menu is created.
     *
     * @param newMenuModifier modifier
     * @return fluent pattern
     */
    public PaginatedMenuBuilder newMenuModifier(Consumer<Menu> newMenuModifier) {
        this.newMenuModifier = newMenuModifier;
        return this;
    }

    /**
     * Sets the slot index for the previous page button.
     *
     * @param previousButtonSlot slot index
     * @return fluent pattern
     */
    public PaginatedMenuBuilder previousButtonSlot(int previousButtonSlot) {
        this.previousButtonSlot = previousButtonSlot;
        return this;
    }

    /**
     * Sets the slot index for the previous page button.
     *
     * <p>Only the first slot index in the mask will be taken.
     *
     * @param previousButtonSlot slot mask
     * @return fluent pattern
     */
    public PaginatedMenuBuilder previousButtonSlot(Mask2D previousButtonSlot) {
        return previousButtonSlot(indexFromMask(previousButtonSlot));
    }

    /**
     * Sets the slot index for the next page button.
     *
     * @param nextButtonSlot slot index
     * @return fluent pattern
     */
    public PaginatedMenuBuilder nextButtonSlot(int nextButtonSlot) {
        this.nextButtonSlot = nextButtonSlot;
        return this;
    }

    /**
     * Sets the slot index for the next page button.
     *
     * <p>Only the first slot index in the mask will be taken.
     *
     * @param nextButtonSlot slot mask
     * @return fluent pattern
     */
    public PaginatedMenuBuilder nextButtonSlot(Mask2D nextButtonSlot) {
        return nextButtonSlot(indexFromMask(nextButtonSlot));
    }

    /* Helper method to get a slot index from a Mask2D */
    private static int indexFromMask(Mask2D mask) {
        return mask.getMask().size() > 0 ? mask.getMask().get(0) : -1;
    }

    /**
     * Sets the previous button icon.
     *
     * @param item icon
     * @return fluent pattern
     */
    public PaginatedMenuBuilder previousButton(ItemStack item) {
        return previousButton(new StaticItemTemplate(item));
    }

    /**
     * Sets the previous button icon.
     *
     * @param item icon template
     * @return fluent pattern
     */
    public PaginatedMenuBuilder previousButton(ItemStackTemplate item) {
        this.previousButton = item;
        return this;
    }

    /**
     * Sets the previous button icon to display when there is no previous page.
     *
     * @param item icon
     * @return fluent pattern
     */
    public PaginatedMenuBuilder previousButtonEmpty(ItemStack item) {
        return previousButtonEmpty(new StaticItemTemplate(item));
    }

    /**
     * Sets the previous button icon to display when there is no previous page.
     *
     * @param item icon template
     * @return fluent pattern
     */
    public PaginatedMenuBuilder previousButtonEmpty(ItemStackTemplate item) {
        this.previousButtonEmpty = item;
        return this;
    }

    /**
     * Sets the next button icon.
     *
     * @param item icon
     * @return fluent pattern
     */
    public PaginatedMenuBuilder nextButton(ItemStack item) {
        return nextButton(new StaticItemTemplate(item));
    }

    /**
     * Sets the next button icon.
     *
     * @param item icon template
     * @return fluent pattern
     */
    public PaginatedMenuBuilder nextButton(ItemStackTemplate item) {
        this.nextButton = item;
        return this;
    }

    /**
     * Sets the next button icon to display when there is no next page.
     *
     * @param item icon
     * @return fluent pattern
     */
    public PaginatedMenuBuilder nextButtonEmpty(ItemStack item) {
        return nextButtonEmpty(new StaticItemTemplate(item));
    }

    /**
     * Sets the next button icon to display when there is no next page.
     *
     * @param item icon template
     * @return fluent pattern
     */
    public PaginatedMenuBuilder nextButtonEmpty(ItemStackTemplate item) {
        this.nextButtonEmpty = item;
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
     * Builds the pages of the menu.
     *
     * @return menu pages
     */
    public List<Menu> build() {
        List<Menu> pages = new ArrayList<>();
        List<SlotSettings> items = new ArrayList<>(this.items);

        do {
            Menu page = pageBuilder.build();
            if (newMenuModifier != null) {
                newMenuModifier.accept(page);
            }
            List<Integer> validSlots = getValidSlots(page);
            setPaginationIcon(page, previousButtonSlot, previousButtonEmpty);
            setPaginationIcon(page, nextButtonSlot, nextButtonEmpty);
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

        // Add pagination icons
        for (int i = 1 ; i < pages.size() ; i++) {
            Menu page = pages.get(i);
            Menu prev = pages.get(i - 1);
            setPaginationIcon(prev, nextButtonSlot, nextButton, (p, c) -> page.open(p));
            setPaginationIcon(page, previousButtonSlot, previousButton, (p, c) -> prev.open(p));
        }

        return pages;
    }

    /* Helper method to set pagination icon with validation checking */
    private void setPaginationIcon(Menu menu, int slotIndex, ItemStackTemplate icon) {
        setPaginationIcon(menu, slotIndex, icon, null);
    }

    /* Helper method to set pagination icon with validation checking */
    private void setPaginationIcon(Menu menu, int slotIndex, ItemStackTemplate icon, Slot.ClickHandler clickHandler) {
        if (slotIndex >= 0 && slotIndex < menu.getDimensions().getArea()) {
            Slot slot = menu.getSlot(slotIndex);
            slot.setItemTemplate(icon);
            slot.setClickHandler(clickHandler);
        }
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
    public static PaginatedMenuBuilder builder(Menu.Builder pageBuilder) {
        if (pageBuilder == null) {
            throw new IllegalArgumentException("Menu builder cannot be null");
        }
        return new PaginatedMenuBuilder(pageBuilder);
    }
}
