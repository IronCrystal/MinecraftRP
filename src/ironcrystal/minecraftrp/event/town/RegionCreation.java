package ironcrystal.minecraftrp.event.town;

import ironcrystal.minecraftrp.occupations.Occupations;
import ironcrystal.minecraftrp.player.Mayor;
import ironcrystal.minecraftrp.player.OccupationalPlayer;
import ironcrystal.minecraftrp.town.Town;
import ironcrystal.minecraftrp.town.TownManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class RegionCreation implements Listener {

	@EventHandler
	public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
		if (event.getMessage().startsWith("/region create")) {
			OccupationalPlayer p = new OccupationalPlayer(event.getPlayer().getUniqueId());
			if (!event.getPlayer().hasPermission("rp.worlguardbypass")) {
				if (p.getOccupation() != Occupations.MAYOR || TownManager.getTown(new Mayor(p.getUUID())) == null) {
					event.setCancelled(true);
					event.getPlayer().sendMessage(ChatColor.RED + "[MinecraftRP] You must be the Mayor of a town to use WorldGuard!");
				}else{
					//if region isn't completely inside town, cancel
					if (!isWithinTown(event.getPlayer())) {
						event.setCancelled(true);
						event.getPlayer().sendMessage(ChatColor.RED + "[MinecraftRP] You can only claim land within your town!");
					}else{
						//Player is allowed to use world guard
						Town town = TownManager.getTown(new Mayor(p.getUUID()));
						String[] message = event.getMessage().split(" ");
						if (!message[2].contains(town.getName())) {
							message[2] = town.getName() + "_" + message[2];
							String newMessage = "";
							for (String string: message) {
								newMessage += string;
							}
							event.setMessage(newMessage);
						}
						if (message[2].length() > 15) {
							message[2] = message[2].substring(0, 15);
							String newMessage = "";
							for (String string: message) {
								newMessage += string;
							}
							event.setMessage(newMessage);
						}
					}
				}
			}
		}
	}

	/**
	 * TODO
	 * @return whether region is within town
	 */
	private boolean isWithinTown(Player p) {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
		if (plugin instanceof WorldEditPlugin) {
			WorldEditPlugin wep = (WorldEditPlugin) plugin;
			Selection sel = wep.getSelection(p);
			Location selMaxLoc = sel.getMaximumPoint();
			Location selMinLoc = sel.getMinimumPoint();

			int selMaxLocX = Math.max(selMaxLoc.getBlockX(), selMinLoc.getBlockX());
			int selMaxLocZ = Math.max(selMaxLoc.getBlockZ(), selMinLoc.getBlockZ());

			int selMinLocX = Math.min(selMaxLoc.getBlockX(), selMinLoc.getBlockX());
			int selMinLocZ = Math.min(selMaxLoc.getBlockZ(), selMinLoc.getBlockZ());

			Town town = TownManager.getTown(new Mayor(p.getUniqueId()));

			if (town == null) {
				return false;
			}else{				
				/**
				 * Get opposite corners
				 */
				Location townMaxLoc = town.getTownCorners().get(0);
				Location townMinLoc = town.getTownCorners().get(2);

				int townMaxLocX = townMaxLoc.getBlockX();
				int townMaxLocZ = townMaxLoc.getBlockZ();

				int townMinLocX = townMinLoc.getBlockX();
				int townMinLocZ = townMinLoc.getBlockZ();

				if (townMaxLocX > selMaxLocX && townMaxLocZ > selMaxLocZ && townMinLocX < selMinLocX && townMinLocZ < selMinLocZ) {
					return true;
				}
			}			
		}
		return false;
	}
}
