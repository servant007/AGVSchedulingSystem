package schedulingSystem.component;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import schedulingSystem.component.FunctionNode.FunctionNodeEnum;

public class Graph {
	private ArrayList<Node> nodeArray;
	private ArrayList<Edge> edgeArray;
	private ArrayList<String> AGVSeting;
	private ArrayList<FunctionNode> functionNodeArray;
	private ArrayList<Integer> ignoreCard;
	private int cardQuantity;
	private int stopCard;
	private int executeCard;
	private int startPointCard = 58;
	private ScheduledExecutorService timerPool;
	
	public Graph(){
		nodeArray = new ArrayList<Node>();
		edgeArray = new ArrayList<Edge>();
		functionNodeArray = new ArrayList<FunctionNode>();
		AGVSeting = new ArrayList<String>();
		timerPool = Executors.newScheduledThreadPool(4);
	}
	
	public void initIgnoreCard(){
		boolean firstLoop = true;
		int cardCount = 0;
		ArrayList<Integer> noForkNode = new ArrayList<Integer>();
		for(int i = 0; i < this.getNodeSize(); i++){
			int count = 0;
			for(int j = 0; j < this.getEdgeSize(); j++ ){
				if(this.getNode(i).num == this.getEdge(j).endNode.num || this.getNode(i).num == this.getEdge(j).startNode.num){
					count++;
				}
				if(firstLoop)
					if(this.getEdge(j).twoWay)
						cardCount++;
			}
			firstLoop = false;
			if(count == 2)
				noForkNode.add(i+1);
		}
		this.cardQuantity = 2*this.getEdgeSize() - cardCount;
		ignoreCard = new ArrayList<Integer>();
		for(int i = 0; i < noForkNode.size(); i++){
			
			for(int j = 0; j < this.getEdgeSize(); j++){
				if(noForkNode.get(i) == this.getEdge(j).endNode.num){
					ignoreCard.add(this.getEdge(j).endCardNum);
					//System.out.println("ignoreCard:"+this.getEdge(j).endCardNum);
				}
			}
		}	
	}
	
	public ArrayList<Integer> getIgnoreCard(){
		return this.ignoreCard;
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
	
	public ArrayList<Edge> getEdgeArray(){
		return edgeArray;
	}
	
	public void addFunctionNode(FunctionNodeEnum function,int nodeNum, String ip, int comNum, String tag){
		functionNodeArray.add(new FunctionNode(function, nodeNum,ip, comNum, tag, this.functionNodeArray.size(), timerPool));
		nodeArray.get(nodeNum-1).tag = tag;
		nodeArray.get(nodeNum - 1).functionNode = true;
	}
	
	public void addTagNode(FunctionNodeEnum function,int x , int y, String tag){
		functionNodeArray.add(new FunctionNode(function,x, y, tag));
	}
	
	public ArrayList<FunctionNode> getFunctionNodeArray(){
		return functionNodeArray;
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
	
	public int getCardQuantity(){
		return cardQuantity;
	}
	
	public int getStopCard(){
		return stopCard;
	}
	
	public int getExecuteCard(){
		return executeCard;
	}
	
	public void addAGVSeting(String seting){
		AGVSeting.add(seting);
	}
	
	public ArrayList<String> getAGVSeting(){
		return AGVSeting;
	}
	
	public void setStopCard(int num){
		this.stopCard = num;
	}
	
	public void setExecuteCard(int num){
		this.executeCard = num;
	}
	
	public void setStartPointCard(int num){
		this.startPointCard = num;
	}
	
	public int getStartPointCard(){
		return this.startPointCard;
	}
}
