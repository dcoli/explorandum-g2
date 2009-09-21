package explorandum.f09.g2;

import java.awt.Point;
import java.util.HashMap;

/**
 * Class for the Grid/Map
 * 
 * @author sharadh
 * 
 */
public class Grid {

	/**
	 * Hashmap with key as Point and value as Cell
	 */
	private HashMap<Point, Cell> gridMap;

	/**
	 * Counters to understand map size
	 */
	private int minYSeen = 0;
	private int maxXSeen = 0;
	private int minXSeen = 0;
	private int maxYSeen = 0;

	/**
	 * Private Constructor
	 */
	public Grid() {
		gridMap = new HashMap<Point, Cell>();
	}

	/**
	 * Clears all present information
	 */
	public void clear() {
		gridMap.clear();
	}

	/**
	 * Returns the cell represented by co-ordiantes given by point P
	 * 
	 * @param p
	 * @return
	 */
	public Cell getCell(Point p) {
		return gridMap.get(p);
	}

	/**
	 * Returns a cell indexed as an offset from a given point
	 * 
	 * @param p
	 * @param dx
	 * @param dy
	 * @return
	 */
	public Cell getCell(Point p, int dx, int dy) {
		return getCell(p.x + dx, p.y + dy);
	}

	/**
	 * Returns the cell represented by co-ordiantes
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public Cell getCell(int x, int y) {
		return gridMap.get(new Point(x, y));
	}

	/**
	 * Puts the cell c into the grid, indexed by point p
	 * 
	 * @param p
	 * @param c
	 */
	public void putCell(Point p, Cell c) {
		gridMap.put(p, c);
	}

	/*
	 * public static HashSet<Point> campSites = new HashSet<Point>();
	 * 
	 * public static Point getClosestCampSite(Point p) { return null; }
	 * 
	 * public static void putCell(Point p) { campSites.add(p); }
	 */
	/**
	 * Updates the information for a cell which has been visited by player
	 * 
	 * @param p
	 * @param turn
	 * @param isFootPrintPresent
	 * @param isExplorerPresent
	 */
	public void updateVisitedCellInformation(Point p, int turn,
			boolean isFootPrintPresent, boolean isExplorerPresent) {
		Cell cell = getCell(p);
		if (cell == null) {
			cell = new Cell(p);
			putCell(p, cell);
		}

		// Update Visitation
		cell.setLastTurnVisited(turn);
		cell.incrementNoOfTimesVisited();

		if (!cell.isVisited()) {
			cell.setFirstTurnVisited(turn);
			cell.setVisited(true);
		}
		// Update Ownership
		if (cell.getOwner() != Constants.OWNER_BY_US) {
			if (isFootPrintPresent) {
				cell.setOwner(Constants.OWNED_BY_THEM);
			} else {
				cell.setOwner(Constants.OWNER_BY_US);

			}
		}

		checkAndUpdateXSeen(p.x);
		checkAndUpdateYSeen(p.y);
		Grid.computeScore(cell, this);
	}

	/**
	 * Updates the information of a cell which has been seen by player
	 * 
	 * @param p
	 * @param terrain
	 * @param isExplorerSeen
	 * @return true if seen first time
	 */
	public boolean updateSeenCellInformation(Point p, int terrain,
			boolean isExplorerSeen) {
		Cell cell = getCell(p);
		boolean retVal = false;
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
			retVal = true;
		}

		checkAndUpdateXSeen(p.x);
		checkAndUpdateYSeen(p.y);
		Grid.computeScore(cell, this);
		return retVal;
	}

	/**
	 * Computes the score of this cell based on various parameters
	 * 
	 * @param cell_
	 * @param grid_
	 */
	public static void computeScore(Cell cell_, Grid grid_) {
		if (cell_.isImpassable()) {
			cell_.setScore(-100);
		} else {

			int px = cell_.getPoint().x;
			int py = cell_.getPoint().y;
			int score = 0;

			// Edge Neighbours
			for (int i = 0; i < Constants.EDGE_NEIGHBOR_OFFSETS.length; i++) {
				Cell c = grid_.getCell(cell_.getPoint(),
						Constants.EDGE_NEIGHBOR_OFFSETS[i][0],
						Constants.EDGE_NEIGHBOR_OFFSETS[i][1]);
				if (c != null) {
					switch (c.getTerrain()) {
					case Constants.TERRAIN_LAND:
						score += 1;
						break;

					case Constants.TERRAIN_WATER:
						score += Constants.RANGE;
						break;

					case Constants.TERRAIN_MOUNTAIN:
						score += 2;
						break;
					}
					score += 1;

					switch (c.getOwner()) {
					case Constants.OWNED_BY_THEM:
						score -= 1;
						break;

					case Constants.OWNER_BY_US:
						score -= 1;
						break;

					case Constants.OWNED_BY_UNKNOWN:
						score += 2;
						break;
					}
				} else {
					// Unseen cell +2
					score += 2;
				}

			}

			// Vertex Neighbours
			for (int i = 0; i < Constants.VERTEX_NEIGHBOR_OFFSETS.length; i++) {
				Cell c = grid_.getCell(cell_.getPoint(),
						Constants.VERTEX_NEIGHBOR_OFFSETS[i][0],
						Constants.VERTEX_NEIGHBOR_OFFSETS[i][1]);

				if (c != null) {
					switch (c.getTerrain()) {
					case Constants.TERRAIN_LAND:
						score += 1;
						break;

					case Constants.TERRAIN_WATER:
						score += Constants.RANGE;
						break;

					case Constants.TERRAIN_MOUNTAIN:
						score += 2;
						break;
					}

					switch (c.getOwner()) {
					case Constants.OWNED_BY_THEM:
						score -= 1;
						break;

					case Constants.OWNER_BY_US:
						score -= 1;
						break;

					case Constants.OWNED_BY_UNKNOWN:
						score += 2;
						break;
					}

				} else {
					score += 2;
				}

			}

			cell_.setScore(score);
		}

	}

	/**
	 * @return the maxXSeen
	 */
	public int getMaxXSeen() {
		return maxXSeen;
	}

	/**
	 * Sees if X seen is greater than or lesser than current knowledge and
	 * updates
	 * 
	 * @param xSeen_
	 *            the maxXSeen to set
	 */
	public void checkAndUpdateXSeen(int xSeen_) {
		if (xSeen_ < minXSeen) {
			minXSeen = xSeen_;

		} else if (xSeen_ > maxXSeen) {
			maxXSeen = xSeen_;
		}
	}

	/**
	 * @return the maxYSeen
	 */
	public int getMaxYSeen() {
		return maxYSeen;
	}

	/**
	 * Sees if Y seen is greater than or lesser than current knowledge and
	 * updates
	 * 
	 * @param ySeen_
	 *            the maxYSeen to set
	 */
	public void checkAndUpdateYSeen(int ySeen_) {
		if (ySeen_ < minYSeen) {
			minYSeen = ySeen_;

		} else if (ySeen_ > maxYSeen) {
			maxYSeen = ySeen_;
		}
	}

	/**
	 * @return the minYSeen
	 */
	public int getMinYSeen() {
		return minYSeen;
	}

	/**
	 * @return the minXtSeen
	 */
	public int getMinXtSeen() {
		return minXSeen;
	}

	/**
	 * Analyses and returns an int array of informatiom Index 1 - Estimation of
	 * map size covered Index 2 - Estimation of map left .....
	 * 
	 * @return
	 */
	public static int[] analyseGrid() {
		return new int[] {};
	}

	/**
	 * Returns random points chosen
	 * 
	 * @param current_
	 * @return
	 */
	private Point[] getRandomTargets(Point current_) {

		Point[] p = new Point[8];
		int xSize = maxXSeen - minXSeen;
		int ySize = maxYSeen - minYSeen;
		for (int i = 0; i < p.length; i++) {
			int px = (int) (Math.random() * xSize - minXSeen);
			int py = (int) (Math.random() * ySize - minYSeen);
			p[i] = new Point(px, py);
		}
		return new Point[] {};
	}

	/**
	 * Evaluates random open targets by calling getRandomTargets() and chooses
	 * the one which has most undiscovered neighbors and unvisited neighbors
	 * 
	 * Eventually perhaps also one which will also help pick up most cells on
	 * the way
	 * 
	 * @param current_
	 * @return
	 */
	public Point getOpenRandomTarget(Point current_) {
		Point[] targets = getRandomTargets(current_);

		int maxOpennessScoreIndex = -1;
		int maxOpennessScore = Integer.MIN_VALUE;
		for (int i = 0; i < targets.length; i++) {
			int score = getOpennessScore(targets[i]);
			if (maxOpennessScore > score) {
				maxOpennessScore = score;
				maxOpennessScoreIndex = i;
			}
		}

		return targets[maxOpennessScoreIndex];
	}

	public int getOpennessScore(Point p) {
		Cell curr = getCell(p);
		int score = 0;
		if (curr == null) {
			score += 10;
		} else {
			if (curr.isVisited() || curr.isImpassable()
					|| curr.getOwner() == Constants.OWNED_BY_THEM) {
				return 0;
			}
		}

		int px = p.x;
		int py = p.y;

		// Edge Neighbours
		for (int i = 0; i < Constants.EDGE_NEIGHBOR_OFFSETS.length; i++) {
			Cell c = getCell(p, Constants.EDGE_NEIGHBOR_OFFSETS[i][0],
					Constants.EDGE_NEIGHBOR_OFFSETS[i][1]);
			if (c != null) {
				if (!c.isVisited()) {
					score += 3;
				}
			} else {
				// Unseen cell
				score += 5;
			}

		}

		// Edge Neighbours
		for (int i = 0; i < Constants.VERTEX_NEIGHBOR_OFFSETS.length; i++) {
			Cell c = getCell(p, Constants.VERTEX_NEIGHBOR_OFFSETS[i][0],
					Constants.VERTEX_NEIGHBOR_OFFSETS[i][1]);

			if (c != null) {
				if (!c.isVisited()) {
					score += 3;
				}
			} else {
				score += 5;
			}

		}

		return score;
	}
}
