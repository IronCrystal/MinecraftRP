package ironcrystal.minecraftrp.commands;

import ironcrystal.minecraftrp.Files;
import ironcrystal.minecraftrp.MinecraftRP;
import ironcrystal.minecraftrp.event.MayorClaimLand;
import ironcrystal.minecraftrp.player.Mayor;
import ironcrystal.minecraftrp.town.Town;
import ironcrystal.minecraftrp.town.TownManager;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class MayorCommands {

	public static HashMap<UUID, List<Integer>> MayorsConfirmingClaims = new HashMap<UUID, List<Integer>>();
	public static HashMap<UUID, String> MayorTownNames = new HashMap<UUID, String>();

	public static void claimLand(Player p, Mayor mayor, String name) {
		//if (mayor.getOccupation() == Occupations.MAYOR) {
			OfflinePlayer offP = Bukkit.getOfflinePlayer(p.getUniqueId());
			FileConfiguration fileConfig = new YamlConfiguration();
			Files.loadFile(Files.Config, fileConfig);
			double cost = fileConfig.getDouble("Cost of Starting a Village");
			if (MinecraftRP.econ.getBalance(offP) >= cost) {
				MayorTownNames.put(mayor.getUUID(), name);
				MayorClaimLand.MayorsClaimingChunks.add(mayor.getUUID());
				p.sendMessage(ChatColor.GREEN + "[MinecraftRP] Right Click the Center Chunk for the Town");
			}else{
				p.sendMessage(ChatColor.RED + "[MinecraftRP] You don't have enough money!");
				p.sendMessage(ChatColor.RED + "[MinecraftRP] It costs " + cost + " dollars to start a village.");
			}
		//}else{
		//	p.sendMessage(ChatColor.RED + "[MinecraftRP] This command is for Mayors only.");
		//}
	}

	public static void confirmClaim(Player p) {
		if (MayorCommands.MayorsConfirmingClaims.containsKey(p.getUniqueId())) {
			String name = MayorTownNames.get(p.getUniqueId());

			/**
			 * Save Player File Info
			 */
			File pFile = Files.getPlayerFile(p.getUniqueId());
			FileConfiguration config = new YamlConfiguration();
			Files.loadFile(pFile, config);
			config.set("Town", name);
			Files.saveFile(pFile, config);

			/**
			 * Save Town File Info
			 */
			Town town = TownManager.createNewTown(new Mayor(p.getUniqueId()), name, 1, p.getWorld(), MayorsConfirmingClaims.get(p.getUniqueId()));
			//			TownMethods.createTownFile(name);
			//			FileConfiguration fileConfig = new YamlConfiguration();
			//			Files.loadFile(Files.getTownFile(name), fileConfig);
			//			fileConfig.set("Central Chunk", MayorsConfirmingClaims.get(p.getUniqueId()));
			//			fileConfig.set("Radius", 1);
			//			fileConfig.set("World", p.getWorld().getName());
			//			fileConfig.set("Mayor", p.getUniqueId().toString());
			//			Files.saveFile(Files.getTownFile(name), fileConfig);

			/**
			 * Create World Guard Region
			 */
			town.addWorldGuardRegion();
			//TownMethods.addWorldGuard(name);

			/**
			 * Remove Money
			 */
			Files.loadFile(Files.Config, config);
			double cost = config.getDouble("Cost of Starting a Village");
			MinecraftRP.econ.bankWithdraw(p.getName(), cost);

			p.sendMessage(ChatColor.GREEN + "[MinecraftRP] Center Chunk Claimed");
			p.sendMessage(ChatColor.GREEN + "[MinecraftRP] Your town has a 1 chunk radius, for a total of 3x3 chunks.");
			p.sendMessage(ChatColor.BLUE + "[MinecraftRP] " + cost + " has been removed from your account.");
			MayorClaimLand.MayorsClaimingChunks.remove(p.getUniqueId());
			MayorsConfirmingClaims.remove(p.getUniqueId());
			MayorTownNames.remove(p.getUniqueId());
			MayorClaimLand.MayorsClaimingChunks.remove(p.getUniqueId());
			int confirmTaskID = MayorClaimLand.ConfirmClaimTaskForPlayer.get(p.getUniqueId());
			Bukkit.getScheduler().cancelTask(confirmTaskID);
		}else{
			p.sendMessage(ChatColor.RED + "[MinecraftRP] There is no claim to confirm!  Type /rp claim <name> to claim a town.");
		}
	}

	public static void expandTown(Player p, Mayor mayor) {
		OfflinePlayer offP = Bukkit.getOfflinePlayer(p.getUniqueId());
		Town town = TownManager.getTown(mayor);
		int radius = town.getRadius() + 1;
		FileConfiguration config = new YamlConfiguration();
		/**
		 * Check if player has the money
		 */
		Files.loadFile(Files.Config, config);
		double baseCost = config.getDouble("Cost of Expanding a Village Per Chunk");
		int numberOfChunks = radius * 8;
		double cost = baseCost * numberOfChunks;

		if (MinecraftRP.econ.getBalance(offP) >= cost) {
			town.expandTown();
			p.sendMessage(ChatColor.GREEN + "[MinecraftRP] You have expanded your town!");
			radius = town.getRadius();
			int sideLength = 1 + 2 * radius;
			p.sendMessage(ChatColor.GREEN + "[MinecraftRP] Your town has a " + radius +" chunk radius, for a total of " + sideLength + "x" + sideLength + " chunks.");
			p.sendMessage(ChatColor.BLUE + "[MinecraftRP] " + cost + " has been removed from your account.");
		}else{
			p.sendMessage(ChatColor.RED + "[MinecraftRP] You don't have enough money!");
			p.sendMessage(ChatColor.RED + "[MinecraftRP] It costs " + cost + " dollars to expand your village.");
		}
	}
}
