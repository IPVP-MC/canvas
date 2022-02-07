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


package org.ipvp.canvas.helpers;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public abstract class CancellableTimerTask implements Runnable {

    protected final int totalCycles;

    protected BukkitTask task;
    protected int cycles = 0;

    public CancellableTimerTask(int totalCycles) {
        this.totalCycles = totalCycles;
    }

    @Override
    public final void run() {
        if(cycles <= totalCycles) {
            if (totalCycles == cycles) {
                onLastCycle();
            } else {
                onCycle();
            }

            cycles++;
        }
    }

    protected abstract void onCycle();
    protected void onLastCycle() {
        onCycle();
        task.cancel();
    }

    public final void start(Plugin plugin, long delay, long period) {
        task = Bukkit.getScheduler().runTaskTimer(plugin, this, delay, period);
    }
}
