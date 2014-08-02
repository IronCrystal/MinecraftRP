package ironcrystal.minecraftrp.occupations;

public class Occupation {
	
	public static Occupations getOccupationByString(String str) {
		switch(str) {
		case "Citizen":
		case "citizen": return Occupations.CITIZEN;
		case "supplier": return Occupations.SUPPLIER;
		case "mayor": return Occupations.MAYOR;
		case "shop keeper":
		case "Shop Keeper":
		case "Shop keeper":
		case "shopkeeper": return Occupations.SHOPKEEPER;
		case "construction":
		case "Construction": return Occupations.CONSTRUCTION_WORKER;
		}
		return null;
	}
}
