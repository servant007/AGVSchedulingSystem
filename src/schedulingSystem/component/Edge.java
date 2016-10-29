package schedulingSystem.component;
public class Edge {
	public Node startNode;
	public Node endNode;
	public int realDis;
	public int strCardNum;
	public int endCardNum;
	public Edge(Node str, Node end){
		startNode = str;
		endNode = end;
	}
	
	public Edge(Node str, Node end, int dis, int strCardNum, int endCardNum){
		startNode = str;
		endNode = end;
		realDis = dis;
		this.strCardNum = strCardNum;
		this.endCardNum = endCardNum;
	}
	
	public void setRealDis(int dis){
		realDis = dis;
	}
	
	public void setCarNum(int strCardNum, int endCardNum){
		this.strCardNum = strCardNum;
		this.endCardNum = endCardNum;
	}
	
	
}
