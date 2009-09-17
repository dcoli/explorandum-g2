package explorandum.g2.strategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import explorandum.g2.map.Map;
import explorandum.g2.map.Node;
import explorandum.g2.type.HPoint;
import explorandum.g2.type.IntPair;
import explorandum.g2.type.NodePair;
import explorandum.g2.util.Monitor;
import explorandum.g2.util.Util;


public class FindOpenSpace extends Strategy {

  private Map map = null;
  private boolean newSpace;
  private HPoint target;

  public FindOpenSpace(Monitor monitor){
    super(monitor);
    memory.useful = true;
    newSpace = false;
    target = null;
  }

  public void updateMemory(Map map){
    this.map = map;
    
    // Two choices.
    //complicated(map);
    simple(map);
  }

  private void simple(Map map) {
    goToNewOpenSpace();		
  }

  public void complicated(Map map) {
	if(Math.random() < .2)
		makeRandomMove();
	else {
		if(!(newSpace && goToNewOpenSpace())
				&& !(Util.coinToss() && Util.coinToss() && Util.coinToss() && makeRandomMove())
				&& !tryToContinue()
				&& !(Util.coinToss() && goToNewOpenSpace())
				&& !findABridge()
				&& !findNewPath()
				&& !findOpenAdjacentSquare()
				&& !(Util.coinToss() && makeRandomMove())
				&& !findLargeOpenSpace()
		//		&& !windThroughMountains()
				){
					defaultCondition();
				}
	}
  }
  
  private boolean goToNewOpenSpace() {
    if(newSpace){
      if(Util.whereAmI(map).getLocation().x == target.x
          && Util.whereAmI(map).getLocation().y == target.y){
        // I'm here
        newSpace = false;
        return false;
      }

      else {
        memory.nextMove = Util.nextInOpenPath(map, Util.whereAmI(map).getLocation(), target, false).x;
        if(Math.random() < .1)
          makeRandomMove();
        return true;
      }
    }
    else {
      newSpace = true;
      getNewTarget();
      return goToNewOpenSpace();
    }
  }

  private void getNewTarget() {
 //   System.out.println("Acquiring new target.");
    HashSet<HPoint> bounds = map.boundaryNodes;
    if (bounds.size() > 0) {
      int size = bounds.size();
      int rand = (int)Math.floor(Math.random() * size);
      int count = 0;
      for (HPoint i : bounds) {
        if (count++ == rand) {
          target = i;
        }
      }
    }
    
    // Choose a square only if we haven't stepped on it AND nobody else has been
    // seen stepping on it.  Try to find the closest one (pythagorean).
    else {
    	ArrayList<Node> unstepped = Util.getNotSteppedLocations(map);
    	if(unstepped.size() <= 0){
    		unstepped = map.probableSteppedCells; 
    	}
    	
    	// Choose an unstepped spot as target.
    	int min = Integer.MAX_VALUE;
        HPoint here = Util.whereAmI(map).getLocation();
    	for (Node i : unstepped) {
        	HPoint loc = i.getLocation();
        	if(loc.distance(here) < min){
        		target = loc;
        	}
        }
    }
  }

  private boolean makeRandomMove() {
    HashSet<Node> moves = Util.possibleNonStaticMoves(map);
    for(Node n : moves){
      if(Util.isTraversable(n) && !(map.stepped.size() > 2 && n.getLocation().equals(map.stepped.get(map.stepped.size() - 2)))){
        memory.nextMove = Util.direction(n, Util.whereAmI(map));
      }
    }
    return true;
  }

  // This means all squares around me have already been visited.
  private void defaultCondition() {
    //TODO: Rewrite this method to find new open areas.
    // Want to choose new target and go there.
    makeRandomMove();
  }

  private boolean findLargeOpenSpace() {
    int dir = Util.mostOpenDirection(map);
    if(dir != -1){
      memory.nextMove = dir;
      return true;
    }
    return false;
  }

  private boolean findOpenAdjacentSquare() {
    HashSet<Node> moves = Util.possibleNonStaticMoves(map);
    Node here = Util.whereAmI(map);
    for(Node n : moves){
      if(Util.isAvailableSquare(n, map) && !n.been()){
        memory.nextMove = Util.direction(n, here);
        return true;
      }
    }
    return false;
  }

  private boolean findNewPath() {
    HashSet<Node> moves = Util.possibleNonStaticMoves(map);
    Node now = Util.whereAmI(map);
    for(Node n : moves){
      if(Util.isAvailableSquare(n, map) && !n.been() && Util.isNewPath(n, map)){
        memory.nextMove = Util.direction(n, now);
        return true;
      }
    }
    return false;
  }

  private boolean windThroughMountains() {
    Node next = Util.nextNode(map);
    Node now = Util.whereAmI(map);
    int dir = Util.direction(next, now);
    IntPair adjDirs = Util.twoClosestDirections(dir);
    NodePair nodes = Util.twoNodesInTwoDirections(adjDirs, next, map);
    HashSet<Node> threeToOneSide = new HashSet<Node>();
    threeToOneSide.add(nodes.x);
    threeToOneSide.add(nodes.y);
    threeToOneSide.add(next);
    if(Util.numTraversable(threeToOneSide) == 1
        && Util.numMountain(threeToOneSide) == 2){
      Node option = Util.getTraversable(threeToOneSide);
      if(!option.been()){
        memory.nextMove = Util.dirToTraversable(threeToOneSide, now);
        return true;
      }
    }
    return false;
  }

  private boolean findABridge() {
    HashSet<Node> moves = Util.possibleNonStaticMoves(map);
    Node now = Util.whereAmI(map);
    for(Node n : moves){
      if(Util.isAvailableSquare(n, map) && !n.been() && Util.isBridge(n, map)){
        memory.nextMove = Util.direction(n, now);
        return true;
      }
    }
    return false;
  }

  private boolean tryToContinue() {
    Node next = Util.nextNode(map);
    Node now = Util.whereAmI(map);
    if(Util.isTraversable(next) && !next.been()){
      memory.nextMove = Util.direction(next, now);
      return true;
    }
    return false;
  }

  private void pickCloseUnexploredRegion() {
    // TODO Auto-generated method stub

  }

  private boolean areUnexploredRegions() {
    double xsize = map.rightMost() - map.leftMost();
    double ysize = map.bottomMost() - map.topMost();
    double size = map.size();
    double propSeen = Util.quotient(size, xsize * ysize);

    //TODO
    return false;
  }

}




