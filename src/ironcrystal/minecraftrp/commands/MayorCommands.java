package ironcrystal.minecraftrp.commands;

import ironcrystal.minecraftrp.Files;
import ironcrystal.minecraftrp.event.MayorClaimLand;
import ironcrystal.minecraftrp.occupations.Occupations;
import ironcrystal.minecraftrp.player.OccupationalPlayer;
import ironcrystal.minecraftrp.town.Town;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class MayorCommands {
	
	public static void claimLand(Player p, OccupationalPlayer player, String name) {
		if (player.getOccupation() == Occupations.MAYOR) {
			File file = Files.getPlayerFile(p.getUniqueId());
			FileConfiguration config = new YamlConfiguration();
			Files.loadFile(file, config);
			config.set("Town", name);
			Files.saveFile(file, config);
			Town.createTownFile(name);
			MayorClaimLand.MayorsClaimingChunks.add(player.getUUID());
			p.sendMessage(ChatColor.GREEN + "[MinecraftRP] Right Click in the four (4) chunks you wish to claim.");
		}else{
			p.sendMessage(ChatColor.RED + "[MinecraftRP] This command is for Mayors only.");
		}
	}
	
	public static String getTown(OccupationalPlayer player) {
		File file = Files.getPlayerFile(player.getUUID());
		FileConfiguration config = new YamlConfiguration();
		Files.loadFile(file, config);
		return config.getString("Town");		
	}
}
