package explorandum.f09.g2;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import explorandum.GameConstants;

import explorandum.Logger;

/**
 * Class for the Grid/Map
 * 
 * @author sharadh
 * @author colin
 * @author laima
 * 
 */
public class Grid {

	/**
	 * Hashmap with key as Point and value as Cell
	 */
	private HashMap<Point, Cell> _gridMap;
	private HashSet<Point> _unVisitedCells;
	private HashSet<Point> _visitedCells;

	private ArrayList<Cell> _secondBestChoices;
	private ArrayList<Point> _coastHuggingFootPrints;
	private int landCellCount = 0;

	/**
	 * @return the unVisitedCells
	 */
	public HashSet<Point> getUnVisitedCells() {
		return _unVisitedCells;
	}

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

	private Logger log;

	/**
	 * Private Constructor
	 */
	public Grid(Logger _log) {
		_gridMap = new HashMap<Point, Cell>();
		_unVisitedCells = new HashSet<Point>();
		_visitedCells = new HashSet<Point>();
		_coastHuggingFootPrints = new ArrayList<Point>();
		log = _log;
		_secondBestChoices = new ArrayList<Cell>();

	}

	public Point getClosestUnVisitedPoint(Point curr_) {
		Point closestPt = null;
		double closestDist = Double.MAX_VALUE;
		for (Iterator<Point> iterator = _unVisitedCells.iterator(); iterator
				.hasNext();) {
			Point pt = (Point) iterator.next();
			double dist = curr_.distance(pt);
			if (dist < closestDist) {
				closestDist = dist;
				closestPt = pt;
			}

		}

		return closestPt;
	}

	public Point getFarthestUnVisitedPoint(Point curr_) {
		Point farthestPt = null;
		double farthestDist = Double.MIN_VALUE;
		for (Iterator<Point> iterator = _unVisitedCells.iterator(); iterator
				.hasNext();) {
			Point pt = (Point) iterator.next();
			double dist = curr_.distance(pt);
			if (dist > farthestDist) {
				farthestDist = dist;
				farthestPt = pt;
			}

		}

		return farthestPt;
	}

	public void addToCoastHuggingFootprints(Point p) {
		_coastHuggingFootPrints.add(p);
	}

	public boolean isNearAnyCoastHuggedFootprint(Point p) {
		for (Iterator<Point> iterator = _coastHuggingFootPrints.iterator(); iterator
				.hasNext();) {
			Point pt = (Point) iterator.next();
			if (pt.distance(p) <= 2.0 * Constants.RANGE) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Clears all present information
	 */
	public void clear() {
		_gridMap.clear();
		_unVisitedCells.clear();
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
		// If cell does not exist create it
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
			} else {
				landCellCount++;

				if (isExplorerSeen) {
					cell.setOwner(Constants.OWNED_BY_THEM);
				}

				_unVisitedCells.add(p);
			}

			putCell(p, cell);
			retVal = true;
			cell.setTurnFirstSeen(Constants.CURRENT_TURN);

		}

		cell.updateMinDistanceSeenFrom((int) cell
				.getDistanceFrom(currentLocation));

		if (!cell.isImpassable()) {
			checkAndUpdateXSeen(p.x);
			checkAndUpdateYSeen(p.y);
		}
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
								Constants.CURRENT_TURN, Constants.NO_OF_ROUNDS);
						;
						// score += 3;
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

			// if(cell_.isVisited()){
			// score
			// }

			score += cell_.getMinDistanceSeenFrom();
			// score += 1 - cell_.getNoOfTimesVisited();
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

	public float getEstimatedUnseenAreaPercent() {

		int area = (maxXSeen - minXSeen + 1) * (maxYSeen - minYSeen + 1);
		// System.out.println(area);
		// System.out.println(landCellCount);
		return 1 - (landCellCount) / ((float) area);
	}

	public float getEstimatedUnvisitedAreaPercent() {
		int area = (maxXVisited - minXVisited + 1)
				* (maxYVisited - minYVisited + 1);
		return 1 - (((float) (_visitedCells.size())) / ((float) area));
	}

	/**
	 * Analyzes and returns a boolean saying one should explore unknown but
	 * estimated territory
	 * 
	 * @return if the unexplored percentage reaches a threshold value return
	 *         true
	 */
	boolean[] analyseGrid(Logger log) {
		boolean[] details = new boolean[5];
		details[0] = getEstimatedUnseenAreaPercent() > Constants.RATIO_UNEXPLORED_MAP_THRESHOLD;
		details[1] = maxXSeen > maxXVisited;
		details[2] = minXSeen > minXVisited;
		details[3] = maxYSeen > maxYVisited;
		details[4] = minYSeen > minYVisited;

		return details;
	}

	// =======
	// public boolean analyseGrid( Logger log ) {
	//		
	// //log.debug("max:"+maxXVisited+","+maxYVisited+" min:"+minXVisited+","+minYVisited+" visited:"+_visitedCells.size());
	// int area = (maxXVisited - minXVisited + 1) * (maxYVisited - minYVisited +
	// 1);
	// float percentUnexplored = 1 - ( ((float)(_visitedCells.size())) /
	// ((float)area) );
	// boolean shouldFindCenter = percentUnexplored >
	// Constants.RATIO_UNEXPLORED_MAP_THRESHOLD;
	// if (shouldFindCenter)
	// log.debug("should find center. est. area:"+area+" unexplored:"+(100*percentUnexplored)+"%");
	// return shouldFindCenter;
	// >>>>>>> .r51
	// }

	public boolean isGridOpen() {
		return maxXSeen > maxXVisited || minXSeen > minXVisited
				|| maxYSeen > maxYVisited || minYSeen > minYVisited;

	}

	/**
	 * Gets center of a map given by the extremes of points visited
	 * 
	 * @param log
	 * 
	 * @return Point in center
	 */
	public Point getCenter(Logger log) {
		boolean isNegativeX = maxXVisited + minXVisited < 0;
		boolean isNegativeY = maxYVisited + minYVisited < 0;

		int x = (maxXVisited - minXVisited) / 2;
		if (isNegativeX)
			x = -1 * x;
		int y = (maxYVisited - minYVisited) / 2;
		if (isNegativeY)
			y = -1 * y;

		return new Point(x, y);
	}

	/**
	 * Returns random points chosen
	 * 
	 * @param current_
	 * @return
	 */
	public Point getUnexploredAreaTarget(Point current_) {

		int maxX = this.maxXSeen;
		int minX = this.minXSeen;
		int maxY = this.maxYSeen;
		int minY = this.minYSeen;

		int xSize = maxX - minX;
		int ySize = maxY - minY;

		int xR1 = (int) Math.floor(xSize / 3);
		int yR1 = (int) Math.floor(ySize / 3);
		int xR2 = (int) Math.floor(xSize / 3 * 2);
		int yR2 = (int) Math.floor(ySize / 3 * 2);
		int xR3 = xSize;
		int yR3 = ySize;

		Point[] rectP1 = new Point[] { new Point(minX, minY),
				new Point(minX, minY + yR1 + 1),
				new Point(minX, minY + yR2 + 2),
				new Point(minX + xR1 + 1, minY),
				new Point(minX + xR1 + 1, minY + yR1 + 1),
				new Point(minX + xR1 + 1, minY + yR2 + 2),
				new Point(minX + xR2 + 2, minY),
				new Point(minX + xR2 + 2, minY + yR1 + 1),
				new Point(minX + xR2 + 2, minY + yR2 + 2) };

		Point[] rectP2 = new Point[] { new Point(minX + xR1, minY + yR1),
				new Point(minX + xR1, minY + yR2),
				new Point(minX + xR1, minY + yR3),
				new Point(minX + xR2, minY + yR1),
				new Point(minX + xR2, minY + yR2),
				new Point(minX + xR2, minY + yR3),
				new Point(minX + xR3, minY + yR1),
				new Point(minX + xR3, minY + yR2),
				new Point(minX + xR3, minY + yR3) };

		int maxOpenScore = Integer.MIN_VALUE;
		int maxOpenScoreIndex = -1;
		for (int i = 0; i < rectP1.length; i++) {
			int rectScore = getOpennessOfRectangle(rectP1[i], rectP2[i]);

			if (rectScore > maxOpenScore) {
				maxOpenScore = rectScore;
				maxOpenScoreIndex = i;
			}
		}

		int randX = (int) (xSize * Math.random());
		int randY = (int) (ySize * Math.random());

		int px = rectP1[maxOpenScoreIndex].x;
		int py = rectP1[maxOpenScoreIndex].y;

		// int px = rectP1[maxOpenScoreIndex].x
		// + (rectP2[maxOpenScoreIndex].x - rectP1[maxOpenScoreIndex].x)
		// / 2;
		// int py = rectP1[maxOpenScoreIndex].y
		// + (rectP2[maxOpenScoreIndex].y - rectP1[maxOpenScoreIndex].y)
		// / 2;

		return new Point(px + randX, py + randY);
	}

	private int getOpennessOfRectangle(Point p1, Point p2) {
		int xLow = p1.x > p2.x ? p2.x : p1.x;
		int xHigh = p1.x > p2.x ? p1.x : p2.x;
		int yLow = p1.y > p2.y ? p2.y : p1.y;
		int yHigh = p1.y > p2.y ? p1.y : p2.y;

		int count = 0;
		for (int i = xLow; i <= xHigh; i++) {
			for (int j = yLow; j < yHigh; j++) {
				if (getCell(i, j) == null) {
					count++;
				}
			}
		}

		return count;
	}

	private int getUnvisitednessOffRectangle(Point p1, Point p2) {
		int xLow = p1.x > p2.x ? p2.x : p1.x;
		int xHigh = p1.x > p2.x ? p1.x : p2.x;
		int yLow = p1.y > p2.y ? p2.y : p1.y;
		int yHigh = p1.y > p2.y ? p1.y : p2.y;

		int count = 0;
		for (int i = xLow; i <= xHigh; i++) {
			for (int j = yLow; j < yHigh; j++) {
				Cell c = getCell(i, j);
				if (c == null) {
					count++;
				} else if (!c.isVisited()
						&& c.getOwner() != Constants.OWNED_BY_THEM) {
					count++;
				}
			}
		}

		return count;
	}

	// /**
	// * Evaluates random open targets by calling getRandomTargets() and chooses
	// * the one which has most undiscovered neighbors and unvisited neighbors
	// *
	// * Eventually perhaps also one which will also help pick up most cells on
	// * the way
	// *
	// * @param current_
	// * @return
	// */
	// public Point getOpenRandomTarget(Point current_) {
	// Point[] targets = getRandomTargets(current_);
	//
	// int maxOpennessScoreIndex = -1;
	// int maxOpennessScore = Integer.MIN_VALUE;
	// for (int i = 0; i < targets.length; i++) {
	// int score = getOpennessScore(targets[i]);
	// if (maxOpennessScore > score) {
	// maxOpennessScore = score;
	// maxOpennessScoreIndex = i;
	// }
	// }
	//
	// return targets[maxOpennessScoreIndex];
	// }

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
				score += 3;
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
				score += 4;
			}

		}

		// if (curr.isVisited()) {
		// score += 20 * (1 - curr.getLastTurnVisited() * 1.0
		// / Constants.CURRENT_TURN);
		//
		// }

		// score += curr.getMinDistanceSeenFrom();

		return score;
	}

	public InfoObject[] analyseCurrentLocation(Point currentLocation_,
			Point[] offsets_, int time_) {
		boolean isCoastHugging = false;
		Point waterPt = null;
		for (int i = 0; i < Constants.EDGE_NEIGHBOR_OFFSETS.length; i++) {
			Cell c = getCell(currentLocation_,
					Constants.EDGE_NEIGHBOR_OFFSETS[i][0],
					Constants.EDGE_NEIGHBOR_OFFSETS[i][1]);
			if (c.getTerrain() == Constants.TERRAIN_WATER) {
				isCoastHugging = true;
				waterPt = c.getPoint();
				break;
			}
		}

		boolean isLockedByVisitedCells = true;
		for (int i = 1; i < GameConstants._dx.length; i++) {
			Cell c = getCell(currentLocation_, GameConstants._dx[i],
					GameConstants._dy[i]);
			if (!c.isImpassable() && c.isVisited() != true) {
				isLockedByVisitedCells = false;
				break;
			}
		}

		Point newWaterLocation = null;
		boolean isNewWaterSeen = false;
		for (int i = 0; i < offsets_.length; i++) {
			Cell c = getCell(offsets_[i]);
			if (c.getTerrain() == Constants.TERRAIN_WATER
					&& c.getTurnFirstSeen() == time_) {
				isNewWaterSeen = true;

				if (newWaterLocation == null
						|| newWaterLocation.distance(currentLocation_) > offsets_[i]
								.distance(currentLocation_)) {
					newWaterLocation = offsets_[i];
				}
			}
		}

		int visitedCount = 0;
		int unvisitedCount = 0;
		Cell c1 = null, c2 = null;
		for (int i = 1; i < GameConstants._dx.length; i++) {
			Cell c = getCell(currentLocation_, GameConstants._dx[i],
					GameConstants._dy[i]);
			if (!c.isImpassable()) {
				if (c.isVisited() == false) {
					unvisitedCount++;
				} else {
					visitedCount++;

					if (visitedCount == 1) {
						c1 = c;
					} else if (visitedCount == 2) {
						c2 = c;
					}
				}
			}
		}

		boolean isBackTrackingFromTunnel = false, isInTunnelCycle = false;
		if (c1 != null && c2 != null) {
			int visitDiff = Math.abs(c1.getLastTurnVisited()
					- c2.getLastTurnVisited());
			isBackTrackingFromTunnel = (visitedCount == 2 || unvisitedCount == 0)
					&& (visitDiff >= 4 && visitDiff <= 6);
			isInTunnelCycle = (visitedCount == 2 || unvisitedCount == 0)
					&& (visitDiff > 6);
		} else {
			isBackTrackingFromTunnel = (visitedCount == 2 || unvisitedCount == 0);
		}

		boolean isEnteringTunnel = visitedCount == 1
				&& (unvisitedCount == 1 || unvisitedCount == 2);

		boolean isInDeadEnd = visitedCount == 1 && unvisitedCount == 0;

		return new InfoObject[] { new InfoObject(isCoastHugging, waterPt),
				new InfoObject(isNewWaterSeen, newWaterLocation),
				new InfoObject(isLockedByVisitedCells, null),
				new InfoObject(isEnteringTunnel, null),
				new InfoObject(isBackTrackingFromTunnel, null),
				new InfoObject(isInDeadEnd, null),
				new InfoObject(isInTunnelCycle, null) };
	}

	/**
	 * @param _secondBestChoices
	 *            the _secondBestChoices to set
	 */
	public void set_secondBestChoices(ArrayList<Cell> _secondBestChoices) {
		this._secondBestChoices = _secondBestChoices;

	}

	public InfoObject isNearEdgeOfGrid(Point p) {
		double d1 = p.distance(minXSeen, p.y);
		double d2 = p.distance(maxXSeen, p.y);
		double d3 = p.distance(p.x, minYSeen);
		double d4 = p.distance(p.x, maxYSeen);

		if (d1 < 2 * Constants.RANGE) {
			return new InfoObject(true, new Point(minXSeen + 2
					* Constants.RANGE, (minYSeen + maxYSeen) / 2));
		}
		if (d2 < 2 * Constants.RANGE) {
			return new InfoObject(true, new Point(maxXSeen + 2
					* Constants.RANGE, (minYSeen + maxYSeen) / 2));
		}
		if (d3 < 2 * Constants.RANGE) {
			return new InfoObject(true, new Point((minXSeen + maxXSeen) / 2,
					p.y - 2 * Constants.RANGE));
		}
		if (d4 < 2 * Constants.RANGE) {
			return new InfoObject(true, new Point((minXSeen + maxXSeen) / 2,
					p.y + 2 * Constants.RANGE));
		}

		return new InfoObject(false, null);
	}

	public int getUnvisitedNeighbourCount(Point currentLocation_) {
		int unvisitedCount = 0;
		for (int i = 1; i < GameConstants._dx.length; i++) {
			Cell c = getCell(currentLocation_, GameConstants._dx[i],
					GameConstants._dy[i]);
			if (!c.isImpassable() && !c.isVisited()) {
				unvisitedCount++;
			}
		}
		return unvisitedCount;
	}

	/**
	 * @return the _secondBestChoices
	 */
	public ArrayList<Cell> get_secondBestChoices() {
		return _secondBestChoices;
	}

	/**
	 * Remove from the secondbestchoices arraylist in 2n time
	 */
	public void removeFromSecondBestChoices(Cell c, Logger log) {
		int length = _secondBestChoices.size();
		for (int i = 0; i < length; i++) {
			if (_secondBestChoices.get(i) == c) {
				_secondBestChoices.remove(i);
				log.debug("removing visited cell from second best choices: "
						+ _secondBestChoices.toString());
			}
		}
	}

	public Cell getSecondBestChoiceCell() {
		if (_secondBestChoices.size() != 0) {
			Cell c = _secondBestChoices.get(0);
			removeFromSecondBestChoices(c, log);
			return c;
		} else
			return null;
	}

}
