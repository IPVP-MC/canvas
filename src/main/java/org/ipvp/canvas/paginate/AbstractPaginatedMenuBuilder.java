package org.ipvp.canvas.paginate;

import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.mask.Mask;
import org.ipvp.canvas.slot.Slot;
import org.ipvp.canvas.template.ItemStackTemplate;
import org.ipvp.canvas.template.StaticItemTemplate;

import java.util.List;
import java.util.function.Consumer;

/**
 * Abstract implementation of a PaginatedMenuBuilder, providing basic
 * method implementations.
 */
public abstract class AbstractPaginatedMenuBuilder {

    private final Menu.Builder pageBuilder;
    private Consumer<Menu> newMenuModifier;
    private int previousButtonSlot = -1;
    private int nextButtonSlot = -1;
    private ItemStackTemplate previousButton;
    private ItemStackTemplate previousButtonEmpty;
    private ItemStackTemplate nextButton;
    private ItemStackTemplate nextButtonEmpty;

    public AbstractPaginatedMenuBuilder(Menu.Builder pageBuilder) {
        this.pageBuilder = pageBuilder;
    }

    /**
     * Gets the base builder for creating new pages.
     *
     * @return base page builder
     */
    public Menu.Builder getPageBuilder() {
        return pageBuilder;
    }

    /**
     * Sets the modifier for when a new menu is created.
     *
     * @param newMenuModifier modifier
     * @return fluent pattern
     */
    public AbstractPaginatedMenuBuilder newMenuModifier(Consumer<Menu> newMenuModifier) {
        this.newMenuModifier = newMenuModifier;
        return this;
    }

    /**
     * Gets the current modifier for when a new menu is created.
     *
     * @return menu modifier
     */
    public Consumer<Menu> getNewMenuModifier() {
        return newMenuModifier;
    }

    /**
     * Gets the slot index for the previous page button.
     *
     * @return previous page slot index
     */
    public int getPreviousButtonSlot() {
        return previousButtonSlot;
    }

    /**
     * Sets the slot index for the previous page button.
     *
     * @param previousButtonSlot slot index
     * @return fluent pattern
     */
    public AbstractPaginatedMenuBuilder previousButtonSlot(int previousButtonSlot) {
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
    public AbstractPaginatedMenuBuilder previousButtonSlot(Mask previousButtonSlot) {
        return previousButtonSlot(indexFromMask(previousButtonSlot));
    }

    /**
     * Gets the slot index for the next page button.
     *
     * @return next page slot index
     */
    public int getNextButtonSlot() {
        return nextButtonSlot;
    }

    /**
     * Sets the slot index for the next page button.
     *
     * @param nextButtonSlot slot index
     * @return fluent pattern
     */
    public AbstractPaginatedMenuBuilder nextButtonSlot(int nextButtonSlot) {
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
    public AbstractPaginatedMenuBuilder nextButtonSlot(Mask nextButtonSlot) {
        return nextButtonSlot(indexFromMask(nextButtonSlot));
    }

    /* Helper method to get a slot index from a Mask2D */
    private static int indexFromMask(Mask mask) {
        if (mask.getSlots().isEmpty()) {
            return -1;
        }
        return mask.getSlots().iterator().next();
    }

    /**
     * Gets the icon for previous page button in the case that
     * there is no previous page.
     *
     * @return previous page button empty icon
     */
    public ItemStackTemplate getPreviousButton() {
        return previousButton;
    }

    /**
     * Sets the previous button icon.
     *
     * @param item icon
     * @return fluent pattern
     */
    public AbstractPaginatedMenuBuilder previousButton(ItemStack item) {
        return previousButton(new StaticItemTemplate(item));
    }

    /**
     * Sets the previous button icon.
     *
     * @param item icon template
     * @return fluent pattern
     */
    public AbstractPaginatedMenuBuilder previousButton(ItemStackTemplate item) {
        this.previousButton = item;
        return this;
    }

    /**
     * Gets the icon for previous page button in the case that
     * there is no next page.
     *
     * @return previous page button empty icon
     */
    public ItemStackTemplate getPreviousButtonEmpty() {
        return previousButtonEmpty;
    }

    /**
     * Sets the previous button icon to display when there is no previous page.
     *
     * @param item icon
     * @return fluent pattern
     */
    public AbstractPaginatedMenuBuilder previousButtonEmpty(ItemStack item) {
        return previousButtonEmpty(new StaticItemTemplate(item));
    }

    /**
     * Sets the previous button icon to display when there is no previous page.
     *
     * @param item icon template
     * @return fluent pattern
     */
    public AbstractPaginatedMenuBuilder previousButtonEmpty(ItemStackTemplate item) {
        this.previousButtonEmpty = item;
        return this;
    }

    /**
     * Gets the icon for next page button.
     *
     * @return next page button empty icon
     */
    public ItemStackTemplate getNextButton() {
        return nextButton;
    }

    /**
     * Sets the next button icon.
     *
     * @param item icon
     * @return fluent pattern
     */
    public AbstractPaginatedMenuBuilder nextButton(ItemStack item) {
        return nextButton(new StaticItemTemplate(item));
    }

    /**
     * Sets the next button icon.
     *
     * @param item icon template
     * @return fluent pattern
     */
    public AbstractPaginatedMenuBuilder nextButton(ItemStackTemplate item) {
        this.nextButton = item;
        return this;
    }

    /**
     * Gets the icon for next page button in the case that
     * there is no next page.
     *
     * @return next page button empty icon
     */
    public ItemStackTemplate getNextButtonEmpty() {
        return nextButtonEmpty;
    }

    /**
     * Sets the next button icon to display when there is no next page.
     *
     * @param item icon
     * @return fluent pattern
     */
    public AbstractPaginatedMenuBuilder nextButtonEmpty(ItemStack item) {
        return nextButtonEmpty(new StaticItemTemplate(item));
    }

    /**
     * Sets the next button icon to display when there is no next page.
     *
     * @param item icon template
     * @return fluent pattern
     */
    public AbstractPaginatedMenuBuilder nextButtonEmpty(ItemStackTemplate item) {
        this.nextButtonEmpty = item;
        return this;
    }

    /**
     * Internal helper method to link any generated pages.
     *
     * @param pages pages to link
     */
    void linkPages(List<Menu> pages) {
        for (int i = 1 ; i < pages.size() ; i++) {
            Menu page = pages.get(i);
            Menu prev = pages.get(i - 1);
            setPaginationIcon(prev, nextButtonSlot, nextButton, (p, c) -> page.open(p));
            setPaginationIcon(page, previousButtonSlot, previousButton, (p, c) -> prev.open(p));
        }
    }

    /**
     * Internal helper method to set pagination icon with
     * validation checking.
     *
     * @param menu menu to add
     * @param slotIndex slot index
     * @param icon icon to set
     */
    void setPaginationIcon(Menu menu, int slotIndex, ItemStackTemplate icon) {
        setPaginationIcon(menu, slotIndex, icon, null);
    }

    /**
     * Internal helper method to set pagination icon with
     * validation checking.
     *
     * @param menu menu to add
     * @param slotIndex slot index
     * @param icon icon to set
     * @param clickHandler click handler to apply
     */
    void setPaginationIcon(Menu menu, int slotIndex, ItemStackTemplate icon, Slot.ClickHandler clickHandler) {
        if (slotIndex >= 0 && slotIndex < menu.getDimensions().getArea()) {
            Slot slot = menu.getSlot(slotIndex);
            slot.setItemTemplate(icon);
            slot.setClickHandler(clickHandler);
        }
    }
}
