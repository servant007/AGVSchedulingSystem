package schedulingSystem.component;

import java.util.ArrayList;

public class Graph {
	private ArrayList<Node> nodeArray;
	private ArrayList<Edge> edgeArray;
	private ArrayList<FunctionNode> shipmentNodeArray;
	private ArrayList<FunctionNode> unloadingNodeArray;
	private ArrayList<FunctionNode> emptyCarNodeArray;
	private ArrayList<FunctionNode> tagArray;
	private Edge lastReturnEdge;
	
	public Graph(){
		nodeArray = new ArrayList<Node>();
		edgeArray = new ArrayList<Edge>();
		shipmentNodeArray = new ArrayList<FunctionNode>();
		unloadingNodeArray = new ArrayList<FunctionNode>();
		emptyCarNodeArray = new ArrayList<FunctionNode>();
		tagArray = new ArrayList<FunctionNode>();
		lastReturnEdge = new Edge(new Node(0,0,0), new Node(0,0,0));
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
			}else if(edge.endCardNum == cardNum && edge.twoWay){
				returnEdge = new Edge(edge.endNode, edge.startNode, edge.realDis, edge.strCardNum, edge.endCardNum, true);
			}
		}
		//避免双向路径时自动返回
		if(lastReturnEdge.startNode.num == returnEdge.startNode.num && lastReturnEdge.endNode.num == returnEdge.endNode.num
				|| lastReturnEdge.startNode.num == returnEdge.endNode.num && lastReturnEdge.endNode.num == returnEdge.startNode.num){
			returnEdge = null;
		}
		
		if(returnEdge != null)
			lastReturnEdge = returnEdge;
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
