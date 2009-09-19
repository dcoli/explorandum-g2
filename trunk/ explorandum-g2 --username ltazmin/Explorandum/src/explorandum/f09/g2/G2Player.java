package explorandum.f09.g2;

import java.awt.Color;
import java.awt.Point;
import java.util.Random;

import explorandum.Logger;
import explorandum.Move;
import explorandum.Player;

/**
 * Extension of the Player class for Group 2
 * @author sharadh
 *
 */
public class G2Player implements Player {

	public Color color() throws Exception {
		return new Color(102,205,170);
	}

	public Move move(Point currentLocation_, Point[] offsets_,
			Boolean[] hasExplorer_, Integer[][] visibleExplorers_,
			Integer[] terrain_, int time_, Boolean StepStatus) throws Exception {

		for (int i = 0; i < offsets_.length; i++) {
			Grid.updateSeenCellInformation(offsets_[i], terrain_[i],
					hasExplorer_[i]);
		}

		Grid.updateVisitedCellInformation(currentLocation_, time_, StepStatus,
				false);

		Move m = Grid.getMoveFrom(currentLocation_);
		System.out.println(m);

		return m;

	}

	public String name() throws Exception {
		// TODO Auto-generated method stub
		return "Lumbering Troglodyte";
	}

	public void register(int explorerID_, int rounds_, int explorers_,
			int range_, Logger log_, Random rand_) {
		// TODO Auto-generated method stub

	}

}
