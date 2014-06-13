package ironcrystal.minecraftrp.event;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class CreateOccupationSign implements Listener {
	
	@EventHandler
	public void playerCreateOccupationSign(SignChangeEvent event) {
		Player player = event.getPlayer();
		if (player.hasPermission("rp.placeOccupationSigns")) {
			if (event.getLine(0).equalsIgnoreCase("[MinecraftRP]")) {
				if (event.getLine(2).equalsIgnoreCase("Change Occup")) {
					String str = event.getLine(1);
					if (str.equalsIgnoreCase("mayor") || str.equalsIgnoreCase("supplier")) {
						player.sendMessage(ChatColor.GREEN + "[MinecraftRP] Occupation Change Sign Created for " 
								+ str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase() + "!");
						event.setLine(0, ChatColor.BOLD + "[MinecraftRP]");
						event.setLine(2, ChatColor.BOLD + "Change Occup");
						event.setLine(1, ChatColor.GREEN + str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase());
					}
				}
				
			}
		}
	}

}
