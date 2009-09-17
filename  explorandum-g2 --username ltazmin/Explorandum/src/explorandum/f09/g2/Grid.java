package explorandum.f09.g2;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import explorandum.GameConstants;
import explorandum.Move;

public class Grid {

	private static HashMap<Point, Cell> gridMap = new HashMap<Point, Cell>();

	public static Cell getCell(Point p) {
		return gridMap.get(p);
	}

	public static void putCell(Point p, Cell c) {
		gridMap.put(p, c);
	}

	public static HashSet<Point> campSites = new HashSet<Point>();

	public static Point getClosestCampSite(Point p) {
		return null;
	}

	public static void putCell(Point p) {
		campSites.add(p);
	}

	public static void updateVisitedCellInformation(Point p, int turn,
			boolean isFootPrintPresent, boolean isExplorerPresent) {
		Cell cell = getCell(p);
		if (cell != null) {

			// Update Visitation
			cell.setVisited(true);
			cell.setVisitationTurn(turn);

			// Update Ownership
			if (cell.getOwner() != Cell.OWNERSHIP_US) {
				if (isFootPrintPresent) {
					cell.setOwner(Cell.OWNERSHIP_THEM);
				} else {
					cell.setOwner(Cell.OWNERSHIP_US);
				}
			}
		} else {
			cell = new Cell(p);
			cell.setVisited(true);
			cell.setVisitationTurn(turn);
			cell.setOwner(Cell.OWNERSHIP_US);
			putCell(p, cell);
		}
	}

	public static void updateSeenCellInformation(Point p, int terrain,
			boolean isExplorerSeen) {
		Cell cell = getCell(p);
		if (cell != null) {

			if (!cell.isVisited() && isExplorerSeen) {
				cell.setOwner(Cell.OWNERSHIP_THEM);
			}

		} else {
			cell = new Cell(p);
			cell.setTerrain(terrain);
			if (isExplorerSeen) {
				cell.setOwner(Cell.OWNERSHIP_THEM);
			}
			putCell(p, cell);
		}
	}

	public static Move getMoveFrom(Point point_) {

		ArrayList<Cell> unVisitedNeighbours = new ArrayList<Cell>();
		ArrayList<Cell> visitedNeighbours = new ArrayList<Cell>();
		int maxScore = Integer.MIN_VALUE;
		int maxScoreIndex = 0;
		int oldestVisitIndex = 0;
		int oldestVisit = Integer.MAX_VALUE;
		for (int i = 0; i < 8; i++) {
			Cell c = getCell(new Point(point_.x + GameConstants._dx[i + 1],
					point_.y + GameConstants._dy[i + 1]));
			if (c != null) {
				if (!c.isImpassable()) {
					if (c.isVisited()) {
						visitedNeighbours.add(c);
						if (c.getVisitationTurn() < oldestVisit) {
							oldestVisitIndex = visitedNeighbours.size() - 1;
							oldestVisit = c.getVisitationTurn();

						}
					} else {
						unVisitedNeighbours.add(c);
						int cellScore = getCellScore(c);
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
			return determineMove(point_, to.getPoint());
		} else /* if(visitedNeighbours.size() != 0) */{
			Cell to = visitedNeighbours.get(oldestVisitIndex);
			return determineMove(point_, to.getPoint());
		}
	}

	private static Move determineMove(Point from_, Point to_) {
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

	public static int getCellScore(Cell c_) {
		int px = c_.getPoint().x;
		int py = c_.getPoint().y;
		int score = 0;
		System.out.println(c_);

		// Edge Neighbours
		for (int i = 0; i < Constants.edgeNeighborOffsets.length; i++) {
			Cell c = getCell(new Point(
					px + Constants.edgeNeighborOffsets[i][0], py
							+ Constants.edgeNeighborOffsets[i][1]));
			System.out.println("I : " + i + " " + c);
			if (c != null) {
				switch (c.getTerrain()) {
				case Cell.TERRAIN_LAND:
					score += 1;
					break;

				case Cell.TERRAIN_WATER:
					score += 3;
					break;

				case Cell.TERRAIN_MOUNTAIN:
					score += 2;
					break;
				}
				score += 1;

				if (c.isVisited()) {
					score -= 2;
				}
			}

		}

		// Edge Neighbours
		for (int i = 0; i < Constants.vertexNeighborOffsets.length; i++) {
			Cell c = getCell(new Point(px
					+ Constants.vertexNeighborOffsets[i][0], py
					+ Constants.vertexNeighborOffsets[i][1]));
			System.out.println("I : " + i + " " + c);
			if (c != null) {
				switch (c.getTerrain()) {
				case Cell.TERRAIN_LAND:
					score += 1;
					break;

				case Cell.TERRAIN_WATER:
					score += 3;
					break;

				case Cell.TERRAIN_MOUNTAIN:
					score += 2;
					break;
				}
				if (c.isVisited()) {
					score -= 1;

				}
			}

		}
		System.out.println("Score " + score);
		return score;
	}
}
