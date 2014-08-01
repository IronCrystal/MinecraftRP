package ironcrystal.minecraftrp.contract;

public enum ContractState {

	NOTSTARTED,
	DECLINED,
	INPROGRESS,
	FINISHED_SUCCESFULLY,
	CANCELED_BY_SUPPLIER,
	CANCELED_BY_SHOPKEEPER,
	FINISHED_FAILED;

	@Override
	public String toString() {
		switch(this) {
		case NOTSTARTED: return "Not Started";
		case DECLINED: return "Declined";
		case INPROGRESS: return "In Progress";
		case FINISHED_SUCCESFULLY: return "Finished Succesfully";
		case FINISHED_FAILED: return "Finished Failed";
		case CANCELED_BY_SUPPLIER: return "Canceled By Supplier";
		case CANCELED_BY_SHOPKEEPER: return "Canceled By Shopkeeper";
		}
		return null;
	}
	
	public static ContractState getContractStateByString(String str) {
		switch(str) {
		case "not started":
		case "Not Started": return ContractState.NOTSTARTED;
		case "declined":
		case "Declined": return ContractState.DECLINED;
		case "in progress":
		case "In Progress": return ContractState.INPROGRESS;
		case "finished succesfully":
		case "Finished Succesfully": return ContractState.FINISHED_SUCCESFULLY;
		case "finished failed":
		case "Finished Failed": return ContractState.FINISHED_FAILED;
		case "canceled sy supplier":
		case "Canceled By Supplier": return ContractState.CANCELED_BY_SUPPLIER;
		case "canceled sy shopkeeper":
		case "Canceled By Shopkeeper": return ContractState.CANCELED_BY_SHOPKEEPER;
		}
		return null;
	}

}
