package explorandum.g2.map;

import java.util.TreeSet;
import java.util.ArrayList;

import explorandum.g2.type.HPoint;

public class Node {
	private TreeSet<Sighting>[] sightings;
	private HPoint location;
	private int terrain;
	private int count=0;
	private int id;
	private int firstseen;
	
	@SuppressWarnings("unchecked")
	public Node(HPoint location, int numPlayers, int id, int terrain,int time) {
		this.location = location;
		sightings = new TreeSet[numPlayers];
		for (int i = 0; i < numPlayers; i++) {
			sightings[i] = new TreeSet<Sighting>();
		}
		this.terrain = terrain;
		this.id = id;
		this.firstseen=time;
	}

	public TreeSet<Sighting>[] getSightings() {
		return sightings;
	}

	public int getTerrain() {
		return terrain;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	public HPoint getLocation() {
		return location;
	}
	
	public Sighting owns() {
		Sighting best = null;
		for (int i = 0; i < sightings.length; i++) {
			TreeSet<Sighting> s = sightings[i];
			if (!s.isEmpty() && (best == null || s.first().betterThan(best))) {
				best = s.first();
			}
		}
		return best;
	}

	public boolean been() {
		return sightings[id].first().getDistance() == 0;
	}
	
	public boolean equals(Object o){
		Node that = null;
		try {
			that = (Node)o;
		}
		catch(Exception e){
			return false;
		}
		
		return this.getLocation().x == that.getLocation().x
			   && this.getLocation().y == that.getLocation().y;
	}

	public int getFirstseen() {
		return firstseen;
	}
}