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
	
	public void requestMateriel(){//该仓位要料	
		//System.out.println(this.communicationNum + "仓请求要料");
		if(this.callAGVNum <= 0){
			//System.out.println("this.callAGVNum <= 0"+this.communicationNum + "仓请求要料");
			requestMaterielSendAGV();
		}else{//AGV在来的路上或是已经停在functionNode
			if((AGVArray.get(callAGVNum - 1).getAGVStopInNode() == index )&&(!AGVArray.get(callAGVNum - 1).isOnMission())){//如果AGV已经在该点且没有执行任务
				//派在该仓位的AGV去充电
				
				
				//requestMaterielSendAGV();
			}
		}
	}
	
	public void requestMaterielSendAGV(){
		int materielNodeIndex = -1;
		for(int i = 0; i < graph.getFunctionNodeArray().size(); i++){
			if(this.communicationNum == 17 || this.communicationNum == 18){
				if(graph.getFunctionNodeArray().get(i).communicationNum == 3 
						&& graph.getFunctionNodeArray().get(i).callAGVNum < 0){//去隔板放置区拉料
					materielNodeIndex = i;
				}
			}else if(this.communicationNum == 19 || this.communicationNum == 20 ){
				if(graph.getFunctionNodeArray().get(i).communicationNum == 4
						&& graph.getFunctionNodeArray().get(i).callAGVNum < 0){//去电池放置区拉料
					materielNodeIndex = i;
				}
			}else if(this.communicationNum == 21 || this.communicationNum == 22 ){
				if(graph.getFunctionNodeArray().get(i).communicationNum == 1
						&& graph.getFunctionNodeArray().get(i).callAGVNum < 0){//去底板放置区拉料
					materielNodeIndex = i;
				}
			}else if(this.communicationNum == 23 || this.communicationNum == 24 ){
				if(graph.getFunctionNodeArray().get(i).communicationNum == 2
						&& graph.getFunctionNodeArray().get(i).callAGVNum < 0){//去顶板放置区拉料
					materielNodeIndex = i;
				}
			}
		}
		if( materielNodeIndex >= 0){
			int materielNodeNum = graph.getFunctionNodeArray().get(materielNodeIndex).nodeNum;
			//System.out.println("materielNodeNum = " + materielNodeNum + this.communicationNum + "仓请求要料");
			//System.out.println(this.communicationNum + "仓请求要料materielNodeIndex > 0");
			if((this.callAGVNum = sendingWhichAGV(materielNodeNum)) > 0){				
				logger.debug("派"+this.callAGVNum+"AGV"+"去"+this.communicationNum + "上料");
				System.out.println("派"+this.callAGVNum+"AGV"+"去"+this.communicationNum);
				AGVCar agvCar= AGVArray.get(this.callAGVNum - 1);
				if(agvCar.getAGVStopInNode() >= 0 && graph.getFunctionNodeArray().get(agvCar.getAGVStopInNode()).nodeNum == materielNodeNum){//派遣的AGV在拉物料的地点
					//未测试
					logger.debug("未测试");
					ArrayList<State> triggerArray = new ArrayList<State>();
					triggerArray.add(State.SHIPMENT);//等待装货完成
					ArrayList<Integer> destinationArray = new ArrayList<Integer>();
					destinationArray.add(this.nodeNum);//去该仓位
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
					triggerArray.add(State.NULL);//先去拉物料
					triggerArray.add(State.SHIPMENT);//再等待装货完成
					ArrayList<Integer> destinationArray = new ArrayList<Integer>();
					destinationArray.add(materielNodeNum);//先去拉物料
					destinationArray.add(this.nodeNum);//再去该仓位
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
	
	public void materielUsedUp(){//拉空托盘
		//System.out.println(this.communicationNum + "料已用完，拉空托盘");
		int emptyCarNode = -1;
		for(int i = 0; i < graph.getFunctionNodeArray().size(); i++){
			if(graph.getFunctionNodeArray().get(i).function == FunctionNodeEnum.EMPTYCAR){//去空托盘放置区
				emptyCarNode = graph.getFunctionNodeArray().get(i).nodeNum;
			}
			if(emptyCarNode > 0){
				break;
			}
		}

		if(this.callAGVNum <= 0){
			if((this.callAGVNum = sendingWhichAGV(this.nodeNum)) > 0){				
				logger.debug("派"+this.callAGVNum+"AGV"+"去"+this.communicationNum + "拉空托盘");
				AGVCar agvCar= AGVArray.get(this.callAGVNum - 1);
				ArrayList<State> triggerArray = new ArrayList<State>();
				triggerArray.add(State.NULL);//先去该点
				triggerArray.add(State.SHIPMENT);//再等待装货完成
				ArrayList<Integer> destinationArray = new ArrayList<Integer>();
				destinationArray.add(this.nodeNum);//先去该点
				destinationArray.add(emptyCarNode);//再去放置点
				synchronized(agvCar){
					if(agvCar.getStartEdge().endNode.num!=0 && agvCar.initReady
							&& !agvCar.isOnMission() && !agvCar.getFixRoute()&& !agvCar.charging
							&& !agvCar.getFixRoute()&& !agvCar.ReadyToOffDuty){
						agvCar.setMission(triggerArray, destinationArray);
						this.responsing = true;
					}
				}
			}
		}else{//AGV在来的路上或是已经停在functionNode
			if((AGVArray.get(callAGVNum - 1).getAGVStopInNode() == index )&&(!AGVArray.get(callAGVNum - 1).isOnMission())){//如果AGV已经在该点且没有执行任务
				AGVCar agvCar= AGVArray.get(this.callAGVNum - 1);
				ArrayList<State> triggerArray = new ArrayList<State>();
				triggerArray.add(State.SHIPMENT);//等待装货完成
				ArrayList<Integer> destinationArray = new ArrayList<Integer>();
				destinationArray.add(emptyCarNode);//去放置点
				synchronized(agvCar){
					if(agvCar.getStartEdge().endNode.num!=0 && agvCar.initReady
							&& !agvCar.isOnMission() && !agvCar.getFixRoute()&& !agvCar.charging
							&& !agvCar.getFixRoute()&& !agvCar.ReadyToOffDuty){
						agvCar.setMission(triggerArray, destinationArray);
						agvCar.setIsOnMission(true);
						this.responsing = true;
						if(this.communicationNum < 16){//告诉改仓位小车到位//同时AGV滚筒转动
							logger.debug(this.communicationNum + "AGV到位");
							this.receiveStationMessage.SendMessage("CC0"+Integer.toHexString(this.communicationNum)+"07DD", this.callAGVNum);
						}else {
							logger.debug(this.communicationNum + "AGV到位");
							this.receiveStationMessage.SendMessage("CC"+Integer.toHexString(this.communicationNum)+"07DD", this.callAGVNum);
						}
						agvCar.getRunnable().SendActionMessage("CC02DD");
						logger.debug(this.communicationNum + "AGV上料");
					}
				}				
			}
		}
		
	}
	
	public void finishedEnterPallet(){//进托盘完成,不用
		
	}
	
	public void readyToShipment(){//上料准备好
		//System.out.println(this.communicationNum + "上料准备好");
		for(int i = 0; i < AGVArray.size(); i++){
			if(AGVArray.get(i).getAGVStopInNode() == index){
				if(AGVArray.get(i).getTrigger().size() > 0){
					if((AGVArray.get(i).getTrigger().get(0) == State.SHIPMENT) && !AGVArray.get(i).getSendSignalToAGV()){//到达该点后没有发送过信号给AGV
						AGVArray.get(i).setSendSignalToAGV(true);
						AGVArray.get(i).getRunnable().SendActionMessage("CC02DD");
						logger.debug(this.callAGVNum + "AGV上料");
						
						if(this.communicationNum < 16){
							logger.debug(AGVArray.get(i).getAGVNum() + "AGV请求要料");
							receiveStationMessage.SendMessage("CC0"+Integer.toHexString(this.communicationNum)+"01DD", AGVArray.get(i).getAGVNum());//请求要料
						}else {
							logger.debug(AGVArray.get(i).getAGVNum() + "AGV请求要料");
							receiveStationMessage.SendMessage("CC"+Integer.toHexString(this.communicationNum)+"01DD", AGVArray.get(i).getAGVNum());
						}
					}
				}
				break;
			}
		}
	}
	
	public void allowToEnterPallet(){
		//System.out.println("空托盘区允许进托盘");
		for(int i = 0; i < AGVArray.size(); i++){
			if(AGVArray.get(i).getAGVStopInNode() == index){
				this.stopInEnterPallet = true;
				if(!AGVArray.get(i).getSendSignalToAGV()){//到达该点后没有发送过信号给AGV
					AGVArray.get(i).setSendSignalToAGV(true);
					if(this.communicationNum < 16){
						logger.debug(this.callAGVNum + "AGV请求进托盘");
						this.receiveStationMessage.SendMessage("CC0"+Integer.toHexString(this.communicationNum)+"03DD", AGVArray.get(i).getAGVNum());//请求回收托盘
					}else {
						logger.debug(this.callAGVNum + "AGV请求进托盘");
						this.receiveStationMessage.SendMessage("CC"+Integer.toHexString(this.communicationNum)+"03DD", AGVArray.get(i).getAGVNum());
					}
					AGVArray.get(i).getRunnable().SendActionMessage("CC03DD");
					logger.debug(this.callAGVNum + "AGV卸料");
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
										System.out.println(AGVArray.get(minChargeIndex).getAGVNum() + "AGV没有任务，去充电");
										logger.debug(AGVArray.get(minChargeIndex).getAGVNum() + "AGV没有任务，去充电");
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
												System.out.println("充电复位延时5S，AGV已经有任务了，不能去充电了");
												logger.debug("充电复位延时5S，AGV已经有任务了，不能去充电了");										
											}
										}																		
									}else{
										System.out.println("无充电桩可用");
										logger.debug("无充电桩可用");									
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
		//logger.debug("工作区允许卸料");
		for(int i = 0; i < AGVArray.size(); i++){
			if(AGVArray.get(i).getAGVStopInNode() == index){
				if(!AGVArray.get(i).getSendSignalToAGV()){//到达该点后没有发送过信号给AGV
					AGVArray.get(i).setSendSignalToAGV(true);
					
					AGVArray.get(i).getRunnable().SendActionMessage("CC03DD");
					logger.debug(this.callAGVNum + "AGV卸料");
					break;
				}
				
			}
		}
	}
	
	public void recoveredPallet(){//不用
		
	}
	
	public void chargingStationRetract(){//充电桩伸出了
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
