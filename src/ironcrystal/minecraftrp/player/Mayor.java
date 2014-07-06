package ironcrystal.minecraftrp.player;

import ironcrystal.minecraftrp.occupations.Occupations;
import ironcrystal.minecraftrp.town.Town;
import ironcrystal.minecraftrp.town.TownManager;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Mayor extends OccupationalPlayer {

	private Town town;

	public Mayor(UUID uuid) {
		super(uuid);
		if (this.getOccupation() != Occupations.MAYOR) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[MinecraftRP] WARNING: A Mayor Object was created even though the player isn't a mayor!");
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[MinecraftRP] WARNING: Errors could occur!");
		}
		this.town = TownManager.getTown(this);
	}

	public Town getTown() {
		return town;
	}

	public void setTown(Town town) {
		this.town = town;
	}
}
