package ironcrystal.minecraftrp.occupations;

public class Occupation {
	
	public static Occupations getOccupationByString(String str) {
		switch(str) {
		case "Citizen":
		case "citizen": return Occupations.CITIZEN;
		case "supplier": return Occupations.SUPPLIER;
		case "mayor": return Occupations.MAYOR;
		case "shop keeper": return Occupations.SHOPKEEPER;
		}
		return null;
	}
}
