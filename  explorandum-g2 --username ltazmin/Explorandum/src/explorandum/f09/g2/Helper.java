package explorandum.f09.g2;

import java.awt.Point;
import java.util.ArrayList;

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
	 * Calculates score
	 * 
	 * @param p
	 *            Point object of current cell
	 * @return score
	 */
	public static int getCellScore(Point p) {

		Cell cell;
		return 0;

	}

	/**
	 * Calculates best path
	 * 
	 * @param p1
	 * @param p2
	 * @return path array
	 */
	public static Point[] getPath(Point p1, Point p2) {

		return null;

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

		System.err.println("MaxScore" + maxScore);
		if (unVisitedNeighbours.size() != 0) {

			Cell to = unVisitedNeighbours.get(maxScoreIndex);
			System.out.println(to);
			return Helper.getMove(point_, to.getPoint());
		} else /* if(visitedNeighbours.size() != 0) */{
			Cell to = visitedNeighbours.get(oldestVisitIndex);
			return Helper.getMove(point_, to.getPoint());
		}
	}

}
