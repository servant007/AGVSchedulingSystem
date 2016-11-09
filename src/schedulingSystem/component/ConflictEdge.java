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
				System.out.println(agvCar.getAGVNum() + "agv解除占用" + this.stratNodeNum+ "||" + this.endNodeNum + "边");
		}
		if(waitQueue.size() == 0){
			occupy = false;
			System.out.println(this.stratNodeNum+ "||" + this.endNodeNum + "边" + "点完全解除占用");
		}else{
			timer.schedule(new TimerTask(){
				public void run(){
					waitQueue.get(0).getRunnable().SendMessage("CC02DD");//move
				}
			}, 2000);
			System.out.println("让"+waitQueue.get(0).getAGVNum()+"号agv前进占用" + this.stratNodeNum+ "||" + this.endNodeNum + "边");
		}
	}
}
