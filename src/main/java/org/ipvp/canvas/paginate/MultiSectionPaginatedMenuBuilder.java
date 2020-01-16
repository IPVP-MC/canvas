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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Fluent builder to assist with creating series of Menus.
 */
public class MultiSectionPaginatedMenuBuilder extends AbstractPaginatedMenuBuilder<MultiSectionPaginatedMenuBuilder> {

    private Map<Character, Mask> sectionSlots = new HashMap<>();
    private Map<Character, List<SlotSettings>> sectionItems = new HashMap<>();

    private MultiSectionPaginatedMenuBuilder(Menu.Builder<?> pageBuilder) {
        super(pageBuilder);
    }

    /**
     * Adds an item section where items can be inserted in
     * created Menus.
     *
     * @param character section identifier
     * @param slots slot mask
     * @return fluent pattern
     */
    public MultiSectionPaginatedMenuBuilder slots(char character, Mask slots) {
        this.sectionSlots.put(character, slots);
        return this;
    }

    /**
     * Adds an item to be added for pagination.
     *
     * @param item item
     * @return fluent pattern
     */
    public MultiSectionPaginatedMenuBuilder addItem(char character, ItemStack item) {
        return addItem(character, new StaticItemTemplate(item));
    }

    /**
     * Adds an item to be added for pagination.
     *
     * @param item item template
     * @return fluent pattern
     */
    public MultiSectionPaginatedMenuBuilder addItem(char character, ItemStackTemplate item) {
        return addItem(character, SlotSettings.builder().itemTemplate(item).build());
    }

    /**
     * Adds an item to be added for pagination.
     *
     * @param item item slot details
     * @return fluent pattern
     */
    public MultiSectionPaginatedMenuBuilder addItem(char character, SlotSettings item) {
        List<SlotSettings> currentItems = sectionItems.compute(character, (k, v) -> v == null ? new ArrayList<>() : v);
        currentItems.add(item);
        return this;
    }

    /**
     * Adds a collection of items for pagination.
     *
     * @param items items
     * @return fluent pattern
     */
    public MultiSectionPaginatedMenuBuilder addItems(char character, Collection<ItemStack> items) {
        items.forEach(i -> addItem(character, i));
        return this;
    }

    /**
     * Adds a collection of items for pagination.
     *
     * @param items items
     * @return fluent pattern
     */
    public MultiSectionPaginatedMenuBuilder addItemTemplates(char character, Collection<ItemStackTemplate> items) {
        items.forEach(i -> addItem(character, i));
        return this;
    }

    /**
     * Adds a collection of slot settings for pagination.
     *
     * @param slotSettings slotSettings
     * @return fluent pattern
     */
    public MultiSectionPaginatedMenuBuilder addSlotSettings(char character, Collection<SlotSettings> slotSettings) {
        slotSettings.forEach(s -> addItem(character, s));
        return this;
    }

    /**
     * Builds the pages of the menu.
     *
     * @return menu pages
     */
    public List<Menu> build() {
        List<Menu> pages = new ArrayList<>();
        Map<Character, List<SlotSettings>> sectionItems = new HashMap<>();
        this.sectionItems.forEach((c, l) -> sectionItems.put(c, new ArrayList<>(l))); // Deep clone

        boolean requiresPage = false;

        do {
            Menu page = getPageBuilder().build();
            if (getNewMenuModifier() != null) {
                getNewMenuModifier().accept(page);
            }
            setPaginationIcon(page, getPreviousButtonSlot(), getPreviousButtonEmpty());
            setPaginationIcon(page, getNextButtonSlot(), getNextButtonEmpty());

            for (Map.Entry<Character, List<SlotSettings>> entry : sectionItems.entrySet()) {
                List<SlotSettings> items = entry.getValue();
                Mask slots = sectionSlots.get(entry.getKey());
                Iterator<Integer> slotIterator = slots.iterator();

                while (!items.isEmpty() && slotIterator.hasNext()) {
                    int slotIndex = slotIterator.next();
                    if (slotIndex >= 0 && page.getDimensions().getArea() > slotIndex) {
                        SlotSettings item = items.remove(0);
                        Slot slot = page.getSlot(slotIndex);
                        slot.setSettings(item);
                    }
                }

                requiresPage = requiresPage || !items.isEmpty();
            }

            pages.add(page);
        } while (requiresPage);

        linkPages(pages);
        return pages;
    }

    /**
     * Returns a new pagination builder.
     *
     * @param pageBuilder menu page builder
     * @return builder instance
     */
    public static MultiSectionPaginatedMenuBuilder builder(Menu.Builder<?> pageBuilder) {
        if (pageBuilder == null) {
            throw new IllegalArgumentException("Menu builder cannot be null");
        }
        return new MultiSectionPaginatedMenuBuilder(pageBuilder);
    }
}
