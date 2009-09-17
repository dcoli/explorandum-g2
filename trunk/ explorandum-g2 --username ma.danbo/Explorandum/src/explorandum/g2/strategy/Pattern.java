package explorandum.g2.strategy;

import explorandum.GameConstants;
import explorandum.g2.map.Map;
import explorandum.g2.util.Monitor;
import explorandum.g2.util.Util;

public class Pattern extends Strategy {

	private int xmark;
	private int ymark;
	private int xtraversed;
	private int ytraversed;
	private int size;
	
	public Pattern(Monitor monitor){
		super(monitor);
		memory.useful = true;
		memory.used = false;
	}
	
	@Override
	public void updateMemory(Map map) {	
		// Restart if I wasn't used last turn.
		if(!memory.used)
			reset(map);
		spiral();
	}

	private void reset(Map m) {
		xmark = Util.whereAmI(m).getLocation().x;
		ymark = Util.whereAmI(m).getLocation().y;
		ytraversed = 0;
		xtraversed = 0;
		size = 1;
	}

	// Go right, then down, then left, then up, then right,
	// Then increase size.
	private void spiral() {
		// If in top right, increase size and go down right.
		if(xtraversed == size && ytraversed == size){
			size++;
			memory.nextMove = GameConstants.SOUTHEAST;
			ytraversed--;
			xtraversed++;
		}
		
		// If in top, go right.
		else if(ytraversed == size){
			memory.nextMove = GameConstants.EAST;
			xtraversed++;
		}
		
		// If in left, go up.
		else if(xtraversed == -size){
			memory.nextMove = GameConstants.NORTH;
			ytraversed++;
		}
		
		// If in bottom, go left.
		else if(ytraversed == -size){
			memory.nextMove = GameConstants.WEST;
			xtraversed--;
		}
		
		// If in right, go down.
		else if(xtraversed == size){
			memory.nextMove = GameConstants.SOUTH;
			ytraversed--;
		}
		
		// Else go right.
		else {
			memory.nextMove = GameConstants.EAST;
			xtraversed++;
		}
	}

}


