package ironcrystal.minecraftrp.occupations;

public class Occupation {
	
	public static Occupations getOccupationByString(String str) {
		if (str.equalsIgnoreCase("generic occupation")) {
			return Occupations.GENERICOCCUPATION;
		}
		else if (str.equalsIgnoreCase("supplier")) {
			return Occupations.SUPPLIER;
		}
		return null;
	}

}
