package explorandum.g1;

import explorandum.GameConstants;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class MapInHash extends Map {
	private HashMap<Point, Cell> map;
	private int min_x = 0, min_y = 0, max_x = 0, max_y = 0;
	
	/*
	 * The set of all discovered land cells, sorted by their score.
	 */
	private ArrayList<Cell> landCellList;

	public MapInHash() {
		this.map = new HashMap<Point, Cell>();
		this.landCellList = new ArrayList<Cell>();
	}

	// Add what the player sees to the map then updates the scores and link costs.
	public void add(int explorerId, Point currentLocation, Point[] offsets,
			Boolean[] hasExplorer, Integer[][] otherExplorers,
			Integer[] terrain, int time, int range) {
		for (int i = 0; i < offsets.length; i++) {
			Point location = offsets[i];
			if (!map.containsKey(location)) {
				Cell c = new Cell(new Point(location), terrain[i],
						currentLocation.distance(location), time, range);

				map.put(location, c);
				buildNeighborLinks(c);
				if(c.terrain == GameConstants.LAND)
				{
					this.landCellList.add(c);					
				}

				// To add other players data...
				if (hasExplorer[i]) {
					seeOtherPlayers(explorerId, c, otherExplorers[i], range);
				}

			} else {
				Cell c = getCell(location);
				double distance = currentLocation.distance(location);
				if (distance < c.claimed_distance) {
					c.claimed_distance = distance;
					c.claimed_time = time;
				}

				// To add other players data...
				if (hasExplorer[i]) {
					seeOtherPlayers(explorerId, c, otherExplorers[i], range);
				}

			}
			updateBound(location);
		}

		for(Cell landCell : this.landCellList)
		{
			updateScoresAndLinks(landCell,this.map.get(currentLocation),time);			
		}
	}

	public Point getmin() {
		return new Point(min_x, min_y);
	}

	public Point getmax() {
		return new Point(max_x, max_y);
	}

	// To add other players data...
	private void markNeighbors(Point p, int range) {
		ArrayList<Point> neighbors = getNeighbors(p, range);
		for (Point point : neighbors) {
			if (point == null)
				continue;

			Cell cell = map.get(point);
			if (cell == null)
				continue;

			double distance = point.distance(p);
			if (distance < cell.claimed_distance_other) {
				cell.claimed_distance_other = distance;
			}
		}
	}

	// To add other players data...
	private void seeOtherPlayers(int myId, Cell c, Integer[] otherExplorers,
			int range) {
		for (int i = 0; i < otherExplorers.length; i++) {
			if (otherExplorers[i] != null) {
				int val = otherExplorers[i];
				if (val != myId) {
					c.claimed_distance_other = 0;
					c.other_players.add(val);
					markNeighbors(c.location, range);
				}
			}
		}
	}

	private void buildNeighborLinks(Cell c) {
		for (int d = 1; d <= 8; d++) {
			Cell neighbor = getCell(c.location.x + GameConstants._dx[d],
					c.location.y + GameConstants._dy[d]);
			if (neighbor != null)
			{
				if(c.terrain == GameConstants.LAND && neighbor.terrain == GameConstants.LAND) {
					// Build a link from c to neighbor
					Link link = new Link();
					link.destination = neighbor;
					link.cost = (d % 2 == 1) ? 2 : 3; // orthogonal == 2 hours,
					// diagonal == 3 hours
					link.direction = d;
					link.lastUpdateTime = 0; // haven't updated the cost yet
					// actually
					c.neighbors.add(link);
	
					// Build a link from neighbor to c
					Link r_link = new Link();
					r_link.destination = c;
					r_link.cost = (d % 2 == 1) ? 2 : 3; // orthogonal == 2 hours,
					// diagonal == 3 hours
					r_link.direction = (d - 1 + 4) % 8 + 1; // reverse direction
					r_link.lastUpdateTime = 0; // haven't updated the cost yet
					// actually
					neighbor.neighbors.add(r_link);
				}
				else if(c.terrain == GameConstants.WATER)
				{
					neighbor.distance_to_water = 1;
					neighbor.number_of_adjacent_water_cells += 1;
				}
				else if(neighbor.terrain == GameConstants.WATER)
				{
					c.distance_to_water = 1;
					c.number_of_adjacent_water_cells += 1;
				}
				else if(c.terrain == GameConstants.MOUNTAIN)
				{
					neighbor.distance_to_mountains = 1;
					neighbor.number_of_adjacent_mountain_cells += 1;
				}
				else if(neighbor.terrain == GameConstants.MOUNTAIN)
				{
					c.distance_to_mountains = 1;
					c.number_of_adjacent_mountain_cells += 1;
				}
			}
			
			if (neighbor != null)
				neighbor.unexplored_directions.remove((d - 1 + 4) % 8 + 1);
			else
				c.unexplored_directions.add(d);
		}
	}

	/*
	 * Recursively update all links by recalculating all cell's distance from
	 * water, distance from mountains, and ultimately scores. Then recalculate
	 * all links' costs based on the sum of the two linked cells. Only updates
	 * cells and links once by storing the currentTime as the lastUpdateTime.
	 * which means that distance from water/mountains could take a while to
	 * propagate.
	 * @param c the scoring location's cell
	 * @param currentLocation the cell of the current location of our explorer
	 * @param currentTime the current game time
	 */
	private void updateScoresAndLinks(Cell c, Cell currentLocation, int currentTime) {
		for (int d = 1; d <= 8; d++) {
			for(Link neighborLink : c.neighbors)
			{
				Cell neighbor = neighborLink.destination;

				if(c.lastUpdateTime < currentTime)
				{
					// update distance to nearest water or mountains
					if((neighbor.distance_to_water + 1) < c.distance_to_water)
					{
						c.distance_to_water = neighbor.distance_to_water + 1;
					}
					if((neighbor.distance_to_mountains + 1) < c.distance_to_mountains)
					{
						c.distance_to_mountains = neighbor.distance_to_mountains + 1;
					}

					c.updateScore(currentLocation, currentTime);			
				}
				
				if(neighbor.lastUpdateTime < currentTime)
				{		
					// update distance to nearest water or mountains
					if((c.distance_to_water + 1) < neighbor.distance_to_water)
					{
						neighbor.distance_to_water = c.distance_to_water + 1;
					}
					if((c.distance_to_mountains + 1) < neighbor.distance_to_mountains)
					{
						neighbor.distance_to_mountains = c.distance_to_mountains + 1;
					}
		
					neighbor.updateScore(currentLocation, currentTime);
				}
				
				if(neighborLink.lastUpdateTime < currentTime)
				{
					// orthogonal == 2 hours, diagonal == 3 hours
					int directional_multiplier = (neighborLink.direction % 2 == 1) ? 2 : 3;

					double updatedCost = 0;
					updatedCost += directional_multiplier + (c.score + neighbor.score);

					neighborLink.cost = updatedCost;
					neighborLink.lastUpdateTime = currentTime;
				}
			}
		}
	}

	private void updateBound(Point p) {
		if (p.x < min_x)
			min_x = p.x;
		if (p.x > max_x)
			max_x = p.x;
		if (p.y < min_y)
			min_y = p.y;
		if (p.y > max_y)
			max_y = p.y;
	}

	// Get the current bound of the map.
	public Rectangle getBound() {
		return new Rectangle(min_x, min_y, max_x - min_x, max_y - min_y);
	}

	// Get the cell at (x, y). The coordinates are relative to the start point.
	public Cell getCell(int x, int y) {
		return getCell(new Point(x, y));
	}

	// Get the cell at p. The coordinates are relative to the start point.
	public Cell getCell(Point p) {
		return map.get(p);
	}

	// Print debug message to stdout
	public void printDebugMsg() {
		System.out.println("=== Map ===");
		// for (int y = max_y; y >= min_y; y--) {
		// for (int x = max_x; x >= min_x; x--) {
		for (int y = min_y; y <= max_y; y++) {
			for (int x = min_x; x <= max_x; x++) {
				Cell c = getCell(x, y);
				if (c != null) {
					if (c.terrain == 0)
						System.out.print("_ ");
					else if (c.terrain == 1)
						System.out.print("~ ");
					else
						System.out.print("^ ");
				} else {
					System.out.print("? ");
				}
			}
			System.out.println();
		}
	}

	// Get the coordinates of all discovered cells
	public Set<Point> getAllDiscoveredCoordinates() {
		return map.keySet();
	}

	/*
	 * Get all discovered land cells, sorted by their score.
	 */
	public ArrayList<Cell> getLandCellsSortedByScore() {
		Collections.sort(this.landCellList);
		return this.landCellList;
	}

}
