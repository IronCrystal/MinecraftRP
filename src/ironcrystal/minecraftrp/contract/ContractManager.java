package ironcrystal.minecraftrp.contract;

import ironcrystal.minecraftrp.Files;
import ironcrystal.minecraftrp.MinecraftRP;
import ironcrystal.minecraftrp.occupations.Occupations;
import ironcrystal.minecraftrp.player.OccupationalPlayer;
import ironcrystal.minecraftrp.player.Shopkeeper;
import ironcrystal.minecraftrp.player.Supplier;
import ironcrystal.minecraftrp.timer.contract.ContractFinishTimer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class ContractManager {

	public static List<Contract> contractList;
	public static List<Contract> inProgressContractList;
	public static List<UUID> playersPlacingChests = new ArrayList<UUID>();
	private static File file;
	private static int nextID;

	private static FileConfiguration fileConfig;

	public static void initializeTownList() {
		contractList = new ArrayList<Contract>();
		inProgressContractList = new ArrayList<Contract>();
		ContractManager.file = Files.Contracts;
		ContractManager.fileConfig = new YamlConfiguration();
		Files.loadFile(file, fileConfig);
		List<Integer> contracts = fileConfig.getIntegerList("Contracts");
		for (int id : contracts) {
			Contract contract = new Contract(id);
			contractList.add(contract);
			if (contract.getState() == ContractState.INPROGRESS) {
				inProgressContractList.add(contract);
			}
		}
		nextID = fileConfig.getInt("Next ID");
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[MinecraftRP] " + ContractManager.contractList.size() + " contracts loaded.");
	}

	public static Contract createNewContract(Supplier supplier, Shopkeeper shopkeeper, double price, long timeStarted, long timeLimit, List<ItemStack> items) {
		File contractFile = createContractFile(nextID);
		FileConfiguration fileConfig = new YamlConfiguration();
		Files.loadFile(contractFile, fileConfig);
		if (supplier != null) {
			supplier.addContract(nextID);
			fileConfig.set("Supplier", supplier.getUUID().toString());
		}
		if (shopkeeper != null) {
			shopkeeper.addContract(nextID);
			fileConfig.set("Shopkeeper", shopkeeper.getUUID().toString());
		}
		fileConfig.set("Price", price);
		fileConfig.set("Time Started", timeStarted);
		fileConfig.set("Time Limit", timeLimit);
		List<String[]> itemList = new ArrayList<String[]>();
		for (ItemStack itemStack : items) {
			String[] itemInfo = {itemStack.getType().toString(), itemStack.getAmount() + ""};
			itemList.add(itemInfo);
		}
		fileConfig.set("Items", itemList);

		/*List<ItemStack> progressItems = new ArrayList<ItemStack>();
		for (ItemStack itemStack : items) {
			progressItems.add(new ItemStack(itemStack.getType(), 0));
		}*/

		List<String[]> progressList = new ArrayList<String[]>();
		for (ItemStack itemStack : items) {
			String[] itemInfo = {itemStack.getType().toString(), 0 + ""};
			progressList.add(itemInfo);
		}
		fileConfig.set("Item Progress", progressList);
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
		nextID++;
		fileConfig.set("Next ID", nextID);
		Files.saveFile(file, fileConfig);
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
				if (contract.getSupplier() != null) {
					if (contract.getSupplier().getUUID().equals(uuid)) {
						return contract;
					}
				}
			}
		}
		return null;
	}

	public static Contract getContract(int id) {
		for (Contract contract : contractList) {
			if (contract.getId() == id) {
				return contract;
			}
		}
		return null;
	}

	public static ItemStack getCurrentContracts(UUID uuid) {
		OccupationalPlayer occPlayer = new OccupationalPlayer(uuid);
		ItemStack book = new ItemStack(Material.WRITTEN_BOOK, 1);
		ItemMeta itemMeta = book.getItemMeta();
		BookMeta bookMeta = null;
		if (itemMeta instanceof BookMeta) {
			bookMeta = (BookMeta) itemMeta;
		}
		if (occPlayer.getOccupation() == Occupations.SUPPLIER) {
			Supplier supplier = new Supplier(uuid);
			List<Integer> contractIDs = supplier.getContractIDs();
			if (contractIDs.size() == 0) {
				bookMeta.addPage(ChatColor.RED + "You don't have any contracts!");
			}
			for (int id: contractIDs) {
				Contract contract = getContract(id);
				if (contract.getState() == ContractState.NOTSTARTED || contract.getState() == ContractState.INPROGRESS) {
					bookMeta.addPage(contract.getContractPage());
				}
			}
		}
		else if (occPlayer.getOccupation() == Occupations.SHOPKEEPER) {
			Shopkeeper shopkeeper = new Shopkeeper(uuid);
			List<Integer> contractIDs = shopkeeper.getContractIDs();
			if (contractIDs.size() == 0) {
				bookMeta.addPage(ChatColor.RED + "You don't have any contracts!");
			}
			for (int id: contractIDs) {
				Contract contract = getContract(id);
				if (contract.getState() == ContractState.NOTSTARTED || contract.getState() == ContractState.INPROGRESS) {
					bookMeta.addPage(contract.getContractPage());
				}
			}
		}
		bookMeta.setTitle("Contracts");
		book.setItemMeta(bookMeta);
		return book;
	}

	public static ItemStack getContractHistory(UUID uuid) {
		OccupationalPlayer occPlayer = new OccupationalPlayer(uuid);
		ItemStack book = new ItemStack(Material.WRITTEN_BOOK, 1);
		ItemMeta itemMeta = book.getItemMeta();
		BookMeta bookMeta = null;
		if (itemMeta instanceof BookMeta) {
			bookMeta = (BookMeta) itemMeta;
		}
		if (occPlayer.getOccupation() == Occupations.SUPPLIER) {
			Supplier supplier = new Supplier(uuid);
			List<Integer> contractIDs = supplier.getContractIDs();
			if (contractIDs.size() == 0) {
				bookMeta.addPage(ChatColor.RED + "You don't have any contracts!");
			}
			for (int id: contractIDs) {
				Contract contract = getContract(id);
				bookMeta.addPage(contract.getContractPage());
			}
		}
		else if (occPlayer.getOccupation() == Occupations.SHOPKEEPER) {
			Shopkeeper shopkeeper = new Shopkeeper(uuid);
			List<Integer> contractIDs = shopkeeper.getContractIDs();
			if (contractIDs.size() == 0) {
				bookMeta.addPage(ChatColor.RED + "You don't have any contracts!");
			}
			for (int id: contractIDs) {
				bookMeta.addPage(getContract(id).getContractPage());
			}
		}
		bookMeta.setTitle("Contract History");
		book.setItemMeta(bookMeta);
		return book;
	}

	/**
	 * Runs the timer to check for finished contracts every 5 minutes
	 * @param main - MinecraftRP instance
	 */
	public static void runTimers(MinecraftRP main) {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new ContractFinishTimer(), 0L, 6000L);
	}
	
	public static void giveContractPartnersChests(Contract contract) {
		Shopkeeper shop = contract.getShopkeeper();
		Supplier supply = contract.getSupplier();
		
		if (shop != null && supply != null) {
			Player shopPlayer = Bukkit.getPlayer(shop.getUUID());
			Player supplyPlayer = Bukkit.getPlayer(supply.getUUID());
			if (shopPlayer != null && supplyPlayer != null) {
				shopPlayer.getInventory().addItem(new ItemStack(Material.CHEST, 1));
				shopPlayer.sendMessage(ChatColor.GREEN + "[MinecraftRP] Please place the chest down.");
				playersPlacingChests.add(shopPlayer.getUniqueId());
				supplyPlayer.getInventory().addItem(new ItemStack(Material.CHEST, 1));
				supplyPlayer.sendMessage(ChatColor.GREEN + "[MinecraftRP] Please place the chest down.");
				playersPlacingChests.add(supplyPlayer.getUniqueId());
			}else{
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[MinecraftRP] Somehow the players in giveContractPartnersChests() were null!");
			}
		}else{
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[MinecraftRP] Somehow a giveContractPartnerssChests() was called when one of the partners is null!");
		}
	}
	
	public static boolean checkForBothChests(Contract contract) {
		if (contract.getShopChest() != null && contract.getSupplyChest() != null) {
			contract.setState(ContractState.INPROGRESS);
			inProgressContractList.add(contract);
			contract.setTimeStarted(System.currentTimeMillis());
			return true;
		}
		return false;
	}
	
	public static boolean blockIsActiveChest(Block block) {
		for (Contract contract : inProgressContractList) {
			if (contract.getState() == ContractState.INPROGRESS) {
				Block shop = contract.getShopChest();
				Block supply = contract.getSupplyChest();
				if (shop != null && supply != null) {
					if ((shop.getLocation().getBlockX() == block.getLocation().getBlockX() 
							&& shop.getLocation().getBlockY() == block.getLocation().getBlockY()
							&& shop.getLocation().getBlockZ() == block.getLocation().getBlockZ())
						||
							(supply.getLocation().getBlockX() == block.getLocation().getBlockX() 
							&& supply.getLocation().getBlockY() == block.getLocation().getBlockY()
							&& supply.getLocation().getBlockZ() == block.getLocation().getBlockZ())) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static boolean blockIsSupplyChest(Block block) {
		for (Contract contract : inProgressContractList) {
			if (contract.getState() == ContractState.INPROGRESS) {
				Block supply = contract.getSupplyChest();
				if (supply != null) {
					if ((supply.getLocation().getBlockX() == block.getLocation().getBlockX() 
							&& supply.getLocation().getBlockY() == block.getLocation().getBlockY()
							&& supply.getLocation().getBlockZ() == block.getLocation().getBlockZ())) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static Contract getContract(Block block) {
		for (Contract contract : inProgressContractList) {
			if (contract.getState() == ContractState.INPROGRESS) {
				Block shop = contract.getShopChest();
				Block supply = contract.getSupplyChest();
				if (shop != null && supply != null) {
					if ((shop.getLocation().getBlockX() == block.getLocation().getBlockX() 
							&& shop.getLocation().getBlockY() == block.getLocation().getBlockY()
							&& shop.getLocation().getBlockZ() == block.getLocation().getBlockZ())
						||
							(supply.getLocation().getBlockX() == block.getLocation().getBlockX() 
							&& supply.getLocation().getBlockY() == block.getLocation().getBlockY()
							&& supply.getLocation().getBlockZ() == block.getLocation().getBlockZ())) {
						return contract;
					}
				}
			}
		}
		return null;	
	}
	
	public static boolean isContractComplete(Contract contract) {
		List<ContractItem> items = contract.getItems();
		
		for (ContractItem item : items) {
			if (item.getProgress() < item.getTotal()) {
				return false;
			}
		}
		return true;
	}
}
