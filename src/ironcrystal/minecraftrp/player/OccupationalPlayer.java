package ironcrystal.minecraftrp.player;

import ironcrystal.minecraftrp.Files;
import ironcrystal.minecraftrp.occupations.Occupation;
import ironcrystal.minecraftrp.occupations.Occupations;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class OccupationalPlayer {
	private Occupations occupation;
	private String playerName;
	private File playerFile;
	private FileConfiguration config;
	
	public OccupationalPlayer(String playerName) {
		this.playerName = playerName;
		this.playerFile = Files.getPlayerFile(playerName);
		config = new YamlConfiguration();
		Files.loadFile(playerFile, config);
		this.occupation = Occupation.getOccupationByString(config.getString("Occupation"));
	}
	
	public String getPlayerName() {
		return playerName;
	}
	
	public Occupations getOccupation() {
		return occupation;
	}
	
	public void setOccupation(Occupations occupation) {
		config.set("Occupation", occupation.toString().toLowerCase());
		Files.saveFile(playerFile, config);
	}
}
