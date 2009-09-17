package explorandum.g1;

public class Link {
	public int direction;
	public double cost;
	public Cell destination;

	/*
	 * The game time that the link's cost was last updated.
	 */
	public int lastUpdateTime;
}
