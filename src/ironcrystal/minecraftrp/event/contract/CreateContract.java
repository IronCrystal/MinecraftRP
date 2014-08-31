package ironcrystal.minecraftrp.event.contract;

import ironcrystal.minecraftrp.MinecraftRP;
import ironcrystal.minecraftrp.contract.Contract;
import ironcrystal.minecraftrp.contract.ContractManager;
import ironcrystal.minecraftrp.occupations.Occupations;
import ironcrystal.minecraftrp.player.OccupationalPlayer;
import ironcrystal.minecraftrp.player.Shopkeeper;
import ironcrystal.minecraftrp.player.Supplier;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class CreateContract implements Listener {

	private int price;
	private String durationString;
	private long duration;
	private List<ItemStack> items;

	private boolean goodPrice;
	private boolean goodDuration;
	private boolean goodItems;
	private boolean pageLength;

	@EventHandler
	public void playerCreateContract(PlayerEditBookEvent event) {
		final Player player = event.getPlayer();
		BookMeta meta = event.getNewBookMeta();
		if (meta.hasTitle()) {
			if (meta.getTitle().equalsIgnoreCase("contract")) {
				UUID uuid = player.getUniqueId();
				OccupationalPlayer occPlayer = new OccupationalPlayer(uuid);
				if (occPlayer.getOccupation() == Occupations.SHOPKEEPER || occPlayer.getOccupation() == Occupations.SUPPLIER) {
					boolean isContract = isContract(meta, player);
					if (isContract) {
						String newPage = "Price: " + price + "\nTime: " + durationString + "\nItems:\n";
						for (int i = 0; i < items.size(); i++) {
							newPage = newPage + items.get(i).getType().toString() + " x" + items.get(i).getAmount();
							if (i != items.size() - 1) {
								newPage = newPage + "\n";
							}
						}
						
						if (occPlayer.getOccupation() == Occupations.SUPPLIER) {
							Contract contract = ContractManager.createNewContract(new Supplier(uuid), null, price, 0, duration, items);
							player.sendMessage(ChatColor.GREEN + "[MinecraftRP] Contract created succesfully!");
							List<String> page = new ArrayList<String>();
							page.add(contract.getContractPage());
							meta.setPages(page);
							meta.setTitle("Contract");
							meta.setAuthor(player.getName());
							event.setNewBookMeta(meta);
						}
						else if (occPlayer.getOccupation() == Occupations.SHOPKEEPER) {
							OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(occPlayer.getUUID());
							if (MinecraftRP.econ.getBalance(offPlayer) >= price) {
								Contract contract = ContractManager.createNewContract(null, new Shopkeeper(uuid), price, 0, duration, items);
								player.sendMessage(ChatColor.GREEN + "[MinecraftRP] Contract created succesfully!");
								List<String> page = new ArrayList<String>();
								page.add(contract.getContractPage());
								meta.setPages(page);
								meta.setTitle("Contract");
								meta.setAuthor(player.getName());
								event.setNewBookMeta(meta);
							}else{
								player.sendMessage(ChatColor.RED + "[MinecraftRP] You don't have enough money for that contract!");
								event.setCancelled(true);
							}
						}						
					}else{
						player.sendMessage(ChatColor.RED + "[MinecraftRP] Contract not created succesfully!");
						if (!pageLength) {
							player.sendMessage(ChatColor.RED + "[MinecraftRP] Did not write on the second page!");
						}
						if (!goodPrice) {
							player.sendMessage(ChatColor.RED + "[MinecraftRP] Could not determine price!");
						}
						if (!goodDuration) {
							player.sendMessage(ChatColor.RED + "[MinecraftRP] Could not determine time! Must be in h (hour) or d (day)!");
						}
						if (!goodItems) {
							player.sendMessage(ChatColor.RED + "[MinecraftRP] Could not determine one or more items!");
						}
						event.setCancelled(true);
					}
				}else{
					player.sendMessage(ChatColor.RED + "[MinecraftRP] Only shopkeepers and suppliers can make contracts!");
				}
			}
		}		
	}

	private boolean isContract(BookMeta meta, Player player) {
		goodPrice = false;
		goodDuration = false;
		goodItems = false;
		pageLength = false;
		items = new ArrayList<ItemStack>();
		if (meta.getPageCount() > 1) {
			pageLength = true;
			String page = meta.getPage(2);

			boolean passedItemLine = false;
			String[] lines = page.split("\n");
			for (String line : lines) {
				if (line.startsWith("Price:") || line.startsWith("price:")) {
					line = line.replaceFirst("Price:", "");
					line = line.replaceFirst("price:", "");
					if (line.contains("$")) {
						line = line.replace("$", "");
					}
					line = line.trim();
					price = Integer.parseInt(line);
					goodPrice = true;
				}
				else if (line.startsWith("Time:") || line.startsWith("time:")) {
					line = line.replaceFirst("Time:", "");
					line = line.replaceFirst("time:", "");
					if (line.contains("h")) {
						line = line.replace("h", "");
						line = line.trim();
						durationString = Long.parseLong(line) + " hours";
						duration = Long.parseLong(line) * 3600000;
						goodDuration = true;
					}
					else if (line.contains("d")) {
						line = line.replace("d", "");
						line = line.trim();
						durationString = Long.parseLong(line) + " days";
						duration = Long.parseLong(line) * 86400000;
						goodDuration = true;
					}
				}
				else if (line.startsWith("Items:") || line.startsWith("items:")) {
					passedItemLine = true;
					continue;
				}
				else if (passedItemLine) {
					if (items.size() < 7) {
						line = line.trim();
						String[] item = line.split(" ");
						String itemName = item[0];
						if (item.length > 2) {
							for (int x = 1; x < item.length - 1; x++) {
								itemName = itemName.concat("_" + item[x]);
							}
						}
						String itemAmount = item[item.length - 1];
						Material material = Material.matchMaterial(itemName);
						if (itemAmount.contains("x")) {
							itemAmount = itemAmount.replace("x", "");
						}
						itemAmount = itemAmount.trim();
						int amount = Integer.parseInt(itemAmount);
						if (material != null) {
							while (amount > 64) {
								if (items.size() < 7) {
									ItemStack newItem = new ItemStack(material, 64);
									items.add(newItem);
									amount = amount - 64;
								}else{
									player.sendMessage(ChatColor.RED + "[MinecraftRP] Too many items!  Ignoring");
									break;
								}
							}
							if (items.size() < 7) {
								ItemStack newItem = new ItemStack(material, amount);
								items.add(newItem);
								goodItems = true;
							}else{
								player.sendMessage(ChatColor.RED + "[MinecraftRP] Too many items!  Ignoring");
							}
						}else{
							player.sendMessage(ChatColor.RED + "[MinecraftRP] Don't understand the " + itemName);
							goodItems = false;
							return false;
						}
					}else{
						player.sendMessage(ChatColor.RED + "[MinecraftRP] Too many items!  Ignoring");
					}
				}
			}
		}
		if (goodPrice && goodDuration && goodItems && pageLength) {
			return true;
		}
		return false;
	}
}