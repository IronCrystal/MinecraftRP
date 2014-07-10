package ironcrystal.minecraftrp;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Permissions {
	
	public static void initializePermissions() {
		FileConfiguration config = new YamlConfiguration();
		Files.loadFile(Files.Permissions, config);
		config.set("Permissions.Occupations.rp.placeOccupationSigns", "Have permissions to place occupation signs");
		config.set("Permissions.Occuptions.rp.worlguardbypass", "Bypass Worldguard restrictions");
		Files.saveFile(Files.Permissions, config);
	}
}
