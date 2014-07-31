package ironcrystal.minecraftrp.player;

import ironcrystal.minecraftrp.Files;
import ironcrystal.minecraftrp.contract.Contract;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Shopkeeper extends OccupationalPlayer {

	private List<Integer> contractIDs;

	public Shopkeeper(UUID uuid) {
		super(uuid);
		contractIDs = new ArrayList<Integer>();
		contractIDs = getConfig().getIntegerList("Contracts");
	}
	
	public void sendContract(Contract contract) {
		Player p = Bukkit.getPlayer(getUUID());
		p.getInventory().addItem(contract.getBook());
	}
	
	public List<Integer> getContractIDs() {
		return contractIDs;
	}

	public void setContractIDs(List<Integer> contractIDs) {
		this.contractIDs = contractIDs;
		this.getConfig().set("Contracts", contractIDs);
		Files.saveFile(this.getFile(), this.getConfig());
	}
	
	public void addContract(int id) {
		contractIDs.add(id);
		getConfig().set("Contracts", contractIDs);
		Files.saveFile(getFile(), getConfig());
	}
}
