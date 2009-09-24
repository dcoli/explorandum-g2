package explorandum.f09.g2;



import java.awt.Point;
import java.util.ArrayList;

import explorandum.GameConstants;
import explorandum.Move;
import explorandum.Logger;

/**
 * Helper class that supports scoring and path finding etc.
 * 
 * @author laima
 * 
 */
public class Helper {

	/**
	 * Finds the best cell to target
	 * 
	 * @param p
	 *            Point object of current cell
	 * @param g
	 *            Grid of the game
	 * @return optimal cell
	 */
	public static Cell getTargetedCell(Point startingPoint, Grid g, Logger log) {

		Cell startingCell = g.getCell(startingPoint);
		log.info(startingCell + " <--- START");
		
		Queue<Cell> neighborCells = new Queue<Cell>();
		BinaryHeap<Cell> candidateCells = new BinaryHeap<Cell>();
		
		// to prevent duplicates after dequeueing
		ArrayList<Cell> dequeuedCells = new ArrayList<Cell>();

		int howFarAway = 2 * Constants.RANGE; // how far from current cell to
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
			log.info(c + " SCORE " + c.getScore());
			
			// add its neighbors
			numNeighborCells += addNeighbors(c, g, neighborCells, dequeuedCells, log);
			
			// no valid neighbors then add to heap
			if (numNeighborCells < 1) {
				candidateCells.insert(c);
				log.info("Just added to heap (0): " + c + " SCORE " + c.getScore());
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
			log.info("Just added to heap (1): " + nc + " SCORE " + nc.getScore());
		}

		candidateCells.printHeap();
		//System.exit(0);
		
		// pick cell with highest score
		Cell destinationCell = ((BinaryHeap<Cell>) candidateCells).findMax();
		
		log.info("Now targetting "+destinationCell);

		return destinationCell;

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
	private static int addNeighbors(Cell c, Grid grid_, Queue<Cell> cells, ArrayList<Cell> prevCells, Logger log) {

		int retValue = 0;

		// Enqueue edge neighbors
		for (int i = 0; i < Constants.EDGE_NEIGHBOR_OFFSETS.length; i++) {
			Cell neighborCell = grid_.getCell(c.getPoint(),
					Constants.EDGE_NEIGHBOR_OFFSETS[i][0],
					Constants.EDGE_NEIGHBOR_OFFSETS[i][1]);

			// limitation in keeping score of null prevents its candidacy (let's ponder this)
			// if (neighborCell == null) {
			//	Point p = new Point();
			//	Cell nullCell = new Cell(p);
			//}
			
			// if the cells is not already in queue
			// and is land, add to queue
			if (neighborCell != null) {
				if (!prevCells.contains(neighborCell)){
					if (!cells.contains(neighborCell) && !neighborCell.isImpassable()) {
						cells.enqueue(neighborCell);
						retValue++;
						log.info("         neighbor: " + neighborCell);
					}	
					else {
						log.info("Case 1 (neighbor: " + neighborCell + ")");
					}
				}	
				else {
					log.info("Case 2 (neighbor: " + neighborCell + ")");
				}
			}	
			else {
				log.info("Case 3 (neighbor: " + neighborCell + ")");
			}

		}
		
		// Enqueue vertex neighbors
		for (int i = 0; i < Constants.VERTEX_NEIGHBOR_OFFSETS.length; i++) {
			Cell neighborCell = grid_.getCell(c.getPoint(),
					Constants.VERTEX_NEIGHBOR_OFFSETS[i][0],
					Constants.VERTEX_NEIGHBOR_OFFSETS[i][1]);
			
			if (neighborCell != null) {
				if (!prevCells.contains(neighborCell)){
					if (!cells.contains(neighborCell) && !neighborCell.isImpassable()) {
						cells.enqueue(neighborCell);
						retValue++;
						log.info("         neighbor: " + neighborCell);
					}	
					else {
						log.info("Case 1 (neighbor: " + neighborCell + ")");
					}
				}	
				else {
					log.info("Case 2 (neighbor: " + neighborCell + ")");
				}
			}	
			else {
				log.info("Case 3 (neighbor: " + neighborCell + ")");
			}

		}

		return retValue;

	}

	/**
	 * Calculates best path using breadth-first search
	 * 
	 * @param p1
	 *            starting point
	 * @param p2
	 *            ending point
	 * @return path array
	 */
	public static Move getPathMove(Point p1, Point p2, Grid g) {

		// For now get the next cell to go to in the general direction of
		// destination cell

		Move possibleMove = new Move(0);
		Point middleman;

		if (p1.y > p2.y) {
			if (p1.x > p2.x)
				possibleMove = new Move(GameConstants.SOUTHEAST);
			else if (p1.x == p2.x)
				possibleMove = new Move(GameConstants.SOUTH);
			else if (p1.x < p2.x)
				possibleMove = new Move(GameConstants.SOUTHWEST);
		}

		else if (p1.y == p2.y) {
			if (p1.x > p2.x)
				possibleMove = new Move(GameConstants.EAST);
			else if (p1.x < p2.x)
				possibleMove = new Move(GameConstants.WEST);

		}

		else if (p1.y < p2.y) {

			if (p1.x > p2.x)
				possibleMove = new Move(GameConstants.NORTHEAST);
			else if (p1.x == p2.x)
				possibleMove = new Move(GameConstants.NORTH);
			else if (p1.x < p2.x)
				possibleMove = new Move(GameConstants.NORTHWEST);

		}

		return possibleMove;

		/*
		 * Queue<Cell> queue = new Queue<Cell>(); LinkedList<Point> path = new
		 * LinkedList<Point>();
		 * 
		 * // breadth-first search Cell c1 = g.getCell(p1);
		 * 
		 * queue.enqueue(c1);
		 * 
		 * Point pointTo = p2;
		 * 
		 * // search backwards while (pointTo != p1) {
		 * 
		 * // get the cell right before destination cell Point prev =
		 * getPath(pointTo, g, queue);
		 * 
		 * // add to path list path.addFirst(prev);
		 * 
		 * // change destination cell pointTo = prev;
		 * 
		 * }
		 * 
		 * // add root path.addFirst(p1);
		 * 
		 * return (Point[])path.toArray();
		 */

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
		Cell dest = getTargetedCell(point_, grid_, log);
		log.info(dest);

		Point destPoint = dest.getPoint();

		// get the path to that cell
		// Point points[] = getPath(point_, destPoint, grid_);

		// get the first move
		Move m = getPathMove(point_, destPoint, grid_);

		return m;
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
	public static Move getMove(Point from_, Point to_) {
		// int diffX = from_.x - to_.x;
		// int diffY = from_.y - to_.y;

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
	public static Move getMoveFrom(Point point_, Grid grid_, Logger log) {

		ArrayList<Cell> unVisitedNeighbours = new ArrayList<Cell>();
		ArrayList<Cell> visitedNeighbours = new ArrayList<Cell>();
		double maxUnvisitedScore = Integer.MIN_VALUE;
		int maxUnvisitedScoreIndex = 0;
		int maxVisitedScore = Integer.MIN_VALUE;
		int maxVisitedScoreIndex = 0;
		int oldestVisitIndex = 0;
		int oldestVisit = Integer.MAX_VALUE;
		for (int i = 0; i < 8; i++) {
			Cell c = grid_.getCell(point_, GameConstants._dx[i + 1],
					GameConstants._dy[i + 1]);
			if (c != null) {
				if (!c.isImpassable()) {
					if (c.isVisited()) {
						visitedNeighbours.add(c);
//						if (c.getLastTurnVisited() < oldestVisit) {
//							oldestVisitIndex = visitedNeighbours.size() - 1;
//							oldestVisit = c.getLastTurnVisited();
//
//						}
						
						int cellOpennessScore = grid_.getOpennessScore(c.getPoint());
						log.info(cellOpennessScore);
						if (cellOpennessScore > maxVisitedScore) {
							maxVisitedScoreIndex = visitedNeighbours.size() - 1;
							maxVisitedScore = cellOpennessScore;

						}
					} else {
						unVisitedNeighbours.add(c);
						double cellScore = c.getScore();

						log.info(cellScore);
						if (cellScore > maxUnvisitedScore) {
							maxUnvisitedScoreIndex = unVisitedNeighbours.size() - 1;
							maxUnvisitedScore = cellScore;

						}
					}
				}
			}
		}

		log.info("MaxScore" + maxUnvisitedScore);

		if (unVisitedNeighbours.size() != 0) {

			Cell to = unVisitedNeighbours.get(maxUnvisitedScoreIndex);
			// log.info(to);
			return Helper.getMove(point_, to.getPoint());
		} else /* if(visitedNeighbours.size() != 0) */{

			// return Helper.getTargetedMoveFrom(point_, grid_);
			Cell to = visitedNeighbours.get(maxVisitedScoreIndex);
			return Helper.getMove(point_, to.getPoint());
		}
	}

	public static Move getNextMoveTowardsTarget(Point currentLocation_,
			Grid grid_, TargetInfo target_) {

		int dx = target_.getTarget().x - currentLocation_.x;
		int dy = target_.getTarget().y - currentLocation_.y;

		int[][] possOffsets = new int[3][2];
		if (dx == 0) {
			possOffsets[0] = new int[] { 0, (int) Math.signum(dy) * 1 };
			possOffsets[1] = new int[] { 1, (int) Math.signum(dy) * 1 };
			possOffsets[2] = new int[] { -1, (int) Math.signum(dy) * 1 };
		} else if (dy == 0) {
			possOffsets[0] = new int[] { (int) Math.signum(dx) * 1, 0 };
			possOffsets[1] = new int[] { (int) Math.signum(dx) * 1, 1 };
			possOffsets[2] = new int[] { (int) Math.signum(dx) * 1, -1 };
		} else {
			possOffsets[0] = new int[] { (int) Math.signum(dy) * 1,
					(int) Math.signum(dy) * 1 };
			possOffsets[1] = new int[] { 0, (int) Math.signum(dy) * 1 };
			possOffsets[2] = new int[] { (int) Math.signum(dx) * 1, 0 };

		}

		ArrayList<Cell> possMoves = new ArrayList<Cell>();
		double maxScore = Integer.MIN_VALUE;
		int maxScoreIndex = 0;
		for (int i = 0; i < possOffsets.length; i++) {
			Cell c = grid_.getCell(currentLocation_, possOffsets[i][0],
					possOffsets[i][1]);
			// log.info(c.getPoint() + " " + c.getScore());
			if (!c.isImpassable()) {
				possMoves.add(c);
				// int cellScore = c.getScore() - c.getLastTurnVisited() + ;
				double cellScore = c.getScore()
						- (int) c.getDistanceFrom(target_.getTarget());
				if (cellScore > maxScore) {
					maxScoreIndex = possMoves.size() - 1;
					maxScore = cellScore;
				}
			}
		}

		if (possMoves.size() != 0) {

			Cell to = possMoves.get(maxScoreIndex);
			// log.info(to);
			return Helper.getMove(currentLocation_, to.getPoint());
		} else {
			// TODO Auto-generated method stub
			return null;
		}
	}

}
