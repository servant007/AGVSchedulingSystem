package schedulingSystem.component;

import schedulingSystem.toolKit.HandleReceiveMessage;

public class AGVCar{
		private int number;
		private int x = -15;
		private int y = -15;
		private Edge edge;
		private int lastCard;
		private int lastLaterCard;
		public enum Orientation{LEFT,RIGTH,UP,DOWN}
		private Orientation orientation;//true右，false左
		private Graph graph;
		private int electricity;
		private boolean finishEdge;
		private long lastCommunicationTime;
		private HandleReceiveMessage handleReceiveMessage;
		private ConflictDetection conflictDetection;
		private boolean lock = true;
		public AGVCar(){}
		public AGVCar(int number, Graph graph, ConflictDetection conflictDetection){
			this.number = number;
			this.graph = graph;
			this.conflictDetection = conflictDetection;
			finishEdge = true;
			edge = new Edge(new Node(0,0),new Node(0,0));
		}

		public void stepForward(){
			if(!finishEdge){
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
		
		public void setOnEdge(int cardNum){
			Edge returnEdge = null;
			boolean foundStr = false;
			boolean foundEnd = false;
			for(Edge edge : graph.getEdgeArray()){
				if(edge.strCardNum == cardNum){
					returnEdge = edge;
					foundStr = true;
				}else if(edge.endCardNum == cardNum){
					foundEnd = true;
				}
			}
			if(lock){
				if(foundEnd){
					lock = false;
				}else if(foundStr){
					this.edge = returnEdge;
					lock = true;
				}
			}else{
				if(foundStr){
					this.edge = returnEdge;
					lock = true;
				}else if(foundEnd){
					lock = false;
				}
			}

			if((returnEdge != null)&&(lock)){
				finishEdge = false;
				x = edge.startNode.x;
				y = edge.startNode.y;
				judgeOrientation();
			}
		}
		
		public Node getStartNode(){
			//如果最后一张卡是停止卡，
			if(lastCard == graph.getStopCard()){
				edge.endNode.functionNode = true;
				return edge.endNode;
			}else{
				return edge.startNode;
			}
		}
		
		public void judgeOrientation(){
			if(!(lastLaterCard == graph.getStopCard())){//
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
			
			//如果是停止卡则取消闪烁
			for(int i = 0; i < graph.getShipmentNode().size(); i++){
				if(graph.getShipmentNode().get(i).cardNum  == cardNum){
					graph.getShipmentNode().get(i).clicked = false;
				}
			}
			
			for(int i = 0; i < graph.getUnloadingNode().size(); i++){
				if(graph.getUnloadingNode().get(i).cardNum  == cardNum){
					graph.getUnloadingNode().get(i).clicked = false;
				}
			}	
			
			for(int i = 0; i < graph.getEmptyCarNode().size(); i++){
				if(graph.getEmptyCarNode().get(i).cardNum  == cardNum){
					graph.getEmptyCarNode().get(i).clicked = false;
				}
			}	
			
			
			//查询是否是endCard，如果是，检测是否冲突，将结果发送给agv
			for(int i = 0; i < graph.getEdgeSize(); i++){
				if(cardNum == graph.getEdge(i).strCardNum && lastCard == graph.getEdge(i).endCardNum){//检测冲突
					//System.out.println("读到"+cardNum+"号卡"+"查询是否冲突");
					conflictDetection.checkConflict(this, graph.getEdge(i).startNode.num, 0);//根据route
				}else if(cardNum == graph.getEdge(i).endCardNum && lastCard == graph.getEdge(i).strCardNum){//检测冲突
					//System.out.println("读到"+cardNum+"号卡"+"查询是否冲突");
					conflictDetection.checkConflict(this, graph.getEdge(i).endNode.num, 0);//根据route
				}
				if(cardNum == graph.getEdge(i).strCardNum){//解除占用
					//System.out.println("读到"+cardNum+"号卡"+"解除" + this.number + "号对" + graph.getEdge(i).startNode.num +"的占用");
					conflictDetection.removeOccupy(this, graph.getEdge(i).startNode.num);
				}else if(cardNum == graph.getEdge(i).endCardNum){
					//System.out.println("读到"+cardNum+"号卡"+"解除" + this.number + "号对" + graph.getEdge(i).endNode.num + "的占用");
					conflictDetection.removeOccupy(this, graph.getEdge(i).endNode.num);
				}
			}
			this.lastLaterCard = this.lastCard;
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
			if(System.currentTimeMillis() - lastCommunicationTime < 100000)
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

		public int getNumber(){
			return number;
		}
		
		public int getX(){
			return x;
		}
		
		public int getY(){
			return y;
		}
}
