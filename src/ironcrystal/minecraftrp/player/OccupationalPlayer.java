package ironcrystal.minecraftrp.player;

import ironcrystal.minecraftrp.Files;
import ironcrystal.minecraftrp.contract.Contract;
import ironcrystal.minecraftrp.contract.ContractManager;
import ironcrystal.minecraftrp.occupations.Occupation;
import ironcrystal.minecraftrp.occupations.Occupations;
import ironcrystal.minecraftrp.town.Town;
import ironcrystal.minecraftrp.town.TownManager;

import java.io.File;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class OccupationalPlayer {
	private Occupations occupation;
	private UUID uuid;
	private File playerFile;
	private FileConfiguration config;
	private String lastKnownName;
	private String cannotChangeReason;
	
	public OccupationalPlayer(UUID uuid) {
		this.uuid = uuid;
		this.cannotChangeReason = "";
		this.playerFile = Files.getPlayerFile(uuid);
		config = new YamlConfiguration();
		Files.loadFile(playerFile, config);
		this.occupation = Occupation.getOccupationByString(config.getString("Occupation"));
		this.lastKnownName = config.getString("Last Known Name");
	}
	
	public File getFile() {
		return this.playerFile;
	}
	
	/**
	 * Gets the UUID of the OccupationalPlayer
	 * @return UUID
	 */
	public UUID getUUID() {
		return uuid;
	}
	
	public boolean canChangeOccupation() {
		if (getOccupation() == Occupations.MAYOR && TownManager.getTown(new Mayor(uuid)) != null) {
			this.cannotChangeReason = ChatColor.GREEN + "[MinecraftRP] You must leave your town to change occupations!";
			return false;
		}
		if (getOccupation() == Occupations.SUPPLIER || getOccupation() == Occupations.SHOPKEEPER) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "" + ContractManager.inProgressContractList.size());
			for (Contract contract : ContractManager.inProgressContractList) {
				if (contract.getShopkeeper().getUUID().toString().equals(this.getUUID().toString()) || contract.getSupplier().getUUID().toString().equals(this.getUUID().toString())) {
					this.cannotChangeReason = ChatColor.RED + "[MinecraftRP] You must finish your current contracts!";
					return false;
				}
			}
		}
		return true;
	}
	
	public String getCannotChangeReason() {
		return cannotChangeReason;
	}
	
	/**
	 * Gets the Occupation of the OccupationalPlayer
	 * @return Occupation
	 */
	public Occupations getOccupation() {
		return occupation;
	}
	
	public String getName() {
		return Bukkit.getPlayer(uuid).getName();
	}
	
	public Player getPlayer() {
		return Bukkit.getPlayer(uuid);
	}
	
	public void sendOccupationalInfo() {
		Player p = Bukkit.getPlayer(uuid);
		p.sendMessage(ChatColor.GREEN + "[MinecraftRP] Occupation: " + getOccupation().toString());
	}
	
	public void sendTownInfo() {
		Player p = Bukkit.getPlayer(uuid);
		Town town;
		if (getOccupation() == Occupations.MAYOR && TownManager.getTown(new Mayor(uuid)) != null) {
			town = TownManager.getTown(new Mayor(uuid));
			int sideLength = 1 + 2 * town.getRadius();
			p.sendMessage(ChatColor.GREEN + "----------" + town.getName() + "----------");
			p.sendMessage(ChatColor.GREEN + "Mayor: " + town.getMayor().getName());
			p.sendMessage(ChatColor.GREEN + "Size: " + sideLength + "x" + sideLength);
			p.sendMessage(ChatColor.GREEN + "Residents: " + town.getResidents().size());
			p.sendMessage(ChatColor.GREEN + town.getResidents().toString());
		}
		else if (TownManager.getTown(uuid) != null) {
			town = TownManager.getTown(uuid);
			int sideLength = 1 + 2 * town.getRadius();
			p.sendMessage(ChatColor.GREEN + "----------" + town.getName() + "----------");
			p.sendMessage(ChatColor.GREEN + "Mayor: " + town.getMayor().getName());
			p.sendMessage(ChatColor.GREEN + "Size: " + sideLength + "x" + sideLength);
			p.sendMessage(ChatColor.GREEN + "Residents: " + town.getResidents().size());
			p.sendMessage(ChatColor.GREEN + town.getResidents().toString());
		}else{
			p.sendMessage(ChatColor.RED + "[MinecraftRP] You aren't in a town!");
		}
	}
	/**
	 * Sets the Occupation of the OccupationalPlayer
	 * @param new occupation
	 */
	public void setOccupation(Occupations occupation) {
		this.occupation = occupation;
		config.set("Occupation", occupation.toString().toLowerCase());
		Files.saveFile(playerFile, config);
	}

	public String getLastKnownName() {
		return lastKnownName;
	}

	public void setLastKnownName(String lastKnownName) {
		this.lastKnownName = lastKnownName;
	}

	public FileConfiguration getConfig() {
		return config;
	}

	public void setConfig(FileConfiguration config) {
		this.config = config;
	}
}
