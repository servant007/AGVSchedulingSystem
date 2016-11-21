package schedulingSystem.component;

import java.util.ArrayList;

import schedulingSystem.toolKit.MyToolKit;
import schedulingSystem.toolKit.ReceiveAGVMessage;

public class AGVCar{
		private int AGVNum;
		private int x = -20;
		private int y = -20;
		private Edge edge;
		private int lastCard;
		public enum Orientation{LEFT,RIGTH,UP,DOWN}
		private Orientation orientation;
		public enum State{STOP, FORWARD, BACKWARD, SHIPMENT, UNLOADING, NULL}
		private State state;
		private Graph graph;
		private int electricity;
		private boolean finishEdge;
		private long lastCommunicationTime;
		private ReceiveAGVMessage handleReceiveMessage;
		private ConflictDetection conflictDetection;
		private boolean lock = true;
		private int destinationCard;
		private ArrayList<State> trigger;
		private ArrayList<State> triggerCopy;
		private ArrayList<Integer> multiDestination;
		private ArrayList<Integer> multiDestinationCopy;
		private boolean fixRoute;
		private boolean isOnMission;
		private String missionString;
		private MyToolKit myToolKit;
		private Dijkstra dijkstra;
		private boolean first;
		private ArrayList<Edge> routeEdge;
		private ArrayList<Integer> routeNode;
		private ConflictEdge occupyEdge;
		private ConflictEdge lastOccupyEdge;
		private int forwordPix;
		//private long lastTime;
		
		public AGVCar(){}
		
		public AGVCar(int AGVNum, Graph graph, ConflictDetection conflictDetection){
			this.AGVNum = AGVNum;
			this.graph = graph;
			this.conflictDetection = conflictDetection;
			trigger = new ArrayList<State>();
			multiDestination = new ArrayList<Integer>();
			if(graph.getAGVSeting().get(AGVNum-1).length() > 1){
				fixRoute = true;
				String[] route = graph.getAGVSeting().get(AGVNum-1).split("/");
				for(int i = 0; i < route.length; i++){
					if(i%2 == 0){
						if(route[i].equals("4")){
							trigger.add(State.SHIPMENT);
						}else if(route[i].equals("5")){
							trigger.add(State.UNLOADING);
						}
					}else{
						multiDestination.add(Integer.parseInt(route[i]));
					}
						
				}
				
				triggerCopy = new ArrayList<State>(trigger);
				multiDestinationCopy = new ArrayList<Integer>(multiDestination);
			}
			
			System.out.println(this.AGVNum + "AGV"+ this.multiDestination + this.trigger);
			myToolKit = new MyToolKit();
			dijkstra = new Dijkstra(graph);
			finishEdge = true;
			state = State.FORWARD;
			edge = new Edge(new Node(0,0),new Node(0,0));
			first = true;
		}

		public void stepForward(){
			if(!finishEdge&& (state == State.FORWARD || state == State.BACKWARD)){
				if(edge.startNode.x == edge.endNode.x){
					if(edge.startNode.y < edge.endNode.y ){
						if(y < edge.endNode.y){
							y +=forwordPix;
						}else{
							finishEdge = true;
						}	
					}else if(edge.startNode.y > edge.endNode.y ){
						if(y > edge.endNode.y){
							y -=forwordPix;
						}else{
							finishEdge = true;
						}
					}
				}else if(edge.startNode.y == edge.endNode.y){
					if(edge.startNode.x < edge.endNode.x ){
						if(x < edge.endNode.x)
							x +=forwordPix;
						else
							finishEdge = true;
					}else if(edge.startNode.x > edge.endNode.x){
						if(x > edge.endNode.x)
							x -=forwordPix;
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
					if(routeNode != null && routeNode.size() > 2){
						System.out.println(this.AGVNum+"agv查询是否可以通过"+ routeNode.get(1) + "||" + routeNode.get(2) + "边");
						lastOccupyEdge = occupyEdge;
						occupyEdge = conflictDetection.checkConflictEdge(this, routeNode.get(1), routeNode.get(2));//根据route
						routeNode.remove(0);
					}
					lock = false;
				}else if(foundStr){
					this.edge = edgeStr;
					lock = true;
				}
			}else{
				if(foundStr){
					this.edge = edgeStr;

						if(routeNode != null && lastOccupyEdge != null){
							conflictDetection.removeOccupyEdge(this, lastOccupyEdge);
							
						}

					
					lock = true;
				}else if(foundEnd){
					lock = false;
				}
			}

			if((edgeStr != null)&&(lock)){
				finishEdge = false;
				x = edge.startNode.x;
				y = edge.startNode.y;
				int time = edge.realDis/60;
				//System.out.println("time:"+time);
				if(edge.startNode.x == edge.endNode.x){
					forwordPix =  Math.abs(edge.startNode.y - edge.endNode.y)/(10*time);
					if(forwordPix == 0)
						forwordPix = 1;
				}else if(edge.startNode.y == edge.endNode.y){
					forwordPix = Math.abs(edge.startNode.x - edge.endNode.x)/(10*time);
					if(forwordPix == 0)
						forwordPix = 1;
				}
				judgeOrientation();
				if(first && fixRoute){
					first = false;
					routeNode = dijkstra.findRoute(this.getStartEdge(), this.multiDestination.get(0)).getRoute();
					this.getRunnable().SendMessage(myToolKit.routeToOrientation(graph
							, routeNode, this));
					for(int i = 0; i < graph.getFunctionNodeArray().size(); i++){
						if(this.multiDestination.get(0) == graph.getFunctionNodeArray().get(i).nodeNum){
							graph.getFunctionNodeArray().get(i).clicked = true;
							graph.getFunctionNodeArray().get(i).callAGVNum = this.AGVNum;
						}
							
					}
					this.missionString = graph.getNode(multiDestination.get(0)-1).tag;
					this.isOnMission = true;
					for(int i = 0 ; i < graph.getEdgeSize(); i++){
						if(multiDestination.get(0) == graph.getEdge(i).endNode.num){
							this.destinationCard = graph.getEdge(i).endCardNum;
						}
					}
					this.state = State.FORWARD;
					trigger.remove(0);
					multiDestination.remove(0);
				}
			}

			for(int i = 0; i < graph.getFunctionNodeArray().size(); i++){
				if(graph.getFunctionNodeArray().get(i).callAGVNum == this.AGVNum){
					if(cardNum == graph.getStopCard()){
						graph.getFunctionNodeArray().get(i).clicked = false;
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
			if(System.currentTimeMillis() - lastCommunicationTime < 20000)
				return true;
			else 
				return false;
		}
		
		public void setRunnabel(ReceiveAGVMessage handleReceiveMessage){
			this.handleReceiveMessage = handleReceiveMessage;
		}
		
		public ReceiveAGVMessage getRunnable(){
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
		
		public void setDestinationNode(ArrayList<State> trigger, ArrayList<Integer> destination){
			this.trigger = trigger;
			this.multiDestination = destination;
			if(trigger.get(0) == State.NULL){
				routeNode = dijkstra.findRoute(this.getStartEdge(), this.multiDestination.get(0)).getRoute();
				this.getRunnable().SendMessage(myToolKit.routeToOrientation(graph
						, routeNode, this));

				for(int i = 0; i < graph.getFunctionNodeArray().size(); i++){
					if(this.multiDestination.get(0) == graph.getFunctionNodeArray().get(i).nodeNum){
						graph.getFunctionNodeArray().get(i).clicked = true;
						graph.getFunctionNodeArray().get(i).callAGVNum = this.AGVNum;
					}
						
				}
				
				this.missionString = graph.getNode(multiDestination.get(0)-1).tag;
				this.isOnMission = true;
				for(int i = 0 ; i < graph.getEdgeSize(); i++){
					if(multiDestination.get(0) == graph.getEdge(i).endNode.num){
						this.destinationCard = graph.getEdge(i).endCardNum;
					}
				}
				this.state = State.FORWARD;
				trigger.remove(0);
				multiDestination.remove(0);
			}
		}
		
		public boolean isOnMission(){
			return this.isOnMission;
		}
		
		public String getMissionString(){
			return missionString;
		}
		
		public void setRouteEdge(ArrayList<Integer> routeNode){
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
		}
		
		public void setAGVState(int state){
			if(state == 1){
				this.state = State.STOP;
			}else if(state == 2){
				this.state = State.FORWARD;
			}else if(state == 3){
				this.state = State.BACKWARD;
			}else if(state == 4){
				if(trigger != null && trigger.size() > 0 && multiDestination != null && multiDestination.size() > 0){
					if(trigger.get(0) == State.SHIPMENT){
						triggerDestination();
					}
				}else if(fixRoute){
					multiDestination = new ArrayList<Integer>(multiDestinationCopy);
					trigger = new ArrayList<State>(triggerCopy);
					if(trigger.get(0) == State.SHIPMENT){
						triggerDestination();
					}
				}
			}else if(state == 5){
				if(trigger != null && trigger.size() > 0 && multiDestination != null && multiDestination.size() > 0){
					if(trigger.get(0) == State.UNLOADING){
						triggerDestination();
					}
				}else if(fixRoute){
					multiDestination = multiDestinationCopy;
					trigger = triggerCopy;
					if(trigger.get(0) == State.UNLOADING){
						triggerDestination();
					}
				}
			}
		}
		
		private void triggerDestination(){
			routeNode = dijkstra.findRoute(this.getStartEdge(), this.multiDestination.get(0)).getRoute();
			this.getRunnable().SendMessage(myToolKit.routeToOrientation(graph
					, routeNode, this));
			for(int i = 0; i < graph.getFunctionNodeArray().size(); i++){
				if(this.multiDestination.get(0) == graph.getFunctionNodeArray().get(i).nodeNum){
					graph.getFunctionNodeArray().get(i).clicked = true;
					graph.getFunctionNodeArray().get(i).callAGVNum = this.AGVNum;
				}
					
			}

			this.missionString = graph.getNode(multiDestination.get(0)-1).tag;
			this.isOnMission = true;
			for(int i = 0 ; i < graph.getEdgeSize(); i++){
				if(multiDestination.get(0) == graph.getEdge(i).endNode.num){
					this.destinationCard = graph.getEdge(i).endCardNum;
				}
			}
			this.state = State.FORWARD;
			trigger.remove(0);
			multiDestination.remove(0);
		}
		
		public boolean getFixRoute(){
			return this.fixRoute;
		}
		
}
