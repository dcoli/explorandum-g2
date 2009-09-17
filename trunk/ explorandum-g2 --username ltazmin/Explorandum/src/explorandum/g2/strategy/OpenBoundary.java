package explorandum.g2.strategy;

import explorandum.g2.map.Map;
import explorandum.g2.type.HPointInt;
import explorandum.g2.util.Monitor;
import explorandum.g2.util.Util;

public class OpenBoundary extends OpenCoast {

  public OpenBoundary(Monitor monitor) {
    super(monitor);
  }
  
  @Override
  protected HPointInt getNewPoint(Map map) {
    return Util.seeUnexplored(map, map.boundaryNodes);
  }
}
