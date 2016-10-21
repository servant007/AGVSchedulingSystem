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
}
