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
		nodeArray.add(new Node(panelSize.width/5, panelSize.height/4 ));
		nodeArray.add(new Node(panelSize.width/5, 3*panelSize.height/4));
		nodeArray.add(new Node(4*panelSize.width/5, 3*panelSize.height/4));
		nodeArray.add(new Node(4*panelSize.width/5, panelSize.height/4));
		
		edgeArray.add(new Edge(nodeArray.get(1) , nodeArray.get(0)));
		edgeArray.add(new Edge(nodeArray.get(0) , nodeArray.get(3)));
		edgeArray.add(new Edge(nodeArray.get(3) , nodeArray.get(2)));
		edgeArray.add(new Edge(nodeArray.get(2) , nodeArray.get(1)));
	}
	
	public void addEdge(Node strNode, Node endNode){
		nodeArray.add(new Node(strNode.x, strNode.y));
		nodeArray.add(new Node(endNode.x, endNode.y));
		edgeArray.add(new Edge(nodeArray.get(nodeArray.size()-2), nodeArray.get(nodeArray.size()-1)));
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
			if(Math.pow(searchNode.x - nodeArray.get(i).x, 2) < 900){
				return nodeArray.get(i);
			}
		}
		return null;
	}
}
