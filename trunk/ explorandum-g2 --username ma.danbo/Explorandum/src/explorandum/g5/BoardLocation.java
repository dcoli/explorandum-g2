package explorandum.g5;

public class BoardLocation {
	
	public int terrain;
	public double score;
	public boolean flag, stand;
	
	
	public BoardLocation() {
		terrain = 0;
		score = 0;
		flag = false;
		stand = false;
	}
	
	public BoardLocation(int ter, double score1, boolean flagBool,  boolean stand1) {
		terrain = ter;
		score = score1;
		stand = stand1;
		flag = flagBool;
	}
	
	
	//methods
	public void setTerrain(int terrain) {
		this.terrain = terrain;
	}
	
	public void setScore(double score) {
		this.score = score;
	}
	
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	
	public void setStand(boolean stand) {
		this.stand = stand;
	}
	
	
	
	
	
}
