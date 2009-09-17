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

public class G5Player implements Player
{

	Logger log;
	Random rand;
	private Board board;
	private int myRange;
	private int myRounds;
	private int ratioRounds;
	private int myID;

	private Point destination;
	private Point destinationOffset;
	private Point currentLocation;
	private ArrayList<Point> shortestPath;
	
	private double ratio;
	private boolean randDirBool;
	
	private boolean chooseRandDir;
	private int newDir;

	private ArrayList< ArrayList<Point> > playerTracks;
	private ArrayList<Point> myTracks;
	private int myTracksIndex;


	
	public static final double INFINITY = Double.MAX_VALUE;

	public void register(int explorerID, int rounds, int explorers, int range, Logger _log, Random _rand)
	{
		
		myRange = range;
		log = _log;
		rand = _rand;
		myID = explorerID;
		myRounds = rounds;

		board = null;
		destination = null;
		destinationOffset =  new Point(0,0);
		shortestPath = null;
		chooseRandDir = false;
		newDir = 0;
		
		randDirBool = true;
		ratio = rounds*.1;
		
		myTracksIndex = 0;
		myTracks = new ArrayList<Point>(3);
		
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
		
		
		ratioRounds++;
		if (ratioRounds > ratio) {
			randDirBool = false;
		}

		//set current location
		// if the board is null, we're at the origin
		if (board == null) {
			board = new Board(new BoardLocation(0, 2.0, true, false));
			//initiate playerTracks
			playerTracks = new ArrayList<ArrayList<Point>>() ;
			for(int w=0; w<otherExplorers[0].length; w++) {
				playerTracks.add(new ArrayList<Point>() );
			}
			myTracks.add(myTracksIndex, currentLocation);
			myTracksIndex++;
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
		
		log.debug(board.toString(true));
		log.debug(board.toString(false));
		
		
		//update my tracks
		if(myTracksIndex>2) {
			myTracksIndex = 0;
			myTracks = new ArrayList<Point>(3);
			myTracks.add(myTracksIndex, currentLocation);
		}
		else {
			myTracks.add(myTracksIndex, currentLocation);
			myTracksIndex++;
		}
		
		//break out of 3 same move deadlock
		if(myTracksIndex>2) {
			if( myTracks.get(0).x == myTracks.get(1).x && myTracks.get(1).x == myTracks.get(2).x ){
				if( myTracks.get(0).y == myTracks.get(1).y && myTracks.get(1).y == myTracks.get(2).y ){
					//in deadlock
					log.debug("Making random move! IN DEADLOCK HOW???\n-------");
					//int mo =  
					action = ACTIONS[rand.nextInt(ACTIONS.length+1)-1];
					return new Move(action);
					
//					chooseRandDir = true;
//					log.debug("Choosing New Direction to move has been set");
//					//do first move
//					newDir = chooseNewDirection();
//					log.debug("new desty has been set as " + ACTIONS[newDir]);
//					return new Move(newDir);	
					
				}
			}
		}
			

		
		
		//check state variable
		//if we are moving in some direction, keep doing it
		if(chooseRandDir == true && randDirBool == true) {
			//choose new direction
			log.debug("Choosing New Direction is in play");
			log.debug(newDir);
			
			//check if we see water (TODO add other things we like)
			if( countNextToWater(currentLocation.x, currentLocation.y) > 0 || board.getUnexploredSpaces(true).size() > 10) {
				chooseRandDir = false;
			}
			//if no water check validity of point in the chosen direction
			// if not valid (is bad terrain, or we have stepped there)
			//then choose a new direction
			//TODO must handle case if surrounded by those options.....
			else {
				Point nextDoor = new Point(currentLocation.x + _dx[newDir], currentLocation.y + _dy[newDir] );
				log.debug(nextDoor + " 1HERE " + currentLocation + " 1terrain " + board.getLocation(nextDoor).terrain);
				
				//get new point next door
				if(board.getLocation(nextDoor).terrain != 0 || board.getLocation(nextDoor).score == 5.0) {
					log.debug("CANT MOVE IN THIS DIRECTION LEGALY or PLAYER WAS HERE, calling choose New Direction ");
					newDir = chooseNewDirection();
					log.debug(newDir);
				}
				
				
				
				return new Move(newDir);
			}
		}
		
		
		
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
//			boolean nullDestination = board.getLocation(destination) == null ? true : false;
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
								
				//choose new direction logic
				if (destination == null) {
					chooseRandDir = true;
					randDirBool = true;
					ratioRounds = 0;
					newDir = chooseNewDirection();
					
					log.debug("new desty has been set as " + ACTIONS[newDir]);
					return new Move(newDir);				
				}
				
				
				
				
//				// if the destination is null, we're
//				// moving randomly
//				else {
//					log.debug("Making random move!\n-------");
//					action = ACTIONS[rand.nextInt(ACTIONS.length)];
//					return new Move(action);
//				}
				
				//log.debug("Generating new shortest path");
//				boolean nullDestination = board.getLocation(destination) == null ? true : false;
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
				log.debug("Making random move!&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&\n-------");
				action = ACTIONS[rand.nextInt(ACTIONS.length)];
			}
		}
					
		// if destination offset != (0,0) then update players assumed cells prev traveled by player
//		if( destinationOffset.x!=0 || destinationOffset.y!=0) {
//			System.out.println("dfdsgdsF");
//			board.getLocation(currentLocation.x-destinationOffset.x, currentLocation.y-destinationOffset.y).score=2.0;
//		}
		
		
		
		return new Move(action);
	}


	
	
	private int chooseNewDirectionO() {
		// TODO Auto-generated method stub
		board.setDirty(true);
		ArrayList<Point> uexp = board.getUnexploredSpaces(false);
		ArrayList<Point> fairDir = new ArrayList<Point>(8);
		int fullList = 0;
		Point newMove = new Point(0,0);
		Random r = new Random();
		boolean nullSurround = false;
		int MAX = 0;
		int action = 0;
		
		for(int x=0;x<uexp.size();x++) {
			int someIndex = r.nextInt(uexp.size());
			Point p = uexp.get(someIndex);
			uexp.remove(someIndex);
			if( countNextToNull(p.x, p.y) > MAX ) {
				MAX= countNextToNull(p.x, p.y);
				newMove = p;
//				if(countNextToNull(p.x, p.y)==8) {
//					newMove = p;
//					action = getDirectionTo(newMove);
//					Point nextDoor = new Point(currentLocation.x + _dx[action], currentLocation.y + _dy[action] );
//					log.debug(nextDoor + " HERE " + currentLocation + " terrain " + board.getLocation(nextDoor).terrain);
//					//get new point next door
//					if(board.getLocation(nextDoor).terrain == 0 && board.getLocation(nextDoor).score < 1.0) {
//						log.debug("Choosing the following point as our new desty " + p);
//						nullSurround = true;
//						break;
//					}
//				}
				if(countNextToNull(p.x, p.y)==8) {
					newMove = p;
					action = getDirectionTo(newMove);
					Point nextDoor = new Point(currentLocation.x + _dx[action], currentLocation.y + _dy[action] );
					log.debug(nextDoor + " HERE " + currentLocation + " terrain " + board.getLocation(nextDoor).terrain);
					//get new point next door
					if(board.getLocation(nextDoor).terrain == 0 && board.getLocation(nextDoor).score < 1.0) {
						log.debug("Choosing the following point as our new desty " + p);
						nullSurround = true;
						break;
					}
				}
			}
		}
		board.setDirty(false);
		
		if (nullSurround==false) {
			ArrayList<Point> myN = this.getAccessableNeighbors(currentLocation, false);
			double minScore = 100;
			int chosenP = 0;
			int steppedPoints = 0;
			for(int y=0; y<myN.size(); y++) {
				if(board.getLocation(myN.get(y)).score < minScore) {
					chosenP = y;
					minScore = board.getLocation(myN.get(y)).score;
				}
				if(board.getLocation(myN.get(y)).score >= 1.0) {
					steppedPoints++; 
				}
				
			}
			if (steppedPoints == myN.size()) {
			//WE GET IN DEADLOCK HERE BECAUSE IT WILL JUST CHOOSE BETWEEN POINTS IF SURROUNDED BY 2.0
				log.debug(" SURROUNDED BY 2.0 CELLS");
				//chooseRandDir = false;
				//choose cell that has score of less than 1 and not a mountain
				Point freedomCell = getFreedomCell();
				action = getDirectionTo( freedomCell );
			}
			else {
				action = getDirectionTo(myN.get(chosenP));
				log.debug("Choosing the following point nearby direction of point " + myN.get(chosenP) );
			}
		}

		
	
		return action;
	}
	
	private int chooseNewDirection() {
		ArrayList<Integer> possDir = new ArrayList<Integer>(1);
		boolean actionCh = false;
		int action = 0;		
		for(int y = 0; y< ACTIONS.length; y++) {
			action = rand.nextInt(ACTIONS.length);
			Point nextDoor = new Point(currentLocation.x + _dx[action], currentLocation.y + _dy[action] );
			log.debug(nextDoor + " HERE " + currentLocation + " terrain " + board.getLocation(nextDoor).terrain);
			//get new point next door
			if(board.getLocation(nextDoor).terrain == 0) {
				possDir.add(action);
				if(board.getLocation(nextDoor).score < 1.0) {
					log.debug("Choosing the following point as our new desty " + nextDoor);
					actionCh = true;
					break;
				}
			}
		}
		if(actionCh == false) {
			int choice = rand.nextInt(possDir.size());
			action = (int)possDir.get(choice);
			//if we still have a stayput move, too fried to figure out why
			if(action == 0) {
				int loopb = 0;
				while(action != 0 ) {
					loopb++;
					action = (int)possDir.get(choice);
					if(loopb > 10) 
						break;
						log.debug(action);
						log.debug("*************************************************************************");
					
				}
			}
				
		}
		return action;
	}
	
	
	private Point getFreedomCell() {
		// TODO Auto-generated method stub
		for(int x=board.getLowerBound().x;x<board.getUpperBound().x;x++ ) {
			for(int y=board.getLowerBound().y;y<board.getUpperBound().y;y++ ) {
				Point freedomCell = new Point(x,y);
				if(board.getLocation(freedomCell) != null && board.getLocation(freedomCell).terrain == 0 && board.getLocation(freedomCell).score < 1.0 ) 
					return freedomCell;
			}
		}
		return null;
	}

	private int getDirectionTo(Point p) {		
		int xdir, ydir;
		
		//getx
		if( p.x - currentLocation.x > 0) {
			xdir = 1;
		}
		else if( p.x - currentLocation.x == 0) {
			xdir = 0;
		}
		else
			xdir = -1;
		
		//gety
		if( p.y - currentLocation.y > 0) {
			ydir = 1;
		}
		else if( p.y - currentLocation.y == 0) {
			ydir = 0;
		}
		else
			ydir = -1;
		
		
		for (int i = 0; i < _dx.length; i++) {
			if (_dx[i] == xdir && _dy[i] == ydir) {
				log.debug("DIRECTION IS " + ACTIONS[i]);
				return ACTIONS[i];
			}
		}
		
		return 0;
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
					//if( (otherExp[j][u]!=null)&&(otherExp[j][u]!=myID)) { //tracks ourselves..buggy?
						playersInView.add(otherExp[j][u]);
						//playerTracks.get(otherExp[j][u]).add(new Point(currentLocation.x + offsets[j].x, currentLocation.y + offsets[j].y)); 
						playerTracks.get(otherExp[j][u]).add(new Point(offsets[j].x, offsets[j].y)); 
						board.getLocation(new Point(offsets[j].x, offsets[j].y)).score = 5.0;
					}
				}
		}
		//erase player tracks if they are no longer in view
		for(int j=0; j<playerTracks.size(); j++ ) {
				if( (playerTracks.get(j).size() >0 ) && (!playersInView.contains(j)) && (j!=myID) ) {
					playerTracks.get(j).clear();
					//System.out.println("Deleting the tracks of player " + j);
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
		Point p = shortestPath.remove(0);
//		if (shortestPath == null || shortestPath.size() == 0) {
			// I Don't think we should ever get here, I do not remember why I put this conditional in.
//			shortestPath = null;
//			p = destination;//new Point(destination.x + destinationOffset.x, destination.y + destinationOffset.y);
//		} else {
//		 p = shortestPath.remove(0);
//		}
		
		for (int i = 0; i < _dx.length; i++) {
//			System.out.println(p.x - currentLocation.x);
//			System.out.println(p.y - currentLocation.y);

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
				

//				if (l == null) {
//					log.debug("A null location: " + p);
//				}
				
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
		
		// if we just removed our destination, we're doine
		if (shortest.equals(destination)) {
			break;
		}
		
		ArrayList<Point> neighbors = getAccessableNeighbors(shortest, canStepOnNulls);
		for (Iterator<Point> itr = neighbors.iterator(); itr.hasNext();) {
			Point p = itr.next();
			
			if (nodes.contains(p)) {
				double alt = distance.get(shortest);
				// make sure alt is at maximum infinity, we don't want to roll over
				// if its not, add the path cost, which is 1 for orthogonal and 1.5 for diagonal
				if (alt != INFINITY) {
					
					double addedCost = 0;
					
					BoardLocation bl = board.getLocation(p);
					
					if (bl != null && bl.score > 3) {
//						addedCost += 1.6;
					}
					
					if (shortest.x != p.x && shortest.y != p.y) {
						alt += 1.5 + addedCost;  // diagonal
					} else {
						alt += 1 + addedCost; // orthogonal
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
	//
	// if there's just no shortest path, that means its an unreahable cell that
	// we can see
	if (shortestPath.size() == 0) {
		if (board.getLocation(destination) == null) {
			board.setLocation(destination.x, destination.y, new BoardLocation(GameConstants.MOUNTAIN, 2.0, true, false));
		}
		this.destination = null;		// get rid of this destination so we pick another one
	}
	
	return shortestPath;
}
	
	private void printDijkstra(HashMap<Point, Double> distance, HashMap<Point, Point> previous, Point upperBound, Point lowerBound) {
		//System.out.println();
		for (int y = lowerBound.y; y < upperBound.y; y++) {
			for (int x = lowerBound.x; x < upperBound.x; x++) {
				Double d = distance.get(new Point(x, y));
				if (d != null) {
			//		System.out.print(d.toString().substring(0, 3) + " ");
				} else {
				//	System.out.print(".   ");
				}
			}
		//System.out.println();
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
		// can we see any cells that are next to water that we
		// haven't stepped on?
		ArrayList<Point> nearWater = getProximalToWater();
		Collections.sort(nearWater, new DistanceComparator());
		
		if (nearWater.size() > 0) {			
			Point dest = nearWater.get(0);
			
			// make sure we move diagonally down
			// a shore line if there is one
			
			// so, if we're about to go to a neighbor
			ArrayList<Point> neighbors = getAccessableNeighbors(currentLocation, false);
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
						if (newDestBL != null && newDestBL.score < 2.0) {
							destWaterCount = waterCount;
							newDest = n;

						}
					}
				}
				
				if (newDest != null) {
					// this is a bit hackish, there's not really a point in revisiting the corners on a
					// diagonal, so let's manually set their seen value to 2
					BoardLocation oldDestBL = board.getLocation(dest);
					oldDestBL.setScore(2);
					dest = newDest;
				}
			}
			
			//update dest with offset
//			dest = new Point(dest.x + destinationOffset.x, dest.y + destinationOffset.y);
//			
//			System.out.println("offset " + destinationOffset);
//			System.out.println("New Destiantion w/ offset " + dest);
			//log.debug("this is dest" + dest);
			return dest;
		}
		
		ArrayList<Point> spaces = board.getUnexploredSpaces(true);
		
		// Lets explore in the middle of the board
		if (spaces.size() > 0) {			
			Point p = spaces.get(rand.nextInt(spaces.size()));
			log.debug("Moving to unexplored destination: " + p);
			return p;
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
				if (bl != null && bl.terrain == 0 && bl.score < 2.0) {
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
	
	
	private int countNextToNull(int x, int y) {
		int count = 0;
		
		for (int i = x - 1; i < x + 2; i++) {
			for (int j = y - 1; j < y + 2; j++) {
				if (i == x && j == y) {
					continue;
				}
				if (board.getLocation(i, j) == null) {
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
	
	private void printPlayerTracks() {
		//System.out.println("-------PLAYER-TRACKS-------");
		//System.out.println(j);
		for(int j=0; j<playerTracks.size(); j++ ) {
			//System.out.println(j);
			for (int y=0; y<playerTracks.get(j).size(); y++) {
			//	System.out.print(playerTracks.get(j).get(y) + " , ");
			}
			//System.out.println();
		}
		//System.out.println("-------PLAYER-TRACKS-------");
	}
	
}
