public class LabelsHelper {
	public static String[] getAllNames(String[] labels) {
		int i = 0;
		String[] names = {};
		for(String label : labels) {
			label = label.replaceAll(" ", "");
			names[i++] = label.split(":")[0];
		}

		return names;
	}

    public static String[] getNameAndPicks(String label) {
		label = label.replaceAll(" ", "");
	    String split[] = label.split(":");
	    return split;
	}

	public static String getName(String label) {
		label = label.replaceAll(" ", "");
		return label.split(":")[0];
	}

	public static int getPicks(String label) throws NumberFormatException {
		label = label.replaceAll(" ", "");
		return new Integer(label.split(":")[1]);
	}

	public static boolean playerWon(String label) throws NumberFormatException {
		label = label.replaceAll(" ", "");
		int picks = new Integer(label.split(":")[1]);
		return (picks == 0);
	}
}