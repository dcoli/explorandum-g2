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
	public static boolean[] analyseHistory( ArrayList<PastMove> pastMoves,
			Grid grid_, G2Player player_, int currTurn_, int totalNoOfTurns_, Logger log) {

		int historyCount = 0;
		int footprintCount = 0;
		int claimCount = 0;
		int dxCount = 0;
		int dyCount = 0;
		boolean isBouncing = true, isTracking = true, isRetracing = true;
		boolean[] retVal = { false, false, false };
		
		
		/*
		 * bouncing - checks pastMoves for uniqueness within given threshold
		 */
		HashSet<Point> bounceHash = new HashSet();

//		log.debug("pastmoves size "+pastMoves.size());

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
						log.debug("Footprint seen");
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
		
		//TEST BOUNCING
		
		if ( pastMoves.size() - bounceHash.size() > Constants.BOUNCING_THRESHOLD) {
			isBouncing = true;
			log.debug("pastMoves size: "+ pastMoves.size() +" obj:"+pastMoves.toString());
			log.debug("bounceHash size: "+ bounceHash.size() +" obj:"+bounceHash.toString());
		}
				

		//TEST FOOTPRINTS
		Point currentLocation = pastMoves.get(0).getCurrentLocation_();
		if (footprintCount >= Constants.getFootPrintThreshold(currTurn_,totalNoOfTurns_)) { //if too many footprints
			isTracking = true;
			log.debug(footprintCount);

		}

		// TEST RETRACING
		if (currTurn_>= 0.05 * totalNoOfTurns_ && claimCount <= Constants.getClaimThreshold(currTurn_, totalNoOfTurns_)) {
			log.debug("Claim Threshold reached");
			isRetracing = true;
			// log.debug(grid_.getOpenRandomTarget(currentLocation));
		}

		retVal[Constants.HISTORY_BOUNCES_INDEX] = isBouncing;
		retVal[Constants.HISTORY_FOOTPRINT_INDEX] = isTracking;
		retVal[Constants.HISTORY_CLAIM_INDEX] = isRetracing;
		return retVal;

	}
}

/* MOVING THIS TO THE G2PLAYER CLASS
 * 			Move k = Helper.getMove(pastMoves.get(1).getCurrentLocation_(),
					pastMoves.get(0).getCurrentLocation_());
			try {
				dxCount += GameConstants._dx[k.getAction()];
				dyCount += GameConstants._dy[k.getAction()];
			} catch (Exception e) {
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
			}*/