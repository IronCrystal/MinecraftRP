package ironcrystal.minecraftrp;

import ironcrystal.minecraftrp.commands.OccupationalCommands;
import ironcrystal.minecraftrp.event.Listeners;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class MinecraftRP extends JavaPlugin {
	
	public static Economy econ = null;

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
		
		if (!setUpDependencies()) {
			getServer().getPluginManager().disablePlugin(this);
		}
	}

	@Override
	public void onDisable() {

	}

	public MinecraftRP getPlugin() {
		return this;
	}
	
	private boolean setUpDependencies() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "[MinecraftRP] Disabled due to no Vault found!");
            return false;
        }
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[MinecraftRP] Hooked into Vault!");
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
        	Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "[MinecraftRP] Disabled due to no Economy plugin found!");
            return false;
        }
        econ = rsp.getProvider();
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[MinecraftRP] Hooked into Economy Plugin!");
        
        if (getServer().getPluginManager().getPlugin("WorldGuard") == null) {
        	Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "[MinecraftRP] Disabled due to no WorldGuard found!");
        	return false;
        }
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[MinecraftRP] Hooked into WorldGuard!");
        
        if (getServer().getPluginManager().getPlugin("WorldEdit") == null) {
        	Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "[MinecraftRP] Disabled due to no WorldEdit found!");
        	return false;
        }
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[MinecraftRP] Hooked into WorldEdit!");
        return true;
	}
	
	public WorldGuardPlugin getWorldGuard() {
		Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");

		if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
			return null; // Maybe you want throw an exception instead
		}

		return (WorldGuardPlugin) plugin;
	}
}
