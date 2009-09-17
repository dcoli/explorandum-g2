package explorandum.g2.strategy;

import explorandum.g2.util.Monitor;

public class EasyMountainHug extends EasyCoastHug {
  
  public EasyMountainHug(Monitor monitor){
    super(monitor);
    terrainType = MOUNTAIN;
  }
 }
