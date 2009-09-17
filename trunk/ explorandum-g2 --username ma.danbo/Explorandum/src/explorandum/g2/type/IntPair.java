package explorandum.g2.type;


public class IntPair extends HPoint{

	private static final long serialVersionUID = -5795963850064047131L;

	public IntPair(int x, int y){
		super(x, y);
	}
	
	public static IntPair sum(IntPair a, IntPair b){
		return new IntPair(a.x + b.x, a.y + b.y);
	}
	
	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}
