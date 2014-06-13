package ironcrystal.minecraftrp.player;

import ironcrystal.minecraftrp.Files;
import ironcrystal.minecraftrp.occupations.Occupation;
import ironcrystal.minecraftrp.occupations.Occupations;

import java.io.File;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class OccupationalPlayer {
	private Occupations occupation;
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
	
	public UUID getUUID() {
		return uuid;
	}
	
	public Occupations getOccupation() {
		return occupation;
	}
	
	public void setOccupation(Occupations occupation) {
		config.set("Occupation", occupation.toString().toLowerCase());
		Files.saveFile(playerFile, config);
	}
}
