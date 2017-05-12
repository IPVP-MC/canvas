package org.ipvp.canvas.button;

import org.bukkit.entity.Player;
import org.ipvp.canvas.ClickInformation;

public interface ClickableButton extends Button {
    
    default ClickOptions options() {
        return ClickOptions.ALLOW_ALL;
    }
    
    ClickResult click(Player player, ClickInformation information);
}
