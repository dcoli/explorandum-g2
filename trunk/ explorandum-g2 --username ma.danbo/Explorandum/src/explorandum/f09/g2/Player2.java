package explorandum.f09.g2;

import java.awt.Color;
import java.awt.Point;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Properties;

import explorandum.Logger;
import explorandum.Move;
import explorandum.Player;

public class Player2 implements Player {
	ArrayList<Point> CellMemory;
	Random rand;
	Logger log;


	public Color color() throws Exception {
		return Color.GREEN;
	}

	
	public Move move(Point currentLocation, Point[] offsets,
			Boolean[] hasExplorer, Integer[][] visibleExplorers,
			Integer[] terrain, int time, Boolean StepStatus) throws Exception {
		// TODO create custom action
		int action = ACTIONS[rand.nextInt(ACTIONS.length)];
		return new Move(action);
	}

	
	public String name() throws Exception {
		return "Lumbering Troglodyte";
	}

	
	public void register(int explorerID, int rounds, int explorers, int range,
			Logger _log, Random rand) {
		CellMemory = new ArrayList<Point>();
		log = _log;
		log.debug("\nRounds:" + rounds + "\nExplorers:" + explorers + "\nRange:" + range);
	}


}
