package ironcrystal.minecraftrp.event;

import ironcrystal.minecraftrp.MinecraftRP;
import ironcrystal.minecraftrp.occupations.Occupations;
import ironcrystal.minecraftrp.player.OccupationalPlayer;

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
					if (p.canChangeOccupation()) {
						switch(str) {
						case "mayor": 
							p.setOccupation(Occupations.MAYOR);
							player.sendMessage(ChatColor.GREEN + "[MinecraftRP] Occupation Set: Mayor");
							takeSupplierPermissions(player);
							takeShopkeeperPermissions(player);
							break;
						case "supplier": 
							p.setOccupation(Occupations.SUPPLIER);
							player.sendMessage(ChatColor.GREEN + "[MinecraftRP] Occupation Set: Supplier");
							giveSupplierPermissions(player);
							takeShopkeeperPermissions(player);
							break;
						case "citizen": 
							p.setOccupation(Occupations.CITIZEN);
							player.sendMessage(ChatColor.GREEN + "[MinecraftRP] Occupation Set: Citizen");
							takeSupplierPermissions(player);
							takeShopkeeperPermissions(player);
							break;
						case "shopkeeper": 
							p.setOccupation(Occupations.SHOPKEEPER);
							player.sendMessage(ChatColor.GREEN + "[MinecraftRP] Occupation Set: Shopkeeper");
							takeSupplierPermissions(player);
							giveShopkeeperPermissions(player);
							break;
						case "construction": 
							p.setOccupation(Occupations.CONSTRUCTION_WORKER);
							player.sendMessage(ChatColor.GREEN + "[MinecraftRP] Occupation Set: Construction Worker");
							takeSupplierPermissions(player);
							takeShopkeeperPermissions(player);
							break;
						default:
						}
					}else{
						player.sendMessage(p.getCannotChangeReason());
					}
				}
			}
		}
	}

	private void giveSupplierPermissions(Player p) {
		//Skills
		MinecraftRP.permission.playerAdd(p, "mcmmo.skills.excavation");
		MinecraftRP.permission.playerAdd(p, "mcmmo.skills.fishing");
		MinecraftRP.permission.playerAdd(p, "mcmmo.skills.herbalism");
		MinecraftRP.permission.playerAdd(p, "mcmmo.skills.mining");
		MinecraftRP.permission.playerAdd(p, "mcmmo.skills.woodcutting");

		//Commands
		MinecraftRP.permission.playerAdd(p, "mcmmo.commands.excavation");
		MinecraftRP.permission.playerAdd(p, "mcmmo.commands.fishing");
		MinecraftRP.permission.playerAdd(p, "mcmmo.commands.herbalism");
		MinecraftRP.permission.playerAdd(p, "mcmmo.commands.mcmmo.help");
		MinecraftRP.permission.playerAdd(p, "mcmmo.commands.mcrank");
		MinecraftRP.permission.playerAdd(p, "mcmmo.commands.mcstats");
		MinecraftRP.permission.playerAdd(p, "mcmmo.commands.mctop");
		MinecraftRP.permission.playerAdd(p, "mcmmo.commands.mctop.excavation");
		MinecraftRP.permission.playerAdd(p, "mcmmo.commands.mctop.fishing");
		MinecraftRP.permission.playerAdd(p, "mcmmo.commands.mctop.herbalism");
		MinecraftRP.permission.playerAdd(p, "mcmmo.commands.mctop.mining");
		MinecraftRP.permission.playerAdd(p, "mcmmo.commands.mctop.woodcutting");
		MinecraftRP.permission.playerAdd(p, "mcmmo.commands.mining");
		MinecraftRP.permission.playerAdd(p, "mcmmo.commands.woodcutting");
	}

	private void takeSupplierPermissions(Player p) {
		//Skills
		MinecraftRP.permission.playerRemove(p, "mcmmo.skills.excavation");
		MinecraftRP.permission.playerRemove(p, "mcmmo.skills.fishing");
		MinecraftRP.permission.playerRemove(p, "mcmmo.skills.herbalism");
		MinecraftRP.permission.playerRemove(p, "mcmmo.skills.mining");
		MinecraftRP.permission.playerRemove(p, "mcmmo.skills.woodcutting");

		//Commands
		MinecraftRP.permission.playerRemove(p, "mcmmo.commands.excavation");
		MinecraftRP.permission.playerRemove(p, "mcmmo.commands.fishing");
		MinecraftRP.permission.playerRemove(p, "mcmmo.commands.herbalism");
		MinecraftRP.permission.playerRemove(p, "mcmmo.commands.mcmmo.help");
		MinecraftRP.permission.playerRemove(p, "mcmmo.commands.mcrank");
		MinecraftRP.permission.playerRemove(p, "mcmmo.commands.mcstats");
		MinecraftRP.permission.playerRemove(p, "mcmmo.commands.mctop");
		MinecraftRP.permission.playerRemove(p, "mcmmo.commands.mctop.excavation");
		MinecraftRP.permission.playerRemove(p, "mcmmo.commands.mctop.fishing");
		MinecraftRP.permission.playerRemove(p, "mcmmo.commands.mctop.herbalism");
		MinecraftRP.permission.playerRemove(p, "mcmmo.commands.mctop.mining");
		MinecraftRP.permission.playerRemove(p, "mcmmo.commands.mctop.woodcutting");
		MinecraftRP.permission.playerRemove(p, "mcmmo.commands.mining");
		MinecraftRP.permission.playerRemove(p, "mcmmo.commands.woodcutting");
	}
	
	private void giveShopkeeperPermissions(Player p) {
		MinecraftRP.permission.playerAdd(p, "quickshop.create.sell");
		MinecraftRP.permission.playerAdd(p, "quickshop.create.buy");
		MinecraftRP.permission.playerAdd(p, "quickshop.create.double");
		MinecraftRP.permission.playerAdd(p, "quickshop.create.changeprice");
	}

	private void takeShopkeeperPermissions(Player p) {
		MinecraftRP.permission.playerRemove(p, "quickshop.create.sell");
		MinecraftRP.permission.playerRemove(p, "quickshop.create.buy");
		MinecraftRP.permission.playerRemove(p, "quickshop.create.double");
		MinecraftRP.permission.playerRemove(p, "quickshop.create.changeprice");
	}
}