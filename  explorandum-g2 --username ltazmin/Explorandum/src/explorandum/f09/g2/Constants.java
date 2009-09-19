package explorandum.f09.g2;

import explorandum.GameConstants;
import explorandum.Move;

/**
 * Class for holding constants
 * @author sharadh
 *
 */
public interface Constants {

	public static int[] VERTEX_NEIGHBORS = { Move.NORTHWEST, Move.NORTHEAST,
			Move.SOUTHEAST, Move.SOUTHWEST };

	public static int[][] VERTEX_NEIGHBOR_OFFSETS = new int[][] { { -1, -1 },
			{ -1, 1 }, { 1, 1 }, { 1, -1 } };

	public static int[] EDGE_NEIGHBORS = { Move.NORTH, Move.SOUTH, Move.EAST,
			Move.WEST };

	public static int[][] EDGE_NEIGHBOR_OFFSETS = new int[][] { { -1, 0 },
			{ 1, 0 }, { 0, 1 }, { 0, -1 } };

	public static final int OWNED_BY_UNKNOWN = 0;
	public static final int OWNER_BY_US = 1;
	public static final int OWNED_BY_THEM = 2;

	public static final int TERRAIN_UNKNOWN = -1;
	public static final int TERRAIN_LAND = GameConstants.LAND;
	public static final int TERRAIN_MOUNTAIN = GameConstants.MOUNTAIN;
	public static final int TERRAIN_WATER = GameConstants.WATER;
	
	
}