package schedulingSystem.component;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

public class ConflictDetection {
	private static Logger logger = Logger.getLogger(AGVCar.class.getName());
	private ArrayList<ConflictNode> conflictNodeArray;
	private ArrayList<ConflictEdge> conflictEdgeArray;
	private ArrayList<ConflictEdge> edgeRockwellArray;
	private ArrayList<Integer> edgeRockwellAGVNumArray;
	private ConflictEdge edgeRockwell;
	private int edgeRockwellAGVNum;
	private final ReentrantLock lock1;
	private final ReentrantLock lock2;
	private final ReentrantLock lock3;
	private final ReentrantLock lock4;
	public ConflictDetection(Graph graph){
		lock1 = new ReentrantLock();
		lock2 = new ReentrantLock();
		lock3 = new ReentrantLock();
		lock4 = new ReentrantLock();
		conflictEdgeArray = new ArrayList<ConflictEdge>();
		edgeRockwellArray = new ArrayList<ConflictEdge>();
		edgeRockwellAGVNumArray = new ArrayList<Integer>();
		boolean found = false;
		for(int i = 0; i < graph.getEdgeSize(); i++){
			if(graph.getEdge(i).twoWay){
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
		
		conflictEdgeArray.add(new ConflictEdge(45, 25));
		conflictEdgeArray.add(new ConflictEdge(23, 21));
		conflictEdgeArray.add(new ConflictEdge(19, 17));
		conflictEdgeArray.add(new ConflictEdge(15, 13));
		conflictEdgeArray.add(new ConflictEdge(11, 57));
		conflictEdgeArray.add(new ConflictEdge(58, 28));
		conflictEdgeArray.add(new ConflictEdge(30, 32));
		conflictEdgeArray.add(new ConflictEdge(34, 36));
		conflictEdgeArray.add(new ConflictEdge(38, 40));
		conflictEdgeArray.add(new ConflictEdge(42, 43));
		
		conflictEdgeArray.add(new ConflictEdge(8, 10));
		conflictEdgeArray.add(new ConflictEdge(6, 8));
		conflictEdgeArray.add(new ConflictEdge(4, 6));
		conflictEdgeArray.add(new ConflictEdge(2, 4));
		conflictEdgeArray.add(new ConflictEdge(56, 2));
	
		conflictNodeArray = new ArrayList<ConflictNode>();
		for(int i = 0; i < graph.getNodeSize(); i++)
			conflictNodeArray.add(new ConflictNode(i+1, conflictEdgeArray));
	}
	
	public void checkConflict(AGVCar agvCar, int checkNode){
		synchronized(lock1){
			boolean nodeCarInWaitQueue = false;
			for(int i = 0; i < conflictNodeArray.get(checkNode-1).waitQueue.size(); i++ ){
				if(agvCar.getAGVNum() == conflictNodeArray.get(checkNode-1).waitQueue.get(i).getAGVNum()){
					nodeCarInWaitQueue = true;
					logger.debug("nodeCarInWaitQueue");
					System.out.println("nodeCarInWaitQueue");
				}
			}
			if(conflictNodeArray.get(checkNode-1).occupy && !nodeCarInWaitQueue){
				conflictNodeArray.get(checkNode-1).waitQueue.add(agvCar);
				agvCar.getRunnable().SendActionMessage("CC01DD");//马上停止
				logger.debug(checkNode + "点被"+ conflictNodeArray.get(checkNode-1).waitQueue.get(0).getAGVNum()+ "AGV占用，让"+agvCar.getAGVNum()+"AGV马上停止");
				System.out.println(checkNode + "点被"+ conflictNodeArray.get(checkNode-1).waitQueue.get(0).getAGVNum()+ "AGV占用，让"+agvCar.getAGVNum()+"AGV马上停止");
				
			}else if(!conflictNodeArray.get(checkNode-1).occupy){
				conflictNodeArray.get(checkNode-1).waitQueue.add(agvCar);
				conflictNodeArray.get(checkNode-1).occupy = true;
				logger.debug(agvCar.getAGVNum()+"AGV占用"+checkNode + "点");
				//System.out.println(agvCar.getAGVNum()+"agv占用"+checkNode + "点");
			}
		}	
	}
	
	public ConflictEdge checkConflictEdge(AGVCar agvCar, int start, int end){
		synchronized(lock2){
			ConflictEdge edge = null;
			int startRockwell = 0, endRockwell = 0;
			if((start == 1 && end == 2) || (start == 2 && end == 1)){
				startRockwell = 56;
				endRockwell = 2;
			}else if((start == 3 && end == 4) || (start == 4 && end == 3)){
				startRockwell = 2;
				endRockwell = 4;
			}else if((start == 5 && end == 6) || (start == 6 && end == 5)){
				startRockwell = 6;
				endRockwell = 4;
			}else if((start == 7 && end == 8) || (start == 8 && end == 7)){
				startRockwell = 8;
				endRockwell = 6;
			}else if((start == 9 && end == 10) || (start == 10 && end == 9)){
				startRockwell = 8;
				endRockwell = 10;
			}else if((start == 11 && end == 12) || (start == 12 && end == 11)){
				startRockwell = 11;
				endRockwell = 57;
			}else if((start == 13 && end == 14) || (start == 14 && end == 13)){
				startRockwell = 11;
				endRockwell = 13;
			}else if((start == 15 && end == 16) || (start == 16 && end == 15)){
				startRockwell = 13;
				endRockwell = 15;
			}else if((start == 17 && end == 18) || (start == 18 && end == 17)){
				startRockwell = 17;
				endRockwell = 15;
			}else if((start == 19 && end == 20) || (start == 20 && end == 19)){
				startRockwell = 19;
				endRockwell = 17;
			}else if((start == 21 && end == 22) || (start == 22 && end == 21)){
				startRockwell = 21;
				endRockwell = 19;
			}else if((start == 23 && end == 24) || (start == 24 && end == 23)){
				startRockwell = 23;
				endRockwell = 21;
			}else if((start == 25 && end == 26) || (start == 26 && end == 25)){
				startRockwell = 25;
				endRockwell = 23;
			}else if((start == 27 && end == 28) || (start == 28 && end == 27)){
				startRockwell = 28;
				endRockwell = 30;
			}else if((start == 29 && end == 30) || (start == 30 && end == 29)){
				startRockwell = 30;
				endRockwell = 32;
			}else if((start == 31 && end == 32) || (start == 32 && end == 31)){
				startRockwell = 32;
				endRockwell = 34;
			}else if((start == 33 && end == 34) || (start == 34 && end == 33)){
				startRockwell = 34;
				endRockwell = 36;
			}else if((start == 35 && end == 36) || (start == 36 && end == 35)){
				startRockwell = 36;
				endRockwell = 38;
			}else if((start == 37 && end == 38) || (start == 38 && end == 37)){
				startRockwell = 38;
				endRockwell = 40;
			}else if((start == 39 && end == 40) || (start == 40 && end == 39)){
				startRockwell = 42;
				endRockwell = 40;
			}else if((start == 41 && end == 42) || (start == 42 && end == 41)){
				startRockwell = 43;
				endRockwell = 42;
			}else if((start == 44 && end == 45) || (start == 45 && end == 44)){
				startRockwell = 45;
				endRockwell = 25;
			}
			
			for(int i = 0; i < conflictEdgeArray.size(); i++){
				if((start == conflictEdgeArray.get(i).stratNodeNum && end == conflictEdgeArray.get(i).endNodeNum)
						||(start == conflictEdgeArray.get(i).endNodeNum && end == conflictEdgeArray.get(i).stratNodeNum)){
					edge = conflictEdgeArray.get(i);
					edge.waitNodeNum = start;//查这条边防冲突时，同时也会查waitNodeNum点放冲突
				}
				if((startRockwell == conflictEdgeArray.get(i).stratNodeNum && endRockwell == conflictEdgeArray.get(i).endNodeNum)
						||(startRockwell == conflictEdgeArray.get(i).endNodeNum && endRockwell == conflictEdgeArray.get(i).stratNodeNum)){
					edgeRockwell = conflictEdgeArray.get(i);
					edgeRockwellAGVNum = agvCar.getAGVNum();
					if(this.edgeRockwell.occupy && this.edgeRockwell.waitQueue.get(0).getAGVNum() == agvCar.getAGVNum()){
						this.edgeRockwell = null;
						this.edgeRockwellAGVNum = 0;
					}else{
						this.edgeRockwellArray.add(edgeRockwell);
						this.edgeRockwellAGVNumArray.add(edgeRockwellAGVNum);
					}
				}
				if(edge != null && edgeRockwell != null){
					break;
				}
			}
			
			if(this.edgeRockwell != null && this.edgeRockwellAGVNum == agvCar.getAGVNum()){
				if(this.edgeRockwell.occupy){
					if(((start == 9 && end == 10) || (start == 10 && end == 9)) && edge.occupy){
						edge.waitQueue.add(agvCar);
						agvCar.getRunnable().SendActionMessage("CC01DD");//马上停止
					}else{
						this.edgeRockwell.waitQueue.add(agvCar);
						agvCar.getRunnable().SendActionMessage("CC01DD");//马上停止
						logger.debug("edgeRockwell"+edgeRockwell.endNodeNum+"||"+edgeRockwell.stratNodeNum+"让"+agvCar.getAGVNum()+"AGV马上停止."
									+ "被"+edgeRockwell.waitQueue.get(0)+"AGV占用");
						System.out.println("edgeRockwell让"+agvCar.getAGVNum()+"AGV马上停止。"+ "被"+edgeRockwell.waitQueue.get(0).getAGVNum()+"AGV占用");
					}
				}else{
					if(!((start == 9 && end == 10) || (start == 10 && end == 9))){
						this.edgeRockwell.waitQueue.add(agvCar);
						this.edgeRockwell.occupy = true;
						logger.debug(agvCar.getAGVNum()+"AGV占用edgeRockwell"+startRockwell+"||"+endRockwell+"边");
						System.out.println(agvCar.getAGVNum()+"AGV占用edgeRockwell"+startRockwell+"||"+ endRockwell + "边");
					}
				}
				this.edgeRockwell = null;
				this.edgeRockwellAGVNum = 0;
			}
			
			if(edge != null ){
				boolean edgeCarInWaitQueue = false;
				for(int i = 0; i < edge.waitQueue.size(); i++ ){
					if(agvCar.getAGVNum() == edge.waitQueue.get(i).getAGVNum()){
						edgeCarInWaitQueue = true;
						logger.debug("edgeCarInWaitQueue");
						System.out.println("edgeCarInWaitQueue");
					}
				}
				logger.debug(agvCar.getAGVNum()+"AGV查询是否可以通过"+ start + "||" + end + "边");
				//System.out.println(agvCar.getAGVNum()+"AGV查询是否可以通过"+ start + "||" + end + "边");
				if(edge.occupy && !edgeCarInWaitQueue){
					edge.waitQueue.add(agvCar);
					if(agvCar.getRouteNode().get(0) != 9){
						agvCar.getRunnable().SendActionMessage("CC01DD");//马上停止
						logger.debug(start + "||" + end + "边被" + edge.waitQueue.get(0).getAGVNum() +"AGV占用,让"+agvCar.getAGVNum()+"AGV马上停止");
						System.out.println(start + "||" + end + "边被" + edge.waitQueue.get(0).getAGVNum() +"AGV占用,让"+agvCar.getAGVNum()+"AGV马上停止");
					}
				}else if(!edge.occupy){
					edge.waitQueue.add(agvCar);
					edge.occupy = true;
					logger.debug(agvCar.getAGVNum()+"AGV占用"+ edge.stratNodeNum + "||" + edge.endNodeNum + "边");
					//System.out.println(agvCar.getAGVNum()+"agv占用"+ edge.stratNodeNum + "||" + edge.endNodeNum + "边");
				}
			}	
			
			return edge;
		}
		
	}
	
	public void removeOccupyEdge(AGVCar agvCar, ConflictEdge edge){
		
		if(edge.waitNodeNum > 0)
			edge.removeAGV(agvCar, conflictNodeArray.get(edge.waitNodeNum - 1));
		else
			edge.removeAGV(agvCar, null);
	}
	
	public void removeEdgeRockwell(AGVCar agvCar){
		synchronized(lock3){
			if(this.edgeRockwellArray.size() != this.edgeRockwellAGVNumArray.size()){
				logger.error("this.edgeRockwellArray.size() != this.edgeRockwellAGVNumArray.size()");
				System.out.println("this.edgeRockwellArray.size() != this.edgeRockwellAGVNumArray.size()");
			}
			for(int i = 0; i < this.edgeRockwellArray.size(); i++){
				if(this.edgeRockwellAGVNumArray.get(i) == agvCar.getAGVNum()){
					System.out.println(agvCar.getAGVNum() + "AGV  edgeRockwell解除");
					logger.debug(agvCar.getAGVNum() + "AGV  edgeRockwell解除");
					this.edgeRockwellArray.get(i).removeAGV(agvCar, null);
					this.edgeRockwellArray.remove(i);
					this.edgeRockwellAGVNumArray.remove(i);
				}
			}
		}
		
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
