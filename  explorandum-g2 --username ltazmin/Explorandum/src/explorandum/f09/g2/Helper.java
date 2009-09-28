package explorandum.f09.g2;

import java.awt.Point;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import explorandum.GameConstants;
import explorandum.Move;
import explorandum.Logger;

/**
 * Helper class that supports scoring and path finding etc.
 * 
 * @author laima
 * @author sharadh
 * @author colin
 * 
 */
public class Helper {

	/**
	 * Finds the best cell to target
	 * 
	 * @param startingPoint
	 *            Point object of current cell
	 * @param g
	 *            Grid of the game
	 * @param howFarAway
	 *            how far from starting cell to look for a target. If 0, then
	 *            use default 2 * d.
	 * 
	 * @return optimal cell
	 */

	public static Cell getTargetedCell(Point startingPoint, Grid g,
			int howFarAway, Logger log) {

		Cell startingCell = g.getCell(startingPoint);
		log.debug(startingCell + " <--- START");

		Queue<Cell> neighborCells = new Queue<Cell>();
		BinaryHeap<Cell> candidateCells = new BinaryHeap<Cell>();

		// to prevent duplicates after dequeueing
		HashSet<Cell> dequeuedCells = new HashSet<Cell>();

		if (howFarAway == 0)
			howFarAway = 2 * Constants.RANGE; // how far from current cell to
		// look for a target

		int scopeCount = 0;
		int count = -1; // tracks how many cells are dequeued
		int prevNeighborCellsCount = 0;
		int numNeighborCells = 0;

		neighborCells.enqueue(startingCell);

		// evaluate neighbors within a certain range
		while (scopeCount < howFarAway) {

			Cell c = neighborCells.dequeue(); // get cell
			dequeuedCells.add(c);
			count++; // increase

			// (re-)calculate score
			Grid.computeScore(c, g);
			log.debug(c + " SCORE " + c.getScore());

			// add its neighbors
			numNeighborCells += addNeighbors(c, g, neighborCells,
					dequeuedCells, log);

			// no valid neighbors then add to heap
			if (numNeighborCells < 1) {
				candidateCells.insert(c);

				log.debug("Just added to heap (0): " + c + " SCORE "
						+ c.getScore());

			}

			// keep track of how many sets of neighbors we add to queue
			if (count == prevNeighborCellsCount) {
				scopeCount++;
				prevNeighborCellsCount = numNeighborCells;
				numNeighborCells = 0;
				count = 0;
			}

		}

		// add the remaining cells to heap
		while (!neighborCells.isEmpty()) {
			Cell nc = neighborCells.dequeue();
			if (!nc.isVisited()) {
				candidateCells.insert(nc);
			}

			log.debug("Just added to heap (1): " + nc + " SCORE "
					+ nc.getScore());

		}

		candidateCells.printHeap();
		// System.exit(0);

		// pick cell with highest score
		Cell destinationCell = ((BinaryHeap<Cell>) candidateCells).findMax();

		log.debug("Now targetting " + destinationCell);

		return destinationCell;

	}

	/**
	 * Finds the best cell to target
	 * 
	 * @param p
	 *            Point object of current cell
	 * @param g
	 *            Grid of the game
	 * @return optimal cell
	 */

	public static Cell getOpenTargetedCell(Point startingPoint, Grid g,
			Logger log, int range) {

		Cell startingCell = g.getCell(startingPoint);
		log.debug(startingCell + " <--- START");

		Queue<Cell> neighborCells = new Queue<Cell>();
		ArrayList<Cell> candidateCells = new ArrayList<Cell>();

		// to prevent duplicates after dequeueing
		HashSet<Cell> dequeuedCells = new HashSet<Cell>();

		int howFarAway = range; // how far from current cell to
		// look for a target

		int scopeCount = 0;
		int count = -1; // tracks how many cells are dequeued
		int prevNeighborCellsCount = 0;
		int numNeighborCells = 0;

		neighborCells.enqueue(startingCell);

		// evaluate neighbors within a certain range
		while (scopeCount < howFarAway) {

			Cell c = neighborCells.dequeue(); // get cell
			dequeuedCells.add(c);
			count++; // increase

			// (re-)calculate score
			Grid.computeScore(c, g);
			// log.debug(c + " SCORE " + c.getScore());

			// add its neighbors
			numNeighborCells += addNeighbors(c, g, neighborCells,
					dequeuedCells, log);

			// no valid neighbors then add to heap
			if (numNeighborCells < 1) {
				candidateCells.add(c);

				log.debug("Just added to heap (0): " + c + " SCORE "
						+ g.getOpennessScore(c.getPoint()));

			}

			// keep track of how many sets of neighbors we add to queue
			if (count == prevNeighborCellsCount) {
				scopeCount++;
				prevNeighborCellsCount = numNeighborCells;
				numNeighborCells = 0;
				count = 0;
			}

		}

		// add the remaining cells to heap
		while (!neighborCells.isEmpty()) {
			Cell nc = neighborCells.dequeue();
			candidateCells.add(nc);

			log.debug("Just added to heap (1): " + nc + " SCORE "
					+ g.getOpennessScore(nc.getPoint()));

		}

		int maxOpenScore = Integer.MIN_VALUE;
		int maxOpenScoreIndex = -1;
		double maxOpenDist = Double.MIN_VALUE;
		int index = 0;
		for (Iterator<Cell> iterator = candidateCells.iterator(); iterator
				.hasNext();) {
			Cell cell = (Cell) iterator.next();
			int openScore = g.getOpennessScore(cell.getPoint());
			if (maxOpenScore < openScore) {
				maxOpenScoreIndex = index;
				maxOpenScore = openScore;
				maxOpenDist = cell.getDistanceFrom(startingPoint);
			} else if (openScore == maxOpenScore) {
				double dist = cell.getDistanceFrom(startingPoint);
				if (maxOpenDist < dist) {
					maxOpenScoreIndex = index;
					maxOpenDist = dist;
				}
			}
			index++;
		}

		// pick cell with highest score
		Cell destinationCell = candidateCells.get(maxOpenScoreIndex);

		log.debug("Open Cell" + destinationCell);
		return destinationCell;

	}

	/**
	 * Adds to a queue immediate neighbors of a certain cell
	 * 
	 * @param c
	 *            the cell
	 * @param grid_
	 *            the grid of cells
	 * @param cells
	 *            queue of cells being looked at
	 * @param prevCells
	 *            set of cells already looked at
	 * @return number of added cells
	 */

	private static int addNeighbors(Cell c, Grid grid_, Queue<Cell> cells,
			HashSet<Cell> prevCells, Logger log, HashMap<Point, Point> _prevMap) {

		int retValue = 0;
		ArrayList<Cell> unvisitedNeighbours = new ArrayList<Cell>();

		ArrayList<Cell> visitedNeighbours = new ArrayList<Cell>();

		// Enqueue edge neighbors
		for (int i = 1; i < GameConstants._dx.length; i++) {
			Cell neighborCell = grid_.getCell(c.getPoint(),
					GameConstants._dx[i], GameConstants._dy[i]);

			if (neighborCell != null) {
				if (!prevCells.contains(neighborCell)) {
					if (!cells.contains(neighborCell)
							&& !neighborCell.isImpassable()) {

						if (neighborCell.isVisited()
								|| neighborCell.getOwner() == Constants.OWNED_BY_THEM) {
							visitedNeighbours.add(neighborCell);
						} else {
							unvisitedNeighbours.add(neighborCell);
						}
						// cells.enqueue(neighborCell);
						_prevMap.put(neighborCell.getPoint(), c.getPoint());
						retValue++;
						// log.debug("         neighbor: " + neighborCell);
					} else {
						// log.debug("Case 1 (neighbor: " + neighborCell + ")");
					}
				} else {
					// log.debug("Case 2 (neighbor: " + neighborCell + ")");
				}
			} else {
				// log.debug("Case 3 (neighbor: " + neighborCell + ")");
			}

		}

		for (Iterator<Cell> iterator = unvisitedNeighbours.iterator(); iterator
				.hasNext();) {
			cells.enqueue(iterator.next());

		}
		for (Iterator<Cell> iterator = visitedNeighbours.iterator(); iterator
				.hasNext();) {
			cells.enqueue(iterator.next());

		}
		return retValue;

	}

	/**
	 * Adds to a queue immediate neighbors of a certain cell
	 * 
	 * @param c
	 *            the cell
	 * @param g
	 * @param cells
	 * @return number of added cells
	 */

	private static int addNeighbors(Cell c, Grid grid_, Queue<Cell> cells,
			HashSet<Cell> prevCells, Logger log) {

		int retValue = 0;

		// Enqueue edge neighbors
		for (int i = 0; i < Constants.EDGE_NEIGHBOR_OFFSETS.length; i++) {
			Cell neighborCell = grid_.getCell(c.getPoint(),
					Constants.EDGE_NEIGHBOR_OFFSETS[i][0],
					Constants.EDGE_NEIGHBOR_OFFSETS[i][1]);

			// limitation in keeping score of null prevents its candidacy (let's
			// ponder this)
			// if (neighborCell == null) {
			// Point p = new Point();
			// Cell nullCell = new Cell(p);
			// }

			// if the cells is not already in queue
			// and is land, add to queue
			if (neighborCell != null) {
				if (!prevCells.contains(neighborCell)) {
					if (!cells.contains(neighborCell)
							&& !neighborCell.isImpassable()) {
						cells.enqueue(neighborCell);
						retValue++;
						// log.debug("         neighbor: " + neighborCell);
					} else {
						// log.debug("Case 1 (neighbor: " + neighborCell + ")");
					}
				} else {
					// log.debug("Case 2 (neighbor: " + neighborCell + ")");
				}
			} else {
				// log.debug("Case 3 (neighbor: " + neighborCell + ")");
			}

		}

		// Enqueue vertex neighbors
		for (int i = 0; i < Constants.VERTEX_NEIGHBOR_OFFSETS.length; i++) {
			Cell neighborCell = grid_.getCell(c.getPoint(),
					Constants.VERTEX_NEIGHBOR_OFFSETS[i][0],
					Constants.VERTEX_NEIGHBOR_OFFSETS[i][1]);

			if (neighborCell != null) {
				if (!prevCells.contains(neighborCell)) {
					if (!cells.contains(neighborCell)
							&& !neighborCell.isImpassable()) {
						cells.enqueue(neighborCell);
						retValue++;
						// log.debug("         neighbor: " + neighborCell);
					} else {
						// log.debug("Case 1 (neighbor: " + neighborCell + ")");
					}
				} else {
					// log.debug("Case 2 (neighbor: " + neighborCell + ")");
				}
			} else {
				// log.debug("Case 3 (neighbor: " + neighborCell + ")");
			}

		}

		return retValue;

	}

	/**
	 * Calculates best path using a weight on openness
	 * 
	 * @param startPoint
	 *            starting point
	 * @param destPoint
	 *            ending point
	 * @return path array
	 */
	public static Point[] getPath1(Point startPoint, Point destPoint, Grid g,
			Logger log) {

		Queue<Cell> searchQueue = new Queue<Cell>();
		LinkedList<Point> path = new LinkedList<Point>();
		HashSet<Cell> dequeuedCells = new HashSet<Cell>();

		Cell startCell = g.getCell(startPoint);
		Cell destCell = g.getCell(destPoint);

		searchQueue.enqueue(startCell);

		// iterate until start cell is returned
		while (destCell != startCell) {

			Cell c = searchQueue.dequeue();
			dequeuedCells.add(c);

			addNeighbors(c, g, searchQueue, dequeuedCells, log);

			// If destination is found
			if (searchQueue.contains(destCell)) {

				path.add(c.getPoint());
				destCell = c;

				// restart queue
				searchQueue = new Queue<Cell>();
				searchQueue.enqueue(startCell);

			}

		}

		path.addFirst(destPoint);
		Collections.reverse(path);

		return (Point[]) path.toArray();

	}

	/**
	 * From a given point gets the best cell to target and returns a move
	 * 
	 * @param g
	 * @param p
	 * @return
	 */
	public static Move getTargetedMoveFrom(Point point_, Grid grid_, Logger log) {

		// get the targeted cell
		Cell dest = getTargetedCell(point_, grid_, 0, log);
		log.info(dest);

		Point destPoint = dest.getPoint();

		// get the path to that cell
		Point points[] = getPath1(point_, destPoint, grid_, log);

		// get the first move
		// Move m = getPathMove(point_, destPoint, grid_, log);

		return null; // m;
	}

	/**
	 * Calculates path score based on path array
	 * 
	 * @param pArray
	 * @return score
	 */
	public static int getPathScore(Point[] pArray) {

		return 0;
	}

	/**
	 * Determines how to move from point from_ to point to_
	 * 
	 * @param from_
	 * @param to_
	 * @return
	 */
	public static Move getMove(Point from_, Point to_, Grid grid_)
			throws IllegalArgumentException {
		// int diffX = from_.x - to_.x;
		// int diffY = from_.y - to_.y;

		if (to_ == null) {
			throw new IllegalArgumentException("to" + to_);
		}

		if (grid_.getCell(to_).isImpassable()) {
			throw new IllegalArgumentException("to" + grid_.getCell(to_));
		}

		int diffX = to_.x - from_.x;
		int diffY = to_.y - from_.y;

		switch (diffY) {
		case 1:
			switch (diffX) {
			case 1:
				return new Move(GameConstants.SOUTHEAST);
			case 0:
				return new Move(GameConstants.SOUTH);
			case -1:
				return new Move(GameConstants.SOUTHWEST);

			}
		case 0:
			switch (diffX) {
			case 1:
				return new Move(GameConstants.EAST);
			case -1:
				return new Move(GameConstants.WEST);

			}
		case -1:
			switch (diffX) {
			case 1:
				return new Move(GameConstants.NORTHEAST);
			case 0:
				return new Move(GameConstants.NORTH);
			case -1:
				return new Move(GameConstants.NORTHWEST);

			}
		}
		return null;
	}

	/**
	 * For a given point it evaluates best move amongst nearby neighbors
	 * 
	 * @param point_
	 * @return
	 */
	public static Point getMoveFrom(Point point_, Grid grid_, Logger log,
			Point prev, boolean isCoastHugging) throws IllegalArgumentException {

		ArrayList<Cell> unVisitedNeighbours = new ArrayList<Cell>();
		ArrayList<Cell> visitedNeighbours = new ArrayList<Cell>();
		double maxUnvisitedScore = Integer.MIN_VALUE;
		int maxUnvisitedScoreIndex = 0;

		int oldestVisitIndex = 0;
		int oldestVisit = Integer.MAX_VALUE;

		int maxScoreCount = 0;

		for (int i = 0; i < 8; i++) {
			Cell c = grid_.getCell(point_, GameConstants._dx[i + 1],
					GameConstants._dy[i + 1]);
			if (c != null) {
				if (!c.isImpassable()) {
					if (c.isVisited()) {
						visitedNeighbours.add(c);
						visitedNeighbours.add(c);
						if (c.getLastTurnVisited() < oldestVisit) {
							oldestVisitIndex = visitedNeighbours.size() - 1;
							oldestVisit = c.getLastTurnVisited();

						}
					} else {

						double cellScore = c.getScore();

						// if (isCoastHugging && prev != null) {
						// if (2 * c.getDistanceFrom(point_) == c
						// .getDistanceFrom(prev)) {
						// cellScore += 5;
						// }
						// }
						// log.debug(cellScore);

						if (cellScore > maxUnvisitedScore) {

							maxUnvisitedScore = cellScore;

							unVisitedNeighbours.add(0, c);
							maxScoreCount = 1;
						} else if (cellScore == maxUnvisitedScore) {
							maxScoreCount++;
							unVisitedNeighbours.add(0, c);
						} else {
							unVisitedNeighbours.add(c);

						}
					}
				}
			}
		}

		// log.debug("MaxScore" + maxUnvisitedScore);

		if (unVisitedNeighbours.size() != 0) {
			maxUnvisitedScoreIndex = (int) Math.random() / Integer.MAX_VALUE
					% (maxScoreCount + 1);
			Cell to = unVisitedNeighbours.get(maxUnvisitedScoreIndex);

			return to.getPoint();

		} else /* if(visitedNeighbours.size() != 0) */{
			// if (visitedNeighbours.size() == 1) {
			// throw new IllegalArgumentException("Caught in a trap");
			// }

			Cell to = visitedNeighbours.get(oldestVisitIndex);
			return to.getPoint();

		}
	}

	public static ArrayList<Point> getBeeLinePath(Point src_, Point dest_,
			boolean includeDest_) {
		int dx = dest_.x - src_.x;
		int dy = dest_.y - src_.y;

		ArrayList<Point> path = new ArrayList<Point>();

		if (dx == 0) {
			int moddy = Math.abs(dy);
			for (int i = 1; i <= moddy; i++) {
				path.add(new Point(src_.x, src_.y + i * dy / moddy));
			}
		} else if (dy == 0) {
			int moddx = Math.abs(dx);
			for (int i = 1; i <= moddx; i++) {
				path.add(new Point(src_.x + i * dx / moddx, src_.y));
			}
		} else {
			int moddx = Math.abs(dx);
			int moddy = Math.abs(dy);
			int signx = dx > 0 ? 1 : -1;
			int signy = dy > 0 ? 1 : -1;

			int diagDist = moddx < moddy ? moddx : moddy;

			for (int i = 1; i <= diagDist; i++) {
				path.add(new Point(src_.x + i * signx, src_.y + i * signy));
			}

			if (moddx > moddy) {

				for (int i = diagDist + 1; i <= moddx; i++) {
					path.add(new Point(src_.x + i * signx, src_.y + diagDist
							* signy));

				}
			} else if (moddy > moddx) {

				for (int i = diagDist + 1; i <= moddy; i++) {
					path.add(new Point(src_.x + diagDist * signx, src_.y + i
							* signy));
				}
			}
		}

		if (!includeDest_) {
			path.remove(path.size() - 1);
		}
		return path;

	}

	/**
	 * Lumbers towards target
	 * 
	 * @param currentLocation_
	 * @param grid_
	 * @param lti_
	 * @return
	 */
	public static Point lumberTowardsTarget(Point currentLocation_,
			LumberingTargetInfo lti_) {
		Grid grid_ = lti_.getGrid();
		ArrayList<Cell> _unvisitedCellList = new ArrayList<Cell>();
		ArrayList<Cell> _visitedCellList = new ArrayList<Cell>();

		// Enqueue edge neighbors
		for (int i = 1; i < GameConstants._dx.length; i++) {
			Cell neighborCell = grid_.getCell(currentLocation_,
					GameConstants._dx[i], GameConstants._dy[i]);
			if (neighborCell.isVisited()) {
				if (neighborCell.getLastTurnVisited() < lti_.getTurn()) {
					_visitedCellList.add(neighborCell);
				}
			} else {
				_unvisitedCellList.add(neighborCell);
			}
		}

		if (_unvisitedCellList.size() != 0) {
			int bestCellIndex = -1;
			double currDistToTarget = currentLocation_.distance(lti_
					.getTarget());
			double minDistToTarget = Double.MAX_VALUE;
			for (int i = 0; i < _unvisitedCellList.size(); i++) {
				double distToTarget = _unvisitedCellList.get(i).getPoint()
						.distance(lti_.getTarget());
				if (distToTarget < minDistToTarget
						&& distToTarget < currDistToTarget) {
					minDistToTarget = distToTarget;
					bestCellIndex = i;
				}
			}

			if (bestCellIndex != -1) {
				return _unvisitedCellList.get(bestCellIndex).getPoint();
			}
		}

		if (_visitedCellList.size() != 0) {
			int bestCellIndex = -1;
			double currDistToTarget = currentLocation_.distance(lti_
					.getTarget());
			double minDistToTarget = Double.MAX_VALUE;
			for (int i = 0; i < _visitedCellList.size(); i++) {
				double distToTarget = _visitedCellList.get(i).getPoint()
						.distance(lti_.getTarget());
				if (distToTarget < minDistToTarget
						&& distToTarget < currDistToTarget) {
					minDistToTarget = distToTarget;
					bestCellIndex = i;
				}
			}

			if (bestCellIndex != -1) {
				return _visitedCellList.get(bestCellIndex).getPoint();
			}
		}

		return null;
	}

	public static Point getOldestVisitedNeighbourIfNoUnvisitedNeighbourExists(
			Point currentLocation_, Grid grid_) {
		Point oldestNeighbour = null;
		int oldestTurn = Integer.MAX_VALUE;
		for (int i = 1; i < GameConstants._dx.length; i++) {
			Cell c = grid_.getCell(currentLocation_, GameConstants._dx[i],
					GameConstants._dy[i]);
			if (!c.isImpassable()) {

				if (c.isVisited()) {
					if (oldestTurn > c.getLastTurnVisited()) {
						oldestTurn = c.getLastTurnVisited();
						oldestNeighbour = c.getPoint();
					} else {
						return null;
					}
				}
			}
		}

		return oldestNeighbour;
	}

	public static ArrayList<Point> getPath2(Point startPoint, Point destPoint,
			Grid g, Logger log) {

		Queue<Cell> searchQueue = new Queue<Cell>();
		HashSet<Cell> dequeuedCells = new HashSet<Cell>();

		HashMap<Point, Point> _prevMap = new HashMap<Point, Point>();

		Cell startCell = g.getCell(startPoint);
		Cell destCell = g.getCell(destPoint);

		searchQueue.enqueue(startCell);

		boolean isDestFound = false;
		// iterate until start cell is returned
		while (!searchQueue.isEmpty()) {

			Cell c = searchQueue.dequeue();
			dequeuedCells.add(c);

			addNeighbors(c, g, searchQueue, dequeuedCells, log, _prevMap);

			// If destination is found
			if (searchQueue.contains(destCell)) {
				isDestFound = true;
				break;

			}

		}

		if (isDestFound) {
			ArrayList<Point> path = new ArrayList<Point>();
			path.add(0, destPoint);
			Point curr = destPoint;
			Point prev = null;
			while (!curr.equals(startPoint)) {
				prev = _prevMap.get(curr);
				path.add(0, prev);
				curr = prev;
			}

			return path;
		} else {
			return null;
		}
	}
}
