package schedulingSystem.component;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ConflictEdge {
	public int stratNodeNum;
	public int endNodeNum;
	private Timer timer;
	public boolean occupy;
	public ArrayList<AGVCar> waitQueue;
	
	public ConflictEdge(int start, int end){
		this.stratNodeNum = start;
		this.endNodeNum = end;
		waitQueue = new ArrayList<AGVCar>();
		timer = new Timer();
	}
	
	public void removeAGV(AGVCar agvCar){
		for(int i = 0; i < waitQueue.size(); i++){
			if(waitQueue.get(i).getAGVNum() == agvCar.getAGVNum())
				waitQueue.remove(i);
				System.out.println(agvCar.getAGVNum() + "agv���ռ��" + this.stratNodeNum+ "||" + this.endNodeNum + "��");
		}
		if(waitQueue.size() == 0){
			occupy = false;
			System.out.println(this.stratNodeNum+ "||" + this.endNodeNum + "��" + "����ȫ���ռ��");
		}else{
			timer.schedule(new TimerTask(){
				public void run(){
					waitQueue.get(0).getRunnable().SendMessage("CC02DD");//move
				}
			}, 2000);
			System.out.println("��"+waitQueue.get(0).getAGVNum()+"��agvǰ��ռ��" + this.stratNodeNum+ "||" + this.endNodeNum + "��");
		}
	}
}
