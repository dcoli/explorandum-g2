package explorandum.f09.g2;

import java.awt.Point;
import java.util.HashMap;

/**
 * Class for the Grid/Map
 * @author sharadh
 *
 */
public class Grid {

	/**
	 * Hashmap with key as Point and value as Cell
	 */
	private HashMap<Point, Cell> gridMap;
	
	/**
	 * Private Constructor
	 */
	public Grid() {
		gridMap = new HashMap<Point, Cell>();
	}


	/**
	 * Clears all present information
	 */
	public void clear(){
		gridMap.clear();
	}
	/**
	 * Returns the cell represented by co-ordiantes given by point P
	 * @param p
	 * @return
	 */
	public Cell getCell(Point p) {
		return gridMap.get(p);
	}

	/**
	 * Puts the cell c into the grid, indexed by point p
	 * @param p
	 * @param c
	 */
	public void putCell(Point p, Cell c) {
		gridMap.put(p, c);
	}

/*	public static HashSet<Point> campSites = new HashSet<Point>();

	public static Point getClosestCampSite(Point p) {
		return null;
	}

	public static void putCell(Point p) {
		campSites.add(p);
	}
*/
	/**
	 * Updates the information for a cell which has been visited by player
	 * @param p
	 * @param turn
	 * @param isFootPrintPresent
	 * @param isExplorerPresent
	 */
	public void updateVisitedCellInformation(Point p, int turn,
			boolean isFootPrintPresent, boolean isExplorerPresent) {
		Cell cell = getCell(p);
		if (cell != null) {

			// Update Visitation
			cell.setVisited(true);
			cell.setVisitationTurn(turn);

			// Update Ownership
			if (cell.getOwner() != Constants.OWNER_BY_US) {
				if (isFootPrintPresent) {
					cell.setOwner(Constants.OWNED_BY_THEM);
				} else {
					cell.setOwner(Constants.OWNER_BY_US);
				}
			}
		} else {
			cell = new Cell(p);
			cell.setVisited(true);
			cell.setVisitationTurn(turn);
			cell.setOwner(Constants.OWNER_BY_US);
			putCell(p, cell);
		}
	}

	/**
	 * Updates the information of a cell which has been seen by player
	 * @param p
	 * @param terrain
	 * @param isExplorerSeen
	 */
	public void updateSeenCellInformation(Point p, int terrain,
			boolean isExplorerSeen) {
		Cell cell = getCell(p);
		if (cell != null) {

			if (!cell.isVisited() && isExplorerSeen) {
				cell.setOwner(Constants.OWNED_BY_THEM);
			}

		} else {
			cell = new Cell(p);
			cell.setTerrain(terrain);
			if (isExplorerSeen) {
				cell.setOwner(Constants.OWNED_BY_THEM);
			}
			putCell(p, cell);
		}
	}


}
