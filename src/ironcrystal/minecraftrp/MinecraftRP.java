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
		
		if (!setupEconomy() ) {
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "[MinecraftRP] Disabled due to no Vault found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
	}

	@Override
	public void onDisable() {

	}

	public MinecraftRP getPlugin() {
		return this;
	}
	
	private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

	public WorldGuardPlugin getWorldGuard() {
		Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");

		if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
			return null; // Maybe you want throw an exception instead
		}

		return (WorldGuardPlugin) plugin;
	}
}
