package explorandum.g2.map;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

import explorandum.GameConstants;
import explorandum.g2.type.HPoint;
import explorandum.g2.type.HPointInt;
import explorandum.g2.util.Util;

public class Map implements GameConstants{
	private HashMap<HPoint, Node> map;
	public int numPlayers;
	public HPoint currentLocation;
	public int currentTime, totalTime;
	public int id;
	public int d;
	public ArrayList<HPointInt>[] lastSeen;
	public ArrayList<HPoint> stepped;
	public HashSet<HPoint> unexploredCoast;
	public HashSet<HPoint> boundaryNodes;
	public HPoint rightborder;
	public ArrayList<Node> probableSteppedCells;

	private double maxX = Integer.MIN_VALUE;
	private double minX = Integer.MAX_VALUE;
	private double maxY = Integer.MIN_VALUE;
	private double minY = Integer.MAX_VALUE;

	@SuppressWarnings("unchecked")
	public Map(int id, int numPlayers, int d, int n) {
		map = new HashMap<HPoint, Node>();
		this.numPlayers = numPlayers;
		currentLocation = new HPoint(0, 0);
		this.id = id;
		this.d = d;
		this.totalTime=n;
		lastSeen = new ArrayList[numPlayers];
		probableSteppedCells=new ArrayList<Node>();
		for (int i = 0; i < numPlayers; i++) {
			lastSeen[i] = new ArrayList<HPointInt>();
		}
		unexploredCoast = new HashSet<HPoint>();
		stepped = new ArrayList<HPoint>();
		boundaryNodes = new HashSet<HPoint>();
		rightborder = null;
	}

	public void see(Point currentLocation, Point[] offsets, Boolean[] hasExplorer, Integer[][] otherExplorers, Integer[] terrain, int time){
		this.currentLocation = new HPoint(currentLocation);
		stepped.add(new HPoint(currentLocation));
		this.currentTime=time;
		for (int i = 0; i < offsets.length; i++) {
			HPoint p = new HPoint(offsets[i]);
			if (p.getX() > maxX) {
				rightborder = p;
			}
			maxX = Math.max(maxX, p.getX());
			minX = Math.min(minX, p.getX());
			maxY = Math.max(maxY, p.getY());
			minY = Math.min(minY, p.getY());

			Node n = map.get(p); 
			if (n == null){
				n = new Node(p, numPlayers, id, terrain[i],time);
				map.put(new HPoint(p), n);
			}
			TreeSet<Sighting>[] sightings = n.getSightings();
			sightings[id].add(new Sighting(currentLocation.distance(p), time, id));
			probableSteppedCells.remove(n);
		}

		for (int i = 0; i < offsets.length; i++) {
			HPoint p = new HPoint(offsets[i]);
			if (terrain[i] == GameConstants.LAND) {
				if (!Util.along(this, p, WATER).isEmpty() && map.get(p).owns().getDistance() > 0){
					unexploredCoast.add(p);
				}
				if (hasNullBoundary(p)) {
					boundaryNodes.add(p);
				} else {
					boundaryNodes.remove(p);
				}
			}
			if (hasExplorer[i]) {
				unexploredCoast.remove(p);
				for (Integer j : otherExplorers[i]) {
					if (j != null) {
						lastSeen[j].add(new HPointInt(p, time));
					}
				}
				Node n = map.get(p); 
				for (Node next : adjacent(p)) {
					if (canSee(n, next)) {
						TreeSet<Sighting>[] sightings = next.getSightings(); 
						for (Integer j : otherExplorers[i]) {
							if (j != null) {
								sightings[j].add(new Sighting(p.distance(next.getLocation()), time, j));
							}
						}
					}
				}
			}
		}
	}

	private boolean hasNullBoundary(HPoint p) {
		for (HPointInt h : Util.nextTo(p)) {
			if (!contains(h.point)) {
				return true;
			}
		}
		return false;
	}

	public boolean canSee(Node from, Node to) {
		if (from == to) return true;
		if (from.getLocation().distance(to.getLocation()) > d) return false;
		if (from.getTerrain() == GameConstants.MOUNTAIN) return false;
		int distX = to.getLocation().x - from.getLocation().x;
		int distY = to.getLocation().y - from.getLocation().y;

		HPoint nextPoint = from.getLocation();
		if (distX > 0) {
			nextPoint = Util.right(nextPoint);
		} else if (distX < 0) {
			nextPoint = Util.left(nextPoint);
		}
		if (distY > 0) {
			nextPoint = Util.down(nextPoint);
		} else if (distY < 0) {
			nextPoint = Util.up(nextPoint);
		}

		if (!map.containsKey(nextPoint)) return false;
		return canSee(map.get(nextPoint), to);
	}

	public Node get(HPoint p) {
		return map.get(p);
	}

	public boolean contains(HPoint p) {
		return map.containsKey(p);
	}

	public double topMost() {
		return minY;
	}

	public double bottomMost() {
		return maxY;
	}

	public double leftMost() {
		return minX;
	}

	public double rightMost() {
		return maxX;
	}

	public int size() {
		return map.size();
	}

	public HashSet<Node> adjacent() {
		return adjacent(currentLocation);
	}

	public HashSet<Node> adjacent(HPoint p) {
		return adjacent(p, d);
	}

	public HashSet<Node> adjacent(HPoint p, double dist) {
		return adjacent(p, p, dist);
	}

	private HashSet<Node> adjacent(HPoint orig, HPoint now, double dist) {
		HashSet<HPoint> tried = new HashSet<HPoint>();
		tried.add(now);
		return adjacent(new HashSet<Node>(), tried, orig, now, dist);
	}

	private HashSet<Node> adjacent(HashSet<Node> sofar, HashSet<HPoint> tried, HPoint orig, HPoint now, double dist) {
		if (map.containsKey(now))sofar.add(get(now));
		double before = orig.distance(now);
		for (HPointInt p : Util.nextTo(now)) {
		  double to = p.point.distance(orig);
			if (to <= dist && to > before && !tried.contains(p.point)) {
				tried.add(p.point);
				sofar = adjacent(sofar, tried, orig, p.point, dist);
			}
		}
		return sofar;
	}

	public Node up() {
		return get(Util.up(currentLocation));
	}

	public Node down() {
		return get(Util.down(currentLocation));
	}

	public Node left() {
		return get(Util.left(currentLocation));
	}

	public Node right() {
		return get(Util.right(currentLocation));
	}

	public Node upLeft() {
		return get(Util.up(Util.left(currentLocation)));
	}

	public Node upRight() {
		return get(Util.up(Util.right(currentLocation)));
	}

	public Node downLeft() {
		return get(Util.down(Util.left(currentLocation)));
	}

	public Node downRight() {
		return get(Util.down(Util.right(currentLocation)));
	}

	public HPointInt getLastSeen(int pid) {
		ArrayList<HPointInt> seen = lastSeen[pid];
		if (seen.isEmpty()) {
			return null;
		}
		return Util.last(seen);
	}

	public Collection<Node> nodes() {
		return map.values();
	}

	public double area() {
		return (bottomMost() - topMost()) * (rightMost() - leftMost());
	}
}
