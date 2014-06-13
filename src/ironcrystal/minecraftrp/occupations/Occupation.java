package ironcrystal.minecraftrp.occupations;

public class Occupation {
	
	public static Occupations getOccupationByString(String str) {
		switch(str) {
		case "generic occupation":
		case "Generic Occupation": return Occupations.GENERICOCCUPATION;
		case "supplier": return Occupations.SUPPLIER;
		case "mayor": return Occupations.MAYOR;
		}
		return null;
	}
}
