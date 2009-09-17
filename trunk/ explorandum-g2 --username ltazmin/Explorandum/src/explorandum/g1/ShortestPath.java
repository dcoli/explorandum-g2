package explorandum.g1;


import java.awt.Point;
import java.util.*;


public class ShortestPath {

	
	Map map;
	Point sourcePoint;
	ArrayList<Cell> all;
	
	ShortestPath(Map _map){
		map = _map;		
	}
	
	public double getShortestDistance(Point destPoint){
		
		Cell destCell = map.getCell(destPoint);
		if(destCell != null)
			return destCell.minDist;
		return -1;
	}
	
	public ArrayList<Cell> getShortestDistancePath(Point destPoint){
		ArrayList<Cell> path = new ArrayList<Cell>();
		Cell destCell = map.getCell(destPoint);
		Cell sourceCell = map.getCell(sourcePoint);
		Cell tracer = destCell;
		if (destCell == null || destCell.minDist == Double.MAX_VALUE) {
			//System.out.println("ERROR: get shortest distance to an unreachable cell");
			return null;
		}
		for(;tracer != sourceCell; ){
			path.add(tracer);
			tracer = tracer.previous;
		}
		Collections.reverse(path);
		return path;
	}

	
	public void updateShortestPath(Point _sourcePoint) {
		
		sourcePoint = _sourcePoint;
		Cell sourceCell = map.getCell(sourcePoint);

		setDistanceToInfinity();
		setSourceDistanceToZero();
		runDijkstra(sourceCell);
	}

	
	private void setDistanceToInfinity(){//And previous to null
		Set<Point> allPoints = map.getAllDiscoveredCoordinates();
		all = new ArrayList<Cell>();
		for(Iterator<Point> i=allPoints.iterator(); i.hasNext();){
			Point p = i.next();
			Cell cell = map.getCell(p);
			all.add(cell);
			cell.previous = null;
			cell.minDist = Double.MAX_VALUE;
		}
	}
	
	
	private void setSourceDistanceToZero(){
		Cell sourceCell = map.getCell(sourcePoint);
		sourceCell.minDist = 0.0;
	}

	
	
	private void runDijkstra(Cell source){
		
		//Implement Dijkstra's here
		
		while(all.size() != 0){
			Cell cell = getMin(all);

			for(int i=0; i<cell.neighbors.size(); i++){
				Link link = cell.neighbors.get(i);
				Cell dest = cell.neighbors.get(i).destination;
				
				double dist;
				if(cell.minDist != Double.MAX_VALUE)
					dist = cell.minDist + link.cost;
				else
					dist = cell.minDist;
				
				if(dist < dest.minDist){
					dest.minDist = dist;
					dest.previous = cell;
				}
			}
		}
	}
	
	
	
	private Cell getMin(ArrayList<Cell>all){
		double min = all.get(0).minDist;
		int minIndex = 0;
		
		for(int i=1; i<all.size(); i++){
			if(all.get(i).minDist < min){
				minIndex = i;
				min = all.get(i).minDist;
			}
		}
		
		Cell v = all.get(minIndex);
		all.remove(minIndex);
		return v;

 	}
	
	
}
