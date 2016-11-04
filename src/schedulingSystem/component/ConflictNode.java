package schedulingSystem.component;

import java.util.ArrayList;

public class ConflictNode {
	private int number;
	public boolean occupy;
	public ArrayList<Edge> adjoinEdge;
	public ArrayList<AGVCar> waitQueue;
	public ConflictNode(int number){
		this.number = number;
		waitQueue = new ArrayList<AGVCar>();
		adjoinEdge = new ArrayList<Edge>();
	}
	
	public void removeAGV(AGVCar agvCar){
		for(int i = 0; i < waitQueue.size(); i++){
			if(waitQueue.get(i).getNumber() == agvCar.getNumber())
				waitQueue.remove(i);
				System.out.println(agvCar.getNumber() + "�Ž��ռ��" + this.number+ "��");
		}
		if(waitQueue.size() == 0){
			occupy = false;
			System.out.println(this.number + "����ȫ���ռ��");
		}else{
			//waitQueue.get(0).getRunnable().SendMessage("move");//move
			System.out.println("��"+waitQueue.get(0).getNumber()+"��ǰ��ռ��"+this.number+"��");
		}
			
	}
}
