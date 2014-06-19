package ironcrystal.minecraftrp;

import ironcrystal.minecraftrp.commands.OccupationalCommands;
import ironcrystal.minecraftrp.event.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

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
	
	public MinecraftRP getPlugin() {
		return this;
	}
	
	public WorldGuardPlugin getWorldGuard() {
	    Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
	 
	    if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
	        return null; // Maybe you want throw an exception instead
	    }
	 
	    return (WorldGuardPlugin) plugin;
	}
}
