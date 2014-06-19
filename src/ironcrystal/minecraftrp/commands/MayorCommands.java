package ironcrystal.minecraftrp.commands;

import ironcrystal.minecraftrp.Files;
import ironcrystal.minecraftrp.event.MayorClaimLand;
import ironcrystal.minecraftrp.occupations.Occupations;
import ironcrystal.minecraftrp.player.OccupationalPlayer;
import ironcrystal.minecraftrp.town.Town;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class MayorCommands {
	
	public static HashMap<UUID, Integer[]> MayorsConfirmingClaims = new HashMap<UUID, Integer[]>();
	
	public static void claimLand(Player p, OccupationalPlayer player, String name) {
		if (player.getOccupation() == Occupations.MAYOR) {
			File file = Files.getPlayerFile(p.getUniqueId());
			FileConfiguration config = new YamlConfiguration();
			Files.loadFile(file, config);
			config.set("Town", name);
			Files.saveFile(file, config);
			Town.createTownFile(name);
			MayorClaimLand.MayorsClaimingChunks.add(player.getUUID());
			p.sendMessage(ChatColor.GREEN + "[MinecraftRP] Right Click the Center Chunk for the Town");
		}else{
			p.sendMessage(ChatColor.RED + "[MinecraftRP] This command is for Mayors only.");
		}
	}
	
	public static void confirmClaim(Player p) {
		if (MayorCommands.MayorsConfirmingClaims.containsKey(p.getUniqueId())) {
			OccupationalPlayer player = new OccupationalPlayer(p.getUniqueId());
			File townFile = Files.getTownFile(MayorCommands.getTown(player));
			FileConfiguration config = new YamlConfiguration();
			Files.loadFile(townFile, config);
			config.set("Central Chunk", MayorsConfirmingClaims.get(player.getUUID()));
			config.set("Radius", 1);
			Files.saveFile(townFile, config);
			p.sendMessage(ChatColor.GREEN + "[MinecraftRP] Center Chunk Claimed");
			p.sendMessage(ChatColor.GREEN + "[MinecraftRP] Your town has a 1 chunk radius, for a total of 3x3 chunks.");
			MayorClaimLand.MayorsClaimingChunks.remove(p.getUniqueId());
			MayorsConfirmingClaims.remove(player.getUUID());
			//Bukkit.getScheduler().cancelTask(MayorClaimLand.TasksForPlayer.get(p.getUniqueId()));
			//Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("MinecraftRP"), new MayorClaimChunkVisualTimer(p));
		}else{
			p.sendMessage(ChatColor.RED + "[MinecraftRP] There is no claim to confirm!  Type /rp claim <name> to claim a town.");
		}
	}
	
	public static String getTown(OccupationalPlayer player) {
		File file = Files.getPlayerFile(player.getUUID());
		FileConfiguration config = new YamlConfiguration();
		Files.loadFile(file, config);
		return config.getString("Town");		
	}
}
