/*
 * Scoring function:
 * distance_to_mountains
 * distance_to_water
 * adjacent_to_other_player_location
 * visited_by_us
 * visited_by_other_player
 */

package explorandum.g1;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;

import explorandum.GameConstants;
import explorandum.Logger;
import explorandum.Move;
import explorandum.Player;

public class G1DijkstraTest implements Player {

	Logger log;
	Random rand;
	Map map;
	int range;
	int num_explorers;
	int num_rounds;
	int previous_direction = 1;
	ShortestPath sp;
	ArrayList<Cell> currentPath;
	Point current_location;
	Point loc1 = new Point(5, 3);
	Point loc2 = new Point(2, 2);
	Point loc3 = new Point(4, 6);
	int dcounter;
	int explorerId;

	public void register(int _explorerID, int rounds, int explorers,
			int _range, Logger _log, Random _rand) {
		explorerId = _explorerID;
		log = _log;
		rand = _rand;
		map = new MapInHash();
		range = _range;
		num_explorers = explorers;
		num_rounds = rounds;
		sp = new ShortestPath(map);

		log.debug("\nRounds:" + rounds + "\nExplorers:" + explorers
				+ "\nRange:" + range);
	}

	public Color color() throws Exception {
		return Color.RED;
	}

	public Move move(Point currentLocation, Point[] offsets,
			Boolean[] hasExplorer, Integer[][] otherExplorers,
			Integer[] terrain, int time,Boolean StepStatus) {
		try {
			current_location = currentLocation;
			map.add(explorerId, currentLocation, offsets, hasExplorer,
					otherExplorers, terrain, time, range);

			int direction_to_target = getDirectionToOpenSpace();
			if (direction_to_target > 0)
			{
				return new Move(ACTIONS[direction_to_target]);
			}

			// Use random direction if all above approaches don't work.
			return new Move(ACTIONS[rand.nextInt(ACTIONS.length)]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public int getDirectionToOpenSpace() {
		sp.updateShortestPath(current_location);
				
		if (currentPath == null || currentPath.size() == 0) {
			// pick cell with highest score and go there :)
			ArrayList<Cell> sortedLandCells = map.getLandCellsSortedByScore();
			Cell target_cell = sortedLandCells.get(0);
			this.currentPath = sp.getShortestDistancePath(target_cell.location);			
		}
		
		if (currentPath != null && currentPath.size() > 0) {
			Point next = currentPath.get(0).location;
			currentPath.remove(0);
			int direction_to_target = Map.getDirection(current_location, next);
			return direction_to_target;
		}
		
		return 0;
	}

	public String name() throws Exception {
		return "G1 Dijkstra Test";
	}
}
