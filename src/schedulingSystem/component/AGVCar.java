package schedulingSystem.component;

import schedulingSystem.toolKit.HandleReceiveMessage;

public class AGVCar{
		private int x = -15;
		private int y = -15;
		private Edge edge;
		private int electricity;
		private boolean keepAlived;
		private boolean finishEdge;
		private long lastCommunicationTime;
		private HandleReceiveMessage handleReceiveMessage;
		
		public AGVCar(){
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
			this.edge = new Edge( edge.startNode, edge.endNode,edge.realDis, edge.strCardNum, edge.endCardNum, edge.twoWay);
			x = edge.startNode.x;
			y = edge.startNode.y;
		}
		
		public void setElectricity(int electricity){
			this.electricity = electricity;
		}
		
		public int getElectricity(){
			return electricity;
		}
		
		public boolean getKeepAlived(){
			return keepAlived;
		}
		
		public void setTime(long time){
			lastCommunicationTime = time;
		}
		
		public long getLastTime(){
			return lastCommunicationTime;
		}
		
		public boolean isAlived(){
			if(System.currentTimeMillis() - lastCommunicationTime < 6000)
				return true;
			else 
				return false;
		}
		
		public int getStartNode(){
			if(edge.twoWay){
				return edge.endNode.num;
			}else{
				return edge.startNode.num;
			}
		}
		
		public void setRunnabel(HandleReceiveMessage handleReceiveMessage){
			this.handleReceiveMessage = handleReceiveMessage;
		}
		
		public HandleReceiveMessage getRunnable(){
			return handleReceiveMessage;
		}
}
