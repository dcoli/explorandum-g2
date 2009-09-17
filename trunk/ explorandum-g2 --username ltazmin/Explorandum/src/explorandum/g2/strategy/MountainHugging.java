package explorandum.g2.strategy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import explorandum.g2.type.HPoint;

import explorandum.g2.map.Map;
import explorandum.g2.map.Node;
import explorandum.g2.util.Monitor;
import explorandum.g2.util.Util;
import explorandum.GameConstants;

public class MountainHugging extends Strategy implements GameConstants {
  public MountainHugging(Monitor monitor) {
    super(monitor);
  }

  public boolean clockwise=true;
  public int mTrackCount=0;
  public ArrayList<Node> mountList=new ArrayList<Node>();
  
  //	if (sawMostOpenSpace()) {
  //	mountainHug();
  //}
  @Override
  public void updateMemory(Map map) {
    // TODO Auto-generated method stub

    Random rand= new Random();
    boolean N=false;
    boolean E=false;
    boolean S=false;
    boolean W=false;
    boolean NE=false;
    boolean SE=false;
    boolean SW=false;
    boolean NW=false;
    int pdir=0;

    ArrayList<Integer> mountain =new ArrayList<Integer>();
    ArrayList<Node> mountainAdj =new ArrayList<Node>();
    ArrayList<Integer> val =new ArrayList<Integer>();
    HashSet<Node> nodes = map.adjacent(map.currentLocation);
    Iterator<Node> it=nodes.iterator();

    if(memory.used && map.currentLocation.x==0 && map.currentLocation.y==0)
    {
      memory.useful =false;
      return;
    }
    int k =0;

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
            if(nod.getTerrain()==MOUNTAIN)
            {
              //System.out.println("water");
              if(_dy[k] == -1 && _dx[k] == 0)
              {
            	  mountainAdj.add(nod);
                N = true;
                if(!mountList.contains(nod))
                  mountList.add(nod);
               
              }
              if(_dx[k] == 1 && _dy[k] == 0)
              { mountainAdj.add(nod);
                E = true;
                if(!mountList.contains(nod))
                  mountList.add(nod);
              }
              if(_dy[k] == 1 && _dx[k] == 0)
              { mountainAdj.add(nod);
                S = true;
                if(!mountList.contains(nod))
                  mountList.add(nod);
              }
              if(_dx[k] == -1 && _dy[k] == 0)
              { mountainAdj.add(nod);
                W = true;
                if(!mountList.contains(nod))
                  mountList.add(nod);
              }
              if(_dy[k] == -1 && _dx[k] == 1)
              {
            	 // mountainAdj.add(nod);
                NE = true;
              }
              if(_dx[k] == 1 && _dy[k] == 1)
              {// mountainAdj.add(nod);
                SE = true;
              }
              if(_dx[k] == -1 && _dy[k] == 1)
              {// mountainAdj.add(nod);
                SW = true;
              }
              if(_dy[k] == -1 && _dx[k] == -1)
              { //mountainAdj.add(nod);
                NW = true;
              }

              mountain.add(k);	
            }


          }

        }
      }

    }
   
    if(map.totalTime<500 && map.d>=6){
    	memory.useful=false;
    	return;
    	
    }
    	
    
    
    if(mountain.size()==0)
    {
      //System.out.println("The size is "+ water.size());
      memory.useful=false;
      return;

    }
    else
    {
      //		System.out.println("ELSE here");
      if(Util.possibleDeadlockMountain(map)){
        memory.useful =false;
        //System.out.println("Deadlock broken");
        return;
      }
      if(Util.possibleSameTrackMountain(map)){
        memory.useful =false;
        //System.out.println("Deadlock broken");
        return;
      }
      if(NearByPlayerAlongMountain(map))
      {
        //System.out.println("close by player");
        //memory.clockwise=false;
        memory.useful =false;//anticlock(map,N,E,S,W,NE,NW,SE,SW);
        return;
      }
  /*   if(singleLineMountain(map, mountainAdj))
      {
        //System.out.println("close by player");
        //memory.clockwise=false;
        memory.useful =false;//anticlock(map,N,E,S,W,NE,NW,SE,SW);
        return;
      }*/

      memory.useful=true;


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


      if(N&&E&&!NE){
    	  
    	  Node nod=map.get(new HPoint (map.currentLocation.x+1,map.currentLocation.y-1));
          HPoint p =nod.getLocation();

          
            if(nod.getTerrain()==LAND&& !(map.stepped.contains(p)))
            {
              memory.nextMove=NORTHEAST;

              return ;
            }
            Node nod2=map.get(new HPoint (map.currentLocation.x+1,map.currentLocation.y+1));
            HPoint p2 =nod2.getLocation();
            if(nod2.getTerrain()==LAND&&!SE)
            {
              memory.nextMove=SOUTHEAST;

              return ;
            }
            Node nod3=map.get(new HPoint (map.currentLocation.x,map.currentLocation.y+1));
            HPoint p3 =nod3.getLocation();
            if(nod3.getTerrain()==LAND&&!S)
            {
              memory.nextMove=SOUTH;

              return ;
            }
            memory.useful=false;
            return;
          
    	  
      }
      if(E&&S&&!SE){
    	  Node nod=map.get(new HPoint (map.currentLocation.x+1,map.currentLocation.y+1));
          HPoint p =nod.getLocation();

          
            if(nod.getTerrain()==LAND&& !(map.stepped.contains(p)))
            {
              memory.nextMove=SOUTHEAST;

              return ;
            }
            
            Node nod3=map.get(new HPoint (map.currentLocation.x-1,map.currentLocation.y+1));
            HPoint p3 =nod3.getLocation();
            if(nod3.getTerrain()==LAND&&!SW)
            {
              memory.nextMove=SOUTHWEST;

              return ;
            }
            Node nod2=map.get(new HPoint (map.currentLocation.x-1,map.currentLocation.y));
            HPoint p2 =nod2.getLocation();
            if(nod2.getTerrain()==LAND&&!W)
            {
              memory.nextMove=WEST;

              return ;
            }
            memory.useful=false;
            return;
            
    	  
      }
      if(S&&W&&!SW){
    	  Node nod=map.get(new HPoint (map.currentLocation.x-1,map.currentLocation.y+1));
          HPoint p =nod.getLocation();

          
            if(nod.getTerrain()==LAND&& !(map.stepped.contains(p)))
            {
              memory.nextMove=SOUTHWEST;

              return ;
            }
            Node nod3=map.get(new HPoint (map.currentLocation.x-1,map.currentLocation.y-1));
            HPoint p3 =nod3.getLocation();
            if( nod3.getTerrain()==LAND&&!NW)
            {
              memory.nextMove=NORTHWEST;

              return ;
            }
            Node nod2=map.get(new HPoint (map.currentLocation.x,map.currentLocation.y-1));
            HPoint p2 =nod2.getLocation();
            if( nod2.getTerrain()==LAND&&!N)
            {
              memory.nextMove=NORTH;

              return ;
            }
            memory.useful=false;
            return;
    	  
      }
      if(W&&N&&!NW){
    	  Node nod=map.get(new HPoint (map.currentLocation.x-1,map.currentLocation.y-1));
          HPoint p =nod.getLocation();

          
            if(nod.getTerrain()==LAND&& !(map.stepped.contains(p)))
            {
              memory.nextMove=NORTHWEST;

              return ;
            }
            
            Node nod3=map.get(new HPoint (map.currentLocation.x+1,map.currentLocation.y-1));
            HPoint p3 =nod3.getLocation();
            if( nod3.getTerrain()==LAND&&!NE)
            {
              memory.nextMove=NORTHEAST;

              return ;
            }
            Node nod2=map.get(new HPoint (map.currentLocation.x+1,map.currentLocation.y));
            HPoint p2 =nod2.getLocation();
            if( nod2.getTerrain()==LAND&&!E)
            {
              memory.nextMove=EAST;

              return ;
            }
            memory.useful=false;
            return;
            
           
    	  
      }
      /*if(N&&S&&!E&&!W)
			{

				//Node nod2=map.get(new HPoint (map.currentLocation.x,map.currentLocation.y-1));
				Node nod=map.get(new HPoint (map.currentLocation.x+1,map.currentLocation.y));
				HPoint p =nod.getLocation();
				Node nod2=map.get(new HPoint (map.currentLocation.x-1,map.currentLocation.y));
				HPoint p2 =nod2.getLocation();

				if(!map.steppedD.contains(p2)){	
					if(nod2.getTerrain()==LAND)
					{
					memory.nextMove=WEST;

				return ;
					}
					}
				if(!map.steppedD.contains(p)){	
				if(nod.getTerrain()==LAND)
					{
					memory.nextMove=EAST;

				return ;
					}
			}




			}

			if(!N&&!S&&E&&W)
			{

				//Node nod2=map.get(new HPoint (map.currentLocation.x,map.currentLocation.y-1));
				Node nod=map.get(new HPoint (map.currentLocation.x,map.currentLocation.y-1));
				HPoint p =nod.getLocation();
				Node nod2=map.get(new HPoint (map.currentLocation.x,map.currentLocation.y+1));
				HPoint p2 =nod2.getLocation();

				if(!map.steppedD.contains(p2)){	
					if(nod2.getTerrain()==LAND)
					{
					memory.nextMove=SOUTH;

				return ;
					}
					}
				if(!map.steppedD.contains(p)){	
				if(nod.getTerrain()==LAND)
					{
					memory.nextMove=NORTH;

				return ;
					}
			}
			}
if(Util.possibleSameTrackMountain(map)){
        memory.useful =false;
        //System.out.println("Deadlock broken");
        return;
      }
			map.steppedD.clear();*/
      
      if(Util.possibleSameTrackMountain2(map)){
          memory.useful =false;
          //System.out.println("Deadlock broken");
          return;
        }
      if(N&&!NW&&!NE)
      {

        //Node nod2=map.get(new HPoint (map.currentLocation.x,map.currentLocation.y-1));
        Node nod=map.get(new HPoint (map.currentLocation.x+1,map.currentLocation.y-1));


        if(nod.getTerrain()==LAND)
        {
          memory.nextMove=NORTHEAST;

          return ;
        }

      }
      if(E&&!NE&&!SE)
      {

        Node nod=map.get(new HPoint (map.currentLocation.x+1,map.currentLocation.y+1));

        if(nod.getTerrain()==LAND)
        {
          memory.nextMove=SOUTHEAST;

          return ;
        }

      }
      if(W&&!NW&!SW&&!N)
      {

        Node nod2=map.get(new HPoint (map.currentLocation.x,map.currentLocation.y-1));

        if(nod2.getTerrain()==LAND)
        {
          memory.nextMove=NORTH;

          return ;
        }

        Node nod=map.get(new HPoint (map.currentLocation.x-1,map.currentLocation.y-1));

        if(nod.getTerrain()==LAND)
        {
          memory.nextMove=NORTHWEST;

          return ;
        }

      }
      if(!SE&&!SW&S)
      {

        Node nod=map.get(new HPoint (map.currentLocation.x-1,map.currentLocation.y+1));

        if(nod.getTerrain()==LAND)
        {
          memory.nextMove=SOUTHWEST;

          return ;
        }

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

        //if(nod.getTerrain()==LAND)
        //{
        //if(((map.currentTime)-(nod.getFirstseen()))>10)
        //memory.nextMove=val.get(rand.nextInt(val.size()));
        //		return ;
        //	}
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
        if(nod.getTerrain()==LAND)
        {
          memory.nextMove=SOUTH;
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
      if(E&&!S&&!SW&&!W&&N&&NE&&SE&&!NW)
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
      //System.out.println("here");
      //Move m=RandomMoveOrth.getMove(map.currentLocation,offsets, hasExplorer, otherExplorers,terrain,time,n,p,d,myID,stepped,rand,map);
      //	return m;
      memory.useful=false;

      return;
    }


    //	memory.nextMove=1;



  }

  public boolean NearByPlayerAlongMountain(Map map)
  {

    if(map.currentTime>12){
      for(int j=0;j<map.numPlayers;j++)
      {
        if(j!=map.id)
        {

          if(Util.alongMountainPlayer(map, j))
          {
            Node n=Util.alongMountainPlayerPoint(map, j);
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

  
  public boolean singleLineMountain(Map map, ArrayList<Node> mountainAdj){
	  if(memory.used)
	  {
		  for(Node n: mountainAdj){
			  if(mountList.contains(n)){
				  mTrackCount++;
				  
			  }
		  }
		  if(mTrackCount>4){
			  mTrackCount=0;
			  return true;
		  }
		  
	  }
	  return false;
  }
}




