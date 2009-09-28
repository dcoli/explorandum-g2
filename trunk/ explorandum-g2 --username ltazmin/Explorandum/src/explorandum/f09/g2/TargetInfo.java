package explorandum.f09.g2;

import java.awt.Point;
import java.util.ArrayList;

public  abstract class TargetInfo {

	protected Point _src;
	protected Point _target;

	public TargetInfo(Point src_, Point target_) {

		_src = src_;
		_target = target_;


	}

	/**
	 * @return the src
	 */
	public Point getSrc() {
		return _src;
	}

	/**
	 * @return the target
	 */
	public Point getTarget() {
		return _target;
	}

	public abstract boolean isTargetReached(Point currentLocation_);
	
	public abstract Point getNextPoint(Point currentLocation_);
}
