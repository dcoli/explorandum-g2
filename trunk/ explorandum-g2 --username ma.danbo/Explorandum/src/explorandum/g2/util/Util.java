package explorandum.g2.util;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import explorandum.GameConstants;

import explorandum.g2.map.Map;
import explorandum.g2.map.Node;
import explorandum.g2.map.Sighting;
import explorandum.g2.type.HPoint;
import explorandum.g2.type.HPointInt;
import explorandum.g2.type.IntPair;
import explorandum.g2.type.NodePair;

public class Util implements GameConstants {

	public static final double ROOT_TWO = Math.sqrt(2) + .0001;

	public static double quotient(double x, double y) {
		if(y == 0)
			return Double.POSITIVE_INFINITY;
		return x/y;
	}

	public static boolean isTraversable(Node n){
		if(n!=null)
			return n.getTerrain() == GameConstants.LAND;
		else
			return false;
	}

	public static boolean isWater(Node n){
		return n.getTerrain() == GameConstants.WATER;
	}

	public static boolean isMountain(Node n){
		return n.getTerrain() == GameConstants.MOUNTAIN;
	}

	public static Node nextNode(Map m){
		HPoint HP = nextPoint(m);
		if(m.contains(HP)){
			return m.get(HP);
		}
		return null;
	}

	public static HPoint nextPoint(Map m){
		if (m.stepped.size() < 2) {
			return getPointInDirection(m.currentLocation, mostOpenDirection(m));
		} else {
			HPoint current = m.stepped.get(m.stepped.size() - 1);
			HPoint last =  m.stepped.get(m.stepped.size() - 2);
			int xchange = current.x - last.x;
			int ychange = current.y - last.y;
			int newX = current.x + xchange;
			int newY = current.y + ychange;
			return new HPoint(newX, newY);
		}
	}

	public static int direction(Node current, Node last){
		return direction(current.getLocation(), last.getLocation());
	}

	public static int direction(HPoint current, HPoint last){
		int xchange = current.x - last.x;
		int ychange = current.y - last.y;
		if (xchange == 0) {
			if (ychange == 0) {
				return 0;
			} else if (ychange > 0) {
				return 5;
			} else {
				return 1;
			}
		} else if (xchange > 0) {
			if (ychange == 0) {
				return 3;
			} else if (ychange > 0) {
				return 4;
			} else {
				return 2;
			}
		} else {
			if (ychange == 0) {
				return 7;
			} else if (ychange > 0) {
				return 6;
			}else {
				return 8;
			}
		}
	}

	public static IntPair nextInOpenPath(Map m, HPoint a, HPoint b, boolean cautious) {
		HashSet<HPoint> seen = new HashSet<HPoint>();
		TreeSet<HPointInt> sofar = new TreeSet<HPointInt>();
		sofar.add(new HPointInt(a, 0));
		HashMap<HPoint, HPoint> to = new HashMap<HPoint, HPoint>();
		while (seen.size() < m.size()) {
			if (sofar.isEmpty()) {
				return new IntPair(-1, Integer.MAX_VALUE);
			}
			HPointInt next = sofar.first();
			sofar.remove(next);
			seen.add(next.point);
			HPoint here = next.point;
			if (here.equals(b)) {
				int dist = next.param;
				while (to.get(here) != a) {
					here = to.get(here);
				}
				return new IntPair(direction(here, a), dist);
			} else {
				ArrayList<HPointInt> more = nextToTraversible(m, here, next.param, cautious);
				for (HPointInt h : more) {
					if (!seen.contains(h.point)) {
						Node n = m.get(h.point);
						h.param += ((n == null) ? 0 : m.d - n.owns().getDistance());
						boolean already = false;
						for (HPointInt z : sofar) {
							if (z.point.equals(h.point)) {
								if (h.param < z.param) {
									sofar.remove(z);
								} else {
									already = true;
								}
								break;
							}
						}
						if (!already) {
							sofar.add(h);
							to.put(h.point, here);
						}
					}
				}
			}
		}
		return new IntPair(-1, Integer.MAX_VALUE);
	}

	public static int traversingDistance(HPoint a, HPoint b, Map m, boolean cautious) {
		HashSet<HPoint> seen = new HashSet<HPoint>();
		TreeSet<HPointInt> sofar = new TreeSet<HPointInt>();
		sofar.add(new HPointInt(a, 0));
		while (seen.size() < m.size()) {
			if (sofar.isEmpty()) {
				return -1;
			}
			HPointInt next = sofar.first();
			sofar.remove(next);
			seen.add(next.point);
			HPoint here = next.point;
			if (here.equals(b)) {
				return next.param;
			} else {
				ArrayList<HPointInt> more = nextTo(here, next.param);
				for (HPointInt h : more) {
					if (!seen.contains(h.point)) {
						if (m.contains(h.point)) {
							Node n = m.get(h.point);
							if (n.getTerrain() == LAND) {
								for (HPointInt z : sofar) {
									if (z.point == h.point) {
										h.param = Math.min(h.param, z.param);
										sofar.remove(z);
										break;
									}
								}
								sofar.add(h);
							}
						} else if (!cautious) {
							for (HPointInt z : sofar) {
								if (z.point == h.point) {
									h.param = Math.min(h.param, z.param);
									sofar.remove(z);
									break;
								}
							}
							sofar.add(h);
						}
					}
				}
			}
		}
		return -1;
	}

	public static ArrayList<HPointInt> nextTo(HPoint p) {
		return nextTo(p, 0);
	}

	public static ArrayList<HPointInt> nextTo(HPoint p, int sofar) {
		ArrayList<HPointInt> ans = new ArrayList<HPointInt>();
		ans.add(new HPointInt(up(p), 2 + sofar));
		ans.add(new HPointInt(down(p), 2 + sofar));
		ans.add(new HPointInt(right(p), 2 + sofar));
		ans.add(new HPointInt(left(p), 2 + sofar));
		ans.add(new HPointInt(upLeft(p), 3 + sofar));
		ans.add(new HPointInt(upRight(p), 3 + sofar));
		ans.add(new HPointInt(downLeft(p), 3 + sofar));
		ans.add(new HPointInt(downRight(p), 3 + sofar));
		return ans;
	}

	public static ArrayList<HPointInt> nextToTraversible(Map m, HPoint p, int sofar, boolean cautious) {
		ArrayList<HPointInt> ans = new ArrayList<HPointInt>();
		HPointInt h = new HPointInt(up(p), 2 + sofar);
		Node n = m.get(h.point);
		if ((n == null && !cautious) || (n != null && isTraversable(n))) {
			ans.add(h);
		}
		h = new HPointInt(down(p), 2 + sofar);
		n = m.get(h.point);
		if ((n == null && !cautious) || (n != null && isTraversable(n))) {
			ans.add(h);
		}
		h = new HPointInt(right(p), 2 + sofar);
		n = m.get(h.point);
		if ((n == null && !cautious) || (n != null && isTraversable(n))) {
			ans.add(h);
		}
		h = new HPointInt(left(p), 2 + sofar);
		n = m.get(h.point);
		if ((n == null && !cautious) || (n != null && isTraversable(n))) {
			ans.add(h);
		}
		h = new HPointInt(upLeft(p), 3 + sofar);
		n = m.get(h.point);
		if ((n == null && !cautious) || (n != null && isTraversable(n))) {
			ans.add(h);
		}
		h = new HPointInt(upRight(p), 3 + sofar);
		n = m.get(h.point);
		if ((n == null && !cautious) || (n != null && isTraversable(n))) {
			ans.add(h);
		}
		h = new HPointInt(downLeft(p), 3 + sofar);
		n = m.get(h.point);
		if ((n == null && !cautious) || (n != null && isTraversable(n))) {
			ans.add(h);
		}
		h = new HPointInt(downRight(p), 3 + sofar);
		n = m.get(h.point);
		if ((n == null && !cautious) || (n != null && isTraversable(n))) {
			ans.add(h);
		}
		return ans;
	}

	public static int direction(Map m){
		if(m.stepped.size()>=2) {
			HPoint current = m.stepped.get(m.stepped.size() - 1);
			HPoint last = m.stepped.get(m.stepped.size() - 2);
			return direction(current, last);
		}
		else {
			return 0;
		} 
	}

	public static Node whereAmI(Map m){
		return m.get(m.stepped.get(m.stepped.size() - 1));
	}


	public static final HPoint up(HPoint p) {
		return new HPoint(p.x, p.y - 1);
	}

	public static final HPoint upLeft(HPoint p) {
		return left(up(p));
	}

	public static final HPoint upRight(HPoint p) {
		return right(up(p));
	}

	public static final HPoint down(HPoint p) {
		return new HPoint(p.x, p.y + 1);
	}

	public static final HPoint downLeft(HPoint p) {
		return left(down(p));
	} 

	public static final HPoint downRight(HPoint p) {
		return right(down(p));
	}

	public static final HPoint left(HPoint p) {
		return new HPoint(p.x - 1, p.y);
	}

	public static final HPoint right(HPoint p) {
		return new HPoint(p.x + 1, p.y);
	}

	public static HashSet<Node> possibleMoves(Map map){
		Node now = Util.whereAmI(map);
		HPoint loc = now.getLocation();
		return possibleMoves(map, loc);
	}

	public static HashSet<Node> possibleMoves(Map map, HPoint point) {
		return map.adjacent(point, Util.ROOT_TWO);
	}

	public static HashSet<Node> possibleNonStaticMoves(Map map){
		HashSet<Node> result = possibleMoves(map);
		result.remove(whereAmI(map));
		return result;
	}

	public static HashSet<Node> possibleNonStaticMoves(Map map, HPoint point) {
		HashSet<Node> result = possibleMoves(map, point);
		result.remove(whereAmI(map));
		return result;
	}

	public static boolean isNewPath(Node n, Map map){
		return (eastSize(n, map) == 1 && westSize(n, map)== 3)
		|| (westSize(n, map) == 1 && eastSize(n, map)== 3)
		|| (southSize(n, map) == 1 && northSize(n, map)== 3)
		|| (northSize(n, map) == 1 && southSize(n, map)== 3);		
	}

	public static boolean isBridge(Node n, Map map) {
		return mostSquaresInCardinalDirection(n, map) == 1
		&& fewestSquaresInCardinalDirection(n, map) <= 1;
	}

	public static int eastSize(Node n, Map map){
		return eastSquares(n, map, Util.ROOT_TWO).size();
	}

	public static int westSize(Node n, Map map){
		return westSquares(n, map, Util.ROOT_TWO).size();
	}

	public static int northSize(Node n, Map map){
		return northSquares(n, map, Util.ROOT_TWO).size();
	}

	public static int southSize(Node n, Map map){
		return southSquares(n, map, Util.ROOT_TWO).size();
	}

	public static int fewestSquaresInCardinalDirection(Node n, Map map){
		return minOfFourValues(eastSquares(n, map, Util.ROOT_TWO).size(),
				westSquares(n, map, Util.ROOT_TWO).size(),
				northSquares(n, map, Util.ROOT_TWO).size(),
				southSquares(n, map, Util.ROOT_TWO).size());
	}

	public static int mostSquaresInCardinalDirection(Node n, Map map){
		return maxOfFourValues(eastSquares(n, map, Util.ROOT_TWO).size(),
				westSquares(n, map, Util.ROOT_TWO).size(),
				northSquares(n, map, Util.ROOT_TWO).size(),
				southSquares(n, map, Util.ROOT_TWO).size());
	}

	public static int minOfFourValues(int x, int y, int z, int w){
		int result = x;
		if(y < result)
			result = y;
		if(z < result)
			result = z;
		if(w < result)
			result = w;
		return result;
	}

	public static int maxOfFourValues(int x, int y, int z, int w){
		int result = x;
		if(y > result)
			result = y;
		if(z > result)
			result = z;
		if(w > result)
			result = w;
		return result;
	}

	public static boolean isEast(Node here, Node there){
		return there.getLocation().x > here.getLocation().x;
	}

	public static boolean isWest(Node here, Node there){
		return there.getLocation().x < here.getLocation().x;
	}

	public static boolean isNorth(Node here, Node there){
		return there.getLocation().y < here.getLocation().y;
	}

	public static boolean isSouth(Node here, Node there){
		return there.getLocation().y > here.getLocation().y;
	}

	public static HashSet<Node> eastSquares(Node here, Map m, double d){
		HashSet<Node> result = m.adjacent(here.getLocation(), d);
		HashSet<Node> toBeDeleted = new HashSet<Node>();
		for(Node there : result)
			if(!isEast(here, there))
				toBeDeleted.add(there);
		for(Node there : toBeDeleted)
			result.remove(there);
		return result;
	}

	public static HashSet<Node> westSquares(Node here, Map m, double d){
		HashSet<Node> result = m.adjacent(here.getLocation(), d);
		HashSet<Node> toBeDeleted = new HashSet<Node>();
		for(Node there : result)
			if(!isWest(here, there))
				toBeDeleted.add(there);
		for(Node there : toBeDeleted)
			result.remove(there);
		return result;
	}

	public static HashSet<Node> northSquares(Node here, Map m, double d){
		HashSet<Node> result = m.adjacent(here.getLocation(), d);
		HashSet<Node> toBeDeleted = new HashSet<Node>();
		for(Node there : result)
			if(!isNorth(here, there))
				toBeDeleted.add(there);
		for(Node there : toBeDeleted)
			result.remove(there);
		return result;
	}

	public static HashSet<Node> southSquares(Node here, Map m, double d){
		HashSet<Node> result = m.adjacent(here.getLocation(), d);
		HashSet<Node> toBeDeleted = new HashSet<Node>();
		for(Node there : result)
			if(!isSouth(here, there))
				toBeDeleted.add(there);
		for(Node there : toBeDeleted)
			result.remove(there);
		return result;
	}

	// Do not pass 0 (stay still) to this.
	public static IntPair twoClosestDirections(int dir){
		if (dir == 0) {
			return new IntPair(0, 0);
		}
		int x = modAdd(dir, 1, 9);
		if(x == 0)
			x = 1;
		int y = modSubtract(dir, 1, 9);
		if(y == 0)
			y = 8;
		return new IntPair(x, y);
	}

	public static NodePair twoNodesInTwoDirections(IntPair dirs, Node here, Map m){
		Node x = getNodeInDir(dirs.x, here.getLocation(), m);
		Node y = getNodeInDir(dirs.y, here.getLocation(), m);
		return new NodePair(x, y);
	}

	public static Node getNodeInDir(int dir, HPoint here, Map m) {
		return m.get(getPointInDirection(here, dir));
	}

	public static HPoint getPointInDirection(HPoint here, int dir) {
		try{
			return new HPoint(here.x + _dx[dir], here.y + _dy[dir]);
		}
		catch(Exception e)
		{
			return new HPoint(0,0);
		}
	}

	public static int modAdd(int x, int y, int mod){
		return ((x % mod) + (y % mod) + mod) % mod;
	}

	public static int modSubtract(int x, int y, int mod){
		return ((x % mod) - (y % mod) + mod) % mod;
	}

	public static int numTraversable(HashSet<Node> nodes){
		int count = 0;
		for(Node n : nodes)
			if(isTraversable(n))
				count++;
		return count;
	}

	public static int dirToTraversable(HashSet<Node> nodes, Node here) {
		for(Node n : nodes){
			if(isTraversable(n))
				return direction(n, here);
		}
		return 0;
	}

	public static Node getTraversable(HashSet<Node> nodes) {
		for(Node n : nodes){
			if(isTraversable(n))
				return n;
		}
		return null;
	}

	public static int numWater(HashSet<Node> nodes){
		int count = 0;
		for(Node n : nodes)
			if(isWater(n))
				count++;
		return count;
	}

	public static int numMountain(HashSet<Node> nodes){
		int count = 0;
		for(Node n : nodes)
			if(isMountain(n))
				count++;
		return count;
	}

	public static int mostOpenDirection(Map map) {
		Node here = whereAmI(map);
		HPoint loc = here.getLocation();
		int x = eastSquares(here, map, map.d).size();
		int y = westSquares(here, map, map.d).size();
		int z = northSquares(here, map, map.d).size();
		int w = southSquares(here, map, map.d).size();

		int best = maxOfFourValues(x, y, z, w);
		if(best == x){
			if(isAvailableSquare(map.get(right(loc)), map))
				return GameConstants.EAST;
			else if(isAvailableSquare(map.get(up(right(loc))), map))
				return GameConstants.NORTHEAST;
			else if(isAvailableSquare(map.get(down(right(loc))), map))
				return GameConstants.SOUTHEAST;
		}
		if(best == y){
			if(isAvailableSquare(map.get(left(loc)), map))
				return GameConstants.WEST;
			else if(isAvailableSquare(map.get(up(left(loc))), map))
				return GameConstants.NORTHWEST;
			else if(isAvailableSquare(map.get(down(left(loc))), map))
				return GameConstants.SOUTHWEST;
		}
		if(best == z){
			if(isAvailableSquare(map.get(up(loc)), map))
				return GameConstants.NORTH;
			else if(isAvailableSquare(map.get(up(right(loc))), map))
				return GameConstants.NORTHEAST;
			else if(isAvailableSquare(map.get(up(left(loc))), map))
				return GameConstants.NORTHWEST;
		}
		else {
			if(isAvailableSquare(map.get(down(loc)), map))
				return GameConstants.SOUTH;
			else if(isAvailableSquare(map.get(down(right(loc))), map))
				return GameConstants.SOUTHEAST;
			else if(isAvailableSquare(map.get(down(left(loc))), map))
				return GameConstants.SOUTHWEST;
		}
		return -1;
	}	

	public static boolean isAvailableSquare(Node n, Map m){
		Node now = whereAmI(m);
		if(m.stepped.size()>=2)
		{
			Node last = m.get(m.stepped.get(m.stepped.size() - 2));
			return !n.equals(now) && !n.equals(last) && isTraversable(n);		
		}
		else
			return false;
	}

	public static Integer getLastTime(TreeSet<Sighting> set) {
		if (set.isEmpty()) {
			return null;
		}
		int max = Integer.MIN_VALUE;
		for (Sighting s : set) {
			if (s.getTime() > max) {
				max = s.getTime();
			}
		}
		return max;
	}

	public static <T> T  last(List<T> list) {
		return list.get(list.size() - 1);
	}

	public static int opposite(int d1) {
		int ans = d1 + 4;
		if (ans > 8) {
			ans -= 8;
		}
		return ans;
	}
	//
	//  public static int across(int d1, int d2) {
		//
		//  }
	//
	//  public static int mid(int d1, int d2) {
		//  
		//  }

	public static boolean nswe(int dir) {
		return dir % 2 == 1;
	}

	public static ArrayList<Integer> orthogonal(int dir) {
		ArrayList<Integer> ans = new ArrayList<Integer>();
		int d1 = dir - 2;
		if (d1 < 1) {
			d1 += 8;
		}
		ans.add(d1);
		int d2 = dir + 2;
		if (d2 > 8) {
			d2 -= 8;
		}
		ans.add(d2);
		return ans;
	}

	public static IntPair toIntPair(ArrayList<Integer> list) {
		if (list.size() != 2) {
			return null;
		}
		Iterator<Integer> ints = list.iterator();
		return new IntPair(ints.next(), ints.next());
	}

	public static <T> ArrayList<T> from(T... t) {
		ArrayList<T> ans = new ArrayList<T>();
		for (T tt : t) {
			ans.add(tt);
		}
		return ans;
	}

	public static boolean possibleDeadlock(Map map) {
		if(map.stepped.size()>12)
		{
			int count=0,k=0;

			for(int j=map.stepped.size()-2;k<12;j--)
			{
				HPoint p= map.stepped.get(j);
				if(p.x==map.currentLocation.x && p.y==map.currentLocation.y)
				{
					count ++;
				}
				k++;
			}
			//System.out.println("The count is" +count);
			if(count>=3)
			{
				return true;
			}
			else{
				return false;
			}
		}
		else
		{
			return false;	
		}
	}

	public static boolean possibleDeadlockMountain(Map map) {
		if(map.stepped.size()>23)
		{
			int count=0,k=0;

			for(int j=map.stepped.size()-2;k<21;j--)
			{
				HPoint p= map.stepped.get(j);
				if(p.x==map.currentLocation.x && p.y==map.currentLocation.y)
				{
					count ++;
				}
				k++;
			}
			//System.out.println("The count is" +count);
			if(count>=2)
			{
				return true;
			}
			else{
				return false;
			}
		}
		else
		{
			return false;	
		}
	}


	public static boolean possibleSameTrack(Map map) {
		if(map.stepped.size()>20)
		{
			int count=0,k=0,i=0;

			for(int j=map.stepped.size()-1;k<10;j--)
			{
				HPoint p= map.stepped.get(j);
				for( i=map.stepped.size()-11;i>=0;i--)
				{
					HPoint p2= map.stepped.get(i);
					if(p.x==p2.x && p.y==p2.y)
					{
						count ++;
						break;
					}
				}

				k++;
			}
			//System.out.println("The count is" +count);
			if(count>=4)
			{
				return true;
			}
			else{
				return false;
			}
		}
		else
		{
			return false;	
		}
	}

	public static boolean possibleSameTrackMountain(Map map) {
		if(map.stepped.size()>20)
		{
			int count=0,k=0,i=0;

			for(int j=map.stepped.size()-1;k<18;j--)
			{
				HPoint p= map.stepped.get(j);
				for( i=map.stepped.size()-19;i>=0;i--)
				{
					HPoint p2= map.stepped.get(i);
					if(p.x==p2.x && p.y==p2.y)
					{
						count ++;
						break;
					}
				}

				k++;
			}
			//System.out.println("The count is" +count);
			if(count>=12)
			{
				return true;
			}
			else{
				return false;
			}
		}
		else
		{
			return false;	
		}
	}

	public static boolean possibleSameTrackMountain2(Map map) {
		if(map.stepped.size()>20)
		{
			int count=0,k=0,i=0;

			for(int j=map.stepped.size()-1;k<18;j--)
			{
				HPoint p= map.stepped.get(j);
				for( i=map.stepped.size()-19;i>=0;i--)
				{
					HPoint p2= map.stepped.get(i);
					if(p.x==p2.x && p.y==p2.y)
					{
						count ++;
						break;
					}
				}

				k++;
			}
			//System.out.println("The count is" +count);
			if(count>=2)
			{
				return true;
			}
			else{
				return false;
			}
		}
		else
		{
			return false;	
		}
	}

	public static boolean adjacent(int next, int next2) {
		int diff = Math.abs(next2 - next2);
		return diff == 1 || diff == 7;
	}

	//TODO: this was throwing an error when it was called on the first turn.
	public static Integer direction(Map map, int player) {
		ArrayList<HPointInt> seen = map.lastSeen[player];
		HPointInt to = Util.last(seen);
		int size = seen.size();
		if (size == 1) {
			return null;
		} else {
			HPointInt from = seen.get(seen.size() - 2);
			if (to.param - from.param <= DIAGMOVE * 2) {
				int dir = Util.direction(to.point, from.point);
				return dir;
			} else {
				return null;
			}
		}
	}

	public static int clockwise(int coast, int dir) {
		int diff = dir - coast;
		if (diff == 0 || diff == 4) {
			return 1;
		} else if (Math.abs(diff) < 4){
			return diff / Math.abs(diff);
		} else {
			return -diff / Math.abs(diff);
		}
	}

	public static int addDir(int dir, int plus) {
		dir += plus;
		if (dir > 8) {
			dir = dir % 8;
		}
		while (dir < 1) {
			dir = 8 + dir;
		}
		return dir;
	}

	public static boolean coinToss(){
		return Math.random() < .5;
	}
	public static Node alongMountainPlayerPoint(Map map, int player) {
		HPointInt loc = map.getLastSeen(player);
		// HPoint coast = alongCoastPlayer(map, loc.point);
		Node n=map.get(loc.point);
		return n;
	}

	public static boolean alongMountainPlayer(Map map,int player ) {
		HPointInt loc = map.getLastSeen(player);
		if(loc==null)
		{
			return false;
		}
		HPoint point=loc.point;
		ArrayList<HPoint> ans = new ArrayList<HPoint>();
		ArrayList<HPointInt> next = Util.nextTo(point);
		for (HPointInt h : next) {
			Node n = map.get(h.point);
			if (n != null && n.getTerrain() == MOUNTAIN) {
				ans.add(h.point);
			}
		}
		if(ans.size()!=0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}


	public static HPointInt seeUnexplored(Map map, Set<HPoint> set) {
		HPoint location = map.currentLocation;
		HPoint best = null;
		int bestDir = -1;
		double bestVal = Integer.MAX_VALUE;
		for (HPoint p : set) {
			IntPair pair = Util.nextInOpenPath(map, location, p, true);
			double newscore = pair.y - Util.plusScore(map, p);
			if (pair.x > 0 && newscore < bestVal) {
				best = p;
				bestVal = newscore;
				bestDir = pair.x;
			}
		}
		return new HPointInt(best, bestDir);
	}

	public static double plusScore(Map map, HPoint p) {
		double plus = 0;

		HashSet<Node> canSee = map.adjacent(p);
		for (Node n : canSee) {
			Sighting s = n.owns();
			if (s.getId() != map.id && s.getDistance() > p.distance(n.getLocation())) {
				plus++;
			}
		}
		//int curdir = direction(map);
		//IntPair close = twoClosestDirections(curdir);
		//int ndir = direction(p, whereAmI(map).getLocation());
		//if (ndir == curdir) {
		//	ndir += 3;
		//} else if (ndir == close.x || ndir == close.y) {
		//	ndir += 2;
		//}
		return plus;
	}

	public static int closest(int terrain) {
		if (terrain == LAND) return 0;
		return 1;
	}

	public static HPointInt hug(Map map, HPoint current, int coastDir, int clockwise, int terrain) {
		HPoint next;
		do {
			coastDir = Util.addDir(coastDir, clockwise);
			next = Util.getPointInDirection(current, coastDir);
		} while (map.contains(next) && map.get(next).getTerrain() == terrain);
		coastDir = Util.addDir(coastDir, clockwise * (Util.nswe(coastDir) ? -2 : -3));
		return new HPointInt(next, coastDir);
	}

	public static ArrayList<HPoint> along(Map map, int player, int terrain) {
		return along(map, player, false, terrain);
	}

	public static ArrayList<HPoint> along(Map map, int player, boolean open, int terrain) {
		HPointInt loc = map.getLastSeen(player);
		ArrayList<HPoint> coast = along(map, loc.point, open, terrain);
		return coast;
	}

	public static ArrayList<HPoint> along(Map map, HPoint point, int terrain) {
		return along(map, point, false, terrain);
	}

	public static ArrayList<HPoint> along(Map map, HPoint point, boolean open, int terrain) {
		ArrayList<HPoint> ans = new ArrayList<HPoint>();
		ArrayList<HPointInt> next = Util.nextTo(point);
		for (HPointInt h : next) {
			Node n = map.get(h.point);
			if (n != null && n.getTerrain() == terrain && (!open || n.owns().getDistance() > 1)) {
				ans.add(h.point);
			}
		}
		return ans;
	}


	public static ArrayList<Node> getNotSteppedLocations(Map map){
		Collection<Node> totalNodes=map.nodes();
		ArrayList<Node> steppedCells=new ArrayList<Node>();
		//System.out.println("the size is "+totalNodes.size());
		for(Node n: totalNodes){
			if(n.owns().getDistance() > 0 && n.getTerrain() == LAND) {
				steppedCells.add(n);
			}

		}
		return steppedCells;
	}

	public static boolean isComingInSameDirection(Map map, int p){
		ArrayList<HPointInt> loc=map.lastSeen[p];
		HPointInt pt;
		HPointInt pt2;
		HPoint p1,p2;
		if(loc.size()<=1||loc==null){
			return false;
		}
		else{
			pt=loc.get(loc.size()-1);
			pt2=loc.get(loc.size()-2);
			p1=(pt.point);
			p2=(pt2.point);
			int mydir=direction(map);
			int theirdir=direction(p1,p2);

			return false;
		}		
	}
	
	public static void setPossibleSteppedNodes(Map map){

		for(int i=0;i<map.numPlayers;i++){
			if(map.id!=i){
				ArrayList<HPointInt> loc=map.lastSeen[i];
				HPointInt pt, pt2;
				HPoint p1,p2;
				if(loc.size()<=1||loc==null){
					continue;
				}
				else{
					pt=loc.get(loc.size()-1);
					pt2=loc.get(loc.size()-2);
					p1=(pt.point);
					p2=(pt2.point);
					Integer theirdir=direction(map,i);
					int dir=opposite(theirdir);
					Node nod=addNode(dir,p2,map,i);
					HPoint p3;
					Node nod2=null;
					if(nod!=null){
						p3=nod.getLocation();
						nod2=addNode(dir,p3,map,i);
					}
					
					Node nod3=addNode(theirdir,p1,map,i);
					HPoint p4;
					Node nod4=null;
					if(nod3!=null){
						p4=nod3.getLocation();
						nod4=addNode(theirdir,p4,map,i);

					}

					
				}	
			}

		}
		return ;
	}
	
	public static Node addNode(int dir, HPoint p, Map map, int i){
		Node n=null;
		n=getNodeInDir(dir,p,map);
	

		if(n!=null){
			TreeSet<Sighting>[]sighting= n.getSightings();
			sighting[i].add(new Sighting(0, map.currentTime, i));
			if(!(map.probableSteppedCells.contains(n))){
				map.probableSteppedCells.add(n);
				
			}
			
		}
		
		return n;
		
	}


}
