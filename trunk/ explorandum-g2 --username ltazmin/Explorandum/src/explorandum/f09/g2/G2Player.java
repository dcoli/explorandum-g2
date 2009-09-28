package explorandum.f09.g2;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.HashMap;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

import explorandum.GameConstants;
import explorandum.Logger;
import explorandum.Move;
import explorandum.Player;

/**
 * Extension of the Player class for Group 2
 * 
 * @author sharadh
 * @author colin
 * @author laima
 * 
 */
public class G2Player implements Player {

	private int _explorerID;
	public Logger _log;

	private Grid _grid;
	private Random _rand;
	private ArrayList<PastMove> _pastMoves;
	private TargetInfo _targetInfo;
	private TargetInfo _secondTargetInfo;
	private int _backTrackingIndex = 0;
	private int _tunnelState = -1;
	private boolean isCoastHugging = false;

	public G2Player() {
		_grid = new Grid(_log);
		_pastMoves = new ArrayList<PastMove>();
	}

	public Color color() throws Exception {
		return new Color(102, 205, 170);
	}

	public Move move(Point currentLocation_, Point[] offsets_,
			Boolean[] hasExplorer_, Integer[][] visibleExplorers_,
			Integer[] terrain_, int time_, Boolean StepStatus) throws Exception {

		Constants.CURRENT_TURN = time_;

		int newSeenCellCount = 0;
		// Update visible cell information for all cells
		for (int i = 0; i < offsets_.length; i++) {

			boolean isNewCell = _grid.updateSeenCellInformation(offsets_[i],
					terrain_[i], hasExplorer_[i], currentLocation_);

			if (isNewCell && i != 0) {
				newSeenCellCount++;
			}

		}

		// Update the visited cell information
		_grid.updateVisitedCellInformation(currentLocation_, time_, StepStatus,
				false);

		// Put move in pastMoves list
		_pastMoves.add(0, new PastMove(currentLocation_, offsets_,
				hasExplorer_, visibleExplorers_, time_, StepStatus,
				newSeenCellCount));

		if (_pastMoves.size() > Constants.LONG_TERM_HISTORY_LENGTH) {
			_pastMoves.remove(Constants.LONG_TERM_HISTORY_LENGTH - 1);
		}

		if (time_ > 0.8 * Constants.NO_OF_ROUNDS
				&& !Constants.CONSOLIDATION_MODE_ON) {
			Constants.CONSOLIDATION_MODE_ON = true;
			stopTargetting();
		}

		if (Constants.TARGETTING_MODE_ON
				&& _targetInfo.isTargetReached(currentLocation_)) {
			stopTargetting();
		}

		if (!Constants.CONSOLIDATION_MODE_ON && !Constants.TARGETTING_MODE_ON) {

			InfoObject[] info = _grid.analyseCurrentLocation(currentLocation_,
					offsets_, time_);

			boolean isCoastHugging = info[Constants.CURR_LOC_IS_COAST_HUGGING_INDEX]
					.isBoolField();
			boolean isNewCoastSeen = info[Constants.CURR_LOC_IS_NEW_COAST_SEEN_INDEX]
					.isBoolField();
			boolean isLocked = info[Constants.CURR_LOC_IS_LOCKED_BY_VISITED_INDEX]
					.isBoolField();

			boolean[] considerations = HistoryHelper.analyseHistory(_pastMoves,
					_grid, this, time_, Constants.NO_OF_ROUNDS, _log);

			boolean isBouncing = considerations[Constants.HISTORY_BOUNCES_INDEX];
			boolean isFootPrints = considerations[Constants.HISTORY_FOOTPRINT_INDEX];
			boolean isRetracing = considerations[Constants.HISTORY_CLAIM_INDEX];
			boolean isNewCellNotSeen = considerations[Constants.HISTORY_NEW_CELL_COUNT_INDEX];

			logStatus(isBouncing, "isBouncing");
			logStatus(isCoastHugging, "isCoastHugging");
			logStatus(isNewCoastSeen, "isNewCoastSeen");
			logStatus(isNewCellNotSeen, "isNewCellNotSeen");
			logStatus(isRetracing, "isRetracing");
			logStatus(isFootPrints, "isFootPrints");

			logStatus(isLocked, "isLocked");

			if (isBouncing) {
				handleBouncing(currentLocation_);
			} else if (isLocked) {
				gotoNearestUnvisitedNeighbour(currentLocation_);
			} else if (isCoastHugging) {
				Point next = Helper.getMoveFrom(currentLocation_, _grid, _log,
						_pastMoves.get(1).getCurrentLocation(), true);

				if (isFootPrints) {
					handleFootPrintOnCoast(currentLocation_, next);
				} else {
					// Check for footprint and do something
					return Helper.getMove(currentLocation_, next, _grid);
				}
			} else if (isNewCoastSeen) {
				// Handle New Coast
				handleNewCoast(currentLocation_,
						info[Constants.CURR_LOC_IS_NEW_COAST_SEEN_INDEX]);
			} else if (isFootPrints) {
				handleLocalCongestion(currentLocation_);
			} else if (isRetracing) {
				handleLocalCongestion(currentLocation_);
			} else if (isNewCellNotSeen) {
				handleLocalCongestion(currentLocation_);
			}

		}

		if (Constants.TARGETTING_MODE_ON) {
			Point next = _targetInfo.getNextPoint(currentLocation_);
			try {
				return Helper.getMove(currentLocation_, next, _grid);
			} catch (IllegalArgumentException ie) {
				_log.debug("Target Unreachable", ie);
				stopTargetting();
			}
		}

		if (Constants.CONSOLIDATION_MODE_ON) {
			gotoNearestUnvisitedNeighbour(currentLocation_);
		}
		// Point openNeighbour = Helper.getOpenTargetedCell(currentLocation_,
		// _grid, _log, 1).getPoint();
		// if (openNeighbour != null) {
		// return Helper.getMove(currentLocation_, openNeighbour, _grid);
		// }

		// if(time_ % 5 == 0){
		// gotoFarthestUnvisitedNeighbour(currentLocation_);
		// }
		try {
			Point p = Helper.getMoveFrom(currentLocation_, _grid, _log,
					_pastMoves.get(1).getCurrentLocation(), false);
			Move m = Helper.getMove(currentLocation_, p, _grid);
			return m;
		} catch (IllegalArgumentException iae) {
			_log.debug("Here");
			gotoNearestUnvisitedNeighbour(currentLocation_);
			return new Move(Move.STAYPUT);
		}

	}

	public String name() throws Exception {
		return "Lumbering Troglodyte";
	}

	public void register(int explorerID_, int rounds_, int explorers_,
			int range_, Logger log_, Random rand_) {

		// Update game variables
		_explorerID = explorerID_;
		// _log = new Logger(Logger.LogLevel.INFO, this.getClass());
		_log = log_;
		_rand = rand_;
		/**
		 * @author sharadh I know these are not constants .. I just wanted them
		 *         to easily accessible for now
		 */
		Constants.NO_OF_ROUNDS = rounds_;
		Constants.NO_OF_EXPLORERS = explorers_;
		Constants.RANGE = range_;
		Constants.TARGETTING_MODE_ON = false;

		Constants.CONSOLIDATION_MODE_ON = false;
		Constants.RANDOM_FUNC = rand_;

		// Clear previous information
		_grid.clear();
		_pastMoves.clear();
	}

	/**
	 * Stops targetting and clears targetting info
	 */
	public void stopTargetting() {
		Constants.TARGETTING_MODE_ON = false;
		_targetInfo = null;
		if (_secondTargetInfo != null) {
			startTargetting(_secondTargetInfo);
		}
	}

	/**
	 * Stops targetting and clears targetting info
	 */
	public void stopBackTracking() {
		Constants.BACKTRACKING_MODE_ON = false;
		// _backTrackingIndex = 0;
	}

	/**
	 * Sets targetting mode on and stores targetting info
	 * 
	 * @param currentLocation_
	 * @param target_
	 */
	public void startBackTracking() {
		Constants.BACKTRACKING_MODE_ON = true;
		// _backTrackingIndex = _pastMoves.size() - 2;
	}

	/**
	 * Sets targetting mode on and stores targetting info
	 * 
	 * @param currentLocation_
	 * @param target_
	 */
	public void startTargetting(TargetInfo targetInfo_) {
		Constants.TARGETTING_MODE_ON = true;
		_targetInfo = targetInfo_;
		_secondTargetInfo = null;
	}

	/**
	 * Sets targetting mode on and stores targetting info
	 * 
	 * @param currentLocation_
	 * @param target_
	 */
	public void startTargetting(TargetInfo targetInfo_, TargetInfo lit) {
		Constants.TARGETTING_MODE_ON = true;
		_targetInfo = targetInfo_;
		_secondTargetInfo = lit;
	}

	/**
	 * Handles new coast when seen
	 * 
	 * @param currentLocation_
	 * @param inf
	 */
	private void handleNewCoast(Point currentLocation_, InfoObject inf) {
		// Player is not coast hugging but found new water

		Point target = (Point) (inf.getObjField());

		// Ignore new water if it was near a footprint
		if (!_grid.isNearAnyCoastHuggedFootprint(target)) {
			startTargetting(new FixedPathTargetInfo(currentLocation_, target,
					Helper.getBeeLinePath(currentLocation_, target, false)));

			_log.debug("New Coast Seen. Current Location = " + currentLocation_
					+ " Target = " + target);

		} else {
			_log.warn("New Coast Ignored as it is near footprint");
		}
	}

	/**
	 * Handles footprint seen on coast
	 * 
	 * @param currentLocation_
	 */
	private void handleFootPrintOnCoast(Point currentLocation_, Point next) {

		// Player if Coast hugging and found footprints
		_grid.addToCoastHuggingFootprints(currentLocation_);

		// Player is not coast hugging but found new water

		Point target = Helper.getOpenTargetedCell(currentLocation_, _grid,
				_log, Constants.RANGE).getPoint();

		ArrayList<Point> path = Helper.getPath2(currentLocation_, target,
				_grid, _log);

		// Point prev = _pastMoves.get(1).getCurrentLocation();
		//
		// int dx = currentLocation_.x - prev.x;
		// int dy = currentLocation_.y - prev.y;

		int dx = next.x - currentLocation_.x;
		int dy = next.y - currentLocation_.y;

		Point secondTarget = new Point(target.x + Constants.RANGE * dx,
				target.y + Constants.RANGE * dy);

		startTargetting(
				new FixedPathTargetInfo(currentLocation_, target, path),
				new LumberingTargetInfo(target, secondTarget,
						Constants.CURRENT_TURN, _grid));

		_log.debug("Footprint on coast. Current Location = " + currentLocation_
				+ " Target = " + target + " Second Target = " + secondTarget);
	}

	/**
	 * Handles Bouncing
	 * 
	 * @param currentLocation_
	 */
	private void handleBouncing(Point currentLocation_) {

		gotoNearestUnvisitedNeighbour(currentLocation_);
	}

	/**
	 * 
	 * @param currentLocation_
	 */
	private void handleLocalCongestion(Point currentLocation_) {
		double unseenAreaPercent = _grid.getEstimatedUnseenAreaPercent();
		double unvisitedAreaPercent = _grid.getEstimatedUnvisitedAreaPercent();
		// System.out.println("Here " + unseenAreaPercent + " "
		// + unvisitedAreaPercent);

		if (unseenAreaPercent > 0.25) {
			// System.out.println("Here1");
			Point target = _grid.getUnexploredAreaTarget(currentLocation_);
			startTargetting(new LumberingTargetInfo(currentLocation_, target,
					Constants.CURRENT_TURN, _grid));

			_log.debug("Local Congestion.Explore Globally. Current Location = "
					+ currentLocation_ + " Target = " + target);
		} else {
			Point target = Helper.getOpenTargetedCell(currentLocation_, _grid,
					_log, Constants.RANGE).getPoint();

			// Point target =_grid.getFarthestUnVisitedPoint(currentLocation_);

			ArrayList<Point> path = Helper.getPath2(currentLocation_, target,
					_grid, _log);

			int dx = target.x - currentLocation_.x;
			int dy = target.y - currentLocation_.y;

			Point secondTarget = new Point(target.x + Constants.RANGE * dx,
					target.y + Constants.RANGE * dy);

			startTargetting(new FixedPathTargetInfo(currentLocation_, target,
					path), new LumberingTargetInfo(target, secondTarget,
					Constants.CURRENT_TURN, _grid));

			_log.debug("Local Congestion. Explore Locally. Current Location = "
					+ currentLocation_ + " Target = " + target);
		}

	}

	private void gotoNearestUnvisitedNeighbour(Point currentLocation_) {
		// System.out.println("Here1");
		Point target = _grid.getClosestUnVisitedPoint(currentLocation_);

		if (target != null) {
			// System.out.println("Here2 " + currentLocation_ + " " + target);
			ArrayList<Point> path = Helper.getPath2(currentLocation_, target,
					_grid, _log);

			if (path == null) {
				// System.out.println("Here3 " + path);
				startTargetting(new FixedPathTargetInfo(currentLocation_,
						target, path));

				_log.debug("Nearest Unvisited Neighbour. Current Location = "
						+ currentLocation_ + " Target = " + target);
			} else {
				HashSet<Point> hp = _grid.getUnVisitedCells();
				for (Iterator<Point> iterator = hp.iterator(); iterator
						.hasNext();) {
					Point point = (Point) iterator.next();
					path = Helper
							.getPath2(currentLocation_, point, _grid, _log);
					if (path != null) {
						break;
					}
				}

				startTargetting(new FixedPathTargetInfo(currentLocation_,
						target, path));

				_log.debug("Nearest Unvisited Neighbour. Current Location = "
						+ currentLocation_ + " Target = " + target);
			}
		} else {
			_log.debug("Nearest Unvisited Neighbour. Current Location = "
					+ currentLocation_ + " Target = None Exists");
		}
	}

	private void gotoFarthestUnvisitedNeighbour(Point currentLocation_) {
		// System.out.println("Here1");
		Point target = _grid.getFarthestUnVisitedPoint(currentLocation_);

		if (target != null) {
			// System.out.println("Here2 " + currentLocation_ + " " + target);
			ArrayList<Point> path = Helper.getPath2(currentLocation_, target,
					_grid, _log);

			// System.out.println("Here3 " + path);
			startTargetting(new FixedPathTargetInfo(currentLocation_, target,
					path));

			_log.debug("Nearest Unvisited Neighbour. Current Location = "
					+ currentLocation_ + " Target = " + target);
		} else {
			_log.debug("Nearest Unvisited Neighbour. Current Location = "
					+ currentLocation_ + " Target = None Exists");
		}
	}

	private void logStatus(boolean var, String varName) {
		_log.debug("[" + varName + "=" + var + "]");
	}
}
