package schedulingSystem.component;

import java.util.ArrayList;

public class ConflictDetection {
	private ArrayList<ConflictNode> conflictNodeArray;
	private ArrayList<ConflictEdge> conflictEdgeArray;
	public ConflictDetection(Graph graph){
		conflictNodeArray = new ArrayList<ConflictNode>();
		for(int i = 0; i < graph.getNodeSize(); i++)
			conflictNodeArray.add(new ConflictNode(i+1));
		
		conflictEdgeArray = new ArrayList<ConflictEdge>();
		boolean found = false;
		for(int i = 0; i < graph.getEdgeSize(); i++){
			for(int j = 0; j < conflictEdgeArray.size(); j++){
				if((graph.getEdge(i).startNode.num == conflictEdgeArray.get(j).stratNodeNum && graph.getEdge(i).endNode.num == conflictEdgeArray.get(j).endNodeNum)
						||(graph.getEdge(i).startNode.num == conflictEdgeArray.get(j).endNodeNum && graph.getEdge(i).endNode.num == conflictEdgeArray.get(j).stratNodeNum)){
					found = true;
				}
			}
			if(!found){
				conflictEdgeArray.add(new ConflictEdge(graph.getEdge(i).startNode.num, graph.getEdge(i).endNode.num));
				
			}else {
				found = false;
			}
		}
		
	}
	
	public void checkConflict(AGVCar agvCar, int checkNode, int endNode){
		if(conflictNodeArray.get(checkNode-1).occupy){
			conflictNodeArray.get(checkNode-1).waitQueue.add(agvCar);
			agvCar.getRunnable().SendMessage("CC01DD");//马上停止
			System.out.println("让"+agvCar.getAGVNum()+"号马上停止");
			
		}else{
			conflictNodeArray.get(checkNode-1).waitQueue.add(agvCar);
			conflictNodeArray.get(checkNode-1).occupy = true;
			System.out.println(agvCar.getAGVNum()+"agv占用"+checkNode + "点");
		}
			
	}
	
	public ConflictEdge checkConflictEdge(AGVCar agvCar, int start, int end){
		ConflictEdge edge = null;
		for(int i = 0; i < conflictEdgeArray.size(); i++){
			if((start == conflictEdgeArray.get(i).stratNodeNum && end == conflictEdgeArray.get(i).endNodeNum)
					||(start == conflictEdgeArray.get(i).endNodeNum && end == conflictEdgeArray.get(i).stratNodeNum)){
				edge = conflictEdgeArray.get(i);
			}
		}
		if(edge.occupy){
			edge.waitQueue.add(agvCar);
			agvCar.getRunnable().SendMessage("CC01DD");//马上停止
			System.out.println("让"+agvCar.getAGVNum()+"号马上停止");
		}else{
			edge.waitQueue.add(agvCar);
			edge.occupy = true;
			System.out.println(agvCar.getAGVNum()+"agv占用"+ edge.stratNodeNum + "||" + edge.endNodeNum + "边");
		}
		return edge;
	}
	
	public void removeOccupyEdge(AGVCar agvCar, ConflictEdge edge){
		edge.removeAGV(agvCar);
	}
	
	public void removeOccupy(AGVCar agvCar, int nodeNum){
		conflictNodeArray.get(nodeNum-1).removeAGV(agvCar);
	}
	
	public ArrayList<ConflictNode> getConflictNodeArray(){
		return conflictNodeArray;
	}
	
	public ArrayList<ConflictEdge> getConflictEdgeArray(){
		return conflictEdgeArray;
	}
	
}
