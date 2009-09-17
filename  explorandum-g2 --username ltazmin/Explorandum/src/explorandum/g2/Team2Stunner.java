package explorandum.g2;

import java.awt.Color;
import java.awt.Point;
import java.util.Random;

import explorandum.Logger;
import explorandum.Move;
import explorandum.Player;
import explorandum.g2.map.Map;
import explorandum.g2.strategy.OpenBoundary;
import explorandum.g2.strategy.OpenCoast;
import explorandum.g2.strategy.RandomMove;
import explorandum.g2.util.Monitor;

public class Team2Stunner implements Player {

  public int n,pp,d,myID;
  private Map map;
  private OpenBoundary openBoundary;
  private OpenCoast openCoast;
  private RandomMove randomMove;
  private Monitor monitor;
  private ThreadStarter starter;
  
  public Team2Stunner() {
    monitor = new Monitor(3);
    openBoundary = new OpenBoundary(monitor);
    openCoast = new OpenCoast(monitor);
    randomMove = new RandomMove(monitor);
    starter = new ThreadStarter(100);
  }
  
  public Color color() throws Exception {
    return Color.GREEN;
  }

  
  public Move move(Point currentLocation, Point[] offsets,
      Boolean[] hasExplorer, Integer[][] visibleExplorers, Integer[] terrain,
      int time,Boolean StepStatus) throws Exception {
    int limit = 10 * (pp - 1);
    if (time < limit + 4) {
      starter.done();
    }
    map.see(currentLocation, offsets, hasExplorer, visibleExplorers, terrain, time);
    monitor.reset(time);
    openBoundary.start(map);
    openCoast.start(map);
    randomMove.start(map);
    monitor.waitFor(time < limit ? 20 : 900);
    System.out.println("Got move!");
    
    if (openCoast.alive()) {
      openCoast.memory.useful = false;
    }
    if (openBoundary.alive()) {
      openBoundary.memory.useful = false;
    }
    
    if (time < limit) {
      System.out.println("Starting threads!");
      starter.run();
    }
    if (openCoast.memory.useful) {
      System.out.println("Open coast");
      return new Move(openCoast.memory.nextMove);
    } else if (openBoundary.memory.useful) {
      System.out.println("Open boundary");
      return new Move(openBoundary.memory.nextMove);
    }
    System.out.println("Random move");
    return new Move(randomMove.memory.nextMove);
  }

  public String name() throws Exception {
    return "Stunner";
  }

  public void register(int explorerID, int rounds, int explorers, int range,
      Logger _log, Random _rand) {
    n=rounds;
    pp=explorers;
    d=range;
    myID=explorerID;
    map = new Map(explorerID, explorers, d, n);
  }

  
  private class ThreadStarter {
    private Thread[] threads;
    private volatile double ctr;
    
    private ThreadStarter(int num) {
      threads = new Thread[num];
      ctr = 0;
    }
    
    public void run() {
      for (int i = 0; i < threads.length; i++) {
        threads[i] = new Thread() {
          public void run() {
            long start = System.currentTimeMillis();
            while(System.currentTimeMillis() < start + threads.length * 10) {
              ctr = Math.pow((Math.random() / Math.random()), Math.random());
            }
          }
        };
      }
      for (Thread t : threads) {
        t.start();
      }
    }
    
    private void done() {
      (new ThreadDeleter()).start();
    }
    
    private class ThreadDeleter extends Thread {
      public void run() {
        System.out.println("Deleting threads!");
        finish();
      }
    }
    
    @SuppressWarnings("deprecation")
    private void finish() {
      for (Thread t : threads) {
        if (t != null && t.isAlive())t.stop();
      }
    }
    
    public double getCtr() {
      return ctr;
    }
  }
}
