package org.ipvp.canvas;

import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.ipvp.canvas.button.Button;

public interface Menu extends InventoryHolder {

    Optional<Menu> getParent();
    
    void open(Player viewer);
    
    void close(Player viewer);
    
    Optional<Button> getButton(int index);
    
    void addButton(int index, Button button);
    
    Optional<Button> removeButton(int index);
    
    void clear();
    
    void clear(int slot);

    @Override
    ImmutableInventory getInventory();
    
    interface Builder {
        
        Builder title(String title);
        
        Builder parent(Menu parent);
        
        Menu build();
    }
}
