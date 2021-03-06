package ironcrystal.minecraftrp.commands;

import ironcrystal.minecraftrp.MinecraftRP;
import ironcrystal.minecraftrp.contract.Contract;
import ironcrystal.minecraftrp.contract.ContractManager;
import ironcrystal.minecraftrp.contract.ContractState;
import ironcrystal.minecraftrp.occupations.Occupations;
import ironcrystal.minecraftrp.player.OccupationalPlayer;
import ironcrystal.minecraftrp.player.Shopkeeper;
import ironcrystal.minecraftrp.player.Supplier;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class ContractCommands {

	public static HashMap<UUID, Contract> PlayersAcceptingContract = new HashMap<UUID, Contract>();

	public static void sendContract(Player player) {
		if(ContractManager.getUnStartedContract(player.getUniqueId()) == null) {
			ItemStack book = new ItemStack(Material.BOOK_AND_QUILL);

			BookMeta bookMeta = (BookMeta) book.getItemMeta();

			//bookMeta.setTitle(ChatColor.GREEN + "Contract");
			//bookMeta.setAuthor(ChatColor.GRAY + "IronCrystal");

			bookMeta.addPage(ChatColor.ITALIC + "Format as follows:\nPrice: <amount>\nTime: <amount>[h:d]\nItems:\n<ItemName> x<amount>\n"
					+ "\n"
					+ "Please Start Writing On The Next Page" 
					+ "\n\n\n\n\n\n\n\n\n" + ChatColor.RESET);
			//bookMeta.setPage(1, "Price:\nTime:\nItems\n");

			book.setItemMeta(bookMeta);
			player.getInventory().addItem(book);
			player.sendMessage(ChatColor.GREEN + "[MinecraftRP] Sent blank contract!");
		}else{
			player.sendMessage(ChatColor.RED + "[MinecraftRP] Wait until your pending contract is accepted or declined!");
		}
	}

	public static void declineContract(OccupationalPlayer occPlayer) {
		if (PlayersAcceptingContract.containsKey(occPlayer.getUUID())) {
			Contract contract = PlayersAcceptingContract.get(occPlayer.getUUID());
			contract.setState(ContractState.DECLINED);
			if (contract.getShopkeeper() != null) {
				OfflinePlayer offP = Bukkit.getOfflinePlayer(contract.getShopkeeper().getUUID());
				if (offP.isOnline()) {
					Player player = Bukkit.getPlayer(contract.getShopkeeper().getUUID());
					player.sendMessage(ChatColor.RED + "[MinecraftRP] " + occPlayer.getLastKnownName() + " has declined your contract!");
					PlayersAcceptingContract.remove(occPlayer.getUUID());
				}
			}
			else if (contract.getSupplier() != null) {
				OfflinePlayer offP = Bukkit.getOfflinePlayer(contract.getSupplier().getUUID());
				if (offP.isOnline()) {
					Player player = Bukkit.getPlayer(contract.getSupplier().getUUID());
					player.sendMessage(ChatColor.RED + "[MinecraftRP] " + occPlayer.getLastKnownName() + " has declined your contract!");
					PlayersAcceptingContract.remove(occPlayer.getUUID());
				}
			}
		}else{
			Bukkit.getPlayer(occPlayer.getUUID()).sendMessage(ChatColor.RED + "[MinecraftRP] You don't have any pending contracts!");
		}
	}

	public static void acceptContract(OccupationalPlayer occPlayer) {
		if (PlayersAcceptingContract.containsKey(occPlayer.getUUID())) {
			Contract contract = PlayersAcceptingContract.get(occPlayer.getUUID());			
			Player p = Bukkit.getPlayer(occPlayer.getUUID());
			p.sendMessage(ChatColor.GREEN + "[MinecraftRP] You accepted the contract!");
			if (occPlayer.getOccupation() == Occupations.SHOPKEEPER) {
				Shopkeeper shopkeeper = new Shopkeeper(occPlayer.getUUID());
				shopkeeper.addContract(contract.getId());
				contract.setShopkeeper(shopkeeper);
				Bukkit.getPlayer(contract.getSupplier().getUUID()).sendMessage(ChatColor.GREEN + "[MinecraftRP] " + p.getName() + " accepted your contract!");
			}
			else if (occPlayer.getOccupation() == Occupations.SUPPLIER) {
				Supplier supplier = new Supplier(occPlayer.getUUID());
				supplier.addContract(contract.getId());
				contract.setSupplier(supplier);
				Bukkit.getPlayer(contract.getShopkeeper().getUUID()).sendMessage(ChatColor.GREEN + "[MinecraftRP] " + p.getName() + " accepted your contract!");
			}
			boolean moneyTaken = ContractManager.takeMoneyFromShopkeeper(contract);
			if (!moneyTaken) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[MinecraftRP] Somehow the money transaction for contract " + contract.getId() + " didn't work!");
			}
			PlayersAcceptingContract.remove(occPlayer.getUUID());
			ContractManager.giveContractPartnersChests(contract);
		}else{
			Bukkit.getPlayer(occPlayer.getUUID()).sendMessage(ChatColor.RED + "[MinecraftRP] You don't have any pending contracts!");
		}
	}

	@SuppressWarnings("deprecation")
	public static void sendContractToPlayer(OccupationalPlayer player, String nameToSend) {
		Contract contract = ContractManager.getUnStartedContract(player.getUUID());
		OfflinePlayer offP = Bukkit.getOfflinePlayer(nameToSend);
		if (offP.isOnline()) {
			Player pl = offP.getPlayer();
			Player p = Bukkit.getPlayer(player.getUUID());
			OccupationalPlayer occPlayer = new OccupationalPlayer(pl.getUniqueId());
			if (contract != null) {
				if (contract.getShopkeeper() == null) {
					if (occPlayer.getOccupation() == Occupations.SHOPKEEPER) {
						Bukkit.broadcastMessage("$" + MinecraftRP.econ.getBalance(offP));
						if (MinecraftRP.econ.getBalance(offP) >= contract.getPrice()) {
							Shopkeeper shopkeeper = new Shopkeeper(pl.getUniqueId());
							shopkeeper.sendContract(contract);
							PlayersAcceptingContract.put(shopkeeper.getUUID(), contract);
							pl.sendMessage(ChatColor.GREEN + "[MinecraftRP] " + contract.getSupplier().getLastKnownName() + " has sent you a contract!");
							pl.sendMessage(ChatColor.GREEN + "[MinecraftRP] Read it first and if you agree, type " + ChatColor.RED + "/rp contract accept");
							pl.sendMessage(ChatColor.GREEN + "[MinecraftRP] If you wish to decline, type " + ChatColor.RED + "/rp contract decline");
							p.sendMessage(ChatColor.GREEN + "[MinecraftRP] Sent contract to " + pl.getName());
						}else{
							Bukkit.getPlayer(player.getUUID()).sendMessage(ChatColor.RED + "[MinecraftRP] This player doesn't have enough money!");
						}
					}else{
						Bukkit.getPlayer(player.getUUID()).sendMessage(ChatColor.RED + "[MinecraftRP] You must send this contract to a shopkeeper!");
					}
				}
				else if (contract.getSupplier() == null) {
					if (occPlayer.getOccupation() == Occupations.SUPPLIER) {
						Supplier supplier = new Supplier(pl.getUniqueId());
						supplier.sendContract(contract);
						PlayersAcceptingContract.put(supplier.getUUID(), contract);
						pl.sendMessage(ChatColor.GREEN + "[MinecraftRP] " + contract.getShopkeeper().getLastKnownName() + " has sent you a contract!");
						pl.sendMessage(ChatColor.GREEN + "[MinecraftRP] Read it first and if you agree, type " + ChatColor.RED + "/rp contract accept");
						pl.sendMessage(ChatColor.GREEN + "[MinecraftRP] If you wish to decline, type " + ChatColor.RED + "/rp contract decline");
						p.sendMessage(ChatColor.GREEN + "[MinecraftRP] Sent contract to " + pl.getName());
					}else{
						Bukkit.getPlayer(player.getUUID()).sendMessage(ChatColor.RED + "[MinecraftRP] You must send this contract to a supplier!");
					}
				}else{
					Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[MinecraftRP] Contract tried sending, but neither shopkeeper and supplier are null");
				}
			}else{
				Bukkit.getPlayer(player.getUUID()).sendMessage(ChatColor.RED + "[MinecraftRP] You don't have any contracts that aren't started!");
			}
		}else{
			Bukkit.getPlayer(player.getUUID()).sendMessage(ChatColor.RED + "[MinecraftRP] You must send the contract to an online player!");
		}
	}

	public static void getCurrentContracts(OccupationalPlayer occPlayer) {
		if (occPlayer.getOccupation() == Occupations.SHOPKEEPER || occPlayer.getOccupation() == Occupations.SUPPLIER) {
			Player player = Bukkit.getPlayer(occPlayer.getUUID());
			player.getInventory().addItem(ContractManager.getCurrentContracts(occPlayer.getUUID()));
			player.sendMessage(ChatColor.GREEN + "[MinecraftRP] Contracts Book Recieved");
		}else{
			Bukkit.getPlayer(occPlayer.getUUID()).sendMessage(ChatColor.RED + "[MinecraftRP] You must be a shopkeeper or supplier to have contracts!");
		}
	}

	public static void getContractHistory(OccupationalPlayer occPlayer) {
		if (occPlayer.getOccupation() == Occupations.SHOPKEEPER || occPlayer.getOccupation() == Occupations.SUPPLIER) {
			Player player = Bukkit.getPlayer(occPlayer.getUUID());
			player.getInventory().addItem(ContractManager.getContractHistory(occPlayer.getUUID()));
			player.sendMessage(ChatColor.GREEN + "[MinecraftRP] Contract History Book Received");
		}else{
			Bukkit.getPlayer(occPlayer.getUUID()).sendMessage(ChatColor.RED + "[MinecraftRP] You must be a shopkeeper or supplier to have contracts!");
		}
	}
}
