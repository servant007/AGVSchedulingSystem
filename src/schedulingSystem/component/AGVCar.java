package schedulingSystem.component;

import java.util.ArrayList;

import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import schedulingSystem.component.AGVCar.State;
import schedulingSystem.component.FunctionNode.FunctionNodeEnum;
import schedulingSystem.toolKit.MyToolKit;
import schedulingSystem.toolKit.ReceiveAGVMessage;

public class AGVCar{
		private static Logger logger = Logger.getLogger(AGVCar.class.getName());
		private int AGVNum;
		private int x = -20;
		private int y = -20;
		private Edge edge;
		private int lastCard;
		public enum Orientation{LEFT,RIGTH,UP,DOWN}
		private Orientation orientation;
		public enum State{STOP, FORWARD, BACKWARD, SHIPMENT, UNLOADING, NULL}
		private State state;
		private Graph graph;
		private int electricity;
		private boolean finishEdge;
		private long lastCommunicationTime;
		private ReceiveAGVMessage receiveAGVMessage;
		private ConflictDetection conflictDetection;
		private boolean lock = true;
		private ArrayList<State> trigger;
		private ArrayList<State> triggerCopy;
		private ArrayList<Integer> multiDestination;
		private ArrayList<Integer> multiDestinationCopy;
		private boolean fixRoute;
		private boolean isOnMission;
		private String missionString;
		private MyToolKit myToolKit;
		private Dijkstra dijkstra;
		private boolean first;
		private ArrayList<Integer> routeNode;
		private ArrayList<Integer> routeCard;
		private ArrayList<Integer> ignoreCard;
		private ConflictEdge occupyEdge;
		private ConflictEdge lastOccupyEdge;
		private int forwordPix;
		private int AGVStopInNode;
		private boolean sendSignalToAGV;
		private ArrayList<ConflictEdge> conflictEdgeRockwellArray;
		public boolean finishShipment;
		public boolean finishUnloading;
		public boolean checkFinishShipment;
		public boolean checkFinishUnloading;
		public boolean finishStart;
		private int removeCount;
		public boolean initReady;
		private boolean startInInitNode;
		public String sendArriveInMessageStr;
		public String sendRequareMatirielMessageStr;
		public String sendRequestRecyclePalletStr;
		public String sendFinishChargeStr;
		public String sendNeedChargeStr;
		public boolean firstInit;
		public boolean charging;
		public int chargeCount;
		public ScheduledExecutorService chargeTimer;
		public boolean ReadyToOffDuty;
		public boolean realyOffDuty;
		public boolean AGVInit;
		public int chargeDuration;
		public boolean readyToLeft;
		private PlayAudio playAudio;
		private AGVComVar agvComVar;
		public String stateString = " ";

		public AGVCar(int AGVNum, Graph graph, ConflictDetection conflictDetection, PlayAudio playAudio){
			//this.initReady = true;
			this.chargeDuration = graph.getChargeDuration();
			this.AGVInit = true;
			this.agvComVar = new AGVComVar();
			this.playAudio = playAudio;
			conflictEdgeRockwellArray = new ArrayList<ConflictEdge>();
			this.startInInitNode = true;
			AGVStopInNode = -1;
			this.AGVNum = AGVNum;
			this.graph = graph;
			this.conflictDetection = conflictDetection;
			this.routeNode = new ArrayList<Integer>();
			this.routeCard = new ArrayList<Integer>();
			this.ignoreCard = new ArrayList<Integer>();
			this.chargeTimer = Executors.newScheduledThreadPool(2);
			trigger = new ArrayList<State>();
			multiDestination = new ArrayList<Integer>();
			if(graph.getAGVSeting().get(AGVNum-1).length() > 1){
				fixRoute = true;
				String[] route = graph.getAGVSeting().get(AGVNum-1).split("/");
				for(int i = 0; i < route.length; i++){
					if(i%2 == 0){
						if(route[i].equals("3")){
							trigger.add(State.SHIPMENT);
						}else if(route[i].equals("4")){
							trigger.add(State.UNLOADING);
						}
					}else{
						multiDestination.add(Integer.parseInt(route[i]));
					}
				}
				triggerCopy = new ArrayList<State>(trigger);
				multiDestinationCopy = new ArrayList<Integer>(multiDestination);
			}
			
			myToolKit = new MyToolKit();
			dijkstra = new Dijkstra(graph);
			finishEdge = true;
			state = State.FORWARD;
			edge = new Edge(new Node(0,0),new Node(0,0));
			first = true;
		}

		public void stepForward(){			
			if(!finishEdge&& (state == State.FORWARD || state == State.BACKWARD)){
				if(edge.startNode.x == edge.endNode.x){
					if(edge.startNode.y < edge.endNode.y ){
						if(y < edge.endNode.y){
							y +=forwordPix;
						}else{
							finishEdge = true;
						}	
					}else if(edge.startNode.y > edge.endNode.y ){
						if(y > edge.endNode.y){
							y -=forwordPix;
						}else{
							finishEdge = true;
						}
					}
				}else if(edge.startNode.y == edge.endNode.y){
					if(edge.startNode.x < edge.endNode.x ){
						if(x < edge.endNode.x)
							x +=forwordPix;
						else
							finishEdge = true;
					}else if(edge.startNode.x > edge.endNode.x){
						if(x > edge.endNode.x)
							x -=forwordPix;
						else
							finishEdge = true;
					}
				}
			}
		} 
		
		public Edge getStartEdge(){
			//如果最后一张卡是停止卡，
			if(this.lastCard == graph.getStopCard()){
				edge.endNode.functionNode = true;
				return edge;
			}else{
				return edge;
			}
		}
		
		public void judgeOrientation(){
			if(edge.startNode.x == edge.endNode.x){
				if(edge.startNode.y < edge.endNode.y){
					orientation = Orientation.DOWN;
				}else{
					orientation = Orientation.UP;
				} 	
			}else if(edge.startNode.y == edge.endNode.y){
				if(edge.startNode.x < edge.endNode.x){
					orientation = Orientation.RIGTH;
				}else{
					orientation = Orientation.LEFT;
				} 
					
			}
			if(!(this.lastCard == graph.getStopCard())){
				
			}
		}		
		
		public void setReadCard(int cardNum){
			if(this.readyToLeft && cardNum == graph.getExecuteCard()){
				this.readyToLeft = false;
				this.AGVlLeft();
			}
			
			if(this.removeCount == 2 && !this.startInInitNode && cardNum == graph.getExecuteCard()){
				this.receiveAGVMessage.SendActionMessage("CC07DD");//加速指令
				this.stateString = "加速";
				System.out.println("发送加速指令");
				logger.debug("发送加速指令");
			}

			if(cardNum != graph.getExecuteCard() && cardNum != graph.getStopCard()){//运行中测试读卡及容错
				if(this.routeCard.size() > 0 ){
					boolean isInRouteCard = false;
					for(int i = 0; i < this.routeCard.size(); i++){
						if(cardNum == this.routeCard.get(i)){
							isInRouteCard = true;
							if(i == 0){//正常情况
								this.setCardInConflictDetection(cardNum);
								routeCard.remove(0);
								this.removeCount++;
							}else if(i == 1 && this.firstInit){//正常情况
								this.setCardInConflictDetection(cardNum);						
								routeCard.remove(0);
								routeCard.remove(0);
								this.removeCount++;
								this.removeCount++;
							}else{//漏卡
								if(this.routeCard.get(0) %2 !=0)
									this.setCardInConflictDetection(this.routeCard.get(0));
								this.setCardInConflictDetection(cardNum);								
								for(int j = 0; j <= i; j++){
									System.out.println(this.AGVNum +"AGV遗漏"+this.routeCard.get(0));
									logger.error(this.AGVNum + "AGV遗漏"+this.routeCard.get(0));
									logger.debug(this.AGVNum + "AGV遗漏"+this.routeCard.get(0));
									if(j < i)
										this.ignoreCard.add(this.routeCard.get(0));	
									routeCard.remove(0);
									this.removeCount++;
								}
							}
							break;
						}
					}
					if(!isInRouteCard && cardNum != 161 && cardNum != 162 && cardNum != 163 && cardNum != 164 && cardNum != 165 
							&& cardNum != 166 && cardNum != 167 && cardNum != 168 && cardNum != 169
							&& cardNum != 170 && cardNum != 50 && cardNum != 171 && cardNum != 107){
						System.out.println(this.AGVNum + "跑出预定轨道" + cardNum);
						logger.error(this.AGVNum + "跑出预定轨道" + cardNum);
						this.stateString = "跑出预定轨道";
						this.playAudio.continuePlay();
					}
				}
			}		
			
			if(cardNum == graph.getStopCard() && this.routeNode.size() > 0 && !this.realyOffDuty){
				System.out.println(this.AGVNum + "AGV小车到位");
				this.lastOccupyEdge = null;
				if(this.routeCard.size() > 0){//revise in 12.18
					for(Integer r: this.routeCard){
						this.ignoreCard.add(r);
						this.setCardInConflictDetection(r);
					}
				}
				
				for(int i = 0; i < graph.getFunctionNodeArray().size(); i++){
					if(graph.getFunctionNodeArray().get(i).nodeNum == graph.getNode(this.routeNode.get(this.routeNode.size()-1) - 1).num){
						AGVStopInNode = i;
						int num = graph.getFunctionNodeArray().get(i).communicationNum;
						
						if(graph.getFunctionNodeArray().get(i).function == FunctionNodeEnum.UNLOADING){
							
							if(trigger.size() != 0){
								if(trigger.get(0) == State.SHIPMENT){//拉空托盘
									this.receiveAGVMessage.SendActionMessage("CC02DD");
									logger.debug(this.AGVNum + "AGV上料");
								}
							}
							
							if(num < 16){
								logger.debug(this.AGVNum + "AGV到位");
								graph.getFunctionNodeArray().get(i).getReceiveStationMessage().SendMessage("CC0"+Integer.toHexString(num)+"07DD", this.AGVNum);//AGV到位
							}else {
								logger.debug(this.AGVNum + "AGV到位");
								graph.getFunctionNodeArray().get(i).getReceiveStationMessage().SendMessage("CC"+Integer.toHexString(num)+"07DD", this.AGVNum);
							}
							this.stateString = "到位，拉空托盘";
						}else if(graph.getFunctionNodeArray().get(i).function == FunctionNodeEnum.CHARGE){
							if(!this.ReadyToOffDuty){
								graph.getFunctionNodeArray().get(i).getReceiveStationMessage().SendMessage("CC01DD", this.AGVNum);//
								this.stateString = "到达充电桩";
								System.out.println(this.AGVNum + "AGV到达充电桩");
								logger.debug(this.AGVNum + "AGV到达充电桩");								
							}else{
								//this.realyOffDuty = true;
								this.AGVOffDuty();
							}
							
						}
					}
					
					
					if(graph.getFunctionNodeArray().get(i).callAGVNum == this.AGVNum){
						graph.getFunctionNodeArray().get(i).clicked = false;
					}				
				}
				
				if(this.ignoreCard.size() != 0){
					if(!this.startInInitNode){
						logger.error(this.AGVNum + "遗失卡：" + this.ignoreCard);
						System.out.println(this.AGVNum + "遗失卡：" + this.ignoreCard);
					}
					this.ignoreCard = new ArrayList<Integer>();
					
				}				
			}
			
			this.lastCard = cardNum;
		}
	
		public void setCardInConflictDetection(int cardNum){
			Edge edgeStr = null;
			Edge edgeEnd = null;
			for(Edge edge : graph.getEdgeArray()){
				if(edge.strCardNum == cardNum){
					edgeStr = edge;
				}else if(edge.endCardNum == cardNum){
					edgeEnd = edge;
				}
			}
			if(lock){
				if(edgeEnd != null){
					lock = false;
					if(edge.endNode.num != 0){//点冲突
						logger.debug(this.AGVNum+"AGV查询是否可以通过"+edge.endNode.num+"点");
					//	System.out.println(this.AGVNum+"AGV查询是否可以通过"+edge.endNode.num+"点");
						conflictDetection.checkConflict(this, edge.endNode.num);//根据route
					}
					
					if(routeNode != null && routeNode.size() > 2){//边冲突
						lastOccupyEdge = occupyEdge;
						occupyEdge = conflictDetection.checkConflictEdge(this, routeNode.get(1), routeNode.get(2));//根据route
						routeNode.remove(0);
					}
					
				}else if(edgeStr != null){
					this.edge = edgeStr;
					lock = true;
				}
			}else{
				if(edgeStr != null){
					this.edge = edgeStr;
					lock = true;
					conflictDetection.removeOccupy(this, edge.startNode.num);//点冲突
					conflictDetection.removeEdgeRockwell(this);
					if(routeNode != null && lastOccupyEdge != null){//边冲突
						conflictDetection.removeOccupyEdge(this, lastOccupyEdge);
					}	
					
					if(this.conflictEdgeRockwellArray.size() > 0){
						if(this.lastCard == graph.getExecuteCard()){
							logger.debug(this.AGVNum +"AGV解除conflictEdgeRockwell");
							System.out.println(this.AGVNum +"AGV解除conflictEdgeRockwell");
							this.conflictEdgeRockwellArray.get(0).removeAGV(this, null);
							this.conflictEdgeRockwellArray.remove(0);
							if(this.conflictEdgeRockwellArray.size() == 0)
								this.conflictEdgeRockwellArray = new ArrayList<ConflictEdge>();							
						}
					}
				}else if(edgeEnd != null){
					lock = false;
				}
			}

			if((edgeStr != null)&&(lock)){
				finishEdge = false;
				x = edge.startNode.x;
				y = edge.startNode.y;
				int time = edge.realDis/60;
				if(edge.startNode.x == edge.endNode.x){
					forwordPix =  Math.abs(edge.startNode.y - edge.endNode.y)/(10*time);
					if(forwordPix == 0)
						forwordPix = 1;
				}else if(edge.startNode.y == edge.endNode.y){
					forwordPix = Math.abs(edge.startNode.x - edge.endNode.x)/(10*time);
					if(forwordPix == 0)
						forwordPix = 1;
				}
				judgeOrientation();
				if(first && fixRoute){//fixedRoute
					first = false;
					routeNode = dijkstra.findRoute(this.getStartEdge(), this.multiDestination.get(0)).getRoute();
					this.conflictEdgeRockwellArray = this.getRunnable().SendRouteMessageRockwell(myToolKit.routeToOrientation(graph
							, routeNode, this), routeNode);
					this.setRouteCard(routeNode);
					if(AGVStopInNode >= 0){//AGV当前离开点
						graph.getFunctionNodeArray().get(AGVStopInNode).callAGVNum = -1;
						AGVStopInNode = -1;
						this.sendSignalToAGV = false;
					}
					
					for(int i = 0; i < graph.getFunctionNodeArray().size(); i++){
						if(this.multiDestination.get(0) == graph.getFunctionNodeArray().get(i).nodeNum){
							graph.getFunctionNodeArray().get(i).clicked = true;
							graph.getFunctionNodeArray().get(i).callAGVNum = this.AGVNum;
						}
							
					}
					if(this.realyOffDuty){
						this.missionString = "下班";
					}else{
						this.missionString = graph.getNode(multiDestination.get(0)-1).tag;
						this.isOnMission = true;
					}
					this.state = State.FORWARD;
					trigger.remove(0);
					multiDestination.remove(0);
				}
			}
		}
		
		public void setMission(ArrayList<State> trigger, ArrayList<Integer> destination){
			this.trigger = trigger;
			this.multiDestination = destination;
			System.out.println("setMission trigger:"+ trigger);
			System.out.println("setMission destination:"+ destination);
			logger.debug("setMission trigger:"+ trigger);
			logger.debug("setMission destination:"+ destination);
			if(trigger.get(0) == State.NULL){
				triggerDestination();
			}
		}	
		
		public void setRouteCard(ArrayList<Integer> routeNode){
			if(this.lastCard == graph.getStopCard()){
				this.startInInitNode = false;
				this.removeCount = 0;
				//System.out.println("this.startInInitNode = false;");
			}
				
			for(int i = 0; i+1 < routeNode.size(); i++){
				for(int j = 0; j < graph.getEdgeSize(); j++){
					if(routeNode.get(i) == graph.getEdge(j).startNode.num && routeNode.get(i+1) == graph.getEdge(j).endNode.num){
						this.routeCard.add(graph.getEdge(j).strCardNum);
						this.routeCard.add(graph.getEdge(j).endCardNum);
					}
				}
			}
		}
		
		public void setAGVState(int state){
			if(state == 1){//待机
				//this.state = State.STOP;
				System.out.println(this.AGVNum + "AGV待机");
				logger.debug(this.AGVNum + "AGV待机");
			}else if(state == 2){//运行
				logger.debug(this.AGVNum + "AGV运行");
				System.out.println(this.AGVNum + "AGV运行");
				logger.debug(this.AGVNum + "AGV运行");
				this.finishStart = true;
				this.state = State.FORWARD;
			}else if(state == 3){//AGV上料完成，进托盘完成
				System.out.println(this.AGVNum + "AGV上料完成，进托盘完成");
				logger.debug(this.AGVNum + "AGV上料完成，进托盘完成");
				this.stateString = "上料完成";
				this.finishShipment = true;
				this.checkFinishShipment = true;
				if(this.multiDestination.size() == 0){
					this.isOnMission = false;
					if(this.charging && !this.ReadyToOffDuty){
						this.AGVCharge();
					}
					if(this.ReadyToOffDuty){
						this.AGVOffDuty();
					}
				}
				
				if(this.AGVStopInNode >= 0){
					int num = graph.getFunctionNodeArray().get(this.AGVStopInNode).communicationNum;
					if(graph.getFunctionNodeArray().get(this.AGVStopInNode).function == FunctionNodeEnum.UNLOADING){
						if(num < 16)
							graph.getFunctionNodeArray().get(this.AGVStopInNode).getReceiveStationMessage().SendMessage("CC0"+Integer.toHexString(num)+"08DD", 0);
						else 
							graph.getFunctionNodeArray().get(this.AGVStopInNode).getReceiveStationMessage().SendMessage("CC"+Integer.toHexString(num)+"08DD", 0);
					}else if(graph.getFunctionNodeArray().get(this.AGVStopInNode).function == FunctionNodeEnum.SHIPMENT){
						if(num < 16)
							graph.getFunctionNodeArray().get(this.AGVStopInNode).getReceiveStationMessage().SendMessage("CC0"+Integer.toHexString(num)+"02DD", 0);
						else 
							graph.getFunctionNodeArray().get(this.AGVStopInNode).getReceiveStationMessage().SendMessage("CC"+Integer.toHexString(num)+"02DD", 0);
					}else if(graph.getFunctionNodeArray().get(this.AGVStopInNode).function == FunctionNodeEnum.EMPTYCAR){
						if(num < 16)
							graph.getFunctionNodeArray().get(this.AGVStopInNode).getReceiveStationMessage().SendMessage("CC0"+Integer.toHexString(num)+"08DD", 0);
						else 
							graph.getFunctionNodeArray().get(this.AGVStopInNode).getReceiveStationMessage().SendMessage("CC"+Integer.toHexString(num)+"08D", 0);
					}
				}
				
				if(trigger != null && trigger.size() > 0 && multiDestination != null && multiDestination.size() > 0){
					if(trigger.get(0) == State.SHIPMENT){
						triggerDestination();
					}
				}else if(fixRoute){
					multiDestination = new ArrayList<Integer>(multiDestinationCopy);
					trigger = new ArrayList<State>(triggerCopy);
					if(trigger.get(0) == State.SHIPMENT){
						triggerDestination();
					}
				}
			}else if(state == 4){//AGV卸料完成
				System.out.println(this.AGVNum + "AGV卸料完成，出托盘完成");
				logger.debug(this.AGVNum + "AGV卸料完成，出托盘完成");
				this.stateString = "卸料完成";
				this.finishUnloading = true;
				this.checkFinishUnloading = true;
				if(this.multiDestination.size() == 0){
					this.isOnMission = false;
					if(this.charging && !this.ReadyToOffDuty){
						this.AGVCharge();
					}
					if(this.ReadyToOffDuty){
						this.AGVOffDuty();
					}
					if(this.AGVStopInNode >= 0){
						if(graph.getFunctionNodeArray().get(this.AGVStopInNode).responsing){
							graph.getFunctionNodeArray().get(this.AGVStopInNode).responsing = false;
							System.out.println(this.AGVNum + "AGV任务完成" + graph.getFunctionNodeArray().get(this.AGVStopInNode).communicationNum + "工作点响应完成");
							logger.debug(this.AGVNum + "AGV任务完成" + graph.getFunctionNodeArray().get(this.AGVStopInNode).communicationNum + "工作点响应完成");
						}
					}
				}
				
				if(trigger != null && trigger.size() > 0 && multiDestination != null && multiDestination.size() > 0){
					if(trigger.get(0) == State.UNLOADING){
						triggerDestination();
					}
				}else if(fixRoute){
					multiDestination = multiDestinationCopy;
					trigger = triggerCopy;
					if(trigger.get(0) == State.UNLOADING){
						triggerDestination();
					}
				}
			}
		}
		
		synchronized private void triggerDestination(){
			routeNode = dijkstra.findRoute(this.getStartEdge(), this.multiDestination.get(0)).getRoute();
			this.conflictEdgeRockwellArray = this.getRunnable().SendRouteMessageRockwell(myToolKit.routeToOrientation(graph
					, routeNode, this), routeNode);
			this.setRouteCard(routeNode);
			
			for(int i = 0; i < graph.getFunctionNodeArray().size(); i++){//下一个目的地被锁定
				if(this.multiDestination.get(0) == graph.getFunctionNodeArray().get(i).nodeNum){
					graph.getFunctionNodeArray().get(i).clicked = true;
					graph.getFunctionNodeArray().get(i).callAGVNum = this.AGVNum;
				}
					
			}
			if(this.realyOffDuty){
				this.missionString = "下班";
			}else{
				this.missionString = graph.getNode(multiDestination.get(0)-1).tag;
				this.isOnMission = true;
			}
			
			this.state = State.FORWARD;
			this.trigger.remove(0);
			this.multiDestination.remove(0);
			//System.out.println(" trigger:"+ this.trigger);
			//System.out.println(" multiDestination:"+ this.multiDestination);
		}
		
		class ChargeTimerTask extends TimerTask{
			private int AGVStopInNode;
			private int AGVNum;
			public ChargeTimerTask(int AGVStopInNode, int AGVNum){
				this.AGVStopInNode = AGVStopInNode;
				this.AGVNum = AGVNum;
			}
			public void run(){
				if(!ReadyToOffDuty)
					graph.getFunctionNodeArray().get(AGVStopInNode).getReceiveStationMessage().SendMessage("CC02DD", this.AGVNum);
			}
		}
		
		public void AGVCharge(){

			for(int i = 0; i < graph.getFunctionNodeArray().size(); i++){
				if(graph.getFunctionNodeArray().get(i).function == FunctionNodeEnum.CHARGE
						&& !graph.getFunctionNodeArray().get(i).chargingStationRetract
						&& graph.getFunctionNodeArray().get(i).callAGVNum <= 0
						&& !graph.getFunctionNodeArray().get(i).responsing){
					graph.getFunctionNodeArray().get(i).chargingStationRetract = true;
				}
			}
			
			this.chargeTimer.schedule(new TimerTask(){
				public void run(){
					int chargeNode = -1;
					for(int i = graph.getFunctionNodeArray().size() -1; i >= 0 ; i--){
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
						ArrayList<State> triggerArray = new ArrayList<State>();
						triggerArray.add(State.NULL);
						ArrayList<Integer> destinationArray = new ArrayList<Integer>();
						destinationArray.add(chargeNode);						
						if(!isOnMission){
							logger.debug(AGVNum+"AGV 任务完成后去充电");							
							setMission(triggerArray, destinationArray);
						}
						
					}else{
						charging = false;
						chargeCount--;
					}
				}
			}, 5000, TimeUnit.MILLISECONDS);
		}
		
		public void chargeTime(){
			int time = this.chargeDuration;
			if(this.electricity == 1){
				time = 1200000;
			}else if(this.electricity == 2){
				time = 1800000;
			}else if(this.electricity == 3){
				time = 2400000;
			}
			this.chargeTimer.schedule(new ChargeTimerTask(this.AGVStopInNode, this.AGVNum), time, TimeUnit.MILLISECONDS);//定时充电
			this.electricity = 0;
		}
		
		public boolean getFixRoute(){
			return this.fixRoute;
		}
		
		public int getAGVStopInNode(){
			return this.AGVStopInNode;
		}
		
		public void initAGVStopInNode(){
			this.AGVStopInNode = -1;
		}
		
		public int getLastCard(){
			return lastCard;
		}
		
		public Orientation getOrientation(){
			return orientation;
		}
		

		public void setElectricity(int electricity){
			this.electricity = electricity;
			//this.charging = true;//加电压等级
		}
		
		public int getElectricity(){
			return electricity;
		}
		
		public void setLastCommunicationTime(long time){
			lastCommunicationTime = time;
		}
		
		public long getLastCommunitionTime(){
			return lastCommunicationTime;
		}
		
		public void setRunnabel(ReceiveAGVMessage receiveAGVMessage){
			this.receiveAGVMessage = receiveAGVMessage;
		}
		
		public ReceiveAGVMessage getRunnable(){
			return receiveAGVMessage;
		}

		public int getAGVNum(){
			return AGVNum;
		}
		
		public int getX(){
			return x;
		}
		
		public int getY(){
			return y;
		}
		
		public boolean getSendSignalToAGV(){
			return this.sendSignalToAGV;
		}
		
		public void setSendSignalToAGV(boolean b){
			this.sendSignalToAGV = b;
		}
		
		public boolean isOnMission(){
			return this.isOnMission;
		}
		
		public void setIsOnMission(boolean f){
			this.isOnMission = f;
		}
		
		public String getMissionString(){
			return missionString;
		}
		
		public void setMissionString(String str){
			this.missionString = str;
		}
		
		public ConflictDetection getConflictDetection(){
			return this.conflictDetection;
		}
		
		public void initConflictDetection(ConflictDetection conflictDetection){
			this.conflictDetection = conflictDetection;
			System.out.println(this.AGVNum + "initConflictDetection");
			logger.debug(this.AGVNum + "initConflictDetection");
		}
		
		
		
		public ArrayList<State> getTrigger(){
			return this.trigger;
		}
		
		public void initAGV(){
			System.out.println(this.AGVNum + "AGV被初始化");
			logger.debug(this.AGVNum + "AGV被初始化");
			this.agvComVar = new AGVComVar();
			this.AGVInit = false;
			this.x = -20;
			this.y = -20;
			this.sendSignalToAGV = false;
			this.receiveAGVMessage.deleteFaultedAGV();
			this.receiveAGVMessage = null;
			this.AGVStopInNode = -1;
			this.initReady = false;
			this.startInInitNode = true;
			this.routeNode = new ArrayList<Integer>();
			this.routeCard = new ArrayList<Integer>();
			this.ignoreCard = new ArrayList<Integer>();
			this.trigger = new ArrayList<State>();
			this.multiDestination = new ArrayList<Integer>();
			this.finishEdge = true;
			this.state = State.FORWARD;
			this.edge = new Edge(new Node(0,0),new Node(0,0));
			this.first = true;
			
			this.lock = true;
			this.isOnMission = false;
			this.missionString = "";
			this.finishStart = false;
			this.finishShipment = false;
			this.finishUnloading = false;
			this.checkFinishShipment = false;
			this.checkFinishUnloading = false;
			this.removeCount = 0;
			
			this.sendArriveInMessageStr = null;
			this.sendRequareMatirielMessageStr = null;
			this.sendRequestRecyclePalletStr = null;
			this.sendFinishChargeStr = null;
			this.sendNeedChargeStr = null;
			this.charging = false;
		}
		
		public ArrayList<Integer> getRouteNode(){
			return this.routeNode;
		}
		
		synchronized public void setOffDuty(){
			this.ReadyToOffDuty = true;
		}
		
		public void AGVlLeft(){
			if(AGVStopInNode >= 0){//AGV当前离开点
				if(graph.getFunctionNodeArray().get(this.AGVStopInNode).responsing){
					graph.getFunctionNodeArray().get(this.AGVStopInNode).responsing = false;
					System.out.println(this.AGVNum + "AGV任务完成" + graph.getFunctionNodeArray().get(this.AGVStopInNode).communicationNum + "工作点响应完成");
					logger.debug(this.AGVNum + "AGV任务完成" + graph.getFunctionNodeArray().get(this.AGVStopInNode).communicationNum + "工作点响应完成");
				}
				graph.getFunctionNodeArray().get(AGVStopInNode).callAGVNum = -1;
				AGVStopInNode = -1;
				this.sendSignalToAGV = false;
			}else{
				System.out.println("离开时     AGVStopInNode < 0");
				logger.debug("离开时     AGVStopInNode < 0");
			}
		}
		
		public void AGVOffDuty(){
			if(!this.realyOffDuty){
				this.realyOffDuty = true;
				this.isOnMission = true;
				this.receiveAGVMessage.SendActionMessage("CC09DD");
				this.chargeTimer.schedule(new TimerTask(){
					public void run(){
						if(AGVStopInNode >= 0){
							if(graph.getFunctionNodeArray().get(AGVStopInNode).nodeNum != 9){
								ArrayList<State> triggerArray = new ArrayList<State>();
								triggerArray.add(State.NULL);
								ArrayList<Integer> destinationArray = new ArrayList<Integer>();
								destinationArray.add(9);
								setMission(triggerArray, destinationArray);
							}else{
								routeNode = new ArrayList<Integer>();
								routeNode.add(9);
								routeNode.add(10);
								routeNode.add(8);
								routeNode.add(6);
								routeNode.add(4);
								routeNode.add(2);
								routeNode.add(56);
								routeNode.add(55);
								routeNode.add(54);
								routeNode.add(51);
								routeNode.add(48);
								routeNode.add(45);
								routeNode.add(25);
								routeNode.add(23);
								routeNode.add(21);
								routeNode.add(19);
								routeNode.add(17);
								routeNode.add(15);
								routeNode.add(13);
								routeNode.add(11);
								routeNode.add(57);
								routeNode.add(58);
								routeNode.add(28);
								routeNode.add(30);
								routeNode.add(32);
								routeNode.add(34);
								routeNode.add(36);
								routeNode.add(38);
								routeNode.add(40);
								routeNode.add(42);
								routeNode.add(43);
								routeNode.add(46);
								routeNode.add(49);
								routeNode.add(52);
								routeNode.add(10);
								routeNode.add(9);
								conflictEdgeRockwellArray = receiveAGVMessage.SendRouteMessageRockwell("01AA02000000000000000000020000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000BB"
										, routeNode);
								missionString = "下班";
								System.out.println(AGVNum + "AGV从卸料点下班");
								logger.debug(AGVNum + "AGV从卸料点下班");
								setRouteCard(routeNode);
							}
						}else{
							System.out.println("下班AGVStopInNode = -1");
							logger.debug("下班AGVStopInNode = -1");
						}
					}
				}, 2000, TimeUnit.MILLISECONDS);
			
			}
		}
		
		public void chargeOffDuty(){
			if(AGVStopInNode > 0)
				graph.getFunctionNodeArray().get(AGVStopInNode).getReceiveStationMessage().SendMessage("CC02DD", this.AGVNum);
		}
		
		public AGVComVar getAGVComVar(){
			return this.agvComVar;
		}
}
