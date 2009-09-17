package explorandum.g2.strategy;

import java.util.ArrayList;

import explorandum.GameConstants;
import explorandum.g2.map.Map;
import explorandum.g2.map.Node;
import explorandum.g2.type.HPoint;
import explorandum.g2.type.HPointInt;
import explorandum.g2.util.Monitor;
import explorandum.g2.util.Util;

public class EasyCoastHug extends Strategy implements GameConstants{

  private enum MODE{NONE, FOUND, HUGGING}
  private MODE mode;
  private int coastDir;
  private int clockwise;
  protected int terrainType;
  
  public EasyCoastHug(Monitor monitor) {
    super(monitor);
    reset();
    terrainType = WATER;
  }

  @Override
  protected void updateMemory(Map map) {
    if (mode == MODE.NONE) {
      ArrayList<HPoint> choices = Util.along(map, map.id, false, terrainType);
      if (choices.isEmpty()) {
        reset();
        return;
      }
      coastDir = Util.direction(choices.get(0), map.currentLocation);
      mode = MODE.FOUND;
      updateMemory(map);
    } else if (mode == MODE.FOUND) {
      int score1 = 0;
      Node current = map.get(map.currentLocation);
      int fakeCoastDir = coastDir;
      do {
        HPointInt next = Util.hug(map, current.getLocation(), fakeCoastDir, 1, terrainType);
        current = map.get(next.point);
        fakeCoastDir = next.param;
        score1++;
      }while(current != null && current.owns().getDistance() > Util.closest(current.getTerrain()));
      
      int score_1 = 0;
      current = map.get(map.currentLocation);
      fakeCoastDir = coastDir;
      do {
        HPointInt next = Util.hug(map, current.getLocation(), fakeCoastDir, -1, terrainType);
        current = map.get(next.point);
        fakeCoastDir = next.param;
        score_1++;
      }while(current != null && current.owns().getDistance() > Util.closest(current.getTerrain()));
      
      if (score1 <= 1 && score_1 <= 1) {
        reset();
        return;
      }
      
      if (score1 > score_1) {
        clockwise = 1;
      } else {
        clockwise = -1;
      }
      mode = MODE.HUGGING;
      updateMemory(map);
    } else if (mode == MODE.HUGGING){
      HPointInt next = Util.hug(map, map.currentLocation, coastDir, clockwise, terrainType);
      Node n = map.get(next.point);
      if (n.getTerrain() != LAND || n.owns().getDistance() == Util.closest(n.getTerrain())) {
        reset();
        return;
      } else {
        memory.useful = true;
        memory.nextMove = Util.direction(next.point, map.currentLocation);
        coastDir = next.param;
      }
    }
  }
  private void reset() {
    memory.useful = false;
    mode = MODE.NONE;
    coastDir = 0;
    clockwise = 0;
  }
}
