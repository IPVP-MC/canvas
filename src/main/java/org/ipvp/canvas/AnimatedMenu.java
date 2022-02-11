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

package org.ipvp.canvas;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.ipvp.canvas.helpers.CancellableTimerTask;
import org.ipvp.canvas.helpers.UnmodifiableCollections;

import java.util.Collection;
import java.util.Optional;

@SuppressWarnings("unused")
public class AnimatedMenu<K extends Menu> {

    private static final Plugin plugin = JavaPlugin.getProvidingPlugin(AnimatedMenu.class);

    private Menu parent;

    private final Menu.Builder<?> builder;
    private final double updateInterval;
    private final boolean loop;

    private int index = -1;
    private final Menu[] frames;

    public AnimatedMenu(Menu.Builder<?> builder, int frames, double updateInterval, boolean loop) {
        this.frames = new Menu[frames];
        this.updateInterval = updateInterval;
        this.loop = loop;
        this.builder = builder.redraw(true);
    }

    /**
     * Change the next frame's title
     * <br><br>
     * This behaviour should be used with caution because as soon as this frame is loaded it
     * is going to reset the pointer location of the user
     */
    public K getNextFrame() {
        K menu = (K) builder.build();

        if(index > 0) {
            menu.copyMenu(frames[index]);
        }

        setNextFrame(menu);
        return menu;
    }

    /**
     * The title is going to change only on the player-side
     */
    public K getNextFrame(String title) {
        builder.title(title);
        K menu = getNextFrame();
        return menu;
    }
    public void setNextFrame(K frame) {
        if(index+1 == frames.length) {
            throw new IllegalStateException("All the variables frames are already been used");
        }

        index += 1;
        frames[index] = frame;
    }

    public void open(Player player) {
        if(index == -1) {
            return;
        }

        new CancellableTimerTask(index) {
            private int lastIndex = 0;

            @Override
            protected void onCycle() {
                if((cycles == 0 && lastIndex == 0) || frames[lastIndex].isOpen(player)) {
                    frames[cycles].open(player);
                    lastIndex = cycles;
                } else {
                    task.cancel();
                }
            }

            @Override
            protected void onLastCycle() {
                onCycle();
                if(loop) {
                    resetLoop();
                } else {
                    task.cancel();
                }
            }
            private void resetLoop() {
                cycles = -1;
            }

        }.start(plugin, 0, (long) updateInterval);
    }
    public boolean isOpen(Player viewer) {
        for (Menu frame : frames) {
            if(frame.isOpen(viewer)) {
                return true;
            }
        }
        return false;
    }

    public void close() {
        for(Menu frame : frames) {
            frame.close();
        }
    }
    public void close(Player viewer) throws IllegalStateException {
        for(Menu frame : frames) {
            frame.close(viewer);
        }
    }

    public Collection<Player> getViewers() {
        Collection<Player>[] viewers = new Collection[frames.length];
        for(int i=0; i<frames.length; i++) {
            viewers[i] = frames[i].getViewers();
        }

        return new UnmodifiableCollections<>(viewers);
    }

    public void update() {
        for (Menu frame : frames) {
            frame.update();
        }
    }
    public void update(Player viewer) {
        for(Menu frame : frames) {
            try {
                frame.update(viewer);
            } catch (IllegalStateException ignored) {}
        }
    }

    public Optional<Menu> getParent() {
        return Optional.ofNullable(parent);
    }
    public void setParent(Menu parent) {
        this.parent = parent;
    }

}
