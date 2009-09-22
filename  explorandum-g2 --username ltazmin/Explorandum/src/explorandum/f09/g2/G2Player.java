package explorandum.f09.g2;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import explorandum.Logger;
import explorandum.Move;
import explorandum.Player;

/**
 * Extension of the Player class for Group 2
 * 
 * @author sharadh
 * 
 */
public class G2Player implements Player {

	private int _explorerID;
	private Logger _log;

	private Grid _grid;
	private Random _rand;
	private ArrayList<PastMove> _pastMoves;
	private TargetInfo _targetInfo;
	

	public G2Player() {
		_grid = new Grid();
		_pastMoves = new ArrayList<PastMove>();
	}

	public Color color() throws Exception {
		return new Color(102, 205, 170);
	}

	public Move move(Point currentLocation_, Point[] offsets_,
			Boolean[] hasExplorer_, Integer[][] visibleExplorers_,
			Integer[] terrain_, int time_, Boolean StepStatus) throws Exception {

		int newSeenCellCount = 0;
		// Update visible cell information for all cells
		for (int i = 0; i < offsets_.length; i++) {
			if(_grid.updateSeenCellInformation(offsets_[i], terrain_[i],
					hasExplorer_[i],currentLocation_)){
				newSeenCellCount++;
			}
		}

		// Update the visited cell information
		_grid.updateVisitedCellInformation(currentLocation_, time_, StepStatus,
				false);

		// Put move in pastMoves list
		_pastMoves.add(0, new PastMove(currentLocation_, offsets_,
				hasExplorer_, visibleExplorers_, time_, StepStatus,newSeenCellCount));
		
		if(_pastMoves.size() > Constants.LONG_TERM_HISTORY_LENGTH){
			_pastMoves.remove(Constants.LONG_TERM_HISTORY_LENGTH -1);
		}

		
		// System.out.println("Here 1");
		HistoryHelper.analyseHistory(_pastMoves, _grid, this, time_,
				Constants.NO_OF_ROUNDS);

		// System.out.println("Here 3");
		if (Constants.TARGETTING_MODE_ON) {
			evaluateTargetting(currentLocation_);
			Move m = Helper.getNextMoveTowardsTarget(currentLocation_, _grid,
					_targetInfo);
			if (m != null) {
				return m;
			} else {
				//System.out.println(":(1");
				stopTargetting();
			}
		}
		// System.out.println("Here 4");
		Move m = Helper.getMoveFrom(currentLocation_, _grid);
		//System.out.println(m);

		return m;

	}

	public String name() throws Exception {
		return "Lumbering Troglodyte";
	}

	public void register(int explorerID_, int rounds_, int explorers_,
			int range_, Logger log_, Random rand_) {

		// Update game variables
		_explorerID = explorerID_;
		_log = log_;
		_rand = rand_;
		/**
		 * @author sharadh I know these are not constants .. I just wanted them
		 *         to easily accessible for now
		 */
		Constants.NO_OF_ROUNDS = rounds_;
		Constants.NO_OF_EXPLORERS = explorers_;
		Constants.RANGE = range_;

		// Clear previous information
		_grid.clear();
		_pastMoves.clear();
	}

	/**
	 * Stops targetting and clears targetting info
	 */
	public void stopTargetting() {

		//System.out.println("Stopping Targetting");
		Constants.TARGETTING_MODE_ON = false;
		_targetInfo = null;
	}

	/**
	 * Sets targetting mode on and stores targetting info
	 * 
	 * @param currentLocation_
	 * @param target_
	 */
	public void startTargetting(Point currentLocation_, Point target_) {
		//System.out.println("Started Targetting");
		Constants.TARGETTING_MODE_ON = true;
		_targetInfo = new TargetInfo(target_, currentLocation_);
		//System.out.println(currentLocation_);
		//System.out.println(target_);
	}

	public void evaluateTargetting(Point currentLocation_) {
		if (currentLocation_.equals(_targetInfo.getTarget())) {

			// System.out.println(":(2");
			stopTargetting();
		}
		_targetInfo.incrementMoveCount();
		if (_targetInfo.getMoveCount() >= _targetInfo.getMaxMoveAllowed()) {

			// System.out.println(":(3");
			stopTargetting();
		}
	}

}
