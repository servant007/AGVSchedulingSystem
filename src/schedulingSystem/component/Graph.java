package schedulingSystem.component;
import java.awt.Dimension;
import java.util.ArrayList;

public class Graph {
	private ArrayList<Node> nodeArray;
	private ArrayList<Edge> edgeArray;
	public Graph(){
		nodeArray = new ArrayList<Node>();
		edgeArray = new ArrayList<Edge>();
	}
	public void createGraph(Dimension panelSize){
		nodeArray.add(new Node(panelSize.width/5, panelSize.height/4 ,1));
		nodeArray.add(new Node(panelSize.width/5, 3*panelSize.height/4, 2));
		nodeArray.add(new Node(4*panelSize.width/5, 3*panelSize.height/4, 3));
		nodeArray.add(new Node(4*panelSize.width/5, panelSize.height/4, 4));
		
		edgeArray.add(new Edge(nodeArray.get(1) , nodeArray.get(0)));
		edgeArray.add(new Edge(nodeArray.get(0) , nodeArray.get(3)));
		edgeArray.add(new Edge(nodeArray.get(3) , nodeArray.get(2)));
		edgeArray.add(new Edge(nodeArray.get(2) , nodeArray.get(1)));
	}
	
	public void addEdge(int strNodeNum, int endNodeNum){
		for(int i = 0; i < nodeArray.size(); i++){
			if(nodeArray.get(i).num == strNodeNum)
				strNodeNum = i;
		}
		for(int i = 0; i < nodeArray.size(); i++){
			if(nodeArray.get(i).num == endNodeNum)
				endNodeNum = i;
		}
		
		edgeArray.add(new Edge(nodeArray.get(strNodeNum), nodeArray.get(endNodeNum)));
	} 
	public void addNode(Node node){
		nodeArray.add(new Node(node.x, node.y, node.num));
	}
	public Edge getEdge(int num){
		return edgeArray.get(num);
	}
	
	public int getEdgeSize(){
		return edgeArray.size();
	}
	
	public Node getNode(int num){
		return nodeArray.get(num);
	}
	
	public int getNodeSize(){
		return nodeArray.size();		
	}
	
	public Node searchNode(Node searchNode){
		for(int i = 0; i < nodeArray.size(); i++){
			if(Math.pow(searchNode.x - nodeArray.get(i).x, 2) + 
					Math.pow(searchNode.y - nodeArray.get(i).y, 2) < 7000){
				return nodeArray.get(i);
			}
		}
		return null;
	}
	
	public int searchHorizontal(int horizontal){
		for(int i = 0; i < nodeArray.size(); i++){
			if(Math.abs(horizontal - nodeArray.get(i).x) < 84)
				return nodeArray.get(i).x;
		}
		return 0;
	}
	
	public int searchVertical(int vertical){
		for(int i = 0; i < nodeArray.size(); i++){
			if(Math.abs(vertical - nodeArray.get(i).y) < 84)
				return nodeArray.get(i).y;
		}
		return 0;
	}
}
