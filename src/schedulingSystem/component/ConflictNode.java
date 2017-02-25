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
				logger.debug(agvCar.getAGVNum() + "agv���ռ��" + this.number+ "��");
			//	System.out.println(agvCar.getAGVNum() + "agv���ռ��" + this.number+ "��");
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
						System.out.println( number + "����" + waitQueue.get(0).getAGVNum()+"AGV��");
						logger.debug( number + "����" + waitQueue.get(0).getAGVNum()+"AGV��");
						if(rightOrLeft(waitQueue.get(0))){
							waitQueue.get(0).getRunnable().SendActionMessage("CC05DD");//move�����⣬����߳�ͻ���ڵĻ�
							waitQueue.get(0).stateString = "����AGV����";
						}else{
							waitQueue.get(0).getRunnable().SendActionMessage("CC06DD");
							waitQueue.get(0).stateString = "����AGV����";
						}							
					}
				}, 5000, TimeUnit.MILLISECONDS);
				logger.debug("û�л����ͻ�ߵ�һ����"+"��"+waitQueue.get(0).getAGVNum()+"��agvǰ��ռ��"+this.number+"��");
				//System.out.println("û�л����ͻ�ߵ�һ����"+"��"+waitQueue.get(0).getAGVNum()+"��agvǰ��ռ��"+this.number+"��");
			}else{
				logger.debug("���Ƿ���ͻ�ߵ�һ�����ȴ��߽����"+"��"+waitQueue.get(0).getAGVNum()+"��agvǰ��ռ��"+this.number+"��");
				//System.out.println("���Ƿ���ͻ�ߵ�һ�����ȴ��߽����"+"��"+waitQueue.get(0).getAGVNum()+"��agvǰ��ռ��"+this.number+"��");
			}	
		}else{
			occupy = false;
			logger.debug(this.number + "����ȫ���ռ��");
			//System.out.println(this.number + "����ȫ���ռ��");
		}	
	}
	
	public boolean rightOrLeft(AGVCar AGV){
		Edge edge = AGV.getStartEdge();
		boolean result = true;
		if(edge.startNode.x == edge.endNode.x){
			if(edge.startNode.y < edge.endNode.y){//��
				if(AGV.getOrientation() == Orientation.DOWN){
					result = true;
				}else if(AGV.getOrientation() == Orientation.UP){
					result = false;
				}
			}else if(edge.startNode.y > edge.endNode.y){//��
				if(AGV.getOrientation() == Orientation.DOWN){
					result = false;
				}else if(AGV.getOrientation() == Orientation.UP){
					result = true;
				}
			}
		}else if(edge.startNode.y == edge.endNode.y){
			if(edge.startNode.x < edge.endNode.x){//��
				if(AGV.getOrientation() == Orientation.LEFT){
					result = false;
				}else if(AGV.getOrientation() == Orientation.RIGTH){
					result = true;
				}
			}else if(edge.startNode.x > edge.endNode.x){//��
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
