package explorandum.f09.g2;

import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;

import explorandum.Logger;

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
	private HashMap<Point, Cell> _gridMap;
	private HashSet<Point> _unVisitedCells;
	private HashSet<Point> _visitedCells;

	/**
	 * Counters to understand map size
	 */
	private int minYSeen = 0;
	private int maxXSeen = 0;
	private int minXSeen = 0;
	private int maxYSeen = 0;
	private int minXVisited = 0;
	private int maxXVisited = 0;
	private int minYVisited = 0;
	private int maxYVisited = 0;
	
	/**
	 * Private Constructor
	 */
	public Grid() {
		_gridMap = new HashMap<Point, Cell>();
		_unVisitedCells = new HashSet<Point>();
		_visitedCells = new HashSet<Point>();
	}

	/**
	 * Clears all present information
	 */
	public void clear() {
		_gridMap.clear();
	}

	/**
	 * Returns the cell represented by co-ordiantes given by point P
	 * 
	 * @param p
	 * @return
	 */
	public Cell getCell(Point p) {
		return _gridMap.get(p);
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
		return _gridMap.get(new Point(x, y));
	}

	/**
	 * Puts the cell c into the grid, indexed by point p
	 * 
	 * @param p
	 * @param c
	 */
	public void putCell(Point p, Cell c) {
		_gridMap.put(p, c);
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
			cell.updateMinDistanceSeenFrom(0);

			// Remove from unvisited cells
			_unVisitedCells.remove(p);
			_visitedCells.add(p);
		}
		// Update Ownership
		if (cell.getOwner() != Constants.OWNED_BY_US) {
			if (isFootPrintPresent) {
				cell.setOwner(Constants.OWNED_BY_THEM);
			} else {
				cell.setOwner(Constants.OWNED_BY_US);

			}
		}

		checkAndUpdateXSeen(p.x);
		checkAndUpdateYSeen(p.y);
		checkAndUpdateXVisited(p.x);
		checkAndUpdateYVisited(p.y);
		Grid.computeScore(cell, this);

		// Edge Neighbours
		for (int i = 0; i < Constants.EDGE_NEIGHBOR_OFFSETS.length; i++) {
			Cell c = getCell(cell.getPoint(),
					Constants.EDGE_NEIGHBOR_OFFSETS[i][0],
					Constants.EDGE_NEIGHBOR_OFFSETS[i][1]);

			Grid.computeScore(c, this);
		}

		// Vertex Neighbours
		for (int i = 0; i < Constants.VERTEX_NEIGHBOR_OFFSETS.length; i++) {
			Cell c = getCell(cell.getPoint(),
					Constants.VERTEX_NEIGHBOR_OFFSETS[i][0],
					Constants.VERTEX_NEIGHBOR_OFFSETS[i][1]);
			Grid.computeScore(c, this);
		}
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
			boolean isExplorerSeen, Point currentLocation) {
		Cell cell = getCell(p);
		boolean retVal = false;
		if (cell != null) {

			if (!cell.isVisited() && isExplorerSeen) {
				cell.setOwner(Constants.OWNED_BY_THEM);
			}

		} else {
			cell = new Cell(p);
			cell.setTerrain(terrain);
			if (terrain != Constants.TERRAIN_LAND) {
				int dx = currentLocation.x - p.x;
				int dy = currentLocation.y - p.y;
				if (cell.getDistanceFrom(currentLocation) < 2) {
					// cell.setOwner(Constants.OWNED_BY_US);
				}
			}
			if (isExplorerSeen) {
				cell.setOwner(Constants.OWNED_BY_THEM);
			}
			putCell(p, cell);
			retVal = true;

			_unVisitedCells.add(p);
		}

		cell.updateMinDistanceSeenFrom((int) cell
				.getDistanceFrom(currentLocation));

		checkAndUpdateXSeen(p.x);
		checkAndUpdateYSeen(p.y);
		Grid.computeScore(cell, this);
		return retVal;
	}

	/**
	 * Considers neighbor information to assign a score to a cell
	 * 
	 * @param p
	 * @author colin
	 */
	/*
	 * written before sharadh posted his, does about the same thing public int
	 * updateCellScores( Point p ) { int v[][] =
	 * Constants.VERTEX_NEIGHBOR_OFFSETS; int e[][] =
	 * Constants.EDGE_NEIGHBOR_OFFSETS; for (int vi=0; vi < v[0].length; vi++) {
	 * Cell neighbor = getCell(new Point(v[vi][0],v[vi][1])); Cell target =
	 * getCell(p); if (neighbor != null) { target.score +=
	 * (neighbor.getTerrain() == Constants.TERRAIN_WATER)? WATER_SCORE: 0;
	 * target.score += (neighbor.getTerrain() == Constants.TERRAIN_MOUNTAIN)?
	 * MOUNTAIN_SCORE: 0; target.score += (neighbor.isVisited())? 0:
	 * UNVISITED_SCORE; } } for (int ei=0; ei < e[0].length; ei++) { Cell
	 * neighbor = getCell(new Point(e[ei][0],e[ei][1])); Cell target =
	 * getCell(p); if (neighbor != null) { target.score +=
	 * (neighbor.getTerrain() == Constants.TERRAIN_WATER)? WATER_SCORE: 0;
	 * target.score += (neighbor.getTerrain() == Constants.TERRAIN_MOUNTAIN)?
	 * MOUNTAIN_SCORE: 0; target.score += (neighbor.isVisited())? 0:
	 * UNVISITED_SCORE; } } return 0; }
	 */

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
			double score = 0;

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
						score += Constants.getWaterScore(
								Constants.CURRENT_TURN, Constants.NO_OF_ROUNDS);;
						// score += 3;
						break;

					case Constants.TERRAIN_MOUNTAIN:
						score += 2;
						break;
					}

					// score += 1;

					switch (c.getOwner()) {
					case Constants.OWNED_BY_THEM:
						score -= 1;
						break;

					case Constants.OWNED_BY_US:
						score -= 1;
						break;

					case Constants.OWNED_BY_UNKNOWN:
						score += 2;
						break;
					}

				} else {
					// Unseen cell +2
					// score += 4;

					score += Constants.RANGE;
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
						score += Constants.getWaterScore(
								Constants.CURRENT_TURN, Constants.NO_OF_ROUNDS);
						// score += 3;
						break;

					case Constants.TERRAIN_MOUNTAIN:
						score += 2;
						break;
					}

					switch (c.getOwner()) {
					case Constants.OWNED_BY_THEM:
						score -= 1;
						break;

					case Constants.OWNED_BY_US:
						score -= 1;
						break;

					case Constants.OWNED_BY_UNKNOWN:
						score += 2;
						break;
					}

				} else {
					// score += 3;
					score += Constants.RANGE;
				}

			}

			score += Constants.RANGE - cell_.getMinDistanceSeenFrom();
			score += 1 - cell_.getNoOfTimesVisited();
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
	 * @return the minYSeen
	 */
	public int getMinYSeen() {
		return minYSeen;
	}

	/**
	 * @return the minXSeen
	 */
	public int getMinXSeen() {
		return minXSeen;
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
	 * Sees if X visited is greater than or lesser than current knowledge and
	 * updates
	 * 
	 * @param x
	 *            the maxXVisited to set
	 */
	public void checkAndUpdateXVisited(int x) {
		if (x < minXVisited) {
			minXVisited = x;

		} else if (x > maxXVisited) {
			maxXVisited = x;
		}
	}

	/**
	 * Sees if y visited is greater than or lesser than current knowledge and
	 * updates
	 * 
	 * @param y
	 *            the mayyVisited to set
	 */
	public void checkAndUpdateYVisited(int y) {
		if (y < minYVisited) {
			minYVisited = y;

		} else if (y > maxYVisited) {
			maxYVisited = y;
		}
	}

	
	/**
	 * Analyzes and returns a boolean saying one should explore unknown but estimated territory
	 * 
	 * @return if the unexplored percentage reaches a threshold value return true
	 */
	public boolean analyseGrid( Logger log ) {
		
		log.debug("max:"+maxXVisited+","+maxYVisited+" min:"+minXVisited+","+minYVisited+" visited:"+_visitedCells.size());
		int area = (maxXVisited - minXVisited + 1) * (maxYVisited - minYVisited + 1);
		float percentUnexplored = 1 - ( ((float)(_visitedCells.size())) / ((float)area) );
//		float percentUnexplored = (float).5;
		boolean shouldFindCenter = percentUnexplored > Constants.PERCENT_UNEXPLORED_MAP_LIMIT;
		log.debug("est. area:"+area+" unexplored:"+percentUnexplored+"%");
		return shouldFindCenter;
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
		} else if (curr.isImpassable()) {
			return 0;

		}

		int px = p.x;
		int py = p.y;

		// Edge Neighbours
		for (int i = 0; i < Constants.EDGE_NEIGHBOR_OFFSETS.length; i++) {
			Cell c = getCell(p, Constants.EDGE_NEIGHBOR_OFFSETS[i][0],
					Constants.EDGE_NEIGHBOR_OFFSETS[i][1]);
			if (c != null) {
				if (c.getTerrain() == Constants.TERRAIN_LAND && !c.isVisited()) {
					score += 3;
				}
			} else {
				// Unseen cell
				score += 5;
			}

		}

		// Vertex Neighbours
		for (int i = 0; i < Constants.VERTEX_NEIGHBOR_OFFSETS.length; i++) {
			Cell c = getCell(p, Constants.VERTEX_NEIGHBOR_OFFSETS[i][0],
					Constants.VERTEX_NEIGHBOR_OFFSETS[i][1]);

			if (c != null) {
				if (c.getTerrain() == Constants.TERRAIN_LAND && !c.isVisited()) {
					score += 3;
				}
			} else {
				score += 5;
			}

		}

		if (curr.isVisited()) {
			score += 5 * (1 - curr.getLastTurnVisited() * 1.0
					/ Constants.CURRENT_TURN);

			if (curr.getLastTurnVisited() >= Constants.CURRENT_TURN - 5) {
				score -= 5;
			}
		}
		return score;
	}

	public Point getCenter(Logger log) {
		boolean isNegativeX = maxXVisited + minXVisited < 0;
		boolean isNegativeY = maxYVisited + minYVisited < 0;
		
		int x = ( maxXVisited - minXVisited ) / 2;
		if (isNegativeX) x = -1 * x;
		int y = ( maxYVisited - minYVisited ) / 2;
		if (isNegativeY) y = -1 * y;
		
		return new Point(x,y);
	}
}
