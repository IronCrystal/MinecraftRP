package ironcrystal.minecraftrp.event;

import ironcrystal.minecraftrp.occupations.Occupations;
import ironcrystal.minecraftrp.player.Mayor;
import ironcrystal.minecraftrp.player.OccupationalPlayer;
import ironcrystal.minecraftrp.town.TownManager;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class ChangeOccupation implements Listener {

	@EventHandler
	public void changeOccupation(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		OccupationalPlayer p = new OccupationalPlayer(player.getUniqueId());
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block block = event.getClickedBlock();
			if (block.getType() == Material.SIGN || block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN) {
				Sign sign = (Sign) block.getState();
				if (sign.getLine(0).equalsIgnoreCase(ChatColor.BOLD + "[MinecraftRP]") 
						&& sign.getLine(2).equalsIgnoreCase(ChatColor.BOLD + "Change Occup")) {
					String str = ChatColor.stripColor(sign.getLine(1)).toLowerCase();
					if (p.getOccupation() == Occupations.MAYOR && TownManager.getTown(new Mayor(p.getUUID())) != null) {
						player.sendMessage(ChatColor.RED + "[MinecraftRP] You cannot change occupations while you own a town!");
					}else{
						switch(str) {
						case "mayor": 
							p.setOccupation(Occupations.MAYOR);
							player.sendMessage(ChatColor.GREEN + "[MinecraftRP] Occupation Set: Mayor");
							break;
						case "supplier": 
							p.setOccupation(Occupations.SUPPLIER);
							player.sendMessage(ChatColor.GREEN + "[MinecraftRP] Occupation Set: Supplier");
							break;
						case "citizen": 
							p.setOccupation(Occupations.CITIZEN);
							player.sendMessage(ChatColor.GREEN + "[MinecraftRP] Occupation Set: Citizen");
							break;
						case "shopkeeper": 
							p.setOccupation(Occupations.SHOPKEEPER);
							player.sendMessage(ChatColor.GREEN + "[MinecraftRP] Occupation Set: Shopkeeper");
							break;
						default:
						}
					}
				}
			}
		}
	}
}