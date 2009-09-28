package explorandum.f09.g2;

import java.awt.Point;

public class LumberingTargetInfo extends TargetInfo {

	private int _turn;
	private Grid _grid;

	/**
	 * @param src_
	 * @param target_
	 * @param turn_
	 */
	public LumberingTargetInfo(Point src_, Point target_, int turn_, Grid grid_) {
		super(src_, target_);
		_turn = turn_;
		_grid = grid_;
	}

	/**
	 * @return the turn
	 */
	public int getTurn() {
		return _turn;
	}

	@Override
	public boolean isTargetReached(Point currentLocation_) {
		return currentLocation_.equals(_target);
	}

	@Override
	public Point getNextPoint(Point currentLocation_) {
		return Helper.lumberTowardsTarget(currentLocation_,
				this);
	}

	/**
	 * @return the grid
	 */
	public Grid getGrid() {
		return _grid;
	}

	/**
	 * @param grid_ the grid to set
	 */
	public void setGrid(Grid grid_) {
		_grid = grid_;
	}

}
