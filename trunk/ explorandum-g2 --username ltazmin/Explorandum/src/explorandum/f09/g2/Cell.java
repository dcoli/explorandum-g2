package explorandum.f09.g2;

import java.awt.Point;

public class Cell {

	public static final int OWNERSHIP_UNKNOWN = 0;
	public static final int OWNERSHIP_US = 1;
	public static final int OWNERSHIP_THEM = 2;

	public static final int TERRAIN_UNKNOWN = -1;
	public static final int TERRAIN_LAND = 0;
	public static final int TERRAIN_MOUNTAIN = 2;
	public static final int TERRAIN_WATER = 1;

	private Point p;

	private int terrain = TERRAIN_UNKNOWN;
	private int whoOwns = OWNERSHIP_UNKNOWN;
	private boolean isVisited = false;
	private int visitationTurn = -1;
	private int visitationCount = 0;

	public int getVisitationTurn() {
		return visitationTurn;
	}

	public void setVisitationTurn(int visitationTurn_) {
		visitationTurn = visitationTurn_;
	}

	public boolean isVisited() {
		return isVisited;
	}

	public void setVisited(boolean isVisited_) {
		isVisited = isVisited_;
	}

	public Cell(Point p_) {
		super();
		p = p_;
	}

	public int getTerrain() {
		return terrain;
	}

	public void setTerrain(int terrain_) {
		terrain = terrain_;
	}

	public int getOwner() {
		return whoOwns;
	}

	public void setOwner(int whoOwns_) {
		whoOwns = whoOwns_;
	}

	public boolean isImpassable() {
		return getTerrain() == TERRAIN_MOUNTAIN
				|| getTerrain() == TERRAIN_WATER;
	}

	public Point getPoint() {
		return p;
	}

	@Override
	public String toString() {
		return "[ ( " + p.x + "," + p.y + ") , Terrain =" + terrain + " ]";
	}

}
