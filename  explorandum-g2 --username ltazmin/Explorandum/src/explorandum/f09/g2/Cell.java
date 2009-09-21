package explorandum.f09.g2;

import java.awt.Point;

/**
 * Class representing each cell of the map along with information collected
 * about it
 * 
 * @author sharadh
 * 
 */
public class Cell implements Comparable<Cell> {

	/**
	 * @return the noOfTimesVisited
	 */
	public int getNoOfTimesVisited() {
		return noOfTimesVisited;
	}

	/**
	 * Increments number of times visited
	 */
	public void incrementNoOfTimesVisited() {
		noOfTimesVisited++;
	}

	private Point p;

	private int terrain = Constants.TERRAIN_UNKNOWN;
	private int whoOwns = Constants.OWNED_BY_UNKNOWN;
	private boolean isVisited = false;
	private int turnFirstVisited = -1;
	private int turnLastVisited = -1;
	private int noOfTimesVisited = 0;

	private int score = 0;

	/**
	 * Gets the first turn this cell was visited
	 * 
	 * @return
	 */
	public int getFirstTurnVisited() {
		return turnFirstVisited;
	}

	/**
	 * Sets the first turn this cell was visited
	 * 
	 * @param turnFirstVisited_
	 */
	public void setFirstTurnVisited(int turnFirstVisited_) {
		turnFirstVisited = turnFirstVisited_;
	}

	/**
	 * Gets the last turn this cell was visited
	 * 
	 * @return
	 */
	public int getLastTurnVisited() {
		return turnLastVisited;
	}

	/**
	 * Sets the
	 * 
	 * @param turnLastVisited_
	 */
	public void setLastTurnVisited(int turnLastVisited_) {
		turnLastVisited = turnLastVisited_;
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
	 * Implement comparable method
	 */
	public int compareTo(Cell c) {
		
		return score - c.getScore();
		
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

	/**
	 * @return the score
	 */
	public int getScore() {
		return score;
	}

	/**
	 * @param score_
	 *            the score to set
	 */
	public void setScore(int score_) {
		score = score_;
	}

	/**
	 * 
	 * @param c
	 * @return
	 */
	public int getDistanceFrom(Cell c){
		return Math.abs(getPoint().x - c.getPoint().x) +Math.abs(getPoint().y - c.getPoint().y);
	}
	
	/**
	 * 
	 * @param p
	 * @return
	 */
	public int getDistanceFrom(Point p){
		return Math.abs(getPoint().x - p.x) +Math.abs(getPoint().y - p.y);
	}
}
