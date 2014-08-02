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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class Contract {

	private Supplier supplier;
	private Shopkeeper shopkeeper;
	private double price;
	private List<ItemStack> items;
	private List<ItemStack> itemProgress;
	private long timeStarted;
	private long timeLimit;
	private int id;
	private ContractState state;
	private Block shopChest;
	private Block supplyChest;

	private File file;
	private FileConfiguration fileConfig;

	@SuppressWarnings("unchecked")
	protected Contract(int id) {
		this.id = id;
		this.items = new ArrayList<ItemStack>();
		this.itemProgress = new ArrayList<ItemStack>();
		this.file = Files.getContractFile(id);
		if (!this.file.exists()) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[MinecraftRP] WARNING: A contract object was created but the file has not been created!");
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[MinecraftRP] WARNING: Something is wrong!");
		}
		this.fileConfig = new YamlConfiguration();
		Files.loadFile(file, fileConfig);
		this.state = ContractState.getContractStateByString(fileConfig.getString("State"));
		if (fileConfig.getString("Supplier") != null) {
			UUID supplierUUID = UUID.fromString(fileConfig.getString("Supplier"));
			this.supplier = new Supplier(supplierUUID);
		}else{
			this.supplier = null;
			this.state = ContractState.NOTSTARTED;
		}
		if (fileConfig.getString("Shopkeeper") != null) {
			UUID shopkeeperUUID = UUID.fromString(fileConfig.getString("Shopkeeper"));
			this.shopkeeper = new Shopkeeper(shopkeeperUUID);
		}else{
			this.shopkeeper = null;
			this.state = ContractState.NOTSTARTED;
		}
		this.price = fileConfig.getDouble("Price");
		this.timeStarted = fileConfig.getLong("Time Started");
		if (this.timeStarted == 0) {
			this.state = ContractState.NOTSTARTED;
		}
		this.timeLimit = fileConfig.getLong("Time Limit");
		if (fileConfig.getList("Items") != null) {
			List<List<String>> itemList = (List<List<String>>) fileConfig.getList("Items");
			for (List<String> stringArray : itemList) {
				String itemName = stringArray.get(0);
				String stringAmount = stringArray.get(1);
				int amount = Integer.parseInt(stringAmount);
				ItemStack item = new ItemStack(Material.getMaterial(itemName), amount);
				items.add(item);
			}
		}
		if (fileConfig.getList("Item Progress") != null) {
			List<List<String>> itemList = (List<List<String>>) fileConfig.getList("Item Progress");
			for (List<String> stringArray : itemList) {
				String itemName = stringArray.get(0);
				String stringAmount = stringArray.get(1);
				int amount = Integer.parseInt(stringAmount);
				ItemStack item = new ItemStack(Material.getMaterial(itemName), amount);
				itemProgress.add(item);
			}
		}
		if (fileConfig.getIntegerList("Shop Chest") != null && fileConfig.getString("Shop Chest World") != null) {
			List<Integer> shopBlockLoc = fileConfig.getIntegerList("Shop Chest");
			String worldName = fileConfig.getString("Shop Chest World");
			Location loc = new Location(Bukkit.getWorld(worldName), shopBlockLoc.get(0), shopBlockLoc.get(1), shopBlockLoc.get(2));
			this.shopChest = loc.getBlock();
		}else{
			this.shopChest = null;
		}
		if (fileConfig.getIntegerList("Supply Chest") != null && fileConfig.getString("Supply Chest World") != null) {
			List<Integer> supplyBlockLoc = fileConfig.getIntegerList("Supply Chest");
			String worldName = fileConfig.getString("Supply Chest World");
			Location loc = new Location(Bukkit.getWorld(worldName), supplyBlockLoc.get(0), supplyBlockLoc.get(1), supplyBlockLoc.get(2));
			this.supplyChest = loc.getBlock();
		}else{
			this.supplyChest = null;
		}
	}

	public ItemStack getBook() {
		ItemStack item = new ItemStack(Material.WRITTEN_BOOK, 1);
		ItemMeta itemMeta = item.getItemMeta();
		if (itemMeta instanceof BookMeta) {
			BookMeta meta = (BookMeta) itemMeta;
			meta.setTitle("Contract");
			meta.setAuthor("");
			String newPage = getContractPage();
			meta.addPage(newPage);
			item.setItemMeta(meta);
			return item;
		}
		return item;
	}

	public String getContractPage() {
		String newPage = "";
		newPage = newPage + ChatColor.BLUE + getState().toString() + "\n";
		if (getShopkeeper() != null) {
			newPage = newPage + ChatColor.BLUE + "Shop: " + ChatColor.BLACK + getShopkeeper().getLastKnownName() + "\n";
		}
		if (getSupplier() != null) {
			newPage = newPage + ChatColor.BLUE + "Supply: " + ChatColor.BLACK + getSupplier().getLastKnownName() + "\n";
		}
		newPage = newPage + ChatColor.BLUE + "Price: " + ChatColor.BLACK + price + ChatColor.BLUE + "\nTime: " + ChatColor.BLACK
				+ getTimeString(timeLimit) + ChatColor.BLUE + "\nItems:\n" + ChatColor.BLACK;
		for (int i = 0; i < items.size(); i++) {
			ChatColor color = null;
			int progress = itemProgress.get(i).getAmount();
			int total = items.get(i).getAmount();
			double percent = (progress * 1.0 )/ total;
			if (percent < 0.5) {
				color = ChatColor.RED;
			}
			else if (percent < 0.75) {
				color = ChatColor.GOLD;
			}else{
				color = ChatColor.GREEN;
			}
			newPage = newPage + items.get(i).getType().toString() + " x" + color + itemProgress.get(i).getAmount() + "/" + items.get(i).getAmount() + ChatColor.BLACK;
			if (i != items.size() - 1) {
				newPage = newPage + "\n";
			}
		}
		return newPage;
	}

	public void addItemProgress(ItemStack item) {
		for (ItemStack i : itemProgress) {
			i.setAmount(item.getAmount());
			List<String[]> itemList = new ArrayList<String[]>();
			for (ItemStack itemStack : itemProgress) {
				String[] itemInfo = {itemStack.getType().toString(), itemStack.getAmount() + ""};
				itemList.add(itemInfo);
			}
			fileConfig.set("Item Progress", itemList);
		}
	}

	public Long getTimeLeft() {
		if (getTimeStarted() != 0) {
			return timeLimit - (System.currentTimeMillis() - getTimeStarted());
		}
		return timeLimit;
	}

	public String getTimeString(Long time) {
		if (time / 86400000 > 0) {
			return time / 86400000 + " days";
		}
		else if (time / 3600000 > 0) {
			return time / 3600000 + " hours";
		}
		return "";
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
		fileConfig.set("Supplier", getSupplier().getUUID().toString());
		Files.saveFile(file, fileConfig);
	}

	public Shopkeeper getShopkeeper() {
		return shopkeeper;
	}

	public void setShopkeeper(Shopkeeper shopkeeper) {
		this.shopkeeper = shopkeeper;
		fileConfig.set("Shopkeeper", getShopkeeper().getUUID().toString());
		Files.saveFile(file, fileConfig);
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
		fileConfig.set("Price", getPrice());
		Files.saveFile(file, fileConfig);
	}

	public List<ItemStack> getItems() {
		return items;
	}

	public void setItems(List<ItemStack> items) {
		this.items = items;
	}

	public long getTimeStarted() {
		return timeStarted;
	}

	public void setTimeStarted(long timeStarted) {
		this.timeStarted = timeStarted;
		fileConfig.set("Time Started", getTimeStarted());
		Files.saveFile(file, fileConfig);
	}

	public long getTimeLimit() {
		return timeLimit;
	}

	public void setTimeLimit(long timeLimit) {
		this.timeLimit = timeLimit;
		fileConfig.set("Time Limit", getTimeLimit());
		Files.saveFile(file, fileConfig);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ContractState getState() {
		return state;
	}

	public void setState(ContractState state) {
		this.state = state;
		fileConfig.set("State", getState().toString());
		Files.saveFile(file, fileConfig);
	}

	public Block getShopChest() {
		return shopChest;
	}

	public void setShopChest(Block shopChest) {
		this.shopChest = shopChest;
		Location loc = shopChest.getLocation();
		List<Integer> coords = new ArrayList<Integer>();
		coords.add(loc.getBlockX());
		coords.add(loc.getBlockY());
		coords.add(loc.getBlockZ());
		fileConfig.set("Shop Chest", coords);
		fileConfig.set("Shop Chest World", loc.getWorld().getName());
		Files.saveFile(file, fileConfig);
	}

	public Block getSupplyChest() {
		return supplyChest;
	}

	public void setSupplyChest(Block supplyChest) {
		this.supplyChest = supplyChest;
		Location loc = supplyChest.getLocation();
		List<Integer> coords = new ArrayList<Integer>();
		coords.add(loc.getBlockX());
		coords.add(loc.getBlockY());
		coords.add(loc.getBlockZ());
		fileConfig.set("Supply Chest", coords);
		fileConfig.set("Supply Chest World", loc.getWorld().getName());
		Files.saveFile(file, fileConfig);
	}
	
	public File getFile() {
		return file;
	}
	
	public FileConfiguration getFileConfig() {
		return fileConfig;
	}
}
