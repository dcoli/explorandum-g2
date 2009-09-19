package explorandum.f09.g2;

import java.awt.Point;

/**
 * Class representing each cell of the map along with information collected
 * about it
 * 
 * @author sharadh
 * 
 */
public class Cell {

	private Point p;

	private int terrain = Constants.TERRAIN_UNKNOWN;
	private int whoOwns = Constants.OWNED_BY_UNKNOWN;
	private boolean isVisited = false;
	private int turnVisited = -1;
	private int noOfTimesVisited = 0;

	public int getVisitationTurn() {
		return turnVisited;
	}

	/**
	 * Sets the
	 * 
	 * @param turnVisited_
	 */
	public void setVisitationTurn(int turnVisited_) {
		turnVisited = turnVisited_;
	}

	/**
	 * REturns true if this cell has been visited by player
	 * 
	 * @return
	 */
	public boolean isVisited() {
		return isVisited;
	}

	/**
	 * Sets this as a cell visited by explorer
	 * 
	 * @param isVisited_
	 */
	public void setVisited(boolean isVisited_) {
		isVisited = isVisited_;
	}

	/**
	 * Constructor
	 * 
	 * @param p_
	 */
	public Cell(Point p_) {
		super();
		p = p_;
	}

	/**
	 * Gets the terrain of this cell
	 * 
	 * @return
	 */
	public int getTerrain() {
		return terrain;
	}

	/**
	 * Sets the terrain of this cell
	 * 
	 * @param terrain_
	 */
	public void setTerrain(int terrain_) {
		terrain = terrain_;
	}

	/**
	 * Returns the owner of this cell
	 * 
	 * @return
	 */
	public int getOwner() {
		return whoOwns;
	}

	/**
	 * Sets the owner for this cell
	 * 
	 * @param whoOwns_
	 */
	public void setOwner(int whoOwns_) {
		whoOwns = whoOwns_;
	}

	/**
	 * Returns True if this is an impassable cell
	 * 
	 * @return
	 */
	public boolean isImpassable() {
		return getTerrain() == Constants.TERRAIN_MOUNTAIN
				|| getTerrain() == Constants.TERRAIN_WATER;
	}

	/**
	 * Returns the co-ordinates for this cell
	 * 
	 * @return
	 */
	public Point getPoint() {
		return p;
	}

	@Override
	public String toString() {
		return "[ ( " + p.x + "," + p.y + ") , Terrain =" + terrain + " ]";
	}

	/**
	 * Returns the cell to the north
	 * 
	 * @return
	 */
	public Cell getNorthCell() {
		return null;
	}

	/**
	 * Returns the cell to the east
	 * 
	 * @return
	 */
	public Cell getEastCell() {
		return null;
	}

	/**
	 * Returns the cell to the south
	 * 
	 * @return
	 */
	public Cell getSouthCell() {
		return null;
	}

	/**
	 * Returns the cell to the west
	 * 
	 * @return
	 */
	public Cell getWestCell() {
		return null;
	}

	/**
	 * Returns the cell to the northeast
	 * 
	 * @return
	 */
	public Cell getNorthEastCell() {
		return null;
	}

	/**
	 * Returns the cell to the northwest
	 * 
	 * @return
	 */
	public Cell getNorthWestCell() {
		return null;
	}

	/**
	 * Returns the cell to the southeast
	 * 
	 * @return
	 */
	public Cell getSouthEastCell() {
		return null;
	}

	/**
	 * Returns the cell to the southwest
	 * 
	 * @return
	 */
	public Cell getSouthWestCell() {
		return null;
	}

}
