package explorandum.f09.g2;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import explorandum.Logger;
import explorandum.Move;
import explorandum.Player;

public class Player2 implements Player {
	ArrayList<Point> CellMemory;
	Random rand;

	public Color color() throws Exception {
		// TODO Auto-generated method stub
		return Color.GREEN;
	}

	public Move move(Point currentLocation, Point[] offsets,
			Boolean[] hasExplorer, Integer[][] visibleExplorers,
			Integer[] terrain, int time, Boolean StepStatus) throws Exception {
		// TODO Auto-generated method stub
		int action = ACTIONS[rand.nextInt(ACTIONS.length)];
		return new Move(action);
	}

	public String name() throws Exception {
		// TODO Auto-generated method stub
		return "Lumbering Troglodyte";
	}

	public void register(int explorerID, int rounds, int explorers, int range,
			Logger log, Random rand) {
		CellMemory = new ArrayList<Point>();
		log.debug("\nRounds:" + rounds + "\nExplorers:" + explorers + "\nRange:" + range);
	}

}
