package ironcrystal.minecraftrp.contract;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ContractItem extends ItemStack {
	
	private Contract contract;
	private int total;
	private int progress;
	private Material material;
	
	public ContractItem(Contract contract, int total, int progress, Material material) {
		this.contract = contract;
		this.total = total;
		this.progress = progress;
		this.material = material;
	}

	public Contract getContract() {
		return contract;
	}

	public void setContract(Contract contract) {
		this.contract = contract;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}
}
