package explorandum.f09.g2;

import java.awt.Point;
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
	 * Analysis history based on past moves Short term history size is used to
	 * determine if we are following opponents footprints and Long Term History
	 * Size is used to determine if we are going around in cells but not
	 * claiming any It also givens general idea of how we have moved so we can
	 * change direction easily
	 * 
	 * @param pastMoves
	 * @param grid_
	 * @return
	 */
	public static int[] analyseHistory(ArrayList<PastMove> pastMoves,
			Grid grid_, G2Player player_,int currTurn_,int totalNoOfTurns_) {

		int historyCount = 0;
		int footprintCount = 0;
		int claimCount = 0;
		Cell next = null;
		int dxCount = 0;
		int dyCount = 0;
		for (Iterator iterator = pastMoves.iterator(); iterator.hasNext();) {
			PastMove m = (PastMove) iterator.next();
			Cell c = grid_.getCell(m.getCurrentLocation_());
			historyCount++;

			if (historyCount <= Constants.SHORT_TERM_HISTORY_LENGTH) {
				// //System.out.println("Here " +m.StepStatus + " " +
				// c.getFirstTurnVisited() + " " + c.getLastTurnVisited());
				if (m.StepStatus == true
						&& c.getFirstTurnVisited() == c.getLastTurnVisited()) {
					footprintCount++;
					// //System.out.println("Footprint seen");
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

		Point currentLocation = pastMoves.get(0).getCurrentLocation_();
		if (footprintCount >= Constants.getFootPrintThreshold(currTurn_, totalNoOfTurns_)) {
			if (dxCount == 0) {
				Cell c1 = grid_.getCell(currentLocation, 1, 0);
				Cell c2 = grid_.getCell(currentLocation, -1, 0);

				//System.out.println("dx "  + currentLocation);
				//System.out.println(c1.getPoint() + " " + c1.getScore());
				//System.out.println(c2.getPoint() + " " + c2.getScore());

				if (c1.getScore() >= c2.getScore()) {
					player_.startTargetting(currentLocation, new Point(
							currentLocation.x + Constants.FOOTPRINT_TARGETTING_OFFSET, currentLocation.y));
				} else {
					player_.startTargetting(currentLocation, new Point(
							currentLocation.x - Constants.FOOTPRINT_TARGETTING_OFFSET, currentLocation.y));
				}
			} else if (dyCount == 0) {
				Cell c1 = grid_.getCell(currentLocation, 0, 1);
				Cell c2 = grid_.getCell(currentLocation, 0, -1);

				//System.out.println("dy "  + currentLocation);
				//System.out.println(c1.getPoint() + " " + c1.getScore());
				//System.out.println(c2.getPoint() + " " + c2.getScore());

				if (c1.getScore() >= c2.getScore()) {
					player_.startTargetting(currentLocation, new Point(
							currentLocation.x, currentLocation.y + Constants.FOOTPRINT_TARGETTING_OFFSET));
				} else {
					player_.startTargetting(currentLocation, new Point(
							currentLocation.x, currentLocation.y - Constants.FOOTPRINT_TARGETTING_OFFSET));
				}
			}
		}
		
//		if (currTurn_>= 0.05 * totalNoOfTurns_ && claimCount <= Constants.getClaimThreshold(currTurn_,
//				totalNoOfTurns_)) {
//			System.out.println("Claim Threshold reached");
//			System.out.println(grid_.getOpenRandomTarget(currentLocation));
//		}

		return retVal;

	}
}
