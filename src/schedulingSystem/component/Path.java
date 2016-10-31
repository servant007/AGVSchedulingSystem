package schedulingSystem.component;

import java.util.ArrayList;

public class Path {
	private int startNode;
	private int endNode;
	private int realDis;
	private boolean remove;
	private ArrayList<Integer> route;
	public Path(int startNode, int endNode){
		this.startNode = startNode;
		this.endNode = endNode;
		route = new ArrayList<Integer>();
		route.add(this.startNode);
	}
	
	public void addRouteNode(int node){
		route.add(node);
	}
	
	public void newRoute(ArrayList<Integer> route){
		this.route = new ArrayList<Integer>();
		for(int i = 0; i < route.size(); i++)
			this.route.add(route.get(i));
	}
	
	public ArrayList<Integer> getRoute(){
		return route;
	}
	
	public void setRealDis(int realDis){
		this.realDis = realDis;
	}
	
	public int getRealDis(){
		return realDis;
	}
	
	public void setRemove(){
		remove = true;
	}
	
	public boolean getRemove(){
		return remove;
	}
	
	public int getStartNode(){
		return startNode;
	}
	
	public int getEndNode(){
		return endNode;
	}
}
