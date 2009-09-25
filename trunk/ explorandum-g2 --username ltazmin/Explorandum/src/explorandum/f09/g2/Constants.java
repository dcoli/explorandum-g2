package explorandum.f09.g2;

import explorandum.GameConstants;
import explorandum.Move;

/**
 * Class for holding constants
 * 
 * @author sharadh
 * 
 */
public class Constants {

	public static int[] VERTEX_NEIGHBORS = { Move.NORTHWEST, Move.NORTHEAST,
			Move.SOUTHEAST, Move.SOUTHWEST };

	public static int[][] VERTEX_NEIGHBOR_OFFSETS = new int[][] { { -1, -1 },
			{ -1, 1 }, { 1, 1 }, { 1, -1 } };

	public static int[] EDGE_NEIGHBORS = { Move.NORTH, Move.SOUTH, Move.EAST,
			Move.WEST };

	public static int[][] EDGE_NEIGHBOR_OFFSETS = new int[][] { { -1, 0 },
			{ 1, 0 }, { 0, 1 }, { 0, -1 } };

	public static final int OWNED_BY_UNKNOWN = 0;
	public static final int OWNED_BY_US = 1;
	public static final int OWNED_BY_THEM = 2;

	public static final int TERRAIN_UNKNOWN = -1;
	public static final int TERRAIN_LAND = GameConstants.LAND;
	public static final int TERRAIN_MOUNTAIN = GameConstants.MOUNTAIN;
	public static final int TERRAIN_WATER = GameConstants.WATER;

	public static final int SHORT_TERM_HISTORY_LENGTH = 10;

	public static final int LONG_TERM_HISTORY_LENGTH = 20;

	// Scoring Parameters

	public static int RANGE = 0;
	public static int NO_OF_ROUNDS = 0;
	public static int NO_OF_EXPLORERS = 0;

	public static final int FOOTPRINT_TARGETTING_OFFSET = 10;
	public static final int BOUNCING_THRESHOLD = 6;

	public static int CURRENT_TURN = 0;

	public static boolean TARGETTING_MODE_ON = false;
	
	public static int HISTORY_BOUNCES_INDEX = 0;
	public static int HISTORY_FOOTPRINT_INDEX = 1;
	public static int HISTORY_CLAIM_INDEX = 2;
	public static int HISTORY_DX_INDEX = 3;
	public static int HISTORY_DY_INDEX = 4;

	public static int getFootPrintThreshold(int currTurn, int totalNoOfTurns) {
		if (currTurn <= 0.2 * totalNoOfTurns) {
			//colin changed value from 10
			return 2;
		}
		else if (currTurn <= 0.4 * totalNoOfTurns) {
			return 4;
		} else if (currTurn <= 0.7 * totalNoOfTurns) {
			return 7;
		} else {
			return 10;
		}
		
		
	}

	public static int getWaterScore(int currTurn,int totalNoOfTurns){
		if (currTurn <= 0.4 * totalNoOfTurns) {
			return Constants.RANGE;
		} else if (currTurn <= 0.7 * totalNoOfTurns) {
			return 2;
		} else {
			return 1;
		}
	}
	
	public static int getClaimThreshold(int currTurn, int totalNoOfTurns) {
		if(currTurn <= 0.4 * totalNoOfTurns){
			return (int) (0.6 * LONG_TERM_HISTORY_LENGTH);
		}
		else if(currTurn <= 0.7 * totalNoOfTurns ){
			return (int) (0.3 * LONG_TERM_HISTORY_LENGTH);
		}
		else{
			return (int) (0.1 * LONG_TERM_HISTORY_LENGTH);
		}
	}
}
