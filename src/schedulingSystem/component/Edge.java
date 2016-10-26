package schedulingSystem.component;
public class Edge {
	public Node startNode;
	public Node endNode;
	public int realDis;
	public Edge(Node str, Node end){
		startNode = str;
		endNode = end;
	}
	public void setRealDis(int dis){
		realDis = dis;
	}
	
	public Edge(Node str, Node end, int dis){
		startNode = str;
		endNode = end;
		realDis = dis;
	}
}
