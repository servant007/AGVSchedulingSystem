package schedulingSystem.component;

import java.util.ArrayList;

import schedulingSystem.toolKit.HandleReceiveMessage;

public class AGVCar{
		private int AGVNum;
		private int x = -15;
		private int y = -15;
		private Edge edge;
		private int lastCard;
		public enum Orientation{LEFT,RIGTH,UP,DOWN}
		private Orientation orientation;
		private Graph graph;
		private int electricity;
		private boolean finishEdge;
		private long lastCommunicationTime;
		private HandleReceiveMessage handleReceiveMessage;
		private ConflictDetection conflictDetection;
		private boolean lock = true;
		private int destinationCard;
		private boolean isOnMission;
		private String missionString;
		private ArrayList<Edge> routeEdge;
		private int state;
		
		public AGVCar(){}
		
		public AGVCar(int AGVNum, Graph graph, ConflictDetection conflictDetection){
			this.AGVNum = AGVNum;
			this.graph = graph;
			this.conflictDetection = conflictDetection;
			finishEdge = true;
			state = 2;
			edge = new Edge(new Node(0,0),new Node(0,0));
			routeEdge = new ArrayList<Edge>();
		}

		public void stepForward(){
			if(!finishEdge&& (state == 2 || state == 3)){
				if(edge.startNode.x == edge.endNode.x){
					if(edge.startNode.y < edge.endNode.y ){
						if(y < edge.endNode.y){
							y +=12;
						}else{
							finishEdge = true;
						}	
					}else if(edge.startNode.y > edge.endNode.y ){
						if(y > edge.endNode.y){
							y -=12;
						}else{
							finishEdge = true;
						}
					}
				}else if(edge.startNode.y == edge.endNode.y){
					if(edge.startNode.x < edge.endNode.x ){
						if(x < edge.endNode.x)
							x +=12;
						else
							finishEdge = true;
					}else if(edge.startNode.x > edge.endNode.x){
						if(x > edge.endNode.x)
							x -=12;
						else
							finishEdge = true;
					}
				}
			}
		} 
		
		public Edge getStartEdge(){
			//如果最后一张卡是停止卡，
			if(lastCard == graph.getStopCard()){
				edge.endNode.functionNode = true;
				return edge;
			}else{
				return edge;
			}
		}
		
		public void judgeOrientation(){
			if(!(lastCard == graph.getStopCard())){//
				if(edge.startNode.x == edge.endNode.x){
					if(edge.startNode.y < edge.endNode.y){
						orientation = Orientation.DOWN;
					}else{
						orientation = Orientation.UP;
					} 	
				}else if(edge.startNode.y == edge.endNode.y){
					if(edge.startNode.x < edge.endNode.x){
						orientation = Orientation.RIGTH;
					}else{
						orientation = Orientation.LEFT;
					} 
						
				}
			}
		}		
	
		public void setLastCard(int cardNum){
			
			
			Edge edgeStr = null;
			Edge edgeEnd = null;
			boolean foundStr = false;
			boolean foundEnd = false;
			for(Edge edge : graph.getEdgeArray()){
				if(edge.strCardNum == cardNum){
					edgeStr = edge;
					foundStr = true;
				}else if(edge.endCardNum == cardNum){
					edgeEnd = edge;
					foundEnd = true;
				}
			}
			if(lock){
				if(foundEnd){
					checkConflict(cardNum, true, edgeEnd);
					lock = false;
				}else if(foundStr){
					this.edge = edgeStr;
					//checkConflict(cardNum);
					lock = true;
				}
			}else{
				if(foundStr){
					this.edge = edgeStr;
					checkConflict(cardNum, false, edgeStr);
					lock = true;
				}else if(foundEnd){
					lock = false;
				}
			}

			if((edgeStr != null)&&(lock)){
				finishEdge = false;
				x = edge.startNode.x;
				y = edge.startNode.y;
				judgeOrientation();
			}
			
			
			
			
			//如果是停止卡则取消闪烁
			for(int i = 0; i < graph.getShipmentNode().size(); i++){
				if(graph.getShipmentNode().get(i).callAGVNum == this.AGVNum){
					if(graph.getShipmentNode().get(i).cardNum  == cardNum){
						graph.getShipmentNode().get(i).clicked = false;
					}
				}
				
			}
			
			for(int i = 0; i < graph.getUnloadingNode().size(); i++){
				if(graph.getUnloadingNode().get(i).callAGVNum == this.AGVNum){
					if(graph.getUnloadingNode().get(i).cardNum  == cardNum){
						graph.getUnloadingNode().get(i).clicked = false;
					}
				}
				
			}	
			
			for(int i = 0; i < graph.getEmptyCarNode().size(); i++){
				if(graph.getEmptyCarNode().get(i).callAGVNum == this.AGVNum){
					if(graph.getEmptyCarNode().get(i).cardNum  == cardNum){
						graph.getEmptyCarNode().get(i).clicked = false;
					}
				}
				
			}	
			
			for(int i = 0; i < graph.getChargeNode().size(); i++){
				if(graph.getChargeNode().get(i).callAGVNum == this.AGVNum){
					if(graph.getChargeNode().get(i).cardNum  == cardNum){
						graph.getChargeNode().get(i).clicked = false;
					}
				}
				
			}	
			
						
			
			if(lastCard == this.destinationCard&& cardNum == graph.getStopCard())
				this.isOnMission = false;
			this.lastCard = cardNum;
		}
		
		public int getLastCard(){
			return lastCard;
		}
		
		public Orientation getOrientation(){
			return orientation;
		}
		

		public void setElectricity(int electricity){
			this.electricity = electricity;
		}
		
		public int getElectricity(){
			return electricity;
		}
		
		public void setLastCommunicationTime(long time){
			lastCommunicationTime = time;
		}
		
		public long getLastCommunitionTime(){
			return lastCommunicationTime;
		}
		
		public boolean isAlived(){
			if(System.currentTimeMillis() - lastCommunicationTime < 7000)
				return true;
			else 
				return false;
		}
		
		public void setRunnabel(HandleReceiveMessage handleReceiveMessage){
			this.handleReceiveMessage = handleReceiveMessage;
		}
		
		public HandleReceiveMessage getRunnable(){
			return handleReceiveMessage;
		}

		public int getAGVNum(){
			return AGVNum;
		}
		
		public int getX(){
			return x;
		}
		
		public int getY(){
			return y;
		}
		
		public void setDestinationNode(int nodeNum){
			System.out.println("destination node:" + nodeNum);
			this.missionString = graph.getNode(nodeNum-1).tag;
			this.isOnMission = true;
			for(int i = 0 ; i < graph.getEdgeSize(); i++){
				if(nodeNum == graph.getEdge(i).endNode.num){
					this.destinationCard = graph.getEdge(i).endCardNum;
				}
			}
		}
		
		public boolean isOnMission(){
			return this.isOnMission;
		}
		
		public String getMissionString(){
			return missionString;
		}
		
		public void setRoute(ArrayList<Integer> routeNode){
			routeEdge.clear();
			for(int i = 0; i+1 < routeNode.size(); i++){
				for(int j = 0; j < graph.getEdgeSize(); j++){
					if(routeNode.get(i) == graph.getEdge(j).startNode.num && routeNode.get(i+1) == graph.getEdge(j).endNode.num){
						routeEdge.add(graph.getEdge(j));
					}
				}
			}
			System.out.println("routeEdge:"+routeEdge.size()+"//");
			String str = "";
			for(int i = 0; i < routeEdge.size(); i++){
				str+=routeEdge.get(i).strCardNum;
				str+="/";
				str+=routeEdge.get(i).endCardNum;
				str+="//";
			}
			
			System.out.println(str);
		}
		
		
		
		public void checkConflict(int cardNum, boolean foundEnd, Edge edge){
			if(foundEnd){
				System.out.println(this.AGVNum+"agv查询是否可以通过"+edge.endNode.num+"点");
				conflictDetection.checkConflict(this, edge.endNode.num, 0);//根据route
			}else{
				conflictDetection.removeOccupy(this, edge.startNode.num);
			}	
			/*
			if(routeEdge.size() != 0){//只有被派遣任务的agv才会检查路径冲突
				if(cardNum != routeEdge.get(routeEdge.size() - 1).endCardNum){
					//查询是否是endCard，如果是，检测是否冲突，将结果发送给agv
					for(int i = 0; i < routeEdge.size(); i++){
						if(cardNum == routeEdge.get(i).endCardNum){//检测冲突
							System.out.println(this.AGVNum+"agv查询是否可以通过"+routeEdge.get(i).endNode.num+"点");
							conflictDetection.checkConflict(this, routeEdge.get(i).endNode.num, 0);//根据route
						}
						
						if(cardNum == routeEdge.get(i).strCardNum){//解除占用
							//System.out.println(this.AGVNum + "agv查询解除对" + routeEdge.get(i).startNode.num +"点的占用");
							conflictDetection.removeOccupy(this, routeEdge.get(i).startNode.num);
						}
					}
				}
			}else{
				if(foundEnd){
					System.out.println(this.AGVNum+"agv查询是否可以通过"+edge.endNode.num+"点");
					conflictDetection.checkConflict(this, edge.endNode.num, 0);//根据route
				}else{
					conflictDetection.removeOccupy(this, edge.startNode.num);
				}	
				
			}*/
		}
		
		public void setAGVState(int state){
			this.state = state;
		}
}
