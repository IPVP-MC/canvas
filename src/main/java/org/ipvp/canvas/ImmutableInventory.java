package org.ipvp.canvas;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class ImmutableInventory implements Inventory {

    private Inventory inventory;

    public ImmutableInventory(Inventory inventory) {
        Objects.requireNonNull(inventory);
        this.inventory = inventory;
    }
    
    @Override
    public int getSize() {
        return inventory.getSize();
    }

    @Override
    public int getMaxStackSize() {
        return inventory.getMaxStackSize();
    }

    @Override
    public void setMaxStackSize(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
        return inventory.getName();
    }

    @Override
    public ItemStack getItem(int i) {
        ItemStack itemStack = inventory.getItem(i);
        return itemStack == null ? null : new ItemStack(itemStack);
    }

    @Override
    public void setItem(int i, ItemStack itemStack) {
        throw new UnsupportedOperationException();
    }

    @Override
    public HashMap<Integer, ItemStack> addItem(ItemStack... itemStacks) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public HashMap<Integer, ItemStack> removeItem(ItemStack... itemStacks) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public ItemStack[] getContents() {
        ItemStack[] original = inventory.getContents();
        ItemStack[] deepCopy = new ItemStack[original.length];
        for (int i = 0 ; i < original.length ; i++) {
            deepCopy[i] = original[i] == null ? null : new ItemStack(original[i]);
        }
        return deepCopy;
    }

    @Override
    public void setContents(ItemStack[] itemStacks) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(int i) {
        return inventory.contains(i);
    }

    @Override
    public boolean contains(Material material) throws IllegalArgumentException {
        return inventory.contains(material);
    }

    @Override
    public boolean contains(ItemStack itemStack) {
        return inventory.contains(itemStack);
    }

    @Override
    public boolean contains(int i, int i1) {
        return inventory.contains(i, i1);
    }

    @Override
    public boolean contains(Material material, int i) throws IllegalArgumentException {
        return inventory.contains(material, i);
    }

    @Override
    public boolean contains(ItemStack itemStack, int i) {
        return inventory.contains(itemStack, i);
    }

    @Override
    public boolean containsAtLeast(ItemStack itemStack, int i) {
        return inventory.containsAtLeast(itemStack, i);
    }

    @Override
    public HashMap<Integer, ? extends ItemStack> all(int i) {
        return clone(inventory.all(i));
    }

    @Override
    public HashMap<Integer, ? extends ItemStack> all(Material material) throws IllegalArgumentException {
        return clone(inventory.all(material));
    }

    @Override
    public HashMap<Integer, ? extends ItemStack> all(ItemStack itemStack) {
        return clone(inventory.all(itemStack));
    }
    
    private HashMap<Integer, ? extends ItemStack> clone(Map<Integer, ? extends ItemStack> map) {
        HashMap<Integer, ItemStack> deep = new HashMap<>(map.size());
        map.forEach((s, i) -> deep.put(s, i == null ? null : new ItemStack(i)));
        return deep;
    }

    @Override
    public int first(int i) {
        return inventory.first(i);
    }

    @Override
    public int first(Material material) throws IllegalArgumentException {
        return inventory.first(material);
    }

    @Override
    public int first(ItemStack itemStack) {
        return inventory.first(itemStack);
    }

    @Override
    public int firstEmpty() {
        return inventory.firstEmpty();
    }

    @Override
    public void remove(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(Material material) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(ItemStack itemStack) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<HumanEntity> getViewers() {
        return inventory.getViewers();
    }

    @Override
    public String getTitle() {
        return inventory.getTitle();
    }

    @Override
    public InventoryType getType() {
        return inventory.getType();
    }

    @Override
    public InventoryHolder getHolder() {
        return inventory.getHolder();
    }

    @Override
    public ListIterator<ItemStack> iterator() {
        return new ImmutableWrappedCloningListIterator(inventory.iterator());
    }

    @Override
    public ListIterator<ItemStack> iterator(int i) {
        return new ImmutableWrappedCloningListIterator(inventory.iterator(i));
    }
    
    private class ImmutableWrappedCloningListIterator implements ListIterator<ItemStack> {

        private ListIterator<ItemStack> handle;
        
        public ImmutableWrappedCloningListIterator(ListIterator<ItemStack> handle) {
            this.handle = handle;
        }

        @Override
        public boolean hasNext() {
            return handle.hasNext();
        }

        @Override
        public ItemStack next() {
            ItemStack next = handle.next();
            return next == null ? null : new ItemStack(next);
        }

        @Override
        public boolean hasPrevious() {
            return handle.hasPrevious();
        }

        @Override
        public ItemStack previous() {
            ItemStack prev = handle.previous();
            return prev == null ? null : new ItemStack(prev);
        }

        @Override
        public int nextIndex() {
            return handle.nextIndex();
        }

        @Override
        public int previousIndex() {
            return handle.previousIndex();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void set(ItemStack itemStack) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(ItemStack itemStack) {
            throw new UnsupportedOperationException();
        }
    }
}
