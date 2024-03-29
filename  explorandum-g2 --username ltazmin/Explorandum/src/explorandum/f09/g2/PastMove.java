package explorandum.f09.g2;

import java.awt.Point;

/**
 * Object to hold information about previous move.
 * @author sharadh
 *
 */
public class PastMove {

	Point currentLocation_;
	Point[] offsets_;
	Boolean[] hasExplorer_;
	Integer[][] visibleExplorers_;
	int time_;
	Boolean StepStatus;
	int newSeenCellCount;
	
	/**
	 * @return the newSeenCellCount
	 */
	public int getNewSeenCellCount() {
		return newSeenCellCount;
	}

	/**
	 * @return the currentLocation_
	 */
	public Point getCurrentLocation() {
		return currentLocation_;
	}

	/**
	 * @return the offsets_
	 */
	public Point[] getOffsets_() {
		return offsets_;
	}

	/**
	 * @return the hasExplorer_
	 */
	public Boolean[] getHasExplorer_() {
		return hasExplorer_;
	}

	/**
	 * @return the visibleExplorers_
	 */
	public Integer[][] getVisibleExplorers_() {
		return visibleExplorers_;
	}

	/**
	 * @return the time_
	 */
	public int getTime_() {
		return time_;
	}

	/**
	 * @return the stepStatus
	 */
	public Boolean getStepStatus() {
		return StepStatus;
	}



	public PastMove(Point currentLocation__, Point[] offsets__,
			Boolean[] hasExplorer__, Integer[][] visibleExplorers__,
			int time__, Boolean stepStatus_, int newSeenCellCount_) {
		super();
		currentLocation_ = currentLocation__;
		offsets_ = offsets__;
		hasExplorer_ = hasExplorer__;
		visibleExplorers_ = visibleExplorers__;
		time_ = time__;
		StepStatus = stepStatus_;
		newSeenCellCount = newSeenCellCount_;
	}
	
	
}
