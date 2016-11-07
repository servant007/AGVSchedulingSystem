package schedulingSystem.component;

import java.util.ArrayList;

public class ConflictDetection {
	private Graph graph;
	private ArrayList<ConflictNode> conflictNodeArray;
	public ConflictDetection(Graph graph){
		this.graph = graph;
		conflictNodeArray = new ArrayList<ConflictNode>();
		for(int i = 0; i < graph.getNodeSize(); i++)
			conflictNodeArray.add(new ConflictNode(i+1));
		
		for(int i = 0; i < graph.getNodeSize(); i++){
			for(int j = 0; j < graph.getEdgeSize(); j++){
				if(graph.getNode(i).num == graph.getEdge(j).endCardNum || graph.getNode(i).num == graph.getEdge(j).strCardNum){
					conflictNodeArray.get(i).adjoinEdge.add(graph.getEdge(j));
				}
			}
		}	
	}
	
	public void checkConflict(AGVCar agvCar, int strNode, int endNode){
		if(conflictNodeArray.get(strNode-1).occupy){
			conflictNodeArray.get(strNode-1).waitQueue.add(agvCar);
			agvCar.getRunnable().SendMessage("CC01DD");//马上停止
			System.out.println("让"+agvCar.getAGVNum()+"号马上停止");
			
		}else{
			conflictNodeArray.get(strNode-1).waitQueue.add(agvCar);
			conflictNodeArray.get(strNode-1).occupy = true;
			System.out.println(agvCar.getAGVNum()+"agv占用"+strNode + "点");
		}
			
	}
	
	public void removeOccupy(AGVCar agvCar, int nodeNum){
		conflictNodeArray.get(nodeNum-1).removeAGV(agvCar);
	}
	
	public ArrayList<ConflictNode> getConflictNodeArray(){
		return conflictNodeArray;
	}
	
}
