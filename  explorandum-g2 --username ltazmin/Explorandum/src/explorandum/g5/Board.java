package explorandum.g5;

import java.util.ArrayList;
import java.awt.Point;

public class Board {
		
	private ArrayList<BoardRow> posRows, negRows;
	private Point lowerBound;	// Most negative coordinate in the bounding box of the known board (inclusive)
	private Point upperBound;	// Most positive coordinate in the bounding box of the known board (exclusive)
	private Point currentLocation;
	private ArrayList<Point> unexploredSpaces;
	private boolean dirty;
	
	public Board(BoardLocation origin) {
		posRows = new ArrayList<BoardRow>();
		negRows = new ArrayList<BoardRow>();
		lowerBound = new Point(0,0);
		upperBound = new Point(1,1);
		
		setLocation(0, 0, origin);
		updateUnexploredSpaces(true);
		
		setDirty(false);
	}
	
	private void updateBounds(int x, int y) {
		if (x < lowerBound.x) {
			lowerBound.x = x;
		} else if (x >= upperBound.x) {
			upperBound.x = x + 1;
		}
		
		if (y < lowerBound.y) {
			lowerBound.y = y;
		} else if (y >= upperBound.y) {
			upperBound.y = y + 1;
		}
	}
	
	private BoardRow getRow(int row) {
		if (row >= 0) {
			return posRows.get(row);
		} else {
			return negRows.get(Math.abs(row));
		}
	}
	
	public boolean rowExists(int row) {
		if (row >= 0) {
			return row < posRows.size() && posRows.get(row) != null;
		} else {
			return Math.abs(row) < negRows.size() && negRows.get(Math.abs(row)) != null;
		}
	}
	
	/**
	 * Not safe, does not check whether it is overwriting
	 * an existing row.  Only use if you are actually sure
	 * that the row is empty.
	 */
	private void makeNewRow(int row) {
		BoardRow r = new BoardRow();
		if (row >= 0) {
			if (row >= posRows.size()) {
				Utils.enlargeList(posRows, row + 1);
			}
			posRows.set(row, r);
		} else {
			if (Math.abs(row) >= negRows.size()) {
				Utils.enlargeList(negRows, Math.abs(row) + 1);
			}
			negRows.set(Math.abs(row), r);
		}
	}
	
	public BoardLocation getLocation(Point p) {
		return getLocation(p.x, p.y);
	}
	
	public BoardLocation getLocation(int x, int y) {
		if (!rowExists(y)) {
			return null;
		} else if (getRow(y).get(x) == null) {
			return null;
		}
		return getRow(y).get(x);
	}
	
	public void setLocation(int x, int y, BoardLocation loc) {
		setDirty(true);
		
		if (!rowExists(y)) {
			makeNewRow(y);
		}

		//location has been set
		if(getRow(y).get(x)!=null) {
			if( getRow(y).get(x).score < loc.score ) {
				getRow(y).set(x, loc);
			}
		}
		//location has not been set
		else
			getRow(y).set(x, loc);
					
		updateBounds(x, y);
	}

	
	//if true,  has to be in center
	//if false doesnt have to be in center
	private void updateUnexploredSpaces(boolean centerBool) {
		unexploredSpaces = new ArrayList<Point>();
		for (int y = lowerBound.y; y < upperBound.y; y++) {
			for (int x = lowerBound.x; x < upperBound.x; x++) {
				if(centerBool) {
					if (getLocation(x, y) == null && inCenter(x, y)) {
						unexploredSpaces.add(new Point(x, y));
					}
				}
				else {
					if (getLocation(x, y) == null ) {
						unexploredSpaces.add(new Point(x, y));
					}
				}	
			}
		}
	}
	
	public boolean inCenter(int x, int y) {
		boolean isNull = true;
		int i;
		
		for (i = x+1; i < getUpperBound().x; i++) {
			if (getLocation(i, y) != null) {
				isNull = false;
				break;
			}
		}
		if (isNull) {
			return false;
		}
		
		isNull = true;
		for (i = x-1; i >= getLowerBound().x; i--) {
			if (getLocation(i, y) != null) {
				isNull = false;
				break;
			}
		}
		if (isNull) {
			return false;
		}
		
		isNull = true;
		for (i = y+1; i < getUpperBound().y; i++) {
			if (getLocation(x, i) != null) {
				isNull = false;
				break;
			}
		}
		if (isNull) {
			return false;
		}
		
		isNull = true;
		for (i = y-1; i >= getLowerBound().y; i--) {
			if (getLocation(x, i) != null) {
				isNull = false;
				break;
			}
		}
		if (isNull) {
			return false;
		}
		
		return true;
	}
	
	
	public ArrayList<Point> getUnexploredSpaces(boolean inCenter) {
		if (isDirty()) {
			updateUnexploredSpaces(inCenter);
			setDirty(false);
		}
		return unexploredSpaces;
	}
	
	public Point getLowerBound() {
		return lowerBound;
	}

	public Point getUpperBound() {
		return upperBound;
	}
	
	public void setCurrentLocation(Point p) {
		currentLocation = p;
	}
	
	public String toString(boolean type) {
		StringBuffer sb = new StringBuffer();
		
		if (type) {
			for (int y = lowerBound.y; y < upperBound.y; y++) {
				for (int x = lowerBound.x; x < upperBound.x; x++) {
					if (getLocation(x, y) != null) {
						if (x == currentLocation.x && y == currentLocation.y) {
							sb.append("p ");
						} else if (x == 0 && y == 0) {
							sb.append("o ");
						} else {
							if(getLocation(x,y).terrain == 2)
								sb.append("M ");
							else if(getLocation(x,y).terrain == 1)
								sb.append("W ");
							else
								sb.append("x ");
						}
					} else {
						sb.append(". ");
					}
				}
				sb.append("\n");
			}
		}
		else {
			for (int y = lowerBound.y; y < upperBound.y; y++) {
				for (int x = lowerBound.x; x < upperBound.x; x++) {
					if (getLocation(x, y) != null) {
						if (x == currentLocation.x && y == currentLocation.y) {
							sb.append("ppp ");
						}
					  else {
						  	String sc = ((Double)getLocation(x,y).score).toString();
						  	sb.append( sc.substring(0,3) + " ");
						}
					} else {
						sb.append("... ");
					}
				}
				sb.append("\n");
			}
		}
		
		return sb.toString();
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	public boolean isDirty() {
		return dirty;
	}
}
