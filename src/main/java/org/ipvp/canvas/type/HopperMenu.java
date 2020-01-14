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

package org.ipvp.canvas.type;

import org.bukkit.event.inventory.InventoryType;
import org.ipvp.canvas.Menu;

/**
 * A menu backed by a Hopper Inventory.
 */
public class HopperMenu extends AbstractMenu {

    protected HopperMenu(String title, Menu menu, boolean redraw) {
        super(title, InventoryType.HOPPER, menu, redraw);
    }

    /**
     * Returns a new builder. The Menu generated will be backed by a Hopper inventory.
     */
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Dimension getDimensions() {
        return new Dimension(1, 5);
    }

    /**
     * A builder for creating a BoxMenu instance.
     */
    public static class Builder extends AbstractMenu.Builder<Builder> {

        public Builder() {
            super(new Dimension(1, 5));
        }

        @Override
        public HopperMenu build() {
            return new HopperMenu(getTitle(), getParent(), isRedraw());
        }
    }
}
