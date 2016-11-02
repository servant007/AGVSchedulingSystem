package schedulingSystem.component;

import schedulingSystem.toolKit.HandleReceiveMessage;

public class AGVCar{
		private int x = -15;
		private int y = -15;
		private Edge edge;
		private int lastCard;
		private boolean orientation;//trueÓÒ£¬false×ó
		private Graph graph;
		private int electricity;
		private boolean finishEdge;
		private long lastCommunicationTime;
		private HandleReceiveMessage handleReceiveMessage;
		
		public AGVCar(Graph graph){
			this.graph = graph;
			finishEdge = true;
			edge = new Edge(new Node(0,0),new Node(0,0));
		}
		
		public int getX(){
			return x;
		}
		
		public int getY(){
			return y;
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
		
		public void setOnEdge(Edge edge){
			finishEdge = false;
			this.edge = edge;//new Edge( edge.startNode, edge.endNode,edge.realDis, edge.strCardNum, edge.endCardNum, edge.twoWay);
			x = edge.startNode.x;
			y = edge.startNode.y;
			if(edge.twoWay)
				this.orientation = !this.orientation;
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
			if(System.currentTimeMillis() - lastCommunicationTime < 10000)
				return true;
			else 
				return false;
		}
		
		public Node getStartNode(){
			if(edge.twoWay){
				edge.endNode.functionNode = true;
				return edge.endNode;
			}else{
				return edge.startNode;
			}
		}
		
		public void setRunnabel(HandleReceiveMessage handleReceiveMessage){
			this.handleReceiveMessage = handleReceiveMessage;
		}
		
		public HandleReceiveMessage getRunnable(){
			return handleReceiveMessage;
		}
		
		public void setLastCard(int num){
			this.lastCard = num;
			/*
			if(graph != null){
				if(graph.searchCard(num)!=null){
					if(graph.searchCard(num).twoWay)
						this.orientation = !this.orientation;
				}
				System.out.println("graph!=null");
			}*/
			
			
		}
		
		public int getLastCard(){
			return lastCard;
		}
		
		public boolean getOrientation(){
			return orientation;
		}
}
