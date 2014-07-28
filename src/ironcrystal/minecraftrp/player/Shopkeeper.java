package ironcrystal.minecraftrp.player;

import ironcrystal.minecraftrp.contract.Contract;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Shopkeeper extends OccupationalPlayer {

	public Shopkeeper(UUID uuid) {
		super(uuid);
	}
	
	public void sendContract(Contract contract) {
		Player p = Bukkit.getPlayer(getUUID());
		p.getInventory().addItem(contract.getBook());
	}
}
