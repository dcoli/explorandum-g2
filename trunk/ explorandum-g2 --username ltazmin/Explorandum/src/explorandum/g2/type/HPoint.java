package explorandum.g2.type;

import java.awt.Point;

public class HPoint extends Point{
	private static final long serialVersionUID = 5089774077909842565L;
	
	public HPoint(int x, int y) {
		super(x, y);
	}
	
	public HPoint(Point p) {
		super(p);
	}
	
	public HPoint plus(HPoint o) {
	  return new HPoint(x + o.x, y + o.y);
	}
	
	public HPoint minus(HPoint o) {
	  return new HPoint(x - o.x, y - o.y);
	}
	
	@Override
	public int hashCode() {
		return 1000000 * x + y;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o != null && o instanceof HPoint) {
			return hashCode() == o.hashCode();
		}
		return false;
	}
	
	public static HPoint inverseHash(int code) {
		int rem = code % 1000000;
		int y = rem > 500000 ? rem - 1000000 : rem;
		int x = (code - y)/1000000;
		return new HPoint(x, y);
	}
	
	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}
