package ironcrystal.minecraftrp.event.town;

import ironcrystal.minecraftrp.Files;

import java.io.File;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.thezorro266.bukkit.srm.customevent.RegionExpireEvent;

public class PlayerLeaveTown implements Listener {
	
	@EventHandler
	public void onRegionExpire(RegionExpireEvent event) {
		OfflinePlayer player = event.getCustomer();
		UUID uuid = player.getUniqueId();
		FileConfiguration fileConfig = new YamlConfiguration();
		File file = Files.getPlayerFile(uuid);
		Files.loadFile(file, fileConfig);
		fileConfig.set("Resident of", null);
		Files.saveFile(file, fileConfig);
	}
}
