package schedulingSystem.component;
public class AGVCar{
		private int x;
		private int y;
		private Edge edge;
		private int electricity;
		private boolean keepAlived;
		private boolean finishEdge;
		private long lastCommunicationTime;
		
		public AGVCar(){
			finishEdge = true;
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
			this.edge = edge;
			x = edge.startNode.x;
			y = edge.startNode.y;
		}
		
		public void setElectricity(int electricity){
			this.electricity = electricity;
		}
		
		public int getElectricity(){
			return electricity;
		}
		
		public void setKeepAlived(boolean keepAlived){
			this.keepAlived = keepAlived;
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
	}
