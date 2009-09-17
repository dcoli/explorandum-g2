package explorandum.g5;

import java.util.List;

public class Utils {
	
	/**
	 * Enlarges list to be of size s filling the empty
	 * spaces with null
	 * @param l
	 * @param s
	 */
	public static void enlargeList(List l, int s) {
		for (int i = l.size(); i < s; i++) {
			l.add(null);
		}
	}
}
