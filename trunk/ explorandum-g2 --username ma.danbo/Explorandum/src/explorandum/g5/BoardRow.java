package explorandum.g5;

import java.util.ArrayList;

public class BoardRow {
	private ArrayList<BoardLocation> posLocations, negLocations;
	
	public BoardRow() {
		posLocations = new ArrayList<BoardLocation>();
		negLocations = new ArrayList<BoardLocation>();
	}
	
	public boolean locationExists(int col) {
		if (col >= 0) {
			return col < posLocations.size() && posLocations.get(col) != null;
		} else {
			return Math.abs(col) < negLocations.size() && negLocations.get(Math.abs(col)) != null;
		}
	}
	
	public BoardLocation get(int col) {
		if (!locationExists(col)) {
			return null;
		}
		
		if (col >= 0) {
			return posLocations.get(col);
		} else {
			return negLocations.get(Math.abs(col));
		}
	}
	
	public void set(int col, BoardLocation loc) {
		if (col >= 0) {
			if (col >= posLocations.size()) {
				Utils.enlargeList(posLocations, col + 1);
			}
			posLocations.set(col, loc);
		} else {
			if (Math.abs(col) >= negLocations.size()) {
				Utils.enlargeList(negLocations, Math.abs(col) + 1);
			}
			negLocations.set(Math.abs(col), loc);
		}
	}
}
