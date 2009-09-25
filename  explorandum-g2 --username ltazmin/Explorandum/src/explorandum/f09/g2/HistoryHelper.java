package explorandum.f09.g2;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.omg.CORBA._PolicyStub;

import explorandum.Logger;
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
	public static int[] analyseHistory( ArrayList<PastMove> pastMoves,
			Grid grid_, G2Player player_, int currTurn_, int totalNoOfTurns_, Logger log) {

		int historyCount = 0;
		int footprintCount = 0;
		int claimCount = 0;
		int dxCount = 0;
		int dyCount = 0;
		int isBouncing = 0;
		int[] retVal = new int[] { footprintCount, claimCount, dxCount, dyCount, isBouncing };
		
		
		/*
		 * bouncing - checks pastMoves for uniqueness within given threshold
		 */
		HashSet<Point> bounceHash = new HashSet();

		log.debug("pastmoves size "+pastMoves.size());

		if ( !Constants.TARGETTING_MODE_ON ) {

			for (PastMove m: pastMoves) {
				Cell c = grid_.getCell(m.getCurrentLocation_());

				//BOUNCING
				bounceHash.add(m.getCurrentLocation_());

				//FOOTSTEPS (TRACKING)
				historyCount++;
				if (historyCount <= Constants.SHORT_TERM_HISTORY_LENGTH) {
					log.debug("Here " + m.StepStatus + " "
							+ c.getFirstTurnVisited() + " "
							+ c.getLastTurnVisited() + " " + c.getOwner());
					if (m.StepStatus == true
							&& c.getOwner() != Constants.OWNED_BY_US) {
						footprintCount++;
						// //log.debug("Footprint seen");
					}

				}
				
				//CLAIMS (RETRACING)
				if (historyCount <= Constants.LONG_TERM_HISTORY_LENGTH) {
					if (c.getFirstTurnVisited() == c.getLastTurnVisited()
							&& c.getOwner() == Constants.OWNED_BY_US) {
						claimCount++;

					}
				}

			}
		}
		
		//BOUNCING
		log.debug("bounceHash size: "+ bounceHash.size() +" obj:"+bounceHash.toString());
		
		if ( pastMoves.size() - bounceHash.size() > Constants.BOUNCING_THRESHOLD) {
			retVal[Constants.HISTORY_BOUNCES_INDEX] = Constants.BOUNCING_THRESHOLD;
			/*log.debug("reached bouncing threshold: "+Constants.BOUNCING_THRESHOLD);
			Point curr = pastMoves.get(0).getCurrentLocation_();
			Point target = pastMoves.get( pastMoves.size() - Constants.BOUNCING_THRESHOLD - 2).getCurrentLocation_();//new Point(-30,-30);
			log.debug("switching to target from "+curr.toString()+" to "+target.toString());
			player_.startTargetting( curr, target);
			pastMoves.clear();
			System.exit(0);*/
		}
				
/*
		// log.debug(footprintCount);
		Point currentLocation = pastMoves.get(0).getCurrentLocation_();
		if (footprintCount >= Constants.getFootPrintThreshold(currTurn_,
				totalNoOfTurns_)) {

			Move k = Helper.getMove(pastMoves.get(1).getCurrentLocation_(),
					pastMoves.get(0).getCurrentLocation_());
			try {
				dxCount += GameConstants._dx[k.getAction()];
				dyCount += GameConstants._dy[k.getAction()];
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (dxCount == 0) {
				Cell c1 = grid_.getCell(currentLocation, 1, 0);
				Cell c2 = grid_.getCell(currentLocation, -1, 0);

				// log.debug("dx " + currentLocation);
				// log.debug(c1.getPoint() + " " + c1.getScore());
				// log.debug(c2.getPoint() + " " + c2.getScore());

				if (c1.getScore() >= c2.getScore()) {
					player_.startTargetting(currentLocation, new Point(
							currentLocation.x
									+ Constants.FOOTPRINT_TARGETTING_OFFSET,
							currentLocation.y));
				} else {
					player_.startTargetting(currentLocation, new Point(
							currentLocation.x
									- Constants.FOOTPRINT_TARGETTING_OFFSET,
							currentLocation.y));
				}
			} else if (dyCount == 0) {
				Cell c1 = grid_.getCell(currentLocation, 0, 1);
				Cell c2 = grid_.getCell(currentLocation, 0, -1);

				// log.debug("dy " + currentLocation);
				// log.debug(c1.getPoint() + " " + c1.getScore());
				// log.debug(c2.getPoint() + " " + c2.getScore());

				if (c1.getScore() >= c2.getScore()) {
					player_.startTargetting(currentLocation, new Point(
							currentLocation.x, currentLocation.y
									+ Constants.FOOTPRINT_TARGETTING_OFFSET));
				} else {
					player_.startTargetting(currentLocation, new Point(
							currentLocation.x, currentLocation.y
									- Constants.FOOTPRINT_TARGETTING_OFFSET));
				}
			}
		}

		// if (currTurn_>= 0.05 * totalNoOfTurns_ && claimCount <=
		// Constants.getClaimThreshold(currTurn_,
		// totalNoOfTurns_)) {
		// log.debug("Claim Threshold reached");
		// log.debug(grid_.getOpenRandomTarget(currentLocation));
		// }
*/
		return retVal;

	}
}
