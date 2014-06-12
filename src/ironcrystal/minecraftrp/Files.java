package ironcrystal.minecraftrp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Files {
		
	public static File Suppliers;	
	public static File ShopKeepers;
	public static File Corporations;
	public static File Permissions;
	
	public static void initializeFiles() {
		Suppliers = new File("plugins/MinecraftRP/Occupations/Suppliers.yml");
		if (!Suppliers.exists()) {
			saveFile(Suppliers, new YamlConfiguration());
		}
		ShopKeepers = new File("plugins/MinecraftRP/Occupations/ShopKeepers.yml");
		if (!ShopKeepers.exists()) {
			saveFile(ShopKeepers, new YamlConfiguration());
		}
		Corporations = new File("plugins/MinecraftRP/Occupations/Corporations.yml");
		if (!Corporations.exists()) {
			saveFile(Corporations, new YamlConfiguration());
		}
		Permissions = new File("plugins/MinecraftRP/Permissions/List of Permissions.yml");
		if (!Permissions.exists()) {
			saveFile(Permissions, new YamlConfiguration());
		}
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[MinecraftRP] Files Initialized");
	}
	
	public static File getPlayerFile(String playerName) {
		char firstLetter = playerName.charAt(0);
		String path = "plugins/MinecraftRP/player/" + firstLetter + "/";
		return new File(path + playerName + ".yml");
	}
	
	public static void loadFile(File file, FileConfiguration fileConfig) {
		try {
			fileConfig.load(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	public static void saveFile(File file, FileConfiguration fileConfig) {
		try {
			fileConfig.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}