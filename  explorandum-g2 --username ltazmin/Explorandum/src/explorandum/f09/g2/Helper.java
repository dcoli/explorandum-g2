package explorandum.f09.g2;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;

import explorandum.GameConstants;
import explorandum.Move;

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
	 * @param g Grid of the game
	 * @return optimal cell
	 */
	public static Cell getOptimalCellDestination(Grid g, Point startingPoint) {

		Cell c = g.getCell(startingPoint);
		Queue<Cell> neighborCells = new Queue<Cell>();
		BinaryHeap<Cell> candidateCells = new BinaryHeap<Cell>();
		
		int howFarAway = 2 * Constants.RANGE; // how far from current cell to look for a target
		
		// evaluate neighbors within a range
		for (int i = 0; i < howFarAway; i++) {
			
			// add a set of neighbors if the cell isn't null and is traversable
			if (c != null) {
				if (!c.isImpassable()) {

					neighborCells = addNeighbors(c, g, neighborCells);	
				}	
			}
			
			c = neighborCells.dequeue();
			
			// evaluate that cell
			Grid.computeScore(c, g);
			candidateCells.insert(c);
			
		}
		
		// Pick cell with highest score
		Cell destinationCell = candidateCells.findMax();
		
		return destinationCell;

	}
	
	/**
	 * Adds to a queue immediate neighbors of a certain cell
	 * @param c the cell
	 * @param g
	 * @param cells
	 * @return the queue with added neighbors
	 */
	private static Queue<Cell> addNeighbors(Cell c, Grid g, Queue<Cell> cells) {
		
		Point point = c.getPoint();
		
		// Enqueue immediate neighbors
		for (int i = 0; i < Constants.EDGE_NEIGHBOR_OFFSETS.length; i++) {
			
			Cell neighborCell = g.getCell(new Point(point.x
					+ Constants.EDGE_NEIGHBOR_OFFSETS[i][0], point.y
					+ Constants.EDGE_NEIGHBOR_OFFSETS[i][1]));
			
			if (!cells.contains(neighborCell) && !neighborCell.isImpassable()) {
				cells.enqueue(neighborCell);
			}
			
		}
		
		return cells;
		
	}
	
	/**
	 * Calculates best path using breadth-first search
	 * 
	 * @param p1 starting point
	 * @param p2 ending point
	 * @return path array
	 */
	public static Move getPathMove(Point p1, Point p2, Grid g) {
		
		// For now get the next cell to go to in the general direction of destination cell
		
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
		Queue<Cell> queue = new Queue<Cell>();
		LinkedList<Point> path = new LinkedList<Point>();
		
		// breadth-first search
		Cell c1 = g.getCell(p1);
		
		queue.enqueue(c1);
		
		Point pointTo = p2;
		
		// search backwards
		while (pointTo != p1) {
			
			// get the cell right before destination cell
			Point prev = getPath(pointTo, g, queue);
			
			// add to path list
			path.addFirst(prev);
			
			// change destination cell
			pointTo = prev;
			
		}
		
		// add root
		path.addFirst(p1);
		
		return (Point[])path.toArray();
		*/

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
	 * From a given point gets the best cell to target and returns a move
	 * @param g
	 * @param p
	 * @return
	 */
	public static Move getTargetedMoveFrom(Point point_, Grid grid_) {
		
		// get the targeted cell
		Cell dest = getOptimalCellDestination(grid_, point_);
		System.out.println(dest);
		
		Point destPoint = dest.getPoint();
		
		// get the path to that cell
		//Point points[] = getPath(point_, destPoint, grid_);
		
		// get the first move
		Move m = getPathMove(point_, destPoint, grid_);
		
		return m;
	}
	
	
	/**
	 * For a given point it evaluates best move amongst nearby neighbors
	 * 
	 * @param point_
	 * @return
	 */
	public static Move getMoveFrom(Point point_, Grid grid_) {

		ArrayList<Cell> unVisitedNeighbours = new ArrayList<Cell>();
		ArrayList<Cell> visitedNeighbours = new ArrayList<Cell>();
		int maxScore = Integer.MIN_VALUE;
		int maxScoreIndex = 0;
		int oldestVisitIndex = 0;
		int oldestVisit = Integer.MAX_VALUE;
		for (int i = 0; i < 8; i++) {
			Cell c = grid_.getCell(new Point(point_.x
					+ GameConstants._dx[i + 1], point_.y
					+ GameConstants._dy[i + 1]));
			if (c != null) {
				if (!c.isImpassable()) {
					if (c.isVisited()) {
						visitedNeighbours.add(c);
						if (c.getLastTurnVisited() < oldestVisit) {
							oldestVisitIndex = visitedNeighbours.size() - 1;
							oldestVisit = c.getLastTurnVisited();

						}
					} else {
						unVisitedNeighbours.add(c);
						int cellScore = c.getScore();
						if (cellScore > maxScore) {
							maxScoreIndex = unVisitedNeighbours.size() - 1;
							maxScore = cellScore;

						}
					}
				}
			}
		}

		//System.out.println("MaxScore" + maxScore);
		
		if (unVisitedNeighbours.size() != 0) {

			Cell to = unVisitedNeighbours.get(maxScoreIndex);
			//System.out.println(to);
			return Helper.getMove(point_, to.getPoint());
		} else /* if(visitedNeighbours.size() != 0) */{
			
			//return Helper.getTargetedMoveFrom(point_, grid_);
			Cell to = visitedNeighbours.get(oldestVisitIndex);
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
		int maxScore = Integer.MIN_VALUE;
		int maxScoreIndex = 0;
		for (int i = 0; i < possOffsets.length; i++) {
			Cell c = grid_.getCell(currentLocation_, possOffsets[i][0],
					possOffsets[i][1]);
			//System.out.println(c.getPoint() + " " + c.getScore());
			if (!c.isImpassable()) {
				possMoves.add(c);
				//int cellScore = c.getScore() - c.getLastTurnVisited() + ;
				int cellScore = c.getScore() - c.getDistanceFrom(target_.getTarget());
				if (cellScore > maxScore) {
					maxScoreIndex = possMoves.size() - 1;
					maxScore = cellScore;
				}
			}
		}

		if (possMoves.size() != 0) {

			Cell to = possMoves.get(maxScoreIndex);
			//System.out.println(to);
			return Helper.getMove(currentLocation_, to.getPoint());
		} else {
			// TODO Auto-generated method stub
			return null;
		}
	}

}
