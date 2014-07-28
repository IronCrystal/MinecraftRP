package ironcrystal.minecraftrp.event;

import ironcrystal.minecraftrp.Files;
import ironcrystal.minecraftrp.player.Mayor;
import ironcrystal.minecraftrp.player.OccupationalPlayer;
import ironcrystal.minecraftrp.town.Town;
import ironcrystal.minecraftrp.town.TownManager;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.thezorro266.bukkit.srm.customevent.PrePlayerClickSignEvent;
import com.thezorro266.bukkit.srm.factories.RegionFactory.Region;
import com.thezorro266.bukkit.srm.factories.SignFactory;

public class PlayerJoinTown implements Listener {
	
	/**
	 * NOT UPDATED FOR UUID
	 */
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerClickSign(PrePlayerClickSignEvent event) {
		Player p = event.getPlayer();
		OccupationalPlayer player = new OccupationalPlayer(p.getUniqueId());
		SignFactory.Sign sign = event.getSign();
		Region region = sign.getRegion();
		String hashString = (String) region.getOptions().get("account");		
		Player primaryOwner = Bukkit.getPlayer(hashString);
		UUID uuid = primaryOwner.getUniqueId();
		if (primaryOwner.isOnline()) {
			Player owner = Bukkit.getPlayer(hashString);
			uuid = owner.getUniqueId();
		}
		Mayor mayor = new Mayor(uuid);
		Town town = TownManager.getTown(mayor);
		FileConfiguration fileConfig = new YamlConfiguration();
		Files.loadFile(player.getFile(), fileConfig);
		fileConfig.set("Resident of", town.getName());
		Files.saveFile(player.getFile(), fileConfig);
		List<String> residents = town.getResidents();
		if (!residents.contains(uuid.toString())) {
			town.addResident(uuid);
			p.sendMessage(ChatColor.GREEN + "[MinecraftRP] You are now a resident of " + town.getName());
		}		
	}
}
