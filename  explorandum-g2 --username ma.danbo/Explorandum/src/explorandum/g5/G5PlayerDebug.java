/* 
 * 	$Id: DumbPlayer.java,v 1.3 2007/11/14 22:04:58 johnc Exp $
 * 
 * 	Programming and Problem Solving
 *  Copyright (c) 2007 The Trustees of Columbia University
 */
package explorandum.g5;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import explorandum.Logger;
import explorandum.Move;
import explorandum.Player;

public class G5PlayerDebug implements Player
{

	Logger log;
	Random rand;
	private Board board;
	private int myRange;
	private Point destination;
	private Point currentLocation;
	private ArrayList<Point> shortestPath;
	
	public static final double INFINITY = Double.MAX_VALUE;

	public void register(int explorerID, int rounds, int explorers, int range, Logger _log, Random _rand)
	{
		
		myRange = range;
		log = _log;
		rand = _rand;

		board = null;
		destination = null;
		shortestPath = null;
		
		//log.debug("\nRounds:" + rounds + "\nExplorers:" + explorers + "\nRange:" + range);
	}

	public Color color() throws Exception
	{
		return Color.BLACK;
	}

	/**
	 * NOTES
	 * currentLocation  - computer coordinates of your location (x, -y)
	 * offsets - a list of points relative to the start position that index the below arrays.
	 * hasExplorer - list of booleans representing visible (as determined by d) cells, true if an explorer is there, false otherwise (includes self)
	 * otherExplorers - list of player ids representing visible cells. 
	 *      for otherExplorers[i][j] 
	 *      i=num of visible cells, 
	 *      j=num of players (in the case the more than one player is in one cell) 
	 *      and the value represents the player's id thats in there or null
	 * terrain -  list of terrains... 0:open cell, 1:water, 2:mountain
	 * time - current round
	 */
	
	public Move move(Point currentLocation, Point[] offsets, Boolean[] hasExplorer, Integer[][] otherExplorers, Integer[] terrain, int time,Boolean StepStatus) throws Exception
	{
		
		//set current location
		// if the board is null, we're at the origin
		if (board == null) {
			board = new Board(new BoardLocation(0, 2.0, true, false));
		} else {
			board.setLocation(currentLocation.x, currentLocation.y, new BoardLocation(0, 2.0, true, false));
		}
		
		this.currentLocation = currentLocation;
		board.setCurrentLocation(currentLocation);
		
		if (currentLocation.equals(destination)) {
			destination = null;
			shortestPath = null;
		}
		
		int myIndex = getCurrentLocationIndex(currentLocation, offsets);
		
		//System.out.println(myIndex);
		
		//add all locations
		for (int i=0; i < offsets.length; i++ ) {
				if(i == myIndex) {
					board.setLocation( offsets[i].x, offsets[i].y, new BoardLocation(terrain[i], 2.0, true, false));
				}
				else {
					double myscore = calculateScore(currentLocation, offsets[i], terrain[i], myRange);
					board.setLocation(offsets[i].x, offsets[i].y, new BoardLocation(terrain[i], (double)myscore, false, false));
				}
				
		}
		
		//Iterator<BoardRow> bit = board..nodes.iterator();
		//for(BoardRow b : board) {
			
		//}
		
		int action = 0;
		
		// if we have a destination, move towards it
		if (destination != null) {
			//log.debug("Moving towards destination " + destination + "\n-------");
			//if we see a player..and in less than 100 rounds? ... turn around
			//if (range <100 && )
			
			action = moveTowardsDestination();
		} else {
			// if we don't, pick a new one
			//log.debug("Picking new destination");
			destination = pickNewDestination();
			
			if (destination != null) {
				//log.debug("Generating new shortest path");
				shortestPath = generateShortestPathTo(destination);
				//log.debug("Moving towards destination " + destination + "\n-------");
				action = moveTowardsDestination();
			} else {
				//log.debug("Making random move!\n-------");
				action = ACTIONS[rand.nextInt(ACTIONS.length)];
			}
		}
					

		//System.out.println(board.toString(true));
		//System.out.println(board.toString(false));
		
		return new Move(action);
	}

	private double calculateScore(Point currentLocation, Point point, int terrain, int d) {
		// get euclidean distance
		double distance = 0;
		if (terrain == 1){
			return 1.0;
		}
		else {
			distance = ( Math.abs(point.x - currentLocation.x) + Math.abs(point.y - currentLocation.y) );
			return 1 - (distance/(2*d));
		}
	}

	private int moveTowardsDestination() {
		Point p;
		if (shortestPath == null || shortestPath.size() == 0) {
			shortestPath = null;
			p = destination;
		} else {
		 p = shortestPath.remove(0);
		}
		
		for (int i = 0; i < _dx.length; i++) {
			if (_dx[i] == p.x - currentLocation.x && _dy[i] == p.y - currentLocation.y) {
				return ACTIONS[i];
			}
		}
		
		// we should never get here, but if we do, stay put
	//	log.error("I can't move to a destination!  Not supposed to happen!");
		return 0;
	}
	
	private ArrayList<Point> generateShortestPathTo(Point destination) {
//		 	 1  function Dijkstra(Graph, source):
//			 2      for each vertex v in Graph:           // Initializations
//			 3          dist[v] := infinity               // Unknown distance function from source to v
//			 4          previous[v] := undefined          // Previous node in optimal path from source
//			 5      dist[source] := 0                     // Distance from source to source
//			 6      Q := the set of all nodes in Graph    // All nodes in the graph are unoptimized - thus are in Q
//			 7      while Q is not empty:                 // The main loop
//			 8          u := node in Q with smallest dist[]
//			 9          remove u from Q
//			10          for each neighbor v of u:         // where v has not yet been removed from Q.
//			11              alt := dist[u] + dist_between(u, v)       // be careful in 1st step - dist[u] is infinity yet
//			12              if alt < dist[v]              // Relax (u,v)
//			13                  dist[v] := alt
//			14                  previous[v] := u
//			15      return previous[]
		
		ArrayList<Point> shortestPath = new ArrayList<Point>();
		ArrayList<Point> nodes = new ArrayList<Point>();
		HashMap<Point, Double> distance = new HashMap<Point, Double>();
		HashMap<Point, Point> previous = new HashMap<Point, Point>();
		
		
		Point lowerBound = board.getLowerBound();
		Point upperBound = board.getUpperBound();
		
		// first we'll populate our lists
		for (int y = lowerBound.y; y < upperBound.y; y++) {
			for (int x = lowerBound.x; x < upperBound.x; x++) {
				BoardLocation l = board.getLocation(x, y);
				if (l != null && l.terrain == 0) {
					Point p = new Point(x, y);
					nodes.add(p);
					distance.put(p, INFINITY);
					previous.put(p, null);
				}
			}
		}
		
		distance.put(currentLocation, 0.0);

		while (nodes.size() > 0) {
			// get the node with the shortest distance
			double currentDistance = distance.get(nodes.get(0));
			int shortestIndex = 0;
			for (int i = 1; i < nodes.size(); i++) {
				if (distance.get(nodes.get(i)) < currentDistance) {
					shortestIndex = i;
				}
			}
			
			// get all the neighbors of the shortest distance that are in nodes
			Point shortest = nodes.remove(shortestIndex);
			ArrayList<Point> neighbors = getAccessableNeighbors(shortest);
			for (Iterator<Point> itr = neighbors.iterator(); itr.hasNext();) {
				Point p = itr.next();
				if (nodes.contains(p)) {
					double alt = distance.get(shortest);
					// make sure alt is at maximum infinity, we don't want to roll over
					// if its not, add the path cost, which is 1 for orthogonal and 1.5 for diagonal
					if (alt != INFINITY) {
						if (shortest.x != p.x && shortest.y != p.y) {
							alt += 1.5; // orthogonal
						} else {
							alt += 1;
						}
					}
					
					if (alt < distance.get(p)) {
						distance.put(p, alt);
						previous.put(p, shortest);
					}
				}
			}			
		}
		
		while (previous.get(destination) != null) {
			shortestPath.add(0, destination);
			destination = previous.get(destination);
		}
		
		return shortestPath;
	}
	
	/**
	 * Returns all neighbors of Point p that are not water or mountain
	 * @param p
	 * @return
	 */
	private ArrayList<Point> getAccessableNeighbors(Point p) {
		ArrayList<Point> neighbors = new ArrayList<Point>();
		
		for (int i = p.x - 1; i < p.x + 2; i++) {
			for (int j = p.y - 1; j < p.y + 2; j++) {
				if (board.getLocation(i, j) != null && board.getLocation(i, j).terrain == 0) {
					neighbors.add(new Point(i, j));
				}
			}
		}

		return neighbors;
	}
	
	private Point pickNewDestination() {
		// can we see any cells that are next to water that we
		// haven't stepped on?
		ArrayList<Point> nearWater = getProximalToWater();
		
		Collections.sort(nearWater, new DistanceComparator());
		
		if (nearWater.size() > 0) {
			Point dest = nearWater.get(0);
			
			// make sure we move diagonally down
			// a shore line if there is one
			
			// so, if we're about to go to a neighbor
			ArrayList<Point> neighbors = getAccessableNeighbors(currentLocation);
			if (neighbors.contains(dest)) {
				int destWaterCount = countNextToWater(dest);
				
				// make sure there aren't any other neighbors that we haven't been to
				// with a higher water count
				Point newDest = null;
				for (Iterator<Point> itr = neighbors.iterator(); itr.hasNext();) {
					Point n = itr.next();
					if (n.equals(dest)) {
						continue;
					}
					
					int waterCount = countNextToWater(n);
					if (waterCount > destWaterCount) {
						BoardLocation newDestBL = board.getLocation(n);
						if (newDestBL != null && newDestBL.score != 2) {
							destWaterCount = waterCount;
							newDest = n;
						}
					}
				}
				
				if (newDest != null) {
					// this is a bit hackish, there's not really a point in revisiting the corners on a
					// diagonal, so let's manually set their seen value to 2
					BoardLocation oldDestBL = board.getLocation(dest);
					oldDestBL.setScore(2.0);
					dest = newDest;
				}
			}
			
			return dest;
		}
		
		// else lets move randomly
		return null;
	}
	
	/**
	 * 
	 * @return a list of all the land nodes that are next to water
	 * 
	 */
	private ArrayList<Point> getProximalToWater() {
		ArrayList<Point> nearWater = new ArrayList<Point>();
		
		Point lowerBound = board.getLowerBound();
		Point upperBound = board.getUpperBound();
		
		for (int y = lowerBound.y; y < upperBound.y; y++) {
			for (int x = lowerBound.x; x < upperBound.x; x++) {
				BoardLocation bl = board.getLocation(x, y);
				if (bl != null && bl.terrain == 0 && bl.score != 2.0) {
					int count = countNextToWater(x, y);
					
					if (count > 0) {
						Point p = new Point(x, y);
//						ArrayList<Point> neighbors = getAccessableNeighbors(p);
//						for (Iterator<Point> itr = neighbors.iterator(); itr.hasNext();) {
//							Point n = itr.next();
//							BoardLocation nbl = board.getLocation(n);
//							
////							if (nbl != null && nbl.score != 2 && )
//						}
//						
//						// add this unless it has a neighbor that is
//						// 
						nearWater.add(new Point(p));
					}
				}
			}
		}
		
		return nearWater;
	}
	
	private int countNextToWater(Point p) {
		return countNextToWater(p.x, p.y);
	}
	
	private int countNextToMountain(Point p) {
		return countNextToMountain(p.x, p.y);
	}
	
	private int countNextToMountain(int x, int y) {
		return countNextTo(x, y, 2);
	}
	
	private int countNextToWater(int x, int y) {
		return countNextTo(x, y, 1);
	}
	
	private int countNextTo(int x, int y, int terrain) {
		int count = 0;
		
		for (int i = x - 1; i < x + 2; i++) {
			for (int j = y - 1; j < y + 2; j++) {
				if (i == x && j == y) {
					continue;
				}
				if (board.getLocation(i, j) != null && board.getLocation(i, j).terrain == terrain) {
					count++;
				}
			}
		}
		
		return count;
	}
	
	public String name() throws Exception
	{
		return "G5 Player";
	}
	
	public void getTerrain(Point someLocation, Point[] offsets, Integer[] terrain) {
		
	}
	
	public int getCurrentLocationIndex(Point myLocation, Point[] myOffsets) {
		int myindex = 0;
		for (int j=0; j<myOffsets.length; j++) {
			if((myOffsets[j].x == myLocation.x)&&(myOffsets[j].y == myLocation.y))
				myindex = j;
		}
		return myindex;
	}
	
	private class DistanceComparator implements Comparator {

		public int compare(Object o1, Object o2) {
			Point p1 = (Point)o1;
			Point p2 = (Point)o2;
			
			if (currentLocation.distance(p1) > currentLocation.distance(p2)) 
				return 1;	
			if (currentLocation.distance(p1) < currentLocation.distance(p2))
				return -1;	
			return 0;
		}
		
	}
	
}
