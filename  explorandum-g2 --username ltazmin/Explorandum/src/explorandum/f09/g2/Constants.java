package explorandum.f09.g2;

import java.util.Random;

import explorandum.GameConstants;
import explorandum.Move;

/**
 * Class for holding constants
 * 
 * @author sharadh
 * @author colin
 * @author laima
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

	public static final float RATIO_UNEXPLORED_MAP_THRESHOLD = (float) .50;

	public static int CURRENT_TURN = 0;

	public static boolean TARGETTING_MODE_ON = false;
	public static boolean BACKTRACKING_MODE_ON = false;
	public static boolean CONSOLIDATION_MODE_ON = false;
	public static final int OPENNESS_SCORE_THRESHOLD = 9;
	public static final int HISTORY_BOUNCES_INDEX = 0;
	public static final int HISTORY_FOOTPRINT_INDEX = 1;
	public static final int HISTORY_CLAIM_INDEX = 2;
	public static final int HISTORY_NEW_CELL_COUNT_INDEX = 3;

	public static final int GRID_EXLPORE_PERCENT_INDEX = 0;
	public static final int GRID_MAX_X_EXPLORE_INDEX = 1;
	public static final int GRID_MIN_X_EXPLORE_INDEX = 2;
	public static final int GRID_MAX_Y_EXPLORE_INDEX = 3;
	public static final int GRID_MIN_Y_EXPLORE_INDEX = 4;
	
	public static final int CURR_LOC_IS_COAST_HUGGING_INDEX = 0;
	public static final int CURR_LOC_IS_NEW_COAST_SEEN_INDEX = 1;
	public static final int CURR_LOC_IS_LOCKED_BY_VISITED_INDEX = 2;
	public static final int CURR_LOC_IS_ENTERING_TUNNEL_INDEX = 3;
	public static final int CURR_LOC_IS_BACKTRACKING_FROM_TUNNEL_INDEX = 4;
	public static final int CURR_LOC_IS_IN_DEAD_END_INDEX = 5;

	public static final int CURR_LOC_IS_IN_TUNNEL_CYCLE_INDEX = 6;

	public static Random RANDOM_FUNC;

	public static int getFootPrintThreshold(int currTurn, int totalNoOfTurns) {
		if (currTurn <= 0.2 * totalNoOfTurns) {
			// colin changed value from 10
			return 2;
		} else if (currTurn <= 0.4 * totalNoOfTurns) {
			return 4;
		} else if (currTurn <= 0.7 * totalNoOfTurns) {
			return 7;
		} else {
			return 15;
		}

	}

	public static int getWaterScore(int currTurn, int totalNoOfTurns) {
		if (currTurn <= 0.4 * totalNoOfTurns) {
			return Constants.RANGE;
		} else if (currTurn <= 0.7 * totalNoOfTurns) {
			return Constants.RANGE;
		} else {
			return Constants.RANGE;
		}
	}

	public static int getClaimThreshold(int currTurn, int totalNoOfTurns) {
		if (currTurn <= 0.4 * totalNoOfTurns) {
			return (int) (0.6 * LONG_TERM_HISTORY_LENGTH);
		} else if (currTurn <= 0.7 * totalNoOfTurns) {
			return (int) (0.3 * LONG_TERM_HISTORY_LENGTH);
		} else {
			return (int) (0.1 * LONG_TERM_HISTORY_LENGTH);
		}
	}
	
	
}
