package explorandum.f09.g2;

import java.awt.Point;

public class TargetInfo {

	private Point target;
	private int originaldx = 0;
	private int originaldy = 0;
	private int moveCount = 0;
	private int maxMoveAllowed = 0;

	public TargetInfo(Point target_, Point currentLocation_) {
		super();
		target = target_;
		//System.out.println(target);

		//System.out.println(currentLocation_);
		originaldx = target_.x - currentLocation_.x;
		originaldy = target_.y - currentLocation_.y;
		maxMoveAllowed = Math.abs(originaldx) + Math.abs(originaldy);
	}

	/**
	 * @return the target
	 */
	public Point getTarget() {
		return target;
	}

	/**
	 * @return the originaldx
	 */
	public int getOriginaldx() {
		return originaldx;
	}

	/**
	 * @return the originaldy
	 */
	public int getOriginaldy() {
		return originaldy;
	}

	/**
	 * @return the moveCount
	 */
	public int getMoveCount() {
		return moveCount;
	}

	/**
	 * @return the maxMoveAllowed
	 */
	public int getMaxMoveAllowed() {
		return maxMoveAllowed;
	}

	/**
	 * Increments moveCount
	 */
	public void incrementMoveCount() {
		moveCount++;
	}
}
