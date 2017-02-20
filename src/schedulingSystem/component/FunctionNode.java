package schedulingSystem.component;

import java.util.ArrayList;

import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import schedulingSystem.component.AGVCar.State;
import schedulingSystem.toolKit.ReceiveStationMessage;

public class FunctionNode {
	private static Logger logger = Logger.getLogger(AGVCar.class.getName());
	private ReceiveStationMessage receiveStationMessage;
	public enum FunctionNodeEnum{SHIPMENT, UNLOADING, CHARGE, EMPTYCAR, TAG}
	public FunctionNodeEnum function;
	public int nodeNum;
	public int communicationNum;
	public String tag;
	public boolean clicked;
	public int callAGVNum;
	public ArrayList<AGVCar> AGVArray;
	private Dijkstra dijkstra;
	private Graph graph;
	public String ip;
	public int x;
	public int y;
	private int index;
	private int shipmentCount;
	public boolean responsing;
	public boolean chargingStationRetract;
	private int stopInEnterPalletAGVNum;
	private long stopInEnterPalletTime;
	private boolean stopInEnterPallet;
	private ScheduledExecutorService clearTimer;
	private int minChargeIndex;
	public FunctionNode(FunctionNodeEnum function, int nodeNum, String ip, int communicationNum, String tag, int i, ScheduledExecutorService timerPool){
		this.function = function;
		this.nodeNum = nodeNum;
		this.ip = ip;
		this.communicationNum = communicationNum;
		this.tag = tag;
		this.index = i;
		this.chargingStationRetract = true;
		this.callAGVNum = -1;
		clearTimer = timerPool;
	}
	
	public FunctionNode(FunctionNodeEnum function, int x, int y, String tag){
		this.x = x;
		this.y = y;
		this.tag = tag;
	}
	
	public void initFunctionNode(ArrayList<AGVCar> AGVArray, Dijkstra dijkstra, Graph graph){
		this.AGVArray = AGVArray;
		this.dijkstra = dijkstra;
		this.graph = graph;
	}
	
	public void requestMateriel(){//�ò�λҪ��	
		//System.out.println(this.communicationNum + "������Ҫ��");
		if(this.callAGVNum <= 0){
			//System.out.println("this.callAGVNum <= 0"+this.communicationNum + "������Ҫ��");
			requestMaterielSendAGV();
		}else{//AGV������·�ϻ����Ѿ�ͣ��functionNode
			if((AGVArray.get(callAGVNum - 1).getAGVStopInNode() == index )&&(!AGVArray.get(callAGVNum - 1).isOnMission())){//���AGV�Ѿ��ڸõ���û��ִ������
				//���ڸò�λ��AGVȥ���
				
				
				//requestMaterielSendAGV();
			}
		}
	}
	
	public void requestMaterielSendAGV(){
		int materielNodeIndex = -1;
		for(int i = 0; i < graph.getFunctionNodeArray().size(); i++){
			if(this.communicationNum == 17 || this.communicationNum == 18){
				if(graph.getFunctionNodeArray().get(i).communicationNum == 3 
						&& graph.getFunctionNodeArray().get(i).callAGVNum < 0){//ȥ�������������
					materielNodeIndex = i;
				}
			}else if(this.communicationNum == 19 || this.communicationNum == 20 ){
				if(graph.getFunctionNodeArray().get(i).communicationNum == 4
						&& graph.getFunctionNodeArray().get(i).callAGVNum < 0){//ȥ��ط���������
					materielNodeIndex = i;
				}
			}else if(this.communicationNum == 21 || this.communicationNum == 22 ){
				if(graph.getFunctionNodeArray().get(i).communicationNum == 1
						&& graph.getFunctionNodeArray().get(i).callAGVNum < 0){//ȥ�װ����������
					materielNodeIndex = i;
				}
			}else if(this.communicationNum == 23 || this.communicationNum == 24 ){
				if(graph.getFunctionNodeArray().get(i).communicationNum == 2
						&& graph.getFunctionNodeArray().get(i).callAGVNum < 0){//ȥ�������������
					materielNodeIndex = i;
				}
			}
		}
		if( materielNodeIndex >= 0){
			int materielNodeNum = graph.getFunctionNodeArray().get(materielNodeIndex).nodeNum;
			//System.out.println("materielNodeNum = " + materielNodeNum + this.communicationNum + "������Ҫ��");
			//System.out.println(this.communicationNum + "������Ҫ��materielNodeIndex > 0");
			if((this.callAGVNum = sendingWhichAGV(materielNodeNum)) > 0){				
				logger.debug("��"+this.callAGVNum+"AGV"+"ȥ"+this.communicationNum + "����");
				System.out.println("��"+this.callAGVNum+"AGV"+"ȥ"+this.communicationNum);
				AGVCar agvCar= AGVArray.get(this.callAGVNum - 1);
				if(agvCar.getAGVStopInNode() >= 0 && graph.getFunctionNodeArray().get(agvCar.getAGVStopInNode()).nodeNum == materielNodeNum){//��ǲ��AGV�������ϵĵص�
					//δ����
					logger.debug("δ����");
					ArrayList<State> triggerArray = new ArrayList<State>();
					triggerArray.add(State.SHIPMENT);//�ȴ�װ�����
					ArrayList<Integer> destinationArray = new ArrayList<Integer>();
					destinationArray.add(this.nodeNum);//ȥ�ò�λ
					synchronized(agvCar){
						if(agvCar.getStartEdge().endNode.num!=0 && agvCar.initReady
								&& !agvCar.isOnMission() && !agvCar.getFixRoute() && !agvCar.charging
								&& !agvCar.getFixRoute() && !agvCar.ReadyToOffDuty){
							agvCar.setMission(triggerArray, destinationArray);
							agvCar.setSendSignalToAGV(false);
							agvCar.setIsOnMission(true);
							this.responsing = true;
							this.shipmentCount++;
						}
					}										
				}else{
					ArrayList<State> triggerArray = new ArrayList<State>();
					triggerArray.add(State.NULL);//��ȥ������
					triggerArray.add(State.SHIPMENT);//�ٵȴ�װ�����
					ArrayList<Integer> destinationArray = new ArrayList<Integer>();
					destinationArray.add(materielNodeNum);//��ȥ������
					destinationArray.add(this.nodeNum);//��ȥ�ò�λ
					synchronized(agvCar){
						if(agvCar.getStartEdge().endNode.num!=0 && agvCar.initReady
								&& !agvCar.isOnMission() && !agvCar.getFixRoute()&& !agvCar.charging
								&& !agvCar.getFixRoute()&& !agvCar.ReadyToOffDuty){
							agvCar.setMission(triggerArray, destinationArray);
							this.responsing = true;
							this.shipmentCount++;
						}
					}
					
				}
			}
		}
	}
	
	public void materielUsedUp(){//��������
		//System.out.println(this.communicationNum + "�������꣬��������");
		int emptyCarNode = -1;
		for(int i = 0; i < graph.getFunctionNodeArray().size(); i++){
			if(graph.getFunctionNodeArray().get(i).function == FunctionNodeEnum.EMPTYCAR){//ȥ�����̷�����
				emptyCarNode = graph.getFunctionNodeArray().get(i).nodeNum;
			}
			if(emptyCarNode > 0){
				break;
			}
		}

		if(this.callAGVNum <= 0){
			if((this.callAGVNum = sendingWhichAGV(this.nodeNum)) > 0){				
				logger.debug("��"+this.callAGVNum+"AGV"+"ȥ"+this.communicationNum + "��������");
				AGVCar agvCar= AGVArray.get(this.callAGVNum - 1);
				ArrayList<State> triggerArray = new ArrayList<State>();
				triggerArray.add(State.NULL);//��ȥ�õ�
				triggerArray.add(State.SHIPMENT);//�ٵȴ�װ�����
				ArrayList<Integer> destinationArray = new ArrayList<Integer>();
				destinationArray.add(this.nodeNum);//��ȥ�õ�
				destinationArray.add(emptyCarNode);//��ȥ���õ�
				synchronized(agvCar){
					if(agvCar.getStartEdge().endNode.num!=0 && agvCar.initReady
							&& !agvCar.isOnMission() && !agvCar.getFixRoute()&& !agvCar.charging
							&& !agvCar.getFixRoute()&& !agvCar.ReadyToOffDuty){
						agvCar.setMission(triggerArray, destinationArray);
						this.responsing = true;
					}
				}
			}
		}else{//AGV������·�ϻ����Ѿ�ͣ��functionNode
			if((AGVArray.get(callAGVNum - 1).getAGVStopInNode() == index )&&(!AGVArray.get(callAGVNum - 1).isOnMission())){//���AGV�Ѿ��ڸõ���û��ִ������
				AGVCar agvCar= AGVArray.get(this.callAGVNum - 1);
				ArrayList<State> triggerArray = new ArrayList<State>();
				triggerArray.add(State.SHIPMENT);//�ȴ�װ�����
				ArrayList<Integer> destinationArray = new ArrayList<Integer>();
				destinationArray.add(emptyCarNode);//ȥ���õ�
				synchronized(agvCar){
					if(agvCar.getStartEdge().endNode.num!=0 && agvCar.initReady
							&& !agvCar.isOnMission() && !agvCar.getFixRoute()&& !agvCar.charging
							&& !agvCar.getFixRoute()&& !agvCar.ReadyToOffDuty){
						agvCar.setMission(triggerArray, destinationArray);
						agvCar.setIsOnMission(true);
						this.responsing = true;
						if(this.communicationNum < 16){//���߸Ĳ�λС����λ//ͬʱAGV��Ͳת��
							logger.debug(this.communicationNum + "AGV��λ");
							this.receiveStationMessage.SendMessage("CC0"+Integer.toHexString(this.communicationNum)+"07DD", this.callAGVNum);
						}else {
							logger.debug(this.communicationNum + "AGV��λ");
							this.receiveStationMessage.SendMessage("CC"+Integer.toHexString(this.communicationNum)+"07DD", this.callAGVNum);
						}
						agvCar.getRunnable().SendActionMessage("CC02DD");
						logger.debug(this.communicationNum + "AGV����");
					}
				}				
			}
		}
		
	}
	
	public void finishedEnterPallet(){//���������,����
		
	}
	
	public void readyToShipment(){//����׼����
		//System.out.println(this.communicationNum + "����׼����");
		for(int i = 0; i < AGVArray.size(); i++){
			if(AGVArray.get(i).getAGVStopInNode() == index){
				if(AGVArray.get(i).getTrigger().size() > 0){
					if((AGVArray.get(i).getTrigger().get(0) == State.SHIPMENT) && !AGVArray.get(i).getSendSignalToAGV()){//����õ��û�з��͹��źŸ�AGV
						AGVArray.get(i).setSendSignalToAGV(true);
						AGVArray.get(i).getRunnable().SendActionMessage("CC02DD");
						logger.debug(this.callAGVNum + "AGV����");
						
						if(this.communicationNum < 16){
							logger.debug(AGVArray.get(i).getAGVNum() + "AGV����Ҫ��");
							receiveStationMessage.SendMessage("CC0"+Integer.toHexString(this.communicationNum)+"01DD", AGVArray.get(i).getAGVNum());//����Ҫ��
						}else {
							logger.debug(AGVArray.get(i).getAGVNum() + "AGV����Ҫ��");
							receiveStationMessage.SendMessage("CC"+Integer.toHexString(this.communicationNum)+"01DD", AGVArray.get(i).getAGVNum());
						}
					}
				}
				break;
			}
		}
	}
	
	public void allowToEnterPallet(){
		//System.out.println("�����������������");
		for(int i = 0; i < AGVArray.size(); i++){
			if(AGVArray.get(i).getAGVStopInNode() == index){
				this.stopInEnterPallet = true;
				if(!AGVArray.get(i).getSendSignalToAGV()){//����õ��û�з��͹��źŸ�AGV
					AGVArray.get(i).setSendSignalToAGV(true);
					if(this.communicationNum < 16){
						logger.debug(this.callAGVNum + "AGV���������");
						this.receiveStationMessage.SendMessage("CC0"+Integer.toHexString(this.communicationNum)+"03DD", AGVArray.get(i).getAGVNum());//�����������
					}else {
						logger.debug(this.callAGVNum + "AGV���������");
						this.receiveStationMessage.SendMessage("CC"+Integer.toHexString(this.communicationNum)+"03DD", AGVArray.get(i).getAGVNum());
					}
					AGVArray.get(i).getRunnable().SendActionMessage("CC03DD");
					logger.debug(this.callAGVNum + "AGVж��");
					break;
				}
				
				if(this.stopInEnterPalletAGVNum == AGVArray.get(i).getAGVNum()){
					if(System.currentTimeMillis() - this.stopInEnterPalletTime > 30000 && !AGVArray.get(i).isOnMission()){
						this.stopInEnterPalletTime = System.currentTimeMillis() - 20000;
						synchronized(this){
							minChargeIndex = i;
							for(int m = 0; m < graph.getFunctionNodeArray().size(); m++){
								if(graph.getFunctionNodeArray().get(m).function == FunctionNodeEnum.CHARGE
										&& !graph.getFunctionNodeArray().get(m).chargingStationRetract
										&& graph.getFunctionNodeArray().get(m).callAGVNum <= 0
										&& !graph.getFunctionNodeArray().get(m).responsing){
									graph.getFunctionNodeArray().get(m).chargingStationRetract = true;
								}
							}
							clearTimer.schedule(new TimerTask(){
								public void run(){
									int chargeNode = -1;
									for(int i = graph.getFunctionNodeArray().size() - 1; i >= 0 ; i--){
										if(graph.getFunctionNodeArray().get(i).function == FunctionNodeEnum.CHARGE
												&& !graph.getFunctionNodeArray().get(i).chargingStationRetract
												&& graph.getFunctionNodeArray().get(i).callAGVNum <= 0
												&& !graph.getFunctionNodeArray().get(i).responsing){
											chargeNode = graph.getFunctionNodeArray().get(i).nodeNum;
											graph.getFunctionNodeArray().get(i).responsing = true;
											break;
										}
									}
									if(chargeNode > 0){
										System.out.println(AGVArray.get(minChargeIndex).getAGVNum() + "AGVû������ȥ���");
										logger.debug(AGVArray.get(minChargeIndex).getAGVNum() + "AGVû������ȥ���");
										ArrayList<State> triggerArray = new ArrayList<State>();
										triggerArray.add(State.NULL);
										ArrayList<Integer> destinationArray = new ArrayList<Integer>();
										destinationArray.add(chargeNode);
										synchronized(AGVArray.get(minChargeIndex)){
											if(AGVArray.get(minChargeIndex).getStartEdge().endNode.num!=0 && AGVArray.get(minChargeIndex).initReady
													&& !AGVArray.get(minChargeIndex).isOnMission() && !AGVArray.get(minChargeIndex).getFixRoute()
													&& !AGVArray.get(minChargeIndex).charging && !AGVArray.get(minChargeIndex).ReadyToOffDuty){
												AGVArray.get(minChargeIndex).setMission(triggerArray, destinationArray);
												AGVArray.get(minChargeIndex).charging = true;
												AGVArray.get(minChargeIndex).chargeCount++;
											}else{
												System.out.println("��縴λ��ʱ5S��AGV�Ѿ��������ˣ�����ȥ�����");
												logger.debug("��縴λ��ʱ5S��AGV�Ѿ��������ˣ�����ȥ�����");										
											}
										}																		
									}else{
										System.out.println("�޳��׮����");
										logger.debug("�޳��׮����");									
									}	
									minChargeIndex = -1;
								}
							}, 5000, TimeUnit.MILLISECONDS);	
						}												
					}
				}else if(this.stopInEnterPalletAGVNum != AGVArray.get(i).getAGVNum()){
					this.stopInEnterPalletTime = System.currentTimeMillis();
					this.stopInEnterPalletAGVNum = AGVArray.get(i).getAGVNum();
				}
			}
		}
		if(!this.stopInEnterPallet){
			this.stopInEnterPalletAGVNum = 0;
		}else {
			this.stopInEnterPallet = false;
		}
	}
	
	public void allowUnloading(){
		//logger.debug("����������ж��");
		for(int i = 0; i < AGVArray.size(); i++){
			if(AGVArray.get(i).getAGVStopInNode() == index){
				if(!AGVArray.get(i).getSendSignalToAGV()){//����õ��û�з��͹��źŸ�AGV
					AGVArray.get(i).setSendSignalToAGV(true);
					
					AGVArray.get(i).getRunnable().SendActionMessage("CC03DD");
					logger.debug(this.callAGVNum + "AGVж��");
					break;
				}
				
			}
		}
	}
	
	public void recoveredPallet(){//����
		
	}
	
	public void chargingStationRetract(){//���׮�����
		this.chargingStationRetract = true;
	}
	public void chargingStationRetractBack(){
		this.chargingStationRetract = false;
	}
	
	

	private int sendingWhichAGV(int endNodeNum) {
		ArrayList<Path> pathArray = new ArrayList<Path>();
		for(int i = 0; i < AGVArray.size(); i++){			
			if(AGVArray.get(i).getStartEdge().endNode.num!=0 && AGVArray.get(i).initReady
					&& !AGVArray.get(i).isOnMission() && !AGVArray.get(i).getFixRoute()&& !AGVArray.get(i).charging
					&& !AGVArray.get(i).getFixRoute()&& !AGVArray.get(i).ReadyToOffDuty){
				pathArray.add(dijkstra.findRoute(AGVArray.get(i).getStartEdge(), endNodeNum));
				pathArray.get(pathArray.size()-1).setNumOfAGV(i+1);
			}
		}
		
		if(pathArray.size() != 0){
			int minDis = 65535;
			int minIndex = 0;
			for(int i = 0; i < pathArray.size(); i++){
				if(pathArray.get(i).getRealDis() < minDis && pathArray.get(i).getRoute().size() > 2){
					minDis = pathArray.get(i).getRealDis();
					minIndex = i;
				}
			}
			return pathArray.get(minIndex).getNumOfAGV();
		}else{
			return -1;
		}
	}
	
	public void setReceiveStationMessage(ReceiveStationMessage receiveStationMessage){
		this.receiveStationMessage = receiveStationMessage;
	}
	
	public ReceiveStationMessage getReceiveStationMessage(){
		return this.receiveStationMessage;
	}
	
	public int getShipmentCount(){
		return this.shipmentCount;
	}
}
