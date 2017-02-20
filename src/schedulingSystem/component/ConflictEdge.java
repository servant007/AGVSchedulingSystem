package schedulingSystem.component;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import schedulingSystem.component.AGVCar.Orientation;

public class ConflictEdge {
	private static Logger logger = Logger.getLogger(AGVCar.class.getName());
	public int stratNodeNum;
	public int endNodeNum;
	private ScheduledExecutorService timerPool;
	volatile public boolean occupy;
	volatile public ArrayList<AGVCar> waitQueue;
	public int waitNodeNum;
	
	public ConflictEdge(int start, int end, ScheduledExecutorService timerPool){
		this.stratNodeNum = start;
		this.endNodeNum = end;
		this.waitQueue = new ArrayList<AGVCar>();
		this.timerPool = timerPool;
	}
	
	public void removeAGV(AGVCar agvCar, ConflictNode conflictNode){
		for(int i = 0; i < waitQueue.size(); i++){
			if(waitQueue.get(i).getAGVNum() == agvCar.getAGVNum())
				waitQueue.remove(i);
				logger.debug(agvCar.getAGVNum() + "agv解除占用" + this.stratNodeNum+ "||" + this.endNodeNum + "边");
				//System.out.println(agvCar.getAGVNum() + "agv解除占用" + this.stratNodeNum+ "||" + this.endNodeNum + "边");
		}
		if(waitQueue.size() == 0){
			occupy = false;
			logger.debug(this.stratNodeNum+ "||" + this.endNodeNum + "边" + "点完全解除占用");
			//System.out.println(this.stratNodeNum+ "||" + this.endNodeNum + "边" + "点完全解除占用");
		}else{
			if(conflictNode != null){
				if(conflictNode.waitQueue.size() > 0){
					if(conflictNode.waitQueue.get(0).getAGVNum() == waitQueue.get(0).getAGVNum()){//排在防冲突点第一个则走
						timerPool.schedule(new TimerTask(){
							public void run(){
								System.out.println( stratNodeNum+ "||" + endNodeNum + "边让" + waitQueue.get(0).getAGVNum()+"AGV走");
								logger.debug( stratNodeNum+ "||" + endNodeNum + "边让" + waitQueue.get(0).getAGVNum()+"AGV走");
								if(rightOrLeft(waitQueue.get(0)))
									waitQueue.get(0).getRunnable().SendActionMessage("CC05DD");//有问题如果这台agv在等点解占用，应继续等待
								else
									waitQueue.get(0).getRunnable().SendActionMessage("CC06DD");
							}
						}, 2000, TimeUnit.MILLISECONDS);
						logger.debug("防冲突点的第一辆," + "让"+waitQueue.get(0).getAGVNum()+"号agv前进占用" + this.stratNodeNum+ "||" + this.endNodeNum + "边" );
					}else{
						logger.debug("不是防冲突点的第一辆,边解除,但不让走, conflictNode waitQuene 0:"+conflictNode.waitQueue.get(0) + "agv:" + waitQueue.get(0).getAGVNum());
					}
				}
				
			}else{
				timerPool.schedule(new TimerTask(){
					public void run(){
						System.out.println( stratNodeNum+ "||" + endNodeNum + "边让" + waitQueue.get(0).getAGVNum()+"AGV走");
						logger.debug( stratNodeNum+ "||" + endNodeNum + "边让" + waitQueue.get(0).getAGVNum()+"AGV走");
						if(rightOrLeft(waitQueue.get(0)))
							waitQueue.get(0).getRunnable().SendActionMessage("CC05DD");//有问题如果这台agv在等点解占用，应继续等待
						else
							waitQueue.get(0).getRunnable().SendActionMessage("CC06DD");
					}
				}, 2000, TimeUnit.MILLISECONDS);
				logger.debug("让"+waitQueue.get(0).getAGVNum()+"号agv前进占用" + this.stratNodeNum+ "||" + this.endNodeNum + "边" );
			}
			
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
}
