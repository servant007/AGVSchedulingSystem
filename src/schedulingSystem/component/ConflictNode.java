package schedulingSystem.component;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import schedulingSystem.component.AGVCar.Orientation;


public class ConflictNode {
	private static Logger logger = Logger.getLogger(AGVCar.class.getName());
	private int number;
	private ScheduledExecutorService timerPool;
	public volatile boolean occupy;
	public volatile ArrayList<AGVCar> waitQueue;
	private ArrayList<ConflictEdge> conflictEdgeArray;

	
	public ConflictNode(int number, ArrayList<ConflictEdge> conflictEdgeArray, ScheduledExecutorService timerPool){
		this.conflictEdgeArray = conflictEdgeArray;
		this.number = number;
		this.waitQueue = new ArrayList<AGVCar>();
		this.timerPool = timerPool;
	}
	
	synchronized public void removeAGV(AGVCar agvCar){
		for(int i = 0; i < waitQueue.size(); i++){
			if(waitQueue.get(i).getAGVNum() == agvCar.getAGVNum()){
				waitQueue.remove(i);
				logger.debug(agvCar.getAGVNum() + "agv解除占用" + this.number+ "点");
			//	System.out.println(agvCar.getAGVNum() + "agv解除占用" + this.number+ "点");
				break;
			}	
		}
	
		
		if(waitQueue.size() != 0){
			boolean waitConflictEdge = false;
			for(int i = 0; i < this.conflictEdgeArray.size(); i++){
				if(this.conflictEdgeArray.get(i).stratNodeNum == this.number || this.conflictEdgeArray.get(i).endNodeNum == this.number){
					for(int j = 0; j < this.conflictEdgeArray.get(i).waitQueue.size(); j++){
						if(this.conflictEdgeArray.get(i).waitQueue.get(j).getAGVNum() == this.waitQueue.get(0).getAGVNum()){
							if(j != 0){
								waitConflictEdge = true;
							}
						}
					}
				}
			}
			if(!waitConflictEdge){
				timerPool.schedule(new TimerTask(){
					public void run(){
						System.out.println( number + "点让" + waitQueue.get(0).getAGVNum()+"AGV走");
						logger.debug( number + "点让" + waitQueue.get(0).getAGVNum()+"AGV走");
						if(rightOrLeft(waitQueue.get(0))){
							waitQueue.get(0).getRunnable().SendActionMessage("CC05DD");//move有问题，如果边冲突还在的话
							waitQueue.get(0).stateString = "命令AGV运行";
						}else{
							waitQueue.get(0).getRunnable().SendActionMessage("CC06DD");
							waitQueue.get(0).stateString = "命令AGV运行";
						}							
					}
				}, 5000, TimeUnit.MILLISECONDS);
				logger.debug("没有或防冲突边第一辆，"+"让"+waitQueue.get(0).getAGVNum()+"号agv前进占用"+this.number+"点");
				//System.out.println("没有或防冲突边第一辆，"+"让"+waitQueue.get(0).getAGVNum()+"号agv前进占用"+this.number+"点");
			}else{
				logger.debug("不是防冲突边第一辆，等待边解除，"+"让"+waitQueue.get(0).getAGVNum()+"号agv前进占用"+this.number+"点");
				//System.out.println("不是防冲突边第一辆，等待边解除，"+"让"+waitQueue.get(0).getAGVNum()+"号agv前进占用"+this.number+"点");
			}	
		}else{
			occupy = false;
			logger.debug(this.number + "点完全解除占用");
			//System.out.println(this.number + "点完全解除占用");
		}	
	}
	
	public boolean rightOrLeft(AGVCar AGV){
		Edge edge = AGV.getStartEdge();
		boolean result = true;
		if(edge.startNode.x == edge.endNode.x){
			if(edge.startNode.y < edge.endNode.y){//下
				if(AGV.getOrientation() == Orientation.DOWN){
					result = true;
				}else if(AGV.getOrientation() == Orientation.UP){
					result = false;
				}
			}else if(edge.startNode.y > edge.endNode.y){//上
				if(AGV.getOrientation() == Orientation.DOWN){
					result = false;
				}else if(AGV.getOrientation() == Orientation.UP){
					result = true;
				}
			}
		}else if(edge.startNode.y == edge.endNode.y){
			if(edge.startNode.x < edge.endNode.x){//右
				if(AGV.getOrientation() == Orientation.LEFT){
					result = false;
				}else if(AGV.getOrientation() == Orientation.RIGTH){
					result = true;
				}
			}else if(edge.startNode.x > edge.endNode.x){//左
				if(AGV.getOrientation() == Orientation.LEFT){
					result = true;
				}else if(AGV.getOrientation() == Orientation.RIGTH){
					result = false;
				}
			}
		}
		return result;
	}
	
	public int getNumber(){
		return this.number;
	}
}
