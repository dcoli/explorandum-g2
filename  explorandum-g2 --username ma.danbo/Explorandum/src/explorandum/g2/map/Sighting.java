package explorandum.g2.map;

public class Sighting implements Comparable<Sighting>{
  private double distance;
  private int time;
  private int id;
  
  public Sighting(double distance, int time, int id) {
    this.distance = distance;
    this.time = time;
    this.id = id;
  }
  
  public int getId() {
    return id;
  }

  public double getDistance() {
    return distance;
  }

  public int getTime() {
    return time;
  }
  
  public boolean betterThan(Sighting arg0) {
    return distance < arg0.distance ||
         (distance == arg0.distance && time < arg0.time);
  }

  public int compareTo(Sighting arg0) {
    double dist0 = arg0.getDistance();
    if (distance == dist0) {
      return new Integer(time).compareTo(arg0.time);
    } else {
      return new Double(distance).compareTo(dist0);
    }
  }
}
