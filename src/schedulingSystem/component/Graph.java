package schedulingSystem.component;
import java.awt.Dimension;
import java.util.ArrayList;

public class Graph {
	private ArrayList<Node> nodeArray;
	private ArrayList<Edge> edgeArray;
	private ArrayList<FunctionNode> shipmentNodeArray;
	private ArrayList<FunctionNode> unloadingNodeArray;
	private ArrayList<FunctionNode> emptyCarNodeArray;
	private ArrayList<FunctionNode> tagArray;
	public Graph(){
		nodeArray = new ArrayList<Node>();
		edgeArray = new ArrayList<Edge>();
		shipmentNodeArray = new ArrayList<FunctionNode>();
		unloadingNodeArray = new ArrayList<FunctionNode>();
		emptyCarNodeArray = new ArrayList<FunctionNode>();
		tagArray = new ArrayList<FunctionNode>();
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
	
	public void addEdge(int strNodeNum, int endNodeNum, int dis, int strCardNum, int endCardNum, boolean twoWay){
		for(int i = 0; i < nodeArray.size(); i++){
			if(nodeArray.get(i).num == strNodeNum)
				strNodeNum = i;
		}
		for(int i = 0; i < nodeArray.size(); i++){
			if(nodeArray.get(i).num == endNodeNum)
				endNodeNum = i;
		}
		
		edgeArray.add(new Edge(nodeArray.get(strNodeNum), nodeArray.get(endNodeNum)
				, dis, strCardNum, endCardNum, twoWay));
	} 
	
	public void addNode(Node node){
		nodeArray.add(new Node(node.x, node.y, node.num));
	}
	public void addImportNode(int x , int y, int num){
		nodeArray.add(new Node(x, y, num));
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
	
	public Node searchNode( int x, int y){
		for(int i = 0; i < nodeArray.size(); i++){
			if(Math.pow(x - nodeArray.get(i).x, 2) + 
					Math.pow(y - nodeArray.get(i).y, 2) < 300){
				return nodeArray.get(i);
			}
		}
		return null;
	}
	
	public Node searchWideNode( int x, int y){
		for(int i = 0; i < nodeArray.size(); i++){
			if(Math.pow(x - nodeArray.get(i).x, 2) + 
					Math.pow(y - nodeArray.get(i).y, 2) < 2000){
				return nodeArray.get(i);
			}
		}
		return null;
	}
	
	public int searchHorizontal(int horizontal){
		for(int i = 0; i < nodeArray.size(); i++){
			if(Math.abs(horizontal - nodeArray.get(i).x) < 30)
				return nodeArray.get(i).x;
		}
		return 0;
	}
	
	public int searchVertical(int vertical){
		for(int i = 0; i < nodeArray.size(); i++){
			if(Math.abs(vertical - nodeArray.get(i).y) < 30)
				return nodeArray.get(i).y;
		}
		return 0;
	}
	
	public ArrayList<Node> getNodeArray(){
		return nodeArray;
	}
	
	public Edge searchCard(int cardNum){
		Edge returnEdge = null;
		for(Edge edge : edgeArray){
			if(edge.strCardNum == cardNum){
				returnEdge = edge;
			}else if(edge.endCardNum == cardNum){
				returnEdge = new Edge(edge.endNode, edge.startNode);
			}
		}
		
		return returnEdge;
	}
	
	public void addShipmentNode(int nodeNum, int comNum){
		shipmentNodeArray.add(new FunctionNode(nodeNum, comNum));
	}
	
	public void addUnloadingNode(int nodeNum, int comNum){
		unloadingNodeArray.add(new FunctionNode(nodeNum, comNum));
	}
	
	public void addEmptyCarNode(int nodeNum, int comNum){
		emptyCarNodeArray.add(new FunctionNode(nodeNum, comNum));
	}
	
	public void addTagArray(int x , int y, String tag){
		tagArray.add(new FunctionNode(new Node(x, y,0), tag));
	}
	public ArrayList<FunctionNode> getShipmentNode(){
		return shipmentNodeArray;
	}
	
	public ArrayList<FunctionNode> getUnloadingNode(){
		return unloadingNodeArray;
	}
	
	public ArrayList<FunctionNode> getEmptyCarNode(){
		return emptyCarNodeArray;
	}
	
	public ArrayList<FunctionNode> getTagArray(){
		return tagArray;
	}
}
