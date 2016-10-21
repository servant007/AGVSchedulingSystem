
public class AGVCar{
		private int x;
		private int y;
		private Edge edge;
		
		
		public AGVCar(){
		}
		
		public int getX(){
			return x;
		}
		
		public int getY(){
			return y;
		}
		
		public void stepForward(){
			if(edge.startNode.x == edge.endNode.x){
				if(edge.startNode.y < edge.endNode.y && y < edge.endNode.y){
					y +=5;
				}else if(edge.startNode.y > edge.endNode.y && y > edge.endNode.y){
					y -=5;
				}
			}else if(edge.startNode.y == edge.endNode.y){
				if(edge.startNode.x < edge.endNode.x && x < edge.endNode.x){
					x +=5;
				}else if(edge.startNode.x > edge.endNode.x && x > edge.endNode.x){
					x -=5;
				}
			}
		} 
		
		public void setOnEdge(Edge edge){
			this.edge = edge;
			x = edge.startNode.x;
			y = edge.startNode.y;
		}
		
	}
