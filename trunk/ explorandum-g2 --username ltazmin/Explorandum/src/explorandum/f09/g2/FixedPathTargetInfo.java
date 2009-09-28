package explorandum.f09.g2;

import java.awt.Point;
import java.util.ArrayList;

public class FixedPathTargetInfo extends TargetInfo {

	/**
	 * @param src_
	 * @param target_
	 * @param path_
	 */
	public FixedPathTargetInfo(Point src_, Point target_, ArrayList<Point> path_) {
		super(src_, target_);
		path = path_;
		if (path.get(0).equals(src_)) {
			path.remove(0);
		}
	}

	private ArrayList<Point> path;
	private int count = 0;

	@Override
	public boolean isTargetReached(Point currentLocation_) {
		return count >= path.size();
	}

	@Override
	public Point getNextPoint(Point currentLocation_) {
		return path.get(count++);
	}

}
