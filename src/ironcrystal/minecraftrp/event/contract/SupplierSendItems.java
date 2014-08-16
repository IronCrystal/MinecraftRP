package ironcrystal.minecraftrp.event.contract;

import ironcrystal.minecraftrp.contract.Contract;
import ironcrystal.minecraftrp.contract.ContractManager;
import ironcrystal.minecraftrp.contract.ContractState;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class SupplierSendItems implements Listener {

	@EventHandler
	public void onSupplierSendItems(InventoryCloseEvent event) {
		InventoryHolder holder = event.getInventory().getHolder();
		if (holder instanceof Chest) {
			Chest chest = (Chest) holder;
			Block block = chest.getBlock();

			if (ContractManager.blockIsSupplyChest(block)) {
				Contract contract = ContractManager.getContract(block);
				for (ItemStack item: chest.getBlockInventory().getContents()) {
					if (item != null) {
						Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[MinecraftRP] Item is not null!");
						int amountToTransfer = 0;
						try {
							Material material = item.getType();
							List<ItemStack> requiredItemList = contract.getItems();
							List<ItemStack> itemProgressList = contract.getItemProgress();

							ItemStack totalItem = new ItemStack(Material.AIR);
							ItemStack progressItem = new ItemStack(Material.AIR);
							int index = 0;
							for (ItemStack progressItems: itemProgressList) {
								if (progressItems.getType() == material) {
									progressItem = progressItems;
									Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[MinecraftRP] Setting progressItem to " + progressItems);
									break;
								}
							}
							for (ItemStack requiredItems: requiredItemList) {
								if (requiredItems.getType() == material) {
									totalItem = requiredItems;
									Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[MinecraftRP] Setting totalItem to " + requiredItems);
									break;
								}
							}
							
							if (totalItem.getType() != Material.AIR && progressItem.getType() != Material.AIR) {
								Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[MinecraftRP] Item is on the required lists!");
								int total = totalItem.getAmount();
								int progress = progressItem.getAmount();

								if (progress < total) {
									Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[MinecraftRP] Progress is less than total!");
									if (progress + item.getAmount() > total) {
										amountToTransfer = total - progress;
									}else{
										amountToTransfer = item.getAmount();
									}
									ItemStack itemToTransfer = new ItemStack(material, amountToTransfer);
									chest.getBlockInventory().remove(itemToTransfer);
									Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[MinecraftRP] Removed item from chest!");
									contract.addItemProgress(itemToTransfer);
									Block shop = contract.getShopChest();
									if (shop.getState() instanceof Chest) {
										Chest shopChest = (Chest) shop.getState();
										shopChest.getBlockInventory().addItem(itemToTransfer);
										Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[MinecraftRP] Added item to shop!");
									}
									if (event.getPlayer() instanceof Player) {
										Player player = (Player) event.getPlayer();
										player.sendMessage(ChatColor.GREEN + "[MinecraftRP] Delivered items to " + contract.getShopkeeper().getLastKnownName());

										if (ContractManager.isContractComplete(contract)) {
											player.sendMessage(ChatColor.GREEN + "[MinecraftRP] Congratulations!  You completed your contract!");
											if (Bukkit.getPlayer(contract.getShopkeeper().getUUID()) != null) {
												Bukkit.getPlayer(contract.getShopkeeper().getUUID()).sendMessage(ChatColor.GREEN + "[MinecraftRP] " + player.getName()
														+ " has completed your contract succesfully!");
											}
											contract.setState(ContractState.FINISHED_SUCCESFULLY);
											ContractManager.inProgressContractList.remove(contract);
										}
									}							
								}
							}else{
								Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[MinecraftRP] Item is not on the required lists!");
							}
						}catch(NullPointerException npe) {
							Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[MinecraftRP] Error sending items!");

						}
					}
				}
			}
		}
	}
}
