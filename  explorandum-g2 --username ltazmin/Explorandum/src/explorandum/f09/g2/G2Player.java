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

	private ArrayList<PastMove> _pastMoves;

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

		// Put move in pastMoves list
		_pastMoves.add(0, new PastMove(currentLocation_, offsets_,
				hasExplorer_, visibleExplorers_, time_, StepStatus));

		// Update visible cell information for all cells
		for (int i = 0; i < offsets_.length; i++) {
			_grid.updateSeenCellInformation(offsets_[i], terrain_[i],
					hasExplorer_[i]);
		}

		// Update the visited cell information
		_grid.updateVisitedCellInformation(currentLocation_, time_, StepStatus,
				false);

		Move m = Helper.getMoveFrom(currentLocation_, _grid);
		System.out.println(m);

		return m;

	}

	public String name() throws Exception {
		// TODO Auto-generated method stub
		return "Lumbering Troglodyte";
	}

	public void register(int explorerID_, int rounds_, int explorers_,
			int range_, Logger log_, Random rand_) {

		// Update game variables
		_explorerID = explorerID_;
		_log = log_;
		
		/**
		 * @author sharadh
		 * I know these are not constants .. I just wanted them to 
		 * easily accessible for now
		 */
		Constants.NO_OF_ROUNDS = rounds_;
		Constants.NO_OF_EXPLORERS = explorers_;
		Constants.RANGE = range_;


		// Clear previous information
		_grid.clear();
		_pastMoves.clear();
	}

}
