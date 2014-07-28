package ironcrystal.minecraftrp.contract;

import ironcrystal.minecraftrp.Files;
import ironcrystal.minecraftrp.player.Shopkeeper;
import ironcrystal.minecraftrp.player.Supplier;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class ContractManager {
	
	public static List<Contract> contractList;
	private static File file;
	private static int nextID;

	private static FileConfiguration fileConfig;
	
	public static void initializeTownList() {
		ContractManager.contractList = new ArrayList<Contract>();
		ContractManager.file = Files.Contracts;
		ContractManager.fileConfig = new YamlConfiguration();
		Files.loadFile(file, fileConfig);
		List<Integer> contracts = fileConfig.getIntegerList("Contracts");
		for (int id : contracts) {
			Contract contract = new Contract(id);
			contractList.add(contract);
		}
		nextID = fileConfig.getInt("Next ID");
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[MinecraftRP] " + ContractManager.contractList.size() + " contracts loaded.");
	}
	
	public static Contract createNewContract(Supplier supplier, Shopkeeper shopkeeper, double price, long timeStarted, long timeLimit, List<ItemStack> items) {
		File contractFile = createContractFile(nextID);
		FileConfiguration fileConfig = new YamlConfiguration();
		Files.loadFile(contractFile, fileConfig);
		if (supplier != null)
		fileConfig.set("Supplier", supplier.getUUID().toString());
		if (shopkeeper != null)
		fileConfig.set("Shopkeeper", shopkeeper.getUUID().toString());
		fileConfig.set("Price", price);
		fileConfig.set("Time Started", timeStarted);
		fileConfig.set("Time Limit", timeLimit);
		List<String[]> itemList = new ArrayList<String[]>();
		for (ItemStack itemStack : items) {
			String[] itemInfo = {itemStack.getType().toString(), itemStack.getAmount() + ""};
			itemList.add(itemInfo);
		}
		fileConfig.set("Items", itemList);
		if (supplier == null || shopkeeper == null || timeStarted == 0) {
			fileConfig.set("State", ContractState.NOTSTARTED.toString());
		}else{
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[MinecraftRP] Somehow a contract was instantly created with a supplier and shopkeeper at time 0");
		}
		Files.saveFile(contractFile, fileConfig);
		Contract contract = new Contract(nextID);
		addContract(contract);
		incrementID();
		return contract;
	}
	
	private static File createContractFile(int id) {
		String path = "plugins/MinecraftRP/contracts/" + id + ".yml";
		File file = new File(path);
		if (!file.exists()) {
			Files.saveFile(file, new YamlConfiguration());
		}
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[MinecraftRP] File for contract " + id + " created succesfully.");
		return file;
	}
	
	private static void incrementID() {
		Files.loadFile(Files.Contracts, fileConfig);
		nextID++;
		fileConfig.set("Next ID", nextID);
	}
	
	private static void addContract(Contract contract) {
		List<Integer> ids = new ArrayList<Integer>();
		if (contractList == null) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[MinecraftRP] For some reason the contract list is null!");
		}
		contractList.add(contract);
		for (Contract contracts : contractList) {
			ids.add(contracts.getId());
		}
		ContractManager.fileConfig.set("Contracts", ids);
		Files.saveFile(ContractManager.file, fileConfig);
	}
	
	/**
	 * Get the unstarted contract of a player
	 * @param UUID uuid
	 * @return Contract if found, or null
	 */
	public static Contract getUnStartedContract(UUID uuid) {
		for (Contract contract: contractList) {
			if (contract.getState() == ContractState.NOTSTARTED) {
				if (contract.getShopkeeper() != null) {
					if (contract.getShopkeeper().getUUID().equals(uuid)) {
						return contract;
					}
				}
				else if (contract.getSupplier() != null) {
					if (contract.getSupplier().getUUID().equals(uuid)) {
						return contract;
					}
				}
			}
		}
		return null;
	}
}
