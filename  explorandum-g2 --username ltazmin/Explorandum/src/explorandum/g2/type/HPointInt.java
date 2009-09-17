package explorandum.g2.type;


public class HPointInt implements Comparable<HPointInt> {
	public HPoint point;
	public int param;
	
	public HPointInt(HPoint point, int distance) {
		this.point = point;
		this.param = distance;
	}

	public int compareTo(HPointInt arg0) {
		int a = param;
		int b = arg0.param;
		if (param == arg0.param) {
			a = point.hashCode();
			b = arg0.point.hashCode();
		}
		return (new Integer(a)).compareTo(b);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o != null && o instanceof HPointInt) {
			return point.equals(((HPointInt)o).point);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "(" + point.x + "," + point.y + "):" + param;
	}
}
