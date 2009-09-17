package explorandum.g2.strategy;

import java.util.HashSet;

import explorandum.g2.map.Map;
import explorandum.g2.map.Node;
import explorandum.g2.util.Monitor;
import explorandum.g2.util.Util;
import explorandum.GameConstants;

public class RandomMove extends Strategy implements GameConstants{
	
	public RandomMove(Monitor monitor) {
		super(monitor);
		memory.useful = true;
	}

	@Override
	public void updateMemory(Map map) {
		HashSet<Node> moves = Util.possibleNonStaticMoves(map);
		for(Node n : moves){
			if(Util.isTraversable(n)){
				memory.nextMove = Util.direction(n, Util.whereAmI(map));
			}
		}
	}
}
