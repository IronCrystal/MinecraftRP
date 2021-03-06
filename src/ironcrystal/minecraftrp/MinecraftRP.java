package ironcrystal.minecraftrp;

import java.io.File;

import ironcrystal.minecraftrp.commands.OccupationalCommands;
import ironcrystal.minecraftrp.contract.ContractManager;
import ironcrystal.minecraftrp.event.Listeners;
import ironcrystal.minecraftrp.town.TownManager;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class MinecraftRP extends JavaPlugin {
	
	public static Economy econ = null;
	public static Permission permission = null;

	@Override
	public void onEnable() {
		Files.initializeFiles();
		Permissions.initializePermissions();
		Listeners.registerEvents(this);
		TownManager.initializeTownList();
		ContractManager.initializeTownList();
		ContractManager.runTimers(this);
		/**
		 * Commands
		 */
		OccupationalCommands commands = new OccupationalCommands(this);
		getCommand("rp").setExecutor(commands);
		
		if (!setUpDependencies()) {
			getServer().getPluginManager().disablePlugin(this);
		}
		/**
		 * If Players are already online, make files if they don't exist
		 */
		for (Player p : Bukkit.getOnlinePlayers()) {
			File file = new File("plugins/MinecraftRP/player/" + p.getUniqueId().toString() + ".yml");
			if (!file.exists()) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[MinecraftRP] New Player Found! Creating player file...");
				FileConfiguration config = new YamlConfiguration();
				config.set("Occupation", "citizen");
				Files.saveFile(file, config);
				Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[MinecraftRP] " + p.getName() + " Player File Created!");
			}
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
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(Permission.class);
        if (permissionProvider == null) {
        	Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "[MinecraftRP] Disabled due to no Permissions plugin found!");
        	return false;
        }
        permission = permissionProvider.getProvider();
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
