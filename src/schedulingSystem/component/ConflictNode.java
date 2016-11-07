package schedulingSystem.component;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class ConflictNode {
	private int number;
	private Timer timer;
	public boolean occupy;
	public ArrayList<Edge> adjoinEdge;
	public ArrayList<AGVCar> waitQueue;
	
	
	public ConflictNode(int number){
		this.number = number;
		waitQueue = new ArrayList<AGVCar>();
		adjoinEdge = new ArrayList<Edge>();
		timer = new Timer();
	}
	
	public void removeAGV(AGVCar agvCar){
		for(int i = 0; i < waitQueue.size(); i++){
			if(waitQueue.get(i).getAGVNum() == agvCar.getAGVNum())
				waitQueue.remove(i);
				System.out.println(agvCar.getAGVNum() + "agv���ռ��" + this.number+ "��");
		}
		if(waitQueue.size() == 0){
			occupy = false;
			System.out.println(this.number + "����ȫ���ռ��");
		}else{
			timer.schedule(new TimerTask(){
				public void run(){
					waitQueue.get(0).getRunnable().SendMessage("CC02DD");//move
				}
			}, 2000);
			System.out.println("��"+waitQueue.get(0).getAGVNum()+"��agvǰ��ռ��"+this.number+"��");
		}
			
	}
}
