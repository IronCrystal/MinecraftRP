package ironcrystal.minecraftrp.player;

import ironcrystal.minecraftrp.Files;
import ironcrystal.minecraftrp.occupations.Occupation;
import ironcrystal.minecraftrp.occupations.Occupations;

import java.io.File;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class OccupationalPlayer {
	protected Occupations occupation;
	private UUID uuid;
	private File playerFile;
	private FileConfiguration config;
	
	public OccupationalPlayer(UUID uuid) {
		this.uuid = uuid;
		this.playerFile = Files.getPlayerFile(uuid);
		config = new YamlConfiguration();
		Files.loadFile(playerFile, config);
		this.occupation = Occupation.getOccupationByString(config.getString("Occupation"));
	}
	
	public File getFile() {
		return this.playerFile;
	}
	
	/**
	 * Gets the UUID of the OccupationalPlayer
	 * @return UUID
	 */
	public UUID getUUID() {
		return uuid;
	}
	
	/**
	 * Gets the Occupation of the OccupationalPlayer
	 * @return Occupation
	 */
	public Occupations getOccupation() {
		return occupation;
	}
	
	public String getName() {
		return Bukkit.getPlayer(uuid).getName();
	}
	
	public Player getPlayer() {
		return Bukkit.getPlayer(uuid);
	}
	
	/**
	 * Sets the Occupation of the OccupationalPlayer
	 * @param new occupation
	 */
	public void setOccupation(Occupations occupation) {
		config.set("Occupation", occupation.toString().toLowerCase());
		Files.saveFile(playerFile, config);
	}
}
