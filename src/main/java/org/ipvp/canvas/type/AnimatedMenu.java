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

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.helpers.CancellableTimerTask;

public class AnimatedMenu<K extends Menu> {

    private static final Plugin plugin = JavaPlugin.getProvidingPlugin(AnimatedMenu.class);

    private final AbstractMenu.Builder<?> builder;
    private final double updateInterval;
    private final boolean loop;

    private int index = -1;
    private final Menu[] frames;

    public AnimatedMenu(AbstractMenu.Builder<?> builder, int frames, double updateInterval, boolean loop) {
        this.frames = new Menu[frames];
        this.updateInterval = updateInterval;
        this.loop = loop;

        builder.redraw(true);
        this.builder = builder;
    }

    public K getNextFrame() {
        K menu = (K) builder.build();
        addFrame(menu);
        return menu;
    }
    public K getNextFrame(String title) {
        String oldTitle = builder.getTitle();
        builder.title(title);
        K menu = getNextFrame();
        builder.title(oldTitle);
        return menu;
    }

    public void addFrame(K frame) {
        index += 1;

        if(index == frames.length) {
            throw new IllegalStateException("All the variables frames are already been used");
        }

        frames[index] = frame;
    }

    public void open(Player player) {
        if(index == -1) {
            return;
        }

        if(loop) {
            openLoop(player);
        } else {
            openOnce(player);
        }
    }
    private void openLoop(Player player) {
        new CancellableTimerTask(index) {
            @Override
            protected void onCycle() {
                if(cycles == -1) {
                    if(frames[totalCycles].isOpen(player)) {
                        cycles = 0;
                    } else {
                        task.cancel();
                        return;
                    }
                }

                if(cycles == 0 || frames[cycles-1].isOpen(player)) {
                    frames[cycles].open(player);
                } else {
                    task.cancel();
                }
            }

            @Override
            protected void onLastCycle() {
                onCycle();
                cycles = -2;
            }
        }.start(plugin, 0, (long) updateInterval);
    }
    private void openOnce(Player player) {
        new CancellableTimerTask(index) {
            @Override
            protected void onCycle() {
                if(cycles == 0 || frames[cycles-1].isOpen(player)) {
                    frames[cycles].open(player);
                } else {
                    task.cancel();
                }
            }
        }.start(plugin, 0, (long) updateInterval);
    }

}
