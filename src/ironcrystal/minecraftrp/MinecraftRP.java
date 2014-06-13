package ironcrystal.minecraftrp;

import ironcrystal.minecraftrp.commands.OccupationalCommands;
import ironcrystal.minecraftrp.event.Listeners;

import org.bukkit.plugin.java.JavaPlugin;

public class MinecraftRP extends JavaPlugin {
		
	@Override
	public void onEnable() {
		Files.initializeFiles();
		Permissions.initializePermissions();
		Listeners.registerEvents(this);
		
		/**
		 * Commands
		 */
		OccupationalCommands commands = new OccupationalCommands();
		getCommand("score").setExecutor(commands);
		getCommand("rp").setExecutor(commands);
	}
	
	@Override
	public void onDisable() {
		
	}
}
