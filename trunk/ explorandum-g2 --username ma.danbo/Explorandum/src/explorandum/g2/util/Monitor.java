package explorandum.g2.util;

public class Monitor {
  private volatile int responses = 0;
  private volatile int time = 0;
  private int expected;
  
  public Monitor(int expected) {
    this.expected = expected;
  }
  
  public synchronized void waitFor(long time) throws InterruptedException {
    if (responses == expected) {
      return; 
    }
    wait(time);
  }

  public synchronized void done(int time) {
    if (this.time == time) {
      responses++;
      if (responses == expected) {
        notifyAll();
      }
    }
  }
  
  public synchronized void reset(int time) {
    this.time = time;
    responses = 0;
  }
}
