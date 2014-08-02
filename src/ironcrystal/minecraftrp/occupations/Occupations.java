package ironcrystal.minecraftrp.occupations;

public enum Occupations {
	CITIZEN,
	SUPPLIER,
	SHOPKEEPER,
	MAYOR,
	CONSTRUCTION_WORKER;
	
	@Override
	public String toString() {
		switch(this) {
		case CITIZEN: return "Citizen";
		case SUPPLIER: return "Supplier";
		case MAYOR: return "Mayor";
		case SHOPKEEPER: return "Shopkeeper";
		case CONSTRUCTION_WORKER: return "Construction";
		}
		return null;
	}
}
