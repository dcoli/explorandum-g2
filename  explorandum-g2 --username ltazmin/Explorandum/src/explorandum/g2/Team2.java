package explorandum.g2;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import explorandum.Logger;
import explorandum.Move;
import explorandum.Player;
import explorandum.g2.map.Map;
import explorandum.g2.map.Node;
import explorandum.g2.strategy.EasyCoastHug;
import explorandum.g2.strategy.EasyMountainHug;
import explorandum.g2.strategy.FindOpenSpace;
import explorandum.g2.strategy.OpenBoundary;
import explorandum.g2.strategy.Pattern;
import explorandum.g2.strategy.RandomMove;
import explorandum.g2.strategy.Strategy;
import explorandum.g2.type.HPointInt;
import explorandum.g2.type.Memory;
import explorandum.g2.util.Monitor;
import explorandum.g2.util.Util;

public class Team2 implements Player {
	private Logger log;

	@SuppressWarnings("unused")
	private Random rand;

	public int n,pp,d,myID;

	public static enum STRATEGY{FINDOPENSPACE, COASTHUGGING, MOUNTAINHUGGING, PATTERN, OPENBOUNDARY, RANDOM}

	private final Monitor monitor = new Monitor(STRATEGY.values().length);

	public final Strategy[] strategies =  {new FindOpenSpace(monitor), new EasyCoastHug(monitor),  new EasyMountainHug(monitor), new Pattern(monitor), new OpenBoundary(monitor), new RandomMove(monitor)};

	private Map map;

	public void register(int explorerID, int rounds, int explorers, int range, Logger _log, Random _rand) {
		log = _log;
		rand = _rand;
		n=rounds;
		pp=explorers;
		d=range;
		myID=explorerID;
		map = new Map(explorerID, explorers, d, n);
	}

	public Color color() throws Exception {
		return Color.GREEN;
	}

	public Move move(Point currentLocation, Point[] offsets, Boolean[] hasExplorer, Integer[][] otherExplorers, Integer[] terrain, int time,Boolean StepStatus) throws Exception {
		//log.debug("Recording new information on the map.");
		map.see(currentLocation, offsets, hasExplorer, otherExplorers, terrain, time);
		Util.setPossibleSteppedNodes(map);
		int nextMove = updateStrategy().nextMove;
		for (Strategy s : strategies) {
			if (s.memory.nextMove == nextMove) {
				s.memory.used = true;
			}
		}

		return new Move(nextMove);
	}

	private Memory updateStrategy() {
		monitor.reset(map.currentTime);
		for (Strategy s : strategies) {
			//log.debug("Trying strategy: " + s.toString());
			try {
				s.start(map);
			} catch (Exception e) {
				e.printStackTrace();
				s.memory.useful = false;
			}
		}
		//System.out.println("before sleep");

		try {
			//monitor.waitFor(1000000);
			monitor.waitFor(900);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		for (Strategy s : strategies) {
			if (s.alive()) {
				s.kill();
				s.memory.useful = false;
			}
		}

		return pickBestStrategy();

	}

	private int hugging = 0;

	private Memory pickBestStrategy() {
		if (map.boundaryNodes.isEmpty()) {
			return strategies[STRATEGY.FINDOPENSPACE.ordinal()].memory;
		}
		if (strategies[STRATEGY.COASTHUGGING.ordinal()].memory.useful) {
			if (pp <= 2 || hugging < 4 * (d^2)) {
				log.debug("Coast hugging.");
				hugging++;
				return strategies[STRATEGY.COASTHUGGING.ordinal()].memory;
			} else {
				if (newCoast(strategies[STRATEGY.COASTHUGGING.ordinal()].memory.nextMove)) {
					hugging = 0;
				}
			}
		}

		if (aLotOfOpenSpace()) {
			if (inOpenSpace() && relativelyBigN() && lateInGame()) {
				log.debug("Pattern.");
				return strategies[STRATEGY.PATTERN.ordinal()].memory;
			} else {
				log.debug("Find open space.");
				return strategies[STRATEGY.FINDOPENSPACE.ordinal()].memory;
			}
		} 
		
		if (d <= 3 && mountainyRegion() && strategies[STRATEGY.MOUNTAINHUGGING.ordinal()].memory.useful) {
			return strategies[STRATEGY.MOUNTAINHUGGING.ordinal()].memory;
		}
		
		if (strategies[STRATEGY.OPENBOUNDARY.ordinal()].memory.useful) {
			log.debug("Open Boundary.");
			return strategies[STRATEGY.OPENBOUNDARY.ordinal()].memory;
		}

		if (strategies[STRATEGY.FINDOPENSPACE.ordinal()].memory.useful) {
			log.debug("Find open space as default.");
			return strategies[STRATEGY.FINDOPENSPACE.ordinal()].memory;
		}

		return strategies[STRATEGY.RANDOM.ordinal()].memory;
	}

	private boolean newCoast(int dir) {
		Node next = Util.getNodeInDir(dir, map.currentLocation, map);
		if (next.been()) return false;
		Node otherSide = Util.getNodeInDir(Util.opposite(dir), map.currentLocation, map);
		if (otherSide.been()) return false;
		return true;
	}

	private boolean lateInGame() {
		return map.currentTime > 3 * (map.d ^ 2);
	}

	private boolean relativelyBigN() {
		return (n - map.currentTime) > 10 * (map.area() - map.size());
	}

	private boolean mountainyRegion() {
		int good = 0;
		int count = 0;
		ArrayList<HPointInt> neighbors = Util.nextTo(map.currentLocation);
		for (HPointInt h : neighbors) {
			Node n = map.get(h.point);
			if (!Util.isWater(n)) {
				if (Util.isMountain(n)) {
					good++;
				}
				count++;
			}

		}
		return count >= good * 0.5;
	}

	private boolean inOpenSpace() {
		int count = 0;
		ArrayList<HPointInt> neighbors = Util.nextTo(map.currentLocation);
		for (HPointInt h : neighbors) {
			Node n = map.get(h.point);
			if (n.owns().getDistance() > 0 && n.getTerrain() == LAND) {
				count++;
			}
		}
		return count > neighbors.size() * 0.75;
	}

	private boolean aLotOfOpenSpace() {
		return map.area() > 4 * map.size();
	}

	public String name() throws Exception {
		return "Coppertop";
	}
}
