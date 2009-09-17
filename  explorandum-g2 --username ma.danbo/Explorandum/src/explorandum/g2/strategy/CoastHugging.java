package explorandum.g2.strategy;




import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import explorandum.g2.type.HPoint;
import explorandum.g2.util.Monitor;
import explorandum.g2.util.Util;
import explorandum.g2.map.Map;
import explorandum.g2.map.Node;
import explorandum.GameConstants;

public class CoastHugging extends Strategy implements GameConstants{
  //		if (alreadyBeenThere() || (otherPlayerComingAtUs() && bigEnoughN())) {
  //			strategy.get(s).used = false;
  //		} else if (alongCoastHaventTried() && !checkForNearbyPlayer()) {
  //			startCoastHugging();
  //		}

  public CoastHugging(Monitor monitor) {
    super(monitor);
  }

  @Override
  public void updateMemory(Map map) {
    boolean N=false;
    boolean E=false;
    boolean S=false;
    boolean W=false;
    boolean NE=false;
    boolean SE=false;
    boolean SW=false;
    boolean NW=false;

    int pdir=0;
    ArrayList<Integer> water =new ArrayList<Integer>();
    ArrayList<Integer> val =new ArrayList<Integer>();
    ArrayList<Integer> mountain =new ArrayList<Integer>();
    HashSet<Node> nodes = map.adjacent(map.currentLocation);
    Iterator<Node> it=nodes.iterator();
    int k =0;

    if(memory.used && map.currentLocation.x==0 && map.currentLocation.y==0)
    {
      memory.useful =false;
      return;
    }

    for(k=0;k<_dx.length;k++)
    {
      if(_dx[k]!=0||_dy[k]!=0)
      {
        //while(it.hasNext())
        for(Node nod : nodes)
        {

          //Point point=(Point)it.next();
          //Node nod=it.next();
          HPoint point=nod.getLocation();
          if(point.x == map.currentLocation.x + _dx[k] && point.y == map.currentLocation.y + _dy[k]  )
          {
            //System.out.println("if while");
            if(nod.getTerrain()==LAND)
            {
              //System.out.println("land");	
              val.add(k);
            }
            if(nod.getTerrain()==WATER)
            {
              //System.out.println("water");
              if(_dy[k] == -1 && _dx[k] == 0)
              {
                N = true;
              }
              if(_dx[k] == 1 && _dy[k] == 0)
              {
                E = true;
              }
              if(_dy[k] == 1 && _dx[k] == 0)
              {
                S = true;
              }
              if(_dx[k] == -1 && _dy[k] == 0)
              {
                W = true;
              }
              if(_dy[k] == -1 && _dx[k] == 1)
              {
                NE = true;
              }
              if(_dx[k] == 1 && _dy[k] == 1)
              {
                SE = true;
              }
              if(_dx[k] == -1 && _dy[k] == 1)
              {
                SW = true;
              }
              if(_dy[k] == -1 && _dx[k] == -1)
              {
                NW = true;
              }

              water.add(k);	
            }


          }

        }
      }

    }



    if(water.size()==0)
    {
      //System.out.println("no water");
      memory.useful=false;
      return;
    }
    else
    {
      if(Util.possibleDeadlock(map)){
        //System.out.println("deadlock");
        memory.useful =false;
        //System.out.println("Deadlock broken");
        return;
      }
      if(Util.possibleSameTrack(map)){
        //System.out.println("same track");
        memory.useful =false;
        //System.out.println("Deadlock broken");
        return;
      }


      if(NearByPlayerAlongCoast(map))
      {
        //System.out.println("close by player");
        //memory.clockwise=false;
        memory.useful =false;//anticlock(map,N,E,S,W,NE,NW,SE,SW);
        return;
      }

      memory.useful=true;

      //if(memory.clockwise)
      //{
      //System.out.println("here");

      if(N&&S){
          if(N&&!E)
          {

            //Node nod2=map.get(new HPoint (map.currentLocation.x,map.currentLocation.y-1));
            if(map.currentLocation.x>map.stepped.get(map.stepped.size()-2).x){
              Node nod=map.get(new HPoint (map.currentLocation.x+1,map.currentLocation.y));
              HPoint p =nod.getLocation();


              if(nod.getTerrain()==LAND)
              {
                memory.nextMove=EAST;

                return ;
              }
            }

          }

          if(S&&!W)
          {

            //Node nod2=map.get(new HPoint (map.currentLocation.x,map.currentLocation.y-1));
            if(map.currentLocation.x<map.stepped.get(map.stepped.size()-2).x){
              Node nod=map.get(new HPoint (map.currentLocation.x-1,map.currentLocation.y));
              HPoint p =nod.getLocation();


              if(nod.getTerrain()==LAND)
              {
                memory.nextMove=WEST;

                return ;
              }

            }
          }



        }

        if(E&&W){
          if(W&&!N)
          {

            //Node nod2=map.get(new HPoint (map.currentLocation.x,map.currentLocation.y-1));
            Node nod=map.get(new HPoint (map.currentLocation.x,map.currentLocation.y-1));
            HPoint p =nod.getLocation();

            if(map.currentLocation.y<map.stepped.get(map.stepped.size()-2).y){
              if(nod.getTerrain()==LAND)
              {
                memory.nextMove=NORTH;

                return ;
              }

            }
          }

          if(E&&!S)
          {

            //Node nod2=map.get(new HPoint (map.currentLocation.x,map.currentLocation.y-1));
            Node nod=map.get(new HPoint (map.currentLocation.x,map.currentLocation.y+1));
            HPoint p =nod.getLocation();

            if(map.currentLocation.y>map.stepped.get(map.stepped.size()-2).y){
              if(nod.getTerrain()==LAND)
              {
                memory.nextMove=SOUTH;

                return ;
              }
            }			

          }



        }
      
      
        if(!N&&!NE&&E&&!SE&&!S)
        {
          Node nod=map.get( new HPoint (map.currentLocation.x+1,map.currentLocation.y+1));
          HPoint p =nod.getLocation();
          System.out.println("E only");
          if(nod.getTerrain()==LAND && !(map.stepped.contains(p)))
          {System.out.println("SE only");
            memory.nextMove=SOUTHEAST;
            return ;
          }
          else{
        	  System.out.println("NE only");
        	  Node nod2=map.get( new HPoint (map.currentLocation.x+1,map.currentLocation.y-1));
        	  HPoint p2 =nod2.getLocation();
        	  if(nod2.getTerrain()==LAND && !(map.stepped.contains(p2)))
              {
                memory.nextMove=NORTHEAST;
                return ;
              }
          }

        }
        
      if(N&&NE&&!E)
      {
        Node nod=map.get( new HPoint (map.currentLocation.x+1,map.currentLocation.y));
        if(nod.getTerrain()==LAND)
        {
          memory.nextMove=EAST;
          return ;
        }

      }
      if(S&&SW&&W&&!NW)
      {
        Node nod=map.get( new HPoint (map.currentLocation.x-1,map.currentLocation.y-1));
        if(nod.getTerrain()==LAND)
        {
          memory.nextMove=NORTHWEST;
          return ;
        }

      }
      if(S&&SW&&W&&!NW)
      {
        Node nod=map.get( new HPoint (map.currentLocation.x-1,map.currentLocation.y-1));
        if(nod.getTerrain()==LAND)
        {
          memory.nextMove=NORTHWEST;
          return ;
        }

      }
      if(W&&SW&&!NW)
      {
        Node nod=map.get( new HPoint (map.currentLocation.x-1,map.currentLocation.y-1));
        if(nod.getTerrain()==LAND)
        {
          memory.nextMove=NORTHWEST;
          return ;
        }

      }
      if(S&&SW&&W&&NW&&!N)
      {
        //System.out.println("N");
        Node nod=map.get( new HPoint (map.currentLocation.x,map.currentLocation.y-1));
        if(nod.getTerrain()==LAND)
        {
          memory.nextMove=NORTH;
          return ;
        }

      }


      if(S)
      {
        if(!SW)
        {	
          //System.out.println("ss");
          Node nod=map.get( new HPoint (map.currentLocation.x-1,map.currentLocation.y+1));
          HPoint p =nod.getLocation();
          if(!map.stepped.contains(p))
          {
            if(nod.getTerrain()==LAND)
            {
              memory.nextMove=SOUTHWEST;
              return ;
            }
          }
        }
        if(!W)
        {	Node nod=map.get( new HPoint (map.currentLocation.x-1,map.currentLocation.y));
        if(nod.getTerrain()==LAND)
        {
          memory.nextMove=WEST;
          return ;
        }
        }
        if(!NW)
        {	Node nod=map.get( new HPoint (map.currentLocation.x-1,map.currentLocation.y-1));
        if(nod.getTerrain()==LAND)
        {
          memory.nextMove=NORTHWEST;
          return ;
        }
        }
        if(!N)
        {	Node nod=map.get( new HPoint (map.currentLocation.x,map.currentLocation.y-1));
        if(nod.getTerrain()==LAND)
        {
          memory.nextMove=NORTH;
          return ;
        }
        }

      }

      if(N)
      {
        if(!NE)
        {	Node nod=map.get( new HPoint (map.currentLocation.x+1,map.currentLocation.y-1));
        if(nod.getTerrain()==LAND)
        {
          memory.nextMove=NORTHEAST;
          return ;
        }
        }
        if(!E)
        {	Node nod=map.get( new HPoint (map.currentLocation.x+1,map.currentLocation.y));
        if(nod.getTerrain()==LAND)
        {
          memory.nextMove=EAST;
          return ;
        }
        }
        if(!SE)
        {	Node nod=map.get( new HPoint (map.currentLocation.x+1,map.currentLocation.y+1));
        if(nod.getTerrain()==LAND)
        {
          memory.nextMove=SOUTHEAST;
          return ;
        }
        }
        if(!S)
        {	Node nod=map.get( new HPoint (map.currentLocation.x,map.currentLocation.y+1));
        if(nod.getTerrain()==LAND)
        {
          memory.nextMove=SOUTH;
          return ;
        }
        }

      }

      if(N&&S&&NE&&!W&&!E)
      {
        Node nod=map.get( new HPoint (map.currentLocation.x-1,map.currentLocation.y));
        HPoint p =nod.getLocation();
        if(!(map.stepped.contains(p)))
        {
          if(nod.getTerrain()==LAND)
          {
            memory.nextMove=WEST;
            return ;
          }
        }

        Node nod2=map.get( new HPoint (map.currentLocation.x+1,map.currentLocation.y));
        HPoint p2 =nod2.getLocation();
        if(!(map.stepped.contains(p2)))
        {

          if(nod2.getTerrain()==LAND)
          {
            memory.nextMove=EAST;
            return ;
          }
        }

      }

      if(W&&!N&&!NE&&!S&&!E&&!NW&&!SE&&!SW)
      {
        Node nod=map.get( new HPoint (map.currentLocation.x-1,map.currentLocation.y-1));
        if(nod.getTerrain()==LAND)
        {
          memory.nextMove=NORTHWEST;
          return ;
        }

      }
      if(!W&&!N&&!NE&&!S&&!E&&!NW&&SE&&!SW)
      {
        Node nod=map.get( new HPoint (map.currentLocation.x,map.currentLocation.y-1));
        if(nod.getTerrain()==LAND)
        {
          memory.nextMove=SOUTH;
          return ;
        }

      }

      if(SW&&S)
      {
        if(!W)
        {	Node nod=map.get( new HPoint (map.currentLocation.x-1,map.currentLocation.y));
        if(nod.getTerrain()==LAND)
        {
          memory.nextMove=WEST;
          return ;
        }
        }
        if(!NW){
          Node nod=map.get( new HPoint (map.currentLocation.x-1,map.currentLocation.y-1));
          if(nod.getTerrain()==LAND)
          {
            memory.nextMove=NORTHWEST;
            return ;
          }
        }


      }





      if(NE&&N&&!E)
      {
        Node nod=map.get( new HPoint (map.currentLocation.x+1,map.currentLocation.y));
        if(nod.getTerrain()==LAND)
        {
          memory.nextMove=EAST;
          return ;
        }

      }
      if(NE&&N&&E&&!SE)
      {
        Node nod=map.get( new HPoint (map.currentLocation.x+1,map.currentLocation.y+1));
        if(nod.getTerrain()==LAND)
        {
          memory.nextMove=SOUTHEAST;
          return ;
        }

      }
      if(NE&&E&&SE&&!S&&!SW&&!W&&!NW)
      {
        Node nod=map.get( new HPoint (map.currentLocation.x,map.currentLocation.y+1));
        if(nod.getTerrain()==LAND)
        {
          memory.nextMove=SOUTH;
          return ;
        }

      }




      if(N&&S&&(NW&&SW))
      {
        Node nod=map.get( new HPoint (map.currentLocation.x-1,map.currentLocation.y));
        if(nod.getTerrain()==LAND)
        {
          memory.nextMove=WEST;
          return ;
        }
        Node nod2=map.get( new HPoint (map.currentLocation.x+1,map.currentLocation.y));
        if(nod2.getTerrain()==LAND)
        {
          memory.nextMove=EAST;
          return ;
        }

      }
      if(N&&S&&(NE&&SE))
      {
        Node nod=map.get( new HPoint (map.currentLocation.x+1,map.currentLocation.y));
        if(nod.getTerrain()==LAND)
        {
          memory.nextMove=EAST;
          return ;
        }
        Node nod2=map.get( new HPoint (map.currentLocation.x-1,map.currentLocation.y));
        if(nod2.getTerrain()==LAND)
        {
          memory.nextMove=WEST;
          return ;
        }

      }


      if(N&&NE&&S&&SE&&!E&&!W)
      {
        Node nod=map.get( new HPoint (map.currentLocation.x-1,map.currentLocation.y));
        if(nod.getTerrain()==LAND)
        {
          memory.nextMove=WEST;
          return ;
        }
        Node nod2=map.get( new HPoint (map.currentLocation.x+1,map.currentLocation.y));
        if(nod2.getTerrain()==LAND)
        {
          memory.nextMove=EAST;
          return ;
        }
        //return new Move(SOUTEAST);
      }



      if(!N&&NE&&E)//&&W&&NW)//
      {
        Node nod=map.get( new HPoint (map.currentLocation.x,map.currentLocation.y-1));
        HPoint p =nod.getLocation();
        if(!(map.stepped.contains(p)))
        {
          if(nod.getTerrain()==LAND)
          {
            memory.nextMove=NORTH;
            return ;
          }
        }

        Node nod2=map.get( new HPoint (map.currentLocation.x+1,map.currentLocation.y+1));
        HPoint p2 =nod2.getLocation();
        if(!(map.stepped.contains(p2)))
        {

          if(nod2.getTerrain()==LAND)
          {
            memory.nextMove=SOUTHEAST;
            return ;
          }
        }
        else
        {
          Node nod3=map.get( new HPoint (map.currentLocation.x-1,map.currentLocation.y-1));
          if(nod3.getTerrain()==LAND)
          {
            memory.nextMove=NORTHWEST;
            return ;
          }
        }
        //return new Move(SOUTEAST);
      }
      if(!N&&!NW&&SE&&E&&W&&SW&&NE)
      {
        Node nod=map.get( new HPoint (map.currentLocation.x-1,map.currentLocation.y-1));
        if(nod.getTerrain()==LAND)
        {
          memory.nextMove=NORTHWEST;
          return ;
        }
        //return new Move(SOUTEAST);
      }




      if(W&&NW&&!N)
      {

        Node nod=map.get(new HPoint (map.currentLocation.x+0,map.currentLocation.y-1));
        if(nod.getTerrain()==LAND)
        {
          memory.nextMove=NORTH;

          return ;
        }

      }
      if(W&&NW&&N&&!NE)
      {
        Node nod=map.get(new HPoint (map.currentLocation.x+1,map.currentLocation.y-1));
        if(nod.getTerrain()==LAND)
        {
          memory.nextMove=NORTHEAST;
          return ;
        }
      }
      if(W&&NW&&N&&NE&&!E)
      {
        Node nod=map.get(new HPoint (map.currentLocation.x+1,map.currentLocation.y+0));
        if(nod.getTerrain()==LAND)
        {
          memory.nextMove=EAST;
          return ;
        }
        //return new Move(EAST);

      }
      if(N&&NE&&E&&SE&&!S)
      {
        Node nod=map.get(new HPoint (map.currentLocation.x+0,map.currentLocation.y+1));
        HPoint p =nod.getLocation();
        if(!map.stepped.contains(p))
        {
          if(nod.getTerrain()==LAND)
          {
            memory.nextMove=SOUTH;
            return ;
          }
        }
        Node nod2=map.get(new HPoint (map.currentLocation.x-1,map.currentLocation.y-1));
        if(nod2.getTerrain()==LAND)
        {
          memory.nextMove=NORTHWEST;
          return ;
        }

        //	return new Move(SOUTH);
      }
      if(N&&NE&&NW&&!E)
      {
        Node nod=map.get(new HPoint (map.currentLocation.x+1,map.currentLocation.y+0));
        if(nod.getTerrain()==LAND)
        {
          memory.nextMove=EAST;
          return ;
        }
        //return new Move(EAST);
      }
      if(N&&NE&&NW&&!W)
      {
        Node nod=map.get(new HPoint (map.currentLocation.x+1,map.currentLocation.y+0));
        if(nod.getTerrain()==LAND)
        {
          memory.nextMove=EAST;
          return ;
        }
        //	return new Move(EAST);
      }

      if(NE&&E&&SE&&!S)
      {
        Node nod=map.get(new HPoint (map.currentLocation.x+0,map.currentLocation.y+1));
        if(nod.getTerrain()==LAND)
        {
          memory.nextMove=SOUTH;
          return ;
        }
        //	return new Move(SOUTH);
      }
      if(E&&SE&&!S&&!NE)
      {
        Node nod=map.get(new HPoint (map.currentLocation.x+0,map.currentLocation.y+1));
        if(nod.getTerrain()==LAND)
        {
          memory.nextMove=SOUTH;
          return ;
        }
        //	return new Move(SOUTH);
      }
      if(E&&SE&&S&&!SW)
      {
        Node nod=map.get(new HPoint (map.currentLocation.x-1,map.currentLocation.y+1));
        if(nod.getTerrain()==LAND)
        {
          memory.nextMove=SOUTHWEST;
          return ;
        }
        //	return new Move(SOUTHWEST);
      }
      if(!W&&SE&&S&&SW)
      {
        Node nod=map.get(new HPoint (map.currentLocation.x-1,map.currentLocation.y+0));
        if(nod.getTerrain()==LAND)
        {
          memory.nextMove=WEST;
          return ;
        }
        //	return new Move(WEST);
      }

      if(W&&!NW&&S&&SW)
      {
        Node nod=map.get(new HPoint (map.currentLocation.x-1,map.currentLocation.y-1));
        if(nod.getTerrain()==LAND)
        {
          memory.nextMove=NORTHWEST;
          return ;
        }
        //return new Move(NORTHWEST);
      }
      if(!NW&&S&&SW&&!SE&&!W)
      {
        Node nod=map.get(new HPoint (map.currentLocation.x-1,map.currentLocation.y+0));
        if(nod.getTerrain()==LAND)
        {
          memory.nextMove=WEST;
          return ;
        }
        //return new Move(WEST);
      }
      if(!NW&&S&&SW&&!SE&&!W)
      {
        Node nod=map.get(new HPoint (map.currentLocation.x-1,map.currentLocation.y+0));
        if(nod.getTerrain()==LAND)
        {
          memory.nextMove=WEST;
          return ;
        }
        //		return new Move(WEST);
      }

      if(W&&!NW&&SW)
      {
        Node nod=map.get(new HPoint (map.currentLocation.x-1,map.currentLocation.y-1));
        if(nod.getTerrain()==LAND)
        {
          memory.nextMove=NORTHWEST;
          return ;
        }
        //	return new Move(NORTHWEST);
      }
      if(SE&&S&&!SW)
      {
        Node nod=map.get(new HPoint (map.currentLocation.x-1,map.currentLocation.y+1));
        if(nod.getTerrain()==LAND)
        {
          memory.nextMove=SOUTHWEST;
          return ;
        }
        //return new Move(SOUTHWEST);
      }
      if(E&&!S&&!SW&&!W&&N&&NE&&!SE)
      {
        Node nod=map.get(new HPoint (map.currentLocation.x+1,map.currentLocation.y+1));
        if(nod.getTerrain()==LAND)
        {
          memory.nextMove=SOUTHWEST;
          return ;
        }
        //return new Move(SOUTHEAST);
      }
      if(E&&!S&&!SW&&!W&&!N&&NE&&!SE)
      {
        Node nod=map.get(new HPoint (map.currentLocation.x+1,map.currentLocation.y+1));
        if(nod.getTerrain()==LAND)
        {
          memory.nextMove=SOUTHEAST;
          return ;
        }
        //	return new Move(SOUTHEAST);
      }
      if(E&&!S&&!SW&&!W&&!N&&!NE&&!SE)
      {
        Node nod=map.get(new HPoint (map.currentLocation.x+1,map.currentLocation.y+1));
        if(nod.getTerrain()==LAND)
        {
          memory.nextMove=SOUTHEAST;
          return ;
        }
        //	return new Move(SOUTHEAST);
      }
      if(N&&NE&&!NW&&!E)
      {
        Node nod=map.get(new HPoint (map.currentLocation.x+1,map.currentLocation.y));
        if(nod.getTerrain()==LAND)
        {
          memory.nextMove=EAST;
          return ;
        }
        //	return new Move(EAST);
      }
      if(!NW&&!S&&SW&&!SE&&!W&&!N&&!E)
      {
        Node nod=map.get(new HPoint (map.currentLocation.x-1,map.currentLocation.y+0));
        if(nod.getTerrain()==LAND)
        {
          memory.nextMove=WEST;
          return ;
        }
        //return new Move(WEST);
      }
      if(!SE&&S&&!SW&&!N&&!W&&!E&&!NE&&!NW)
      {
        Node nod=map.get( new HPoint (map.currentLocation.x-1,map.currentLocation.y+1));
        if(nod.getTerrain()==LAND)
        {
          memory.nextMove=SOUTHWEST;
          return ;
        }
        //return new Move(SOUTHWEST);
      }

      if(N&&NW&&!NE)
      {
        Node nod=map.get( new HPoint (map.currentLocation.x+1,map.currentLocation.y-1));
        if(nod.getTerrain()==LAND)
        {
          memory.nextMove=NORTHEAST;
          return ;
        }
        //return new Move(SOUTHWEST);
      }
      if(W&&!NW&&!NE&&!N&&!S&&!SW&&SE&&!E)
      {
        Node nod=map.get( new HPoint (map.currentLocation.x-1,map.currentLocation.y-1));
        if(nod.getTerrain()==LAND)
        {
          memory.nextMove=NORTHWEST;
          return ;
        }
        //return new Move(SOUTHWEST);
      }

      if(NW&&NE&&N&&W&&!SE&&E)
      {
        Node nod=map.get( new HPoint (map.currentLocation.x+1,map.currentLocation.y+1));
        if(nod.getTerrain()==LAND)
        {
          memory.nextMove=SOUTHEAST;
          return ;
        }
        //return new Move(SOUTHWEST);
      }
      if(N&&NE&&E&&!SE)
      {
        Node nod=map.get( new HPoint (map.currentLocation.x+1,map.currentLocation.y+1));
        if(nod.getTerrain()==LAND)
        {
          memory.nextMove=SOUTHEAST;
          return ;
        }
        //return new Move(SOUTHWEST);
      }

      if(SW&&NE&&E&&!SE)
      {
        Node nod=map.get( new HPoint (map.currentLocation.x+1,map.currentLocation.y+1));
        if(nod.getTerrain()==LAND)
        {
          memory.nextMove=SOUTHEAST;
          return ;
        }
        //return new Move(SOUTEAST);
      }
      //}
      //System.out.println("nothin found");
      memory.useful=	false;//anticlock(map,N,E,S,W,NE,NW,SE,SW);
      //memory.clockwise=false;
      return;
    }

  }









  public boolean NearByPlayerAlongCoast(Map map)
  {

    if(map.currentTime>18){
      for(int j=0;j<map.numPlayers;j++)
      {
        if(j!=map.id)
        {

          if(!Util.along(map, j, WATER).isEmpty())
          {
            Node n=map.get(Util.last(map.lastSeen[j]).point);
            HPoint h=n.getLocation();
            //System.out.println("the point is "+h.x+" "+h.y);
            if((Util.traversingDistance(h, map.currentLocation, map, true))<(Util.traversingDistance(h, map.stepped.get(map.stepped.size()-2), map, true)))
            {
              if((Util.traversingDistance(h, map.currentLocation, map, true))<(6))
                return true;
            }
          }
        }
      }
    }
    return false;
  }


  /*public boolean anticlock(Map map,boolean N,boolean E,boolean S,boolean W,boolean NE,boolean NW,boolean SE,boolean SW)
		{

			if(!memory.clockwise)
			{

				if(Util.possibleDeadlock(map)){
					System.out.println("deadlock");
					memory.useful =false;
					memory.clockwise=true;
					//System.out.println("Deadlock broken");
					return false;
				}
				if(Util.possibleSameTrack(map)){
					System.out.println("same track");
					memory.useful =false;
					memory.clockwise=true;
					//System.out.println("Deadlock broken");
					return false;
				}
				if(NearByPlayerAlongCoast(map)&&map.currentTime>20)
				{
					//System.out.println("close by player");
					memory.clockwise=true;
					memory.useful =false;
				return false;
				}
				if(W)
				{
					if(!SW)
					{
					Node nod=map.get( new HPoint (map.currentLocation.x-1,map.currentLocation.y+1));
					if(nod.getTerrain()==LAND)
					{
						memory.nextMove=SOUTHWEST;
						return true;
					}
					}
					if(!S)
					{
					Node nod=map.get( new HPoint (map.currentLocation.x,map.currentLocation.y+1));
					if(nod.getTerrain()==LAND)
					{
						memory.nextMove=SOUTH;
						return true;
					}
					}
				}
				if(S)
				{
					if(!SE)
					{
					Node nod=map.get( new HPoint (map.currentLocation.x+1,map.currentLocation.y+1));
					if(nod.getTerrain()==LAND)
					{
						memory.nextMove=SOUTHEAST;
						return true;
					}
					}
					if(!E)
					{
					Node nod=map.get( new HPoint (map.currentLocation.x+1,map.currentLocation.y));
					if(nod.getTerrain()==LAND)
					{
						memory.nextMove=EAST;
						return true;
					}
					}
				}
				if(E){
					if(!NE)
					{
					Node nod=map.get( new HPoint (map.currentLocation.x+1,map.currentLocation.y-1));
					if(nod.getTerrain()==LAND)
					{
						memory.nextMove=NORTHEAST;
						return true ;
					}
					}
					if(!E)
					{
					Node nod=map.get( new HPoint (map.currentLocation.x,map.currentLocation.y-1));
					if(nod.getTerrain()==LAND)
					{
						memory.nextMove=NORTH;
						return true;
					}
					}

				}
				if(N){
					if(!NW)
					{
					Node nod=map.get( new HPoint (map.currentLocation.x-1,map.currentLocation.y-1));
					if(nod.getTerrain()==LAND)
					{
						memory.nextMove=NORTHWEST;
						return true;
					}
					}
					if(!W)
					{
					Node nod=map.get( new HPoint (map.currentLocation.x-1,map.currentLocation.y));
					if(nod.getTerrain()==LAND)
					{
						memory.nextMove=WEST;
						return true;
					}
					}

				}

	}
			memory.useful=false;
			memory.clockwise=true;
			return false;	

		}*/

}