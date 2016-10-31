package schedulingSystem.component;
public class Edge {
	public Node startNode;
	public Node endNode;
	public int realDis;
	public int strCardNum;
	public int endCardNum;
	public boolean twoWay;
	public Edge(Node str, Node end){
		startNode = str;
		endNode = end;
	}
	
	public Edge(Node str, Node end, int dis, int strCardNum, int endCardNum, boolean twoWay){
		startNode = str;
		endNode = end;
		realDis = dis;
		this.strCardNum = strCardNum;
		this.endCardNum = endCardNum;
		this.twoWay = twoWay;
	}
	
	public void setRealDis(int dis){
		realDis = dis;
	}
	
	public void setCarNum(int strCardNum, int endCardNum){
		this.strCardNum = strCardNum;
		this.endCardNum = endCardNum;
	}
	
	public boolean getTwoWay(){
		return twoWay;
	}
	
}
