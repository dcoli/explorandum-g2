package explorandum.g2.type;

import java.util.ArrayList;

public class WaterStatus {
  public static enum TYPE{WEIRD, HOLE, NARROWS, COAST, CORNER, NOT_ALONG_COAST}
  public TYPE type;
  public ArrayList<Integer> direction;
  
  public WaterStatus(TYPE type, ArrayList<Integer> direction){
    this.type = type;
    this.direction = direction;
  }
}
