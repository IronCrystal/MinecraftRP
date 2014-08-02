package ironcrystal.minecraftrp.timer.contract;

import ironcrystal.minecraftrp.contract.Contract;
import ironcrystal.minecraftrp.contract.ContractManager;
import ironcrystal.minecraftrp.contract.ContractState;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ContractFinishTimer implements Runnable {
//bob
	@Override
	public void run() {
		List<Contract> clone = new ArrayList<Contract>();
		for (Contract contract : ContractManager.inProgressContractList) {
			clone.add(contract);
		}
		for (Contract contract : clone) {
			long timeStarted = contract.getTimeStarted();
			long timeLimit = contract.getTimeLimit();
			long currentTime = System.currentTimeMillis();
			
			if (timeStarted + timeLimit < currentTime) {
				contract.setState(ContractState.FINISHED_FAILED);
				Player shop = Bukkit.getPlayer(contract.getShopkeeper().getUUID());
				Player supply = Bukkit.getPlayer(contract.getSupplier().getUUID());
				if (supply != null) {
					supply.sendMessage(ChatColor.RED + "[MinecraftRP] You failed your contract with " + contract.getShopkeeper().getLastKnownName());
				}
				if (shop != null) {
					shop.sendMessage(ChatColor.RED + "[MinecraftRP] " + contract.getSupplier().getLastKnownName() + " failed the contract with you!");
				}
				ContractManager.inProgressContractList.remove(contract);
			}
		}
	}
}
