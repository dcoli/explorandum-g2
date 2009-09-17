package explorandum.g2.strategy;

import explorandum.GameConstants;
import explorandum.g2.map.Map;
import explorandum.g2.type.HPoint;
import explorandum.g2.type.HPointInt;
import explorandum.g2.util.Monitor;
import explorandum.g2.util.Util;

public class OpenCoast extends Strategy implements GameConstants {

  private static enum MODE{GOING, LOOKING}
  private MODE mode;
  private HPoint point;

  public OpenCoast(Monitor monitor) {
    super(monitor);
    reset();
  }

  @Override
  public void updateMemory(Map map) {
    if (memory.used && mode == MODE.GOING) {
      if (map.contains(point) && map.get(point).owns().getDistance() == 0) {
        reset();
        updateMemory(map);
      } else {
        memory.useful = true;
        HPoint location = map.currentLocation;
        memory.nextMove = Util.nextInOpenPath(map, location, point, true).x;
      }
    } else {
      HPointInt pointInt = getNewPoint(map);
      if (pointInt.point != null) {
        memory.useful = true;
        point = pointInt.point;
        mode = MODE.GOING;
        memory.nextMove = pointInt.param;
      } else {
        reset();
      }
    }
  }

  protected HPointInt getNewPoint(Map map) {
    return Util.seeUnexplored(map, map.unexploredCoast);
  }

  private void reset() {
    mode = MODE.LOOKING;
    point = null;
    memory.useful = false;
  }
}
