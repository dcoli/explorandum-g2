package explorandum.g2.strategy;

import explorandum.g2.map.Map;
import explorandum.g2.type.Memory;
import explorandum.g2.util.Monitor;

public abstract class Strategy {
	public Memory memory;
	private Monitor monitor;
	private Thread thread;

	public Strategy(Monitor monitor) {	
		memory = new Memory(false);
		this.monitor = monitor;
	}

	public final void start(Map map) {
		thread = new StrategyThread(map, monitor);
		thread.start();
	}

	protected abstract void updateMemory(Map map);

	public boolean alive() {
		return thread.isAlive();
	}

	public void kill() {
		thread.stop();
	}
	
	private class StrategyThread extends Thread {

		private Map map;
		private Monitor monitor;

		private StrategyThread(Map map, Monitor monitor) {
			this.map = map;
			this.monitor = monitor;
		}

		@Override
		public void run() {
			updateMemory(map);
			monitor.done(map.currentTime);
		}
	}
}
