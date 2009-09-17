package explorandum.g1;

import java.awt.Point;
import java.util.*;

public class Cell implements Comparable<Cell> {
	public Point location;
	public int terrain;
	public ArrayList<Link> neighbors = new ArrayList<Link>();
	public HashSet<Integer> unexplored_directions = new HashSet<Integer>();

	public Cell previous;
	public double minDist; // used in Dijkstra's shortest path
	public int nullvalue = 0;

	public double claimed_distance_other;
	public HashSet<Integer> other_players = new HashSet<Integer>();

	/*
	 * The score of this cell, which is a Like in golf, a low score is better!
	 */
	public double score;

	// The closest distance from our player to the cell.
	// E.g. if we see the cell for the second time and this time it is closer,
	// we update claimed_distance and claimed_time;
	public double claimed_distance;

	// The last time claimed_distance get updated.
	public int claimed_time;
	
	/*
	 * The number of neighboring cells we have to go through to reach water; 1 == adjacent water
	 */
	public int distance_to_water;
	
	/*
	 * The number of neighboring cells we have to go through to reach mountains; 1 == adjacent mountain
	 */
	public int distance_to_mountains;
	
	public int number_of_adjacent_water_cells;
	public int number_of_adjacent_mountain_cells;

	/*
	 * The game time that the score and cell properties were last updated.
	 */
	public int lastUpdateTime;
	
	/*
	 * sight distance d from the game engine
	 */
	private int range;
	
	public Cell(Point location, int terrain, double claimed_distance,
			int claimed_time, int range) {
		this.location = new Point(location);
		this.terrain = terrain;
		this.claimed_distance = claimed_distance;
		this.claimed_time = claimed_time;
		this.range = range;
		
		this.claimed_distance_other = 99999; // Initialize the others
											 // claimed distance to
											 // infinity
		this.score = 99999;
		this.distance_to_water = 99999;
		this.distance_to_mountains = 99999;
		
		this.number_of_adjacent_water_cells = 0;		
		this.number_of_adjacent_mountain_cells = 0;
		
		this.lastUpdateTime = -1;
	}

	/*
	 * Update this cell's score based on certain qualities of the cell.
	 * 
	 * @return The score after it's been updated.
	 */
	public double updateScore(Cell currentLocation, int currentTime) {
		double d = this.range * 1.0; // this.range == d from the game engine (sight distance)
		
		double distance_from_current_location = currentLocation.location.distance(this.location); 
		
		double updatedScore = 2 * Math.pow(d,2); // remember, low score is good; default is 2 * d^2

		// (d+1)^3 penalty if visited, less if seen from a distance
		updatedScore += Math.pow((d / (1.0 + Math.pow(this.claimed_distance,2))) + 1.0,3);
		// (d+1)^3 penalty if visited by someone else, less if seen from a distance, 1.0 if unvisited
		updatedScore += Math.pow((d / (1.0 + Math.pow(this.claimed_distance_other,2))) + 1.0,3);

		// d^2 benefit if next to water, less if far
		// assume a maximum distance to water of sight distance
		if(this.distance_to_water <= d)
		{
			updatedScore -= Math.pow((d - this.distance_to_water + 1),2);
		}

		// 0.5 * d^2 benefit if d from mountains, less if closer (0.5 benefit if adjacent)
		// assume a maximum distance to water of sight distance
		if(this.distance_to_mountains <= d)
		{
			//range (1,d+1)
			updatedScore -= 0.5 * Math.pow(( d - this.distance_to_mountains + 1),2);
		}
			//range (d-8,d)
		updatedScore -= this.unexplored_directions.size();
			
		
		// updatedScore *= distance_from_current_location;
		
		this.lastUpdateTime = currentTime;
		this.score = updatedScore;
		return this.score;
	}

	/*
	 * Compare based on cell score
	 */
	public int compareTo(Cell otherCell) {
		if (otherCell instanceof Cell) {
			if (((Cell) otherCell).score > this.score) {
				return -1;
			} else if (this.score > ((Cell) otherCell).score) {
				return 1;
			} else {
				return 0;
			}
		} else {
			return 0; // wtf mate! :)
		}
	}
}

class nullcompare implements Comparator {
	public int compare(Object o1, Object o2) {
		int n1, n2;
		Cell c1 = (Cell) (o1);
		Cell c2 = (Cell) (o2);
		n1 = c1.nullvalue;
		n2 = c2.nullvalue;
		return n1 == n2 ? 0 : (n1 > n2) ? -1 : 1;
	}

}
