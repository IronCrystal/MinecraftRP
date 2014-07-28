package ironcrystal.minecraftrp.occupations;

public enum Occupations {
	CITIZEN,
	SUPPLIER,
	SHOPKEEPER,
	MAYOR;
	
	@Override
	public String toString() {
		switch(this) {
		case CITIZEN: return "Citizen";
		case SUPPLIER: return "Supplier";
		case MAYOR: return "Mayor";
		case SHOPKEEPER: return "Shop Keeper";
		}
		return null;
	}
}
