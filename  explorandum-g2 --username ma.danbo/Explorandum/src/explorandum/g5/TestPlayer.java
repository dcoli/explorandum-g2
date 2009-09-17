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

import explorandum.GameConstants;
import explorandum.Logger;
import explorandum.Move;
import explorandum.Player;

public class TestPlayer implements Player
{

	Logger log;
	Random rand;
	private Board board;
	private int myRange;
	private int myRounds;
	private int myID;

	private Point destination;
	private Point destinationOffset;
	private Point currentLocation;
	private ArrayList<Point> shortestPath;

	private ArrayList< ArrayList<Point> > playerTracks;

	
	public static final double INFINITY = Double.MAX_VALUE;

	public void register(int explorerID, int rounds, int explorers, int range, Logger _log, Random _rand)
	{
		
		myRange = range;
		log = _log;
		rand = _rand;
		myID = explorerID;
		myRounds = 0;

		board = null;
		destination = null;
		destinationOffset =  new Point(0,0);
		shortestPath = null;
		
		log.debug("\nRounds:" + rounds + "\nExplorers:" + explorers + "\nRange:" + range);
	}

	public Color color() throws Exception
	{
		return new Color(0,128,128);
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
		
		//print out otherexplorers array
//		for (int i=0; i < otherExplorers.length; i++ ) {
//			for (int y=0; y < otherExplorers[i].length; y++ ) {
//				System.out.println(i + " " + y + " " + otherExplorers[i][y]);
//			}	
//		}
		
		myRounds++;
		

		//set current location
		// if the board is null, we're at the origin
		if (board == null) {
			board = new Board(new BoardLocation(0, 2.0, true, false));
			//initiate playerTracks
			playerTracks = new ArrayList<ArrayList<Point>>() ;
			for(int w=0; w<otherExplorers[0].length; w++) {
				playerTracks.add(new ArrayList<Point>() );
			}
		} else {
			board.setLocation(currentLocation.x, currentLocation.y, new BoardLocation(0, 2.0, true, false));
		}
		
		this.currentLocation = currentLocation;
		board.setCurrentLocation(currentLocation);
		
		if (currentLocation.equals(destination)) {
			destination = null;
			shortestPath = null;
		}
		
		//my index location in the arrays
		int myIndex = getCurrentLocationIndex(currentLocation, offsets);
		System.out.println(myIndex);	
		
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
		
		
		int action = 0;
		
		//update playerTracks
		detectOtherPlayers(hasExplorer, offsets, otherExplorers);
		printPlayerTracks();
		
		// if we're going towards an unexplored area, lets make sure
		// that there's no new water that we've seen.  If we do see
		// some new water, lets get a new destination.
		if (destination != null && board.getLocation(destination) == null && getProximalToWater().size() > 0) {
			destination = null;
		}
		
		// we'll generate our shortest path here if destination != null
		// this is because generateShortestPath() might set destination
		// to be null if the space is unreachable and we want to have a
		// chance to pick a new destination if this is the case
		
		if (destination != null) {
			boolean nullDestination = board.getLocation(destination) == null ? true : false;
//			shortestPath = generateShortestPath(destination, nullDestination);
			shortestPath = generateShortestPath(destination, true);
		}
		
		// if we have a destination, move towards it
		if (destination != null) {
			log.debug("Moving towards destination " + destination + "\n-------");	
			action = moveTowardsDestination();
		} else {
			// if we don't, pick a new one
			log.debug("Picking new destination");

			do {
				destination = pickNewDestination();
				
				// if the destination is null, we're
				// moving randomly
				if (destination == null) {
					log.debug("Making random move!\n-------");
					action = ACTIONS[rand.nextInt(ACTIONS.length)];
					return new Move(action);
				}
				
				log.debug("Generating new shortest path");
				boolean nullDestination = board.getLocation(destination) == null ? true : false;
//				shortestPath = generateShortestPath(destination, nullDestination);
				shortestPath = generateShortestPath(destination, true);
			} while (destination == null);
			
			if (destination != null) {
				
//				//detect if we are following or being followed
//				int[] playersInView = getPlayersInView();
//				System.out.println("IN VIEW: " + playersInView[0]);
//
//				if(getPlayersInView()!=null) {
//					for(int y=0; y<playersInView.length;y++) {
//						//do something now that we know this player is in view
//						
//						//find player with most recorded moves
//						//if he has 2 or 3 moves we can tell where hes going
//						System.out.println("SS: " + playerTracks.get(playersInView[y]).size());
//
//						if( playerTracks.get(playersInView[y]).size()>1) {
//
//							//if coming at us
//							double move1 = calculateScore(currentLocation, playerTracks.get(playersInView[y]).get(0), 0, myRange);
//							double move2 = calculateScore(currentLocation, playerTracks.get(playersInView[y]).get(1), 0, myRange);
//							System.out.println("1 and 2 scores:  " + move1 + " " + move2);
//
//							//if he hugs water and coming at us
//							//then move over 1 square
//							if(move2 > move1) {//we can infer hes getting closer
//								ArrayList<Point> waterCellsNearOpp = getAccessableNeighborsTerrain(playerTracks.get(playersInView[y]).get(1), 1);
//								System.out.println("GETTING CLOSER");
//								if (waterCellsNearOpp.size() > 1) { //we assume he is along water or a mountain range
//									int celldiff = playerTracks.get(playersInView[y]).get(1).x - waterCellsNearOpp.get(0).x;
//									boolean xhug = true;
//									if (celldiff==0) {
//										celldiff = playerTracks.get(playersInView[y]).get(1).y - waterCellsNearOpp.get(0).y;
//										xhug = false;
//									}
//									if(xhug) {
//										destination.x = destination.x + celldiff;
//										destinationOffset = new Point(celldiff, 0);
//									}
//									else {
//										destination.y = destination.y + celldiff;
//										destinationOffset = new Point(0, celldiff);
//
//									}
//									System.out.println("HUGGING: " +xhug);
//									
////									//here we assume player has coast-hugged and we will update our map
////									Point playerLocation = playerTracks.get(playersInView[y]).get(1);
////									Point playerOffset = new Point(-1*destinationOffset.x, -1*destinationOffset.y);
////									updateMapWithPlayersMoves(playerLocation, playerOffset);
//
//								}
//								//if he hugs water and following us
//								//then stay
//								
//							}
//						}
//						//else he has 1 move so keep moving to destination?
//						//or should we stay put
//					}
//				}
				
				
				
				log.debug("Moving towards destination " + destination + "\n-------");
				action = moveTowardsDestination();
			} else {
				log.debug("Making random move!\n-------");
				action = ACTIONS[rand.nextInt(ACTIONS.length)];
			}
		}
					
		// if destination offset != (0,0) then update players assumed cells prev traveled by player
//		if( destinationOffset.x!=0 || destinationOffset.y!=0) {
//			System.out.println("dfdsgdsF");
//			board.getLocation(currentLocation.x-destinationOffset.x, currentLocation.y-destinationOffset.y).score=2.0;
//		}
		
		
		System.out.println(board.toString(true));
		System.out.println(board.toString(false));
		
		return new Move(action);
	}

	private void updateMapWithPlayersMoves(Point playerLocation, Point playerOffset) {
		
		// TODO Auto-generated method stub
		
	}

	private int[] getPlayersInView() {
		int count = 0;
		int[] players = new int[playerTracks.size()];
		for(int j=0; j<playerTracks.size(); j++ ) {
			if(playerTracks.get(j).size() >0 ) {
				players[count] = j;
				count++;
			}
		}
		return players;
	}

	/* Updates the playertracks arraylist with each visible players current moves and deletes players tracks when they are no longer in range of view
	 * 
	 * @param: argument are the matching arrays passed in in Move()
	 * @return
	 */
	private void detectOtherPlayers(Boolean[] hasExp, Point[] offsets, Integer[][] otherExp ) {
		ArrayList playersInView = new ArrayList();
		for(int j=0;j<offsets.length;j++) {
				// TODO :  figure out situation where there is more than one player at a place
				//get other player id
				for(int u=0; u<otherExp[j].length;u++) {
					if( (otherExp[j][u]!=null)&&(otherExp[j][u]!=myID)) {
						playersInView.add(otherExp[j][u]);
						//playerTracks.get(otherExp[j][u]).add(new Point(currentLocation.x + offsets[j].x, currentLocation.y + offsets[j].y)); 
						playerTracks.get(otherExp[j][u]).add(new Point(offsets[j].x, offsets[j].y)); 
						board.getLocation(new Point(offsets[j].x, offsets[j].y)).score = 2.0;
					}
				}
		}
		//erase player tracks if they are no longer in view
		for(int j=0; j<playerTracks.size(); j++ ) {
				if( (playerTracks.get(j).size() >0 ) && (!playersInView.contains(j)) && (j!=myID) ) {
					playerTracks.get(j).clear();
					System.out.println("Deleting the tracks of player " + j);
				}
		}
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

	private void updateShortestPath() {
		for(int i=0; i< shortestPath.size(); i++) {
			shortestPath.get(i).x += destinationOffset.x;
			shortestPath.get(i).y += destinationOffset.y;
		}
	}
	
	private int moveTowardsDestination() {
		Point p;
		if (shortestPath == null || shortestPath.size() == 0) {
			shortestPath = null;
			p = destination;//new Point(destination.x + destinationOffset.x, destination.y + destinationOffset.y);
		} else {
		 p = shortestPath.remove(0);
		}
		
		for (int i = 0; i < _dx.length; i++) {
			System.out.println(p.x - currentLocation.x);
			System.out.println(p.y - currentLocation.y);

			if (_dx[i] == p.x - currentLocation.x && _dy[i] == p.y - currentLocation.y) {
				return ACTIONS[i];
			}
		}
		
		// we should never get here, but if we do, stay put
		log.error("I'm unable to reach the destination!  We should never be here (I think I fixed this stuff)!");
		
		return 0;
	}

	private ArrayList<Point> generateShortestPath(Point destination, boolean canStepOnNulls) {
//	 	 1  function Dijkstra(Graph, source):
//		 2      for each vertex v in Graph:           // Initializations
//		 3          dist[v] := infinity               // Unknown distance function from source to v
//		 4          previous[v] := undefined          // Previous node in optimal path from source
//		 5      dist[source] := 0                     // Distance from source to source
//		 6      Q := the set of all nodes in Graph    // All nodes in the graph are unoptimized - thus are in Q
//		 7      while Q is not empty:                 // The main loop
//		 8          u := node in Q with smallest dist[]
//		 9          remove u from Q
//		10          for each neighbor v of u:         // where v has not yet been removed from Q.
//		11              alt := dist[u] + dist_between(u, v)       // be careful in 1st step - dist[u] is infinity yet
//		12              if alt < dist[v]              // Relax (u,v)
//		13                  dist[v] := alt
//		14                  previous[v] := u
//		15      return previous[]
	
	ArrayList<Point> shortestPath = new ArrayList<Point>();
	ArrayList<Point> nodes = new ArrayList<Point>();
	HashMap<Point, Double> distance = new HashMap<Point, Double>();
	HashMap<Point, Point> previous = new HashMap<Point, Point>();
	
	
	Point lowerBound = board.getLowerBound();
	Point upperBound = board.getUpperBound();
	
	if (destination.x >= upperBound.x) {
		upperBound.x = destination.x + 1;
	}
	if (destination.y >= upperBound.y) {
		upperBound.y = destination.y + 1;
	}
	if (destination.x < lowerBound.x) {
		lowerBound.x = destination.x;
	}
	if (destination.y < lowerBound.y) {
		lowerBound.y = destination.y;
	}
	
	
	// first we'll populate our lists
	for (int y = lowerBound.y; y < upperBound.y; y++) {
		for (int x = lowerBound.x; x < upperBound.x; x++) {
			BoardLocation l = board.getLocation(x, y);
			
			if ((l == null && canStepOnNulls) || (l != null && l.terrain == 0)) {
				Point p = new Point(x, y);
				
				if (l == null) {
					log.debug("A null location: " + p);
				}
				
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
				currentDistance = distance.get(nodes.get(i));
			}
		}
		
		// get all the neighbors of the shortest distance that are in nodes
		Point shortest = nodes.remove(shortestIndex);
		ArrayList<Point> neighbors = getAccessableNeighbors(shortest, canStepOnNulls);
		for (Iterator<Point> itr = neighbors.iterator(); itr.hasNext();) {
			Point p = itr.next();
			if (nodes.contains(p)) {
				double alt = distance.get(shortest);
				// make sure alt is at maximum infinity, we don't want to roll over
				// if its not, add the path cost, which is 1 for orthogonal and 1.5 for diagonal				
				if (alt != INFINITY) {
					if (shortest.x != p.x && shortest.y != p.y) {
						alt += 1.5;  // diagonal
					} else {
						alt += 1; // orthogonal
					}
				}
								
				if (alt < distance.get(p)) {
					distance.put(p, alt);
					previous.put(p, shortest);
				}
			}
		}			
	}
	
	printDijkstra(distance, previous, upperBound, lowerBound);
	
	while (previous.get(destination) != null) {
		shortestPath.add(0, destination);
		destination = previous.get(destination);
	}
	
	// if there is no shortestPath to get to our destination and our destination
	// is null, it means we're looking to go somewhere unreachable.  We'll
	// fix this by pretending the space is a mountain that we can't get to
	if (shortestPath.size() == 0 && board.getLocation(destination) == null) {
		board.setLocation(destination.x, destination.y, new BoardLocation(GameConstants.MOUNTAIN, 2.0, true, false));
		this.destination = null;		// get rid of this destination so we pick another one
	}
	
	return shortestPath;
}
	
	private void printDijkstra(HashMap<Point, Double> distance, HashMap<Point, Point> previous, Point upperBound, Point lowerBound) {
		System.out.println();
		for (int y = lowerBound.y; y < upperBound.y; y++) {
			for (int x = lowerBound.x; x < upperBound.x; x++) {
				Double d = distance.get(new Point(x, y));
				if (d != null) {
					System.out.print(d.toString().substring(0, 3) + " ");
				} else {
					System.out.print(".   ");
				}
			}
		System.out.println();
		}
	}
	
	/**
	 * Returns all neighbors of Point p that are not water or mountain
	 * @param p
	 * @return
	 */
	private ArrayList<Point> getAccessableNeighbors(Point p, boolean canStepOnNulls) {
		ArrayList<Point> neighbors = new ArrayList<Point>();
		
		for (int i = p.x - 1; i < p.x + 2; i++) {
			for (int j = p.y - 1; j < p.y + 2; j++) {
				BoardLocation bl = board.getLocation(i, j);
				if ((bl != null && bl.terrain == 0) || (bl == null && canStepOnNulls)) {
					if (i == p.x && j == p.y) {
						continue;
					}
					neighbors.add(new Point(i, j));
				}
			}
		}

		return neighbors;
	}
	
	/**
	 * Returns all neighbors of Point p that are of type terrain
	 * @param p, teerrain
	 * @return
	 */
	private ArrayList<Point> getAccessableNeighborsTerrain(Point p, int terraintype) {
		ArrayList<Point> neighbors = new ArrayList<Point>();

		for (int i = p.x - 1; i < p.x + 2; i++) {
			for (int j = p.y - 1; j < p.y + 2; j++) {
				if (board.getLocation(i, j) != null && board.getLocation(i, j).terrain == terraintype) {
					neighbors.add(new Point(i, j));
				}
			}
		}

		return neighbors;
	}
	
	private Point pickNewDestination() {
		return new Point(0, 5);
	}
	
//	private Point pickNewDestination() {
//		// can we see any cells that are next to water that we
//		// haven't stepped on?
//		ArrayList<Point> nearWater = getProximalToWater();
//		Collections.sort(nearWater, new DistanceComparator());
//		
//		if (nearWater.size() > 0) {			
//			Point dest = nearWater.get(0);
//			
//			// make sure we move diagonally down
//			// a shore line if there is one
//			
//			// so, if we're about to go to a neighbor
//			ArrayList<Point> neighbors = getAccessableNeighbors(currentLocation, false);
//			if (neighbors.contains(dest)) {
//				int destWaterCount = countNextToWater(dest);
//				
//				// make sure there aren't any other neighbors that we haven't been to
//				// with a higher water count
//				Point newDest = null;
//				for (Iterator<Point> itr = neighbors.iterator(); itr.hasNext();) {
//					Point n = itr.next();
//					if (n.equals(dest)) {
//						continue;
//					}
//					
//					int waterCount = countNextToWater(n);
//					if (waterCount > destWaterCount) {
//						BoardLocation newDestBL = board.getLocation(n);
//						if (newDestBL != null && newDestBL.score != 2) {
//							destWaterCount = waterCount;
//							newDest = n;
//
//						}
//					}
//				}
//				
//				if (newDest != null) {
//					// this is a bit hackish, there's not really a point in revisiting the corners on a
//					// diagonal, so let's manually set their seen value to 2
//					BoardLocation oldDestBL = board.getLocation(dest);
//					oldDestBL.setScore(2);
//					dest = newDest;
//				}
//			}
//			
//			//update dest with offset
//			dest = new Point(dest.x + destinationOffset.x, dest.y + destinationOffset.y);
//			
//			System.out.println("offset " + destinationOffset);
//			System.out.println("New Destiantion w/ offset " + dest);
//			return dest;
//		}
//		
//		ArrayList<Point> spaces = board.getUnexploredSpaces();
//		
//		// Lets explore in the middle of the board
//		if (spaces.size() > 0) {			
//			Point p = spaces.get(rand.nextInt(spaces.size()));
//			log.debug("Moving to unexplored destination: " + p);
//			return p;
//		}
//		
//		// else lets move randomly
//		return null;
//	}
		
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
		return "G5 Test Player";
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
	
	private void printPlayerTracks() {
		System.out.println("-------PLAYER-TRACKS-------");
		//System.out.println(j);
		for(int j=0; j<playerTracks.size(); j++ ) {
			System.out.println(j);
			for (int y=0; y<playerTracks.get(j).size(); y++) {
				System.out.print(playerTracks.get(j).get(y) + " , ");
			}
			System.out.println();
		}
		System.out.println("-------PLAYER-TRACKS-------");
	}
	
}
