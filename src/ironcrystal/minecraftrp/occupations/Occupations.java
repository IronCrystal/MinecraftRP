package ironcrystal.minecraftrp.occupations;

public enum Occupations {
	GENERICOCCUPATION,
	SUPPLIER,
	MAYOR;
	
	@Override
	public String toString() {
		switch(this) {
		case GENERICOCCUPATION: return "Generic Occupation";
		case SUPPLIER: return "Supplier";
		case MAYOR: return "Mayor";
		}
		return null;
	}
}
