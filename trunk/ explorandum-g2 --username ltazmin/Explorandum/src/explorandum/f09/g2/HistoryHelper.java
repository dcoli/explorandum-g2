package explorandum.f09.g2;

import java.util.ArrayList;
import java.util.Iterator;

import explorandum.GameConstants;
import explorandum.Move;

/**
 * Helper class to hold functions which analyze history
 * 
 * @author sharadh
 * 
 */
public class HistoryHelper {

	/**
	 * Analysis history based on past moves 
	 * Short term history size is used to determine if we are following opponents footprints
	 * and Long Term History Size is used to determine if we are going around in cells but not
	 * claiming any
	 * It also givens general idea of how we have moved so we can change direction easily
	 * 
	 * @param pastMoves
	 * @param grid_
	 * @return
	 */
	public static int[] analyseHistory(ArrayList<MoveHistory> pastMoves,
			Grid grid_) {

		int historyCount = 0;
		int footprintCount = 0;
		int claimCount = 0;
		Cell next = null;
		int dxCount = 0;
		int dyCount = 0;
		for (Iterator iterator = pastMoves.iterator(); iterator.hasNext();) {
			MoveHistory m = (MoveHistory) iterator.next();
			Cell c = grid_.getCell(m.getCurrentLocation_());
			historyCount++;

			if (historyCount <= Constants.SHORT_TERM_HISTORY_LENGTH) {
				if (m.StepStatus == true
						&& c.getFirstTurnVisited() == c.getLastTurnVisited()) {
					footprintCount++;
				}

				if (next != null) {
					Move k = Helper.getMove(c.getPoint(), next.getPoint());
					try {
						dxCount += GameConstants._dx[k.getAction()];
						dyCount += GameConstants._dy[k.getAction()];
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			if (historyCount <= Constants.LONG_TERM_HISTORY_LENGTH) {
				if (c.getFirstTurnVisited() == c.getLastTurnVisited()) {
					claimCount++;

				}
			}

			next = c;

		}

		int[] retVal = new int[] { footprintCount, claimCount, dxCount, dyCount };

		return retVal;

	}
}
