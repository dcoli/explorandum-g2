package explorandum.g1;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Set;
import java.util.ArrayList;
import java.util.SortedSet;

import explorandum.GameConstants;

public abstract class Map {
	// Add what the player sees to the map.
	public abstract void add(int explorerId, Point currentLocation,
			Point[] offsets, Boolean[] hasExplorer, Integer[][] otherExplorers,
			Integer[] terrain, int time, int range);

	public abstract Point getmin();

	public abstract Point getmax();

	// Get the current bound of the map.
	public abstract Rectangle getBound();

	// Get the cell at (x, y). The coordinates are relative to the start point.
	public abstract Cell getCell(int x, int y);

	// Get the cell at p. The coordinates are relative to the start point.
	public abstract Cell getCell(Point p);

	// Get the coordinates of all discovered cells
	public abstract Set<Point> getAllDiscoveredCoordinates();

	// Get all discovered land cells, sorted by score
	public abstract ArrayList<Cell> getLandCellsSortedByScore();

	// Print debug message to stdout
	public abstract void printDebugMsg();

	// Get a list of neighbors in some range. This function does consider
	// mountains as occluders.
	public ArrayList<Point> getNeighbors(Point p, double range) {
		ArrayList<Point> neighbors = new ArrayList<Point>();
		for (int x = (int) (p.x - range); x <= p.x + range; x++) {
			for (int y = (int) (p.y - range); y <= p.y + range; y++) {
				Point neighbor_point = new Point(x, y);
				if (p.distance(neighbor_point) <= range + 0.00001) {
					// check if there is a mountin in between
					boolean is_visible = true;
					double distance = p.distance(neighbor_point);
					for (int i = 1; i < distance; i++) {
						Point p_in_between = new Point(
								(int) (neighbor_point.x + (i / distance)
										* (p.x - neighbor_point.x)),
								(int) (neighbor_point.y + (i / distance)
										* (p.y - neighbor_point.y)));
						if (p_in_between.equals(neighbor_point))
							continue;
						Cell cell_in_between = getCell(p_in_between);
						if (cell_in_between != null
								&& cell_in_between.terrain == 2) {
							is_visible = false;
							break;
						}
					}
					if (is_visible)
						neighbors.add(neighbor_point);
				}
			}
		}
		return neighbors;
	}

	// Measure the openness of a cell.
	public double getCellOpenness(Point p, double range) {
		Cell c = getCell(p);
		if (c != null && c.terrain != 0)
			return 0;

		double openness = 0;
		ArrayList<Point> neighbors = getNeighbors(p, range);
		for (Point neighbor_point : neighbors) {
			c = getCell(neighbor_point);
			if (c != null) {
				double all_claimed_distance = Math.min(c.claimed_distance,
						c.claimed_distance_other);
				if (c.terrain == 0 || c.terrain == 2)
					openness += Math.max(0, all_claimed_distance
							- p.distance(neighbor_point));
				else { // water
					openness += Math.max(0, (all_claimed_distance - p
							.distance(neighbor_point)) * 20);
				}
			} else {
				openness += range - p.distance(neighbor_point);
			}
		}
		return openness;
	}

	public Point getNeighborPoint(Point p, int direction) {
		return new Point(p.x + GameConstants._dx[direction], p.y
				+ GameConstants._dy[direction]);
	}

	public static int getDirection(Point a, Point b) {
		if (b.x == a.x && b.y < a.y)
			return 1;
		else if (b.x > a.x && b.y < a.y)
			return 2;
		else if (b.x > a.x && b.y == a.y)
			return 3;
		else if (b.x > a.x && b.y > a.y)
			return 4;
		else if (b.x == a.x && b.y > a.y)
			return 5;
		else if (b.x < a.x && b.y > a.y)
			return 6;
		else if (b.x < a.x && b.y == a.y)
			return 7;
		else if (b.x < a.x && b.y < a.y)
			return 8;
		return 0;
	}
}
