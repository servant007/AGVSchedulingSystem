package schedulingSystem.component;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import schedulingSystem.component.AGVCar.Orientation;

public class ConflictEdge {
	private static Logger logger = Logger.getLogger(AGVCar.class.getName());
	public int stratNodeNum;
	public int endNodeNum;
	private Timer timer;
	volatile public boolean occupy;
	volatile public ArrayList<AGVCar> waitQueue;
	public int waitNodeNum;
	
	public ConflictEdge(int start, int end){
		this.stratNodeNum = start;
		this.endNodeNum = end;
		waitQueue = new ArrayList<AGVCar>();
		timer = new Timer();
	}
	
	public void removeAGV(AGVCar agvCar, ConflictNode conflictNode){
		for(int i = 0; i < waitQueue.size(); i++){
			if(waitQueue.get(i).getAGVNum() == agvCar.getAGVNum())
				waitQueue.remove(i);
				logger.debug(agvCar.getAGVNum() + "agv���ռ��" + this.stratNodeNum+ "||" + this.endNodeNum + "��");
				//System.out.println(agvCar.getAGVNum() + "agv���ռ��" + this.stratNodeNum+ "||" + this.endNodeNum + "��");
		}
		if(waitQueue.size() == 0){
			occupy = false;
			logger.debug(this.stratNodeNum+ "||" + this.endNodeNum + "��" + "����ȫ���ռ��");
			//System.out.println(this.stratNodeNum+ "||" + this.endNodeNum + "��" + "����ȫ���ռ��");
		}else{
			if(conflictNode != null){
				if(conflictNode.waitQueue.size() > 0){
					if(conflictNode.waitQueue.get(0).getAGVNum() == waitQueue.get(0).getAGVNum()){//���ڷ���ͻ���һ������
						timer.schedule(new TimerTask(){
							public void run(){
								System.out.println( stratNodeNum+ "||" + endNodeNum + "����" + waitQueue.get(0).getAGVNum()+"AGV��");
								logger.debug( stratNodeNum+ "||" + endNodeNum + "����" + waitQueue.get(0).getAGVNum()+"AGV��");
								if(rightOrLeft(waitQueue.get(0)))
									waitQueue.get(0).getRunnable().SendActionMessage("CC05DD");//�����������̨agv�ڵȵ��ռ�ã�Ӧ�����ȴ�
								else
									waitQueue.get(0).getRunnable().SendActionMessage("CC06DD");
							}
						}, 2000);
						timer = new Timer();
						logger.debug("����ͻ��ĵ�һ��," + "��"+waitQueue.get(0).getAGVNum()+"��agvǰ��ռ��" + this.stratNodeNum+ "||" + this.endNodeNum + "��" );
					}else{
						logger.debug("���Ƿ���ͻ��ĵ�һ��,�߽��,��������, conflictNode waitQuene 0:"+conflictNode.waitQueue.get(0) + "agv:" + waitQueue.get(0).getAGVNum());
					}
				}
				
			}else{
				timer.schedule(new TimerTask(){
					public void run(){
						System.out.println( stratNodeNum+ "||" + endNodeNum + "����" + waitQueue.get(0).getAGVNum()+"AGV��");
						logger.debug( stratNodeNum+ "||" + endNodeNum + "����" + waitQueue.get(0).getAGVNum()+"AGV��");
						if(rightOrLeft(waitQueue.get(0)))
							waitQueue.get(0).getRunnable().SendActionMessage("CC05DD");//�����������̨agv�ڵȵ��ռ�ã�Ӧ�����ȴ�
						else
							waitQueue.get(0).getRunnable().SendActionMessage("CC06DD");
					}
				}, 2000);
				timer = new Timer();
				logger.debug("��"+waitQueue.get(0).getAGVNum()+"��agvǰ��ռ��" + this.stratNodeNum+ "||" + this.endNodeNum + "��" );
			}
			
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
}
