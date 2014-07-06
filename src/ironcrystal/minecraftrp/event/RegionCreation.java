package ironcrystal.minecraftrp.event;

import ironcrystal.minecraftrp.player.OccupationalPlayer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class RegionCreation implements Listener {
	
	@EventHandler
	public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
		if (event.getMessage().startsWith("/region create")) {
			OccupationalPlayer p = new OccupationalPlayer(event.getPlayer().getUniqueId());
			Bukkit.broadcastMessage("Someone is creating a region!");
			Bukkit.broadcastMessage("Their Occupation is " + p.getOccupation().toString());
			if (!p.getOccupation().toString().equalsIgnoreCase("Mayor")) {
				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.RED + "[MinecraftRP] You must be a Mayor to use WorldGuard!");
			}else{
				//if region isn't completely inside town, cancel
				if (!isWithinTown()) {
					event.setCancelled(true);
					event.getPlayer().sendMessage(ChatColor.RED + "[MinecraftRP] You can only claim land within your town!");
				}
			}
		}
	}
	
	/**
	 * TODO
	 * @return whether region is within town
	 */
	private boolean isWithinTown() {
		return true;
	}
}
