package schedulingSystem.toolKit;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JLabel;

import org.apache.log4j.Logger;
import schedulingSystem.component.AGVCar;
import schedulingSystem.component.ConflictEdge;
import schedulingSystem.component.ConflictNode;
import schedulingSystem.component.Edge;
import schedulingSystem.component.Graph;
import schedulingSystem.component.Node;
import schedulingSystem.component.PlayAudio;

public class ReceiveAGVMessage implements Runnable{
	private static Logger logger = Logger.getLogger(ReceiveAGVMessage.class.getName());
	private InputStream inputStream;
	private OutputStream outputStream;
	private Socket socket;
	private long lastCommunicationTime;
	private MyToolKit myToolKit;
	private ArrayList<AGVCar> AGVArray;
	private int NOOfAGV;
	private int lastNOOfAGV;
	private int NOOfCard;
	private int lastCard;
	private boolean oldRunnable;
	private Graph graph;
	private String lastMessage;
	private int lastSignal;
	private ScheduledExecutorService timer;
	private ArrayList<ConflictEdge> conflictEdgeArray;
	private boolean clearBuffer;
	private boolean foundStart;
	private boolean deleteFaultedAGV;
	private OnDutyBtn onDutyBtn;
	private PlayAudio playAudio;
	private boolean firstSend;
	private AGVCar AGVCar;
	private boolean canNotStop;
	private JLabel stateLabel;

	public ReceiveAGVMessage(Socket socket, Graph graph, ArrayList<AGVCar> AGVArray, JLabel stateLabel, PlayAudio playAudio, OnDutyBtn onDutyBtn){
		this.socket = socket;
		this.onDutyBtn = onDutyBtn;
		this.stateLabel = stateLabel;
		this.playAudio = playAudio;		
		this.graph = graph;
		this.AGVArray = AGVArray;
		this.lastMessage = "";
		this.myToolKit = new MyToolKit();		
		System.out.println("socket connect agv message:"+socket.toString());
		logger.debug("socket connect agv message:"+socket.toString() + this.hashCode());		
		try{
			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e);
		}
		lastCommunicationTime = System.currentTimeMillis();
		timer = Executors.newScheduledThreadPool(3);
		timer.schedule(new TimerTask(){
			public void run(){
				clearBuffer = true;
			}
		}, 3000, TimeUnit.MILLISECONDS);
	}
	
	public void SendActionMessage(String message){//上料、卸料、停止、运行、加速
		this.lastSignal = 0;//发送一个命令后等待一个状态，不管上一个状态是什么
		this.firstSend = true;
		AGVCar.getAGVComVar().sendChargeTime = System.currentTimeMillis();
		if(message.equals("CC01DD")){//停止
			AGVCar.getAGVComVar().sendStopToAGV = true;
			AGVCar.getAGVComVar().stopString = message;
			AGVCar.stateString += ",开始发送停止指令";
			//System.out.println("开始发送停止指令" + this.NOOfAGV + "AGV");
			logger.debug("开始发送停止指令" + this.NOOfAGV + "AGV");
		}else if(message.equals("CC02DD")){//上料
			AGVCar.getAGVComVar().sendShipmentToAGV = true;
			AGVCar.getAGVComVar().shipmentString = message;
			AGVCar.stateString += ",开始发送上料指令";
			//System.out.println("开始发送上料指令" + this.NOOfAGV + "AGV");
			logger.debug("开始发送上料指令" + this.NOOfAGV + "AGV");
		}else if(message.equals("CC03DD")){//卸料
			AGVCar.getAGVComVar().sendUnloadingToAGV = true;
			AGVCar.getAGVComVar().unloadingString = message;
			AGVCar.stateString += ",开始发送卸料指令";
			//System.out.println("开始发送卸料指令" + this.NOOfAGV + "AGV");
			logger.debug("开始发送卸料指令" + this.NOOfAGV + "AGV");
		}else if(message.equals("CC04DD")){//充电
			AGVCar.getAGVComVar().sendChargeAGV = true;
			AGVCar.getAGVComVar().chargeString = message;
			AGVCar.stateString += ",开始发送充电指令";
			//System.out.println("开始发送充电指令" + this.NOOfAGV + "AGV");
			logger.debug("开始发送充电指令" + this.NOOfAGV + "AGV");
		}else if(message.equals("CC05DD")||message.equals("CC06DD")){//正走
			AGVCar.getAGVComVar().sendStartingToAGV = true;
			AGVCar.getAGVComVar().startString = message;
			AGVCar.stateString += ",开始发送运行指令";
			//System.out.println("开始发送运行指令" + message + this.NOOfAGV + "AGV");
			logger.debug("开始发送运行指令" + message + this.NOOfAGV + "AGV");
		}else if(message.equals("CC07DD")){//加速
			AGVCar.getAGVComVar().sendAccessToAGV = true;
			AGVCar.getAGVComVar().accessString = message;
			AGVCar.stateString += ",开始发送加速指令";
			//System.out.println("开始发送加速指令" + this.NOOfAGV + "AGV");
			logger.debug("开始发送加速指令" + this.NOOfAGV + "AGV");
		}else if(message.equals("CC09DD")){
			AGVCar.getAGVComVar().sendOffDutyToAGV = true;
			AGVCar.getAGVComVar().offDutyString = message;
			AGVCar.stateString += ",开始发送下班指令";
			//System.out.println("开始发送下班指令" + this.NOOfAGV + "AGV");
			logger.debug("开始发送下班指令" + this.NOOfAGV + "AGV");
		}			
		//System.out.println("开始发送动作指令" + this.NOOfAGV + "AGV");
	}
	
	public ArrayList<ConflictEdge> SendRouteMessageRockwell(String sendMessageStr, ArrayList<Integer> routeNode){//发送路径
		ArrayList<ConflictEdge> returnConflictEdge = new ArrayList<ConflictEdge>();
		this.lastSignal = 0;//发送一个命令后等待一个状态
		AGVCar.getAGVComVar().firstWait = true;
		AGVCar.getAGVComVar().requartSend = true;
		AGVCar.getAGVComVar().routeString = sendMessageStr;
		AGVCar.getAGVComVar().conflictNode = AGVArray.get(NOOfAGV - 1).getConflictDetection().getConflictNodeArray().get(routeNode.get(1) - 1);
		this.conflictEdgeArray = AGVArray.get(NOOfAGV - 1).getConflictDetection().getConflictEdgeArray();
	//	System.out.println("routeNode"+ routeNode.get(1) + "||" + routeNode.get(2));
		ArrayList<Edge> returnEdge = getConflictEdgeRockwell(routeNode);
		for(int i = 0; i < conflictEdgeArray.size(); i++){
			if(returnEdge.size() >= 1){
				if((returnEdge.get(0).startNode.num == conflictEdgeArray.get(i).stratNodeNum && returnEdge.get(0).endNode.num == conflictEdgeArray.get(i).endNodeNum)
						||(returnEdge.get(0).startNode.num == conflictEdgeArray.get(i).endNodeNum && returnEdge.get(0).endNode.num == conflictEdgeArray.get(i).stratNodeNum)){
					AGVCar.getAGVComVar().conflictEdgeRockwell = conflictEdgeArray.get(i);
					//System.out.println("conflictEdgeRockwell"+ conflictEdgeArray.get(i).endNodeNum + "||" + conflictEdgeArray.get(i).stratNodeNum);
				}
				if(returnEdge.size() == 2){
					if((returnEdge.get(1).startNode.num == conflictEdgeArray.get(i).stratNodeNum && returnEdge.get(1).endNode.num == conflictEdgeArray.get(i).endNodeNum)
							||(returnEdge.get(1).startNode.num == conflictEdgeArray.get(i).endNodeNum && returnEdge.get(1).endNode.num == conflictEdgeArray.get(i).stratNodeNum)){
						AGVCar.getAGVComVar().conflictEdgeRockwell1 = conflictEdgeArray.get(i);
						//System.out.println("conflictEdgeRockwell1"+ conflictEdgeArray.get(i).endNodeNum + "||" + conflictEdgeArray.get(i).stratNodeNum);
					}
				}
			}
			
			if((routeNode.get(1) == conflictEdgeArray.get(i).stratNodeNum && routeNode.get(2) == conflictEdgeArray.get(i).endNodeNum)
					||(routeNode.get(1) == conflictEdgeArray.get(i).endNodeNum && routeNode.get(2) == conflictEdgeArray.get(i).stratNodeNum)){
				AGVCar.getAGVComVar().conflictEdgeRoute = conflictEdgeArray.get(i);
				//System.out.println("conflictEdgeRoute"+ conflictEdgeArray.get(i).endNodeNum + "||" + conflictEdgeArray.get(i).stratNodeNum);
			}
			if(AGVCar.getAGVComVar().conflictEdgeRockwell != null && AGVCar.getAGVComVar().conflictEdgeRoute != null && AGVCar.getAGVComVar().conflictEdgeRockwell1 != null){
				break;
			}
		}
		if(AGVCar.getAGVComVar().conflictEdgeRockwell1 != null)
			returnConflictEdge.add(AGVCar.getAGVComVar().conflictEdgeRockwell1);
		if(AGVCar.getAGVComVar().conflictEdgeRockwell != null)
			returnConflictEdge.add(AGVCar.getAGVComVar().conflictEdgeRockwell);
		return returnConflictEdge;
	}
	
	public void run(){
		while(!this.deleteFaultedAGV){
			try{
				sendMessageToAGV();
			}catch(Exception e){
				e.printStackTrace();	
				//System.out.println(this.NOOfAGV + "AGV sendMessageToAGV Exception\n" + e);
				logger.error(this.NOOfAGV + "AGV sendMessageToAGV Exception\n" + e);
				logger.debug(this.NOOfAGV + "AGV sendMessageToAGV Exception\n" + e);
			}
			
			try{
				receiveMessageFroAGV();
			}catch(Exception e){
				e.printStackTrace();
			//	System.out.println(this.NOOfAGV + "AGV receiveMessageFroAGV Exception\n" + e);
				logger.error(this.NOOfAGV + "AGV receiveMessageFroAGV Exception\n" + e);				
				logger.debug(this.NOOfAGV + "AGV receiveMessageFroAGV Exception\n" + e);			
				try{
					if(this.inputStream != null)
						this.inputStream.close();
					if(this.outputStream != null)
						this.outputStream.close();
					if(this.socket != null)
						this.socket.close();
				}catch(Exception e1){
					e1.printStackTrace();
					logger.error("关闭inputStream和outputStream时发生错误\n"+e);
				}
				break;
			}
		}//while
		if(!this.deleteFaultedAGV){
			this.AGVCar.setIsOnMission(true);
			this.AGVCar.setMissionString("通讯中断");
			//this.playAudio.continuePlay();
			System.out.println(this.NOOfAGV + "AGVsocket 被结束");
			logger.debug(this.NOOfAGV + "AGVsocket 被结束");
		}	
	}
	
	public void receiveMessageFroAGV()throws Exception{
		if(inputStream.available() > 0){
			String message = "";
			if(!foundStart){
				byte[] endCode = new byte[1];
				inputStream.read(endCode);
				message = myToolKit.printHexString(endCode);
				if(message.equals("CC") || message.equals("AA")){
					foundStart = true;
				}else if(this.clearBuffer){
					System.out.println(this.NOOfAGV + "AGV开头数据错误：" + message);
					logger.debug(this.NOOfAGV + "AGV开头数据错误：" + message);
				}
			}
			if(foundStart){
				foundStart = false;
				byte[] buff = new byte[4];
				inputStream.read(buff);
				message = myToolKit.printHexString(buff);
				//System.out.print(message);
				if(this.clearBuffer && (!message.equals(lastMessage))){	
					if(message.endsWith("BB")){//读卡数据
						NOOfAGV = Integer.parseInt(message.substring(0, 2), 16);
						if(!oldRunnable && (NOOfAGV <= AGVArray.size())){
							if(AGVArray.get(NOOfAGV-1).getRunnable() != null ){
								AGVArray.get(NOOfAGV-1).getRunnable().deleteFaultedAGV();
								AGVArray.get(NOOfAGV-1).stateString = "通讯中断，但自动连上了";
								logger.error(NOOfAGV + "AGV通讯中断，但自动连上了");
							}
							//System.out.println(NOOfAGV +"AGV读卡时确定runnable");
							logger.debug(NOOfAGV +"AGV读卡时确定runnable");
							AGVArray.get(NOOfAGV-1).setRunnabel(this);
							AGVCar = AGVArray.get(NOOfAGV-1);
							oldRunnable = true;
							this.lastNOOfAGV = this.NOOfAGV;
						}else{
							if(this.lastNOOfAGV != this.NOOfAGV){
								//System.out.println(this.NOOfAGV + "||" + this.lastNOOfAGV + "AGV两次NOOfAGV居然不同！！！！！！！！！！");
								logger.debug(this.NOOfAGV + "||" + this.lastNOOfAGV + "AGV两次NOOfAGV居然不同！！！！！！！！！！");
								this.lastNOOfAGV = this.NOOfAGV;
							}
						}
							
						if(!message.substring(2, 6).equals("BABA") && NOOfAGV <= AGVArray.size()){
							AGVCar.setLastCommunicationTime(System.currentTimeMillis());
							NOOfCard = Integer.parseInt(message.substring(2, 4), 16);
							AGVCar.setElectricity(Integer.parseInt(message.substring(4, 6), 16));
							if(NOOfCard != lastCard){
								System.out.println(NOOfAGV + "AGVreceive card number:"+NOOfCard);
								logger.debug(NOOfAGV + "AGVreceive card number:"+NOOfCard);
								
								stopSendMessToAGV();							
								
								if(NOOfCard == 50 && !AGVCar.initReady){//停止进行初始化
									this.SendActionMessage("CC01DD");
								}
								
								if(!AGVCar.initReady){
									AGVCar.setCardInConflictDetection(NOOfCard);
								}
								
								AGVCar.setReadCard(NOOfCard);
								
								if(AGVCar.initReady && NOOfCard%2 == 0){//初始化完成，读到108号卡后就不是第一次初始化了
									AGVCar.firstInit = false;
								}
								
								lastCard = NOOfCard;
							}
						}else{
							AGVCar.setLastCommunicationTime(System.currentTimeMillis());
							//outputStream.write(myToolKit.HexString2Bytes("AAC0FFEEBB"));
						}
						lastCommunicationTime = System.currentTimeMillis();
						lastMessage = message;
					}else if(message.endsWith("DD")){//状态信息
						NOOfAGV = Integer.parseInt(message.substring(0, 2), 16);
						int state = Integer.parseInt(message.substring(2, 4), 16);
						//System.out.println(NOOfAGV);
						if(!oldRunnable && NOOfAGV <= AGVArray.size()){
							if(AGVArray.get(NOOfAGV-1).getRunnable() != null ){
								AGVArray.get(NOOfAGV-1).getRunnable().deleteFaultedAGV();
								AGVArray.get(NOOfAGV-1).stateString = "通讯中断，但自动连上了";
								logger.error(NOOfAGV + "AGV通讯中断，但自动连上了");
							}
							AGVArray.get(NOOfAGV-1).setRunnabel(this);
							AGVCar = AGVArray.get(NOOfAGV-1);
							oldRunnable = true;
							this.lastNOOfAGV = this.NOOfAGV;
							System.out.println(NOOfAGV +"AGV状态时确定runnable");
							logger.debug(NOOfAGV +"AGV状态时确定runnable");
						}
						if(AGVCar != null && state != lastSignal && this.NOOfAGV <= this.AGVArray.size()){
							if(state == 1){//待机
								if(AGVCar.getAGVComVar().firstSendOnDutyToAGV 
										&& AGVCar.AGVInit && !AGVCar.initReady){
									AGVCar.getAGVComVar().firstSendOnDutyToAGV = false;
									AGVCar.getAGVComVar().sendOnDutyToAGV = true;
									AGVCar.getAGVComVar().onDutyString = "CC08DD";	
									System.out.println("准备发送上班指令" + this.NOOfAGV + "AGV");
									logger.debug("准备发送上班指令" + this.NOOfAGV + "AGV");
									StringBuffer str = new StringBuffer();
									for(int i = 0; i < AGVArray.size(); i++){
										if(AGVArray.get(i).getAGVComVar().sendOnDutyToAGV){
											str.append(AGVArray.get(i).getAGVNum());
											str.append("AGV/");
										}
									}
									str.append("已经准备好");
									this.stateLabel.setText(str.toString());
								}
								if(AGVCar.getAGVComVar().sendStopToAGV){
									AGVCar.getAGVComVar().sendStopToAGV = false;
									AGVCar.getAGVComVar().stopString = "BBBBBB";
									System.out.println("结束发送停止指令给" + this.NOOfAGV + "AGV");
									logger.debug("结束发送停止指令给" + this.NOOfAGV + "AGV");
																					
									if(!AGVCar.initReady && !this.canNotStop){//直到停止才算初始化完成
										AGVCar.initReady = true;
										AGVCar.firstInit = true;
										AGVCar.stateString = "初始化完成";
										System.out.println(AGVCar.getAGVNum() + "AGV初始化完成");
										logger.debug(AGVCar.getAGVNum() + "AGV初始化完成");
										/*
										timer.schedule(new TimerTask(){
											public void run(){
												
											}
										}, 500);*/
									}									
								}
								
							}else if(state == 2){//运行
								if(AGVCar.getAGVComVar().sendAccessToAGV){
									AGVCar.getAGVComVar().sendAccessToAGV = false;
									AGVCar.getAGVComVar().accessString = "BBBBBB";
									System.out.println("结束发送加速指令给"+ this.NOOfAGV + "AGV");
									logger.debug("结束发送加速指令给"+ this.NOOfAGV + "AGV");
								}
								if(AGVCar.getAGVComVar().sendRouteToAGV || AGVCar.getAGVComVar().sendStartingToAGV){
									AGVCar.getAGVComVar().sendRouteToAGV = false;
									AGVCar.getAGVComVar().sendStartingToAGV = false;
									AGVCar.getAGVComVar().startString = "BBBBBB";
									System.out.println("结束发送运行或路径指令给"+ this.NOOfAGV + "AGV");
									logger.debug("结束发送运行或路径指令给"+ this.NOOfAGV + "AGV");
								}
								if(AGVCar.getAGVComVar().sendOnDutyToAGV){
									AGVCar.getAGVComVar().sendOnDutyToAGV = false;
									AGVCar.getAGVComVar().onDutyString = "BBBBBB";
									System.out.println("结束发送上班指令给"+ this.NOOfAGV + "AGV");
									logger.debug("结束发送上班指令给"+ this.NOOfAGV + "AGV");
								}
								if(AGVCar.getAGVComVar().sendOffDutyToAGV){
									AGVCar.getAGVComVar().sendOffDutyToAGV = false;
									AGVCar.getAGVComVar().offDutyString = "BBBBBB";
									System.out.println("结束发送下班指令给" + this.NOOfAGV + "AGV");
									logger.debug("结束发送下班指令给" + this.NOOfAGV + "AGV");
								}
							}else if(state == 3){//上料完成
								if(AGVCar.getAGVComVar().sendShipmentToAGV){
									AGVCar.getAGVComVar().sendShipmentToAGV = false;
									if(!AGVCar.getAGVComVar().sendRouteToAGV)
										AGVCar.getAGVComVar().shipmentString = "BBBBBB";
									System.out.println("结束发送上料给"+ this.NOOfAGV + "AGV");
									logger.debug("结束发送上料给"+ this.NOOfAGV + "AGV");
								}
								
							}else if(state == 4){//卸料完成
								if(AGVCar.getAGVComVar().sendUnloadingToAGV){
									AGVCar.getAGVComVar().sendUnloadingToAGV = false;
									if(!AGVCar.getAGVComVar().sendRouteToAGV)
										AGVCar.getAGVComVar().routeString = "BBBBBB";
									System.out.println("结束发送卸料给"+ this.NOOfAGV + "AGV");
									logger.debug("结束发送卸料给"+ this.NOOfAGV + "AGV");
								}
								
							}
							AGVCar.setAGVState(state);
							lastSignal = state;
						}									
						lastCommunicationTime = System.currentTimeMillis();
						lastMessage = message;
					}else{
						System.out.println(this.NOOfAGV + "AGV接收数据错误：" + message);
						logger.debug(this.NOOfAGV + "AGV接收数据错误：" + message);
					}
				}
			}						
		}else {
			Thread.sleep(10);
		}	
	}
	
	public void sendMessageToAGV(){
		if(AGVCar != null){
			if(AGVCar.getAGVComVar().requartSend ){
				synchronized(AGVCar.getAGVComVar().conflictNode){
					if(!AGVCar.getAGVComVar().conflictNode.occupy || (AGVCar.getAGVComVar().conflictNode.waitQueue.size() > 0 &&AGVCar.getAGVComVar().conflictNode.waitQueue.get(0).getAGVNum() == this.NOOfAGV)
							|| (AGVCar.getAGVComVar().conflictNode.occupy && AGVCar.getAGVComVar().conflictNode.waitQueue.size() > 0 && AGVCar.getAGVComVar().conflictNode.waitQueue.get(0).getRouteNode().size() > 0 &&
									this.graph.getNode(AGVCar.getAGVComVar().conflictNode.waitQueue.get(0).getRouteNode().get(AGVCar.getAGVComVar().conflictNode.waitQueue.get(0).getRouteNode().size()-1) - 1).num == 9)){
						if(AGVCar.getAGVComVar().conflictEdgeRockwell != null && AGVCar.getAGVComVar().conflictEdgeRockwell1 != null && AGVCar.getAGVComVar().conflictEdgeRoute != null){
							if(!AGVCar.getAGVComVar().conflictEdgeRoute.occupy && !AGVCar.getAGVComVar().conflictEdgeRockwell.occupy && !AGVCar.getAGVComVar().conflictEdgeRockwell1.occupy){
								AGVCar.getAGVComVar().requartSend = false;
								AGVCar.getAGVComVar().sendRouteToAGV = true;
								this.AGVArray.get(this.NOOfAGV - 1).readyToLeft = true;
								//System.out.println("conflictEdgeRockwell1开始发送路径指令给" + this.NOOfAGV + "AGV");
								if(AGVCar.getAGVComVar().AGVWiat){
									AGVCar.getAGVComVar().AGVWiat = false;
								}		
								AGVCar.getAGVComVar().conflictNode.occupy = true;
								AGVCar.getAGVComVar().conflictNode.waitQueue.add(AGVArray.get(NOOfAGV - 1));
								AGVCar.getAGVComVar().conflictEdgeRockwell.occupy = true;
								AGVCar.getAGVComVar().conflictEdgeRockwell.waitQueue.add(AGVArray.get(NOOfAGV - 1));
								AGVCar.getAGVComVar().conflictEdgeRockwell = null;
								AGVCar.getAGVComVar().conflictEdgeRockwell1.occupy = true;
								AGVCar.getAGVComVar().conflictEdgeRockwell1.waitQueue.add(AGVArray.get(NOOfAGV - 1));
								AGVCar.getAGVComVar().conflictEdgeRockwell1 = null;
								AGVCar.getAGVComVar().conflictEdgeRoute.occupy = true;
								AGVCar.getAGVComVar().conflictEdgeRoute.waitQueue.add(AGVArray.get(NOOfAGV - 1));
								AGVCar.getAGVComVar().conflictEdgeRoute = null;
								System.out.println("占用conflictEdgeRockwell1");
								logger.debug("占用conflictEdgeRockwell1");
							}else{
								AGVCar.getAGVComVar().AGVWiat = true;
								if(AGVCar.getAGVComVar().firstWait){
									AGVCar.getAGVComVar().firstWait = false;
									AGVCar.stateString = "等待前方AGV通过";
									System.out.println(this.NOOfAGV +"AGV wait conflictEdgeRockwell1");
								}
							}
						}else if(AGVCar.getAGVComVar().conflictEdgeRockwell != null && AGVCar.getAGVComVar().conflictEdgeRoute != null){
							if(!AGVCar.getAGVComVar().conflictEdgeRoute.occupy && !AGVCar.getAGVComVar().conflictEdgeRockwell.occupy){
								AGVCar.getAGVComVar().requartSend = false;
								AGVCar.getAGVComVar().sendRouteToAGV = true;
								this.AGVArray.get(this.NOOfAGV - 1).readyToLeft = true;
								System.out.println("conflictEdgeRockwell开始发送路径指令给" + this.NOOfAGV + "AGV");
								if(AGVCar.getAGVComVar().AGVWiat){
									AGVCar.getAGVComVar().AGVWiat = false;
								}		
								AGVCar.getAGVComVar().conflictNode.occupy = true;
								AGVCar.getAGVComVar().conflictNode.waitQueue.add(AGVArray.get(NOOfAGV - 1));
								AGVCar.getAGVComVar().conflictEdgeRockwell.occupy = true;
								AGVCar.getAGVComVar().conflictEdgeRockwell.waitQueue.add(AGVArray.get(NOOfAGV - 1));
								AGVCar.getAGVComVar().conflictEdgeRockwell = null;
								AGVCar.getAGVComVar().conflictEdgeRoute.occupy = true;
								AGVCar.getAGVComVar().conflictEdgeRoute.waitQueue.add(AGVArray.get(NOOfAGV - 1));
								AGVCar.getAGVComVar().conflictEdgeRoute = null;
								System.out.println("占用conflictEdgeRockwell");
								logger.debug("占用conflictEdgeRockwell");
							}else{
								AGVCar.getAGVComVar().AGVWiat = true;
								if(AGVCar.getAGVComVar().firstWait){
									AGVCar.getAGVComVar().firstWait = false;
									AGVCar.stateString = "等待前方AGV通过";
									System.out.println(this.NOOfAGV +"AGV wait conflictEdgeRockwell");
								}
							}
						}else if(AGVCar.getAGVComVar().conflictEdgeRoute != null){
							if(!AGVCar.getAGVComVar().conflictEdgeRoute.occupy){
								AGVCar.getAGVComVar().requartSend = false;
								AGVCar.getAGVComVar().sendRouteToAGV = true;
								this.AGVArray.get(this.NOOfAGV - 1).readyToLeft = true;
								System.out.println("conflictEdgeRoute开始发送路径指令给" + this.NOOfAGV + "AGV");
								if(AGVCar.getAGVComVar().AGVWiat){
									AGVCar.getAGVComVar().AGVWiat = false;
								}			
								AGVCar.getAGVComVar().conflictNode.occupy = true;
								AGVCar.getAGVComVar().conflictNode.waitQueue.add(AGVArray.get(NOOfAGV - 1));
								AGVCar.getAGVComVar().conflictEdgeRoute.occupy = true;
								AGVCar.getAGVComVar().conflictEdgeRoute.waitQueue.add(AGVArray.get(NOOfAGV - 1));
								AGVCar.getAGVComVar().conflictEdgeRoute = null;
								System.out.println("占用conflictEdgeRoute");
								logger.debug("占用conflictEdgeRoute");
							}else{
								AGVCar.getAGVComVar().AGVWiat = true;
								if(AGVCar.getAGVComVar().firstWait){
									AGVCar.getAGVComVar().firstWait = false;
									AGVCar.stateString = "等待前方AGV通过";
									System.out.println(this.NOOfAGV +"AGV wait conflictEdgeRoute");
								}
							}
						}else{
							AGVCar.getAGVComVar().requartSend = false;
							if(AGVCar.getAGVComVar().AGVWiat){
								System.out.println("延时5s开始发送路径指令" + NOOfAGV + "AGV");
								timer.schedule(new TimerTask(){
									public void run(){
										AGVCar.getAGVComVar().sendRouteToAGV = true;
										AGVArray.get(NOOfAGV - 1).readyToLeft = true;
									}
								}, 8000, TimeUnit.MILLISECONDS);
								timer.schedule(new TestAGVFinishStart(AGVArray.get(NOOfAGV -1)), 15000, TimeUnit.MILLISECONDS);
								AGVCar.getAGVComVar().AGVWiat = false;
							}else {
								AGVCar.getAGVComVar().sendRouteToAGV = true;
								this.AGVArray.get(this.NOOfAGV - 1).readyToLeft = true;
								System.out.println("node开始发送路径指令给" + this.NOOfAGV + "AGV");
								timer.schedule(new TestAGVFinishStart(AGVArray.get(NOOfAGV -1)), 10000, TimeUnit.MILLISECONDS);
							}			
							AGVCar.getAGVComVar().conflictNode.occupy = true;
							AGVCar.getAGVComVar().conflictNode.waitQueue.add(AGVArray.get(NOOfAGV - 1));
						}
					}else{
						AGVCar.getAGVComVar().AGVWiat = true;
						if(AGVCar.getAGVComVar().firstWait){
							AGVCar.stateString = "等待前方AGV通过";
							System.out.println(this.NOOfAGV +"AGV wait node");
							AGVCar.getAGVComVar().firstWait = false;
						}
					}								
				}
			}
			String sendMessage = null;
			if(AGVCar.getAGVComVar().sendRouteToAGV || AGVCar.getAGVComVar().sendShipmentToAGV || AGVCar.getAGVComVar().sendUnloadingToAGV || AGVCar.getAGVComVar().sendStartingToAGV
					|| AGVCar.getAGVComVar().sendStopToAGV || AGVCar.getAGVComVar().sendAccessToAGV || AGVCar.getAGVComVar().sendChargeAGV || AGVCar.getAGVComVar().sendOffDutyToAGV 
					|| (AGVCar.getAGVComVar().sendOnDutyToAGV && this.onDutyBtn.state)){
				if(System.currentTimeMillis() - lastCommunicationTime > 1000 || this.firstSend 
						|| (AGVCar.getAGVComVar().sendStopToAGV && System.currentTimeMillis() - lastCommunicationTime > 300)){
					if(AGVCar.getAGVComVar().sendShipmentToAGV){
						sendMessage = AGVCar.getAGVComVar().shipmentString;
						//System.out.println("发shipment给" + this.NOOfAGV);
					}
					if(AGVCar.getAGVComVar().sendUnloadingToAGV){
						sendMessage = AGVCar.getAGVComVar().unloadingString;
						//System.out.println("发unloading给" + this.NOOfAGV);
					}
					if(AGVCar.getAGVComVar().sendStopToAGV){
						sendMessage = AGVCar.getAGVComVar().stopString;
						//System.out.println("发stop给" + this.NOOfAGV);
					}
					if(AGVCar.getAGVComVar().sendStartingToAGV){
						sendMessage = AGVCar.getAGVComVar().startString;
						//System.out.println("发starting给" + this.NOOfAGV);
					}
					if(AGVCar.getAGVComVar().sendAccessToAGV){
						sendMessage = AGVCar.getAGVComVar().accessString;
						//System.out.println("发access给" + this.NOOfAGV);
					}
					if(AGVCar.getAGVComVar().sendChargeAGV){
						sendMessage = AGVCar.getAGVComVar().chargeString;
						//System.out.println("发charge给" + this.NOOfAGV);
					}
					if(AGVCar.getAGVComVar().sendOnDutyToAGV && this.onDutyBtn.state){
						sendMessage = AGVCar.getAGVComVar().onDutyString;
						System.out.println("开始发送上班指令" + this.NOOfAGV + "AGV");
						//System.out.println("发onDuty给" + this.NOOfAGV);
					}
					
					if(AGVCar.getAGVComVar().sendOffDutyToAGV){
						sendMessage = AGVCar.getAGVComVar().offDutyString;
						//System.out.println("发offDuty给" + this.NOOfAGV);
					}
					if(AGVCar.getAGVComVar().sendRouteToAGV){
					//	System.out.println("发route给" + this.NOOfAGV);
						sendMessage = AGVCar.getAGVComVar().routeString;
					}
					if(AGVCar.getAGVComVar().sendChargeAGV && System.currentTimeMillis() - AGVCar.getAGVComVar().sendChargeTime > 10000){
						AGVCar.getAGVComVar().sendChargeAGV = false;
						AGVCar.getAGVComVar().chargeString = "BBBBBB";
					}
					System.out.println(sendMessage + "给//" + this.NOOfAGV + "AGV");
					logger.debug(sendMessage + "给//" + this.NOOfAGV + "AGV");
					if(this.firstSend){
						this.firstSend = false;
						for(int i = 0; i < 3; i++){
							try{
								if(!sendMessage.equals("BBBBBB"))
									outputStream.write(myToolKit.HexString2Bytes(sendMessage));
								else
									System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
								//if(this.sendMessageStr.equals("CC05DD")|| this.sendMessageStr.equals("CC06DD"))
									//System.out.print(this.sendMessageStr + "给" + this.NOOfAGV + "AGV");
							}catch(Exception e){
								if(e instanceof SocketException || e instanceof IOException){
									System.out.println(this.NOOfAGV + "AGV发生通讯错误？？？？？？？？？？？？？？？？");
									logger.debug(this.NOOfAGV + "AGV发生通讯错误？？？？？？？？？？？？？？？？");
									logger.error(this.NOOfAGV + "AGV发生通讯错误？？？？？？？？？？？？？？？？");
								}									
								logger.error(e);
								e.printStackTrace();
								try{
									if(this.inputStream != null)
										this.inputStream.close();
									if(this.outputStream != null)
										this.outputStream.close();
									if(this.socket != null)
										this.socket.close();
								}catch(Exception e1){
									e1.printStackTrace();
									logger.error(e);
								}
								break;
							}
						}
					}
					
					try{						
						if(!sendMessage.equals("BBBBBB")){
							outputStream.write(myToolKit.HexString2Bytes(sendMessage));
						}else{
							//System.out.println(sendMessage + "给//" + this.NOOfAGV + "AGV");
							//System.out.println(">>>>>>>>>>>>>>>>>//>>>>>>>>>>>>>>>>>>>>>");
						}
						//if(this.sendMessageStr.equals("CC05DD")|| this.sendMessageStr.equals("CC06DD"))
							//System.out.print(this.sendMessageStr + "给" + this.NOOfAGV + "AGV");
					}catch(Exception e){
						if(e instanceof SocketException || e instanceof IOException){
							System.out.println(this.NOOfAGV + "AGV发生通讯错误？？？？？？？？？？？？？？？？");
							logger.debug(this.NOOfAGV + "AGV发生通讯错误？？？？？？？？？？？？？？？？");
							logger.error(this.NOOfAGV + "AGV发生通讯错误？？？？？？？？？？？？？？？？");
						}							
						e.printStackTrace();
						logger.error(e);
						try{
							if(this.inputStream != null)
								this.inputStream.close();
							if(this.outputStream != null)
								this.outputStream.close();
							if(this.socket != null)
								this.socket.close();
						}catch(Exception e1){
							e1.printStackTrace();
							logger.error(e);
						}
						this.deleteFaultedAGV = true;
					}
					this.lastCommunicationTime = System.currentTimeMillis();
				}
			}
			
			
			
			if(this.deleteFaultedAGV){
				System.out.println(this.NOOfAGV + "AGVsocket cancelAGV true");
				logger.debug(this.NOOfAGV + "AGVsocket cancelAGV true");
			}			
		}		
	
	}
	
	public void stopSendMessToAGV(){
		if(AGVCar.getAGVComVar().sendOffDutyToAGV){
			AGVCar.getAGVComVar().sendOffDutyToAGV = false;
			AGVCar.getAGVComVar().offDutyString = "BBBBBB";
			System.out.println("结束发送下班指令给" + this.NOOfAGV + "AGV");
			logger.debug("结束发送下班指令给" + this.NOOfAGV + "AGV");
		}
		if(AGVCar.getAGVComVar().sendOnDutyToAGV){
			AGVCar.getAGVComVar().sendOnDutyToAGV = false;
			AGVCar.getAGVComVar().onDutyString = "BBBBBB";
			System.out.println("结束发送上班指令给"+ this.NOOfAGV + "AGV");
			logger.debug("结束发送上班指令给"+ this.NOOfAGV + "AGV");
		}
		if(AGVCar.getAGVComVar().sendRouteToAGV){
			AGVCar.getAGVComVar().sendRouteToAGV = false;
			AGVCar.getAGVComVar().routeString = "BBBBBB";
			System.out.println("结束发送路径指令给"+ this.NOOfAGV + "AGV");
			logger.debug("结束发送路径指令给"+ this.NOOfAGV + "AGV");
		}
		if(AGVCar.getAGVComVar().sendAccessToAGV){
			AGVCar.getAGVComVar().sendAccessToAGV = false;
			AGVCar.getAGVComVar().accessString = "BBBBBB";
			System.out.println("结束发送加速指令给"+ this.NOOfAGV + "AGV");
			logger.debug("结束发送加速指令给"+ this.NOOfAGV + "AGV");
		}
		if(AGVCar.getAGVComVar().sendStartingToAGV){
			AGVCar.getAGVComVar().sendStartingToAGV = false;
			AGVCar.getAGVComVar().startString = "BBBBBB";
			System.out.println("结束发送运行指令给"+ this.NOOfAGV + "AGV");
			logger.debug("结束发送运行指令给"+ this.NOOfAGV + "AGV");
		}
		
		if(AGVCar.getAGVComVar().sendStopToAGV && AGVCar.getAGVComVar().stopString.equals("CC01DD")){
			AGVCar.getAGVComVar().sendStopToAGV = false;
			AGVCar.getAGVComVar().stopString = "BBBBBB";
			this.canNotStop = true;
			AGVCar.stateString = "命令AGV停止，但没停下来";
			System.out.println(this.NOOfAGV + "AGV发送停止，但没停下来");
			logger.debug(this.NOOfAGV + "AGV发送停止，但没停下来");
			logger.error(this.NOOfAGV + "AGV发送停止，但没停下来");
			//playAudio.continuePlay();
		}
	}
	
	public void deleteFaultedAGV(){
		this.deleteFaultedAGV = true;
	}	
	
	class TestFinishShipmentUnloading extends TimerTask{
		private  AGVCar car = null;
		public TestFinishShipmentUnloading(AGVCar car){
			this.car = car;
		}
		

		public void run(){
			if(!this.car.checkFinishShipment && !this.car.checkFinishUnloading){
				logger.error("没有收到"+ NOOfAGV+"卸料上料完成信号");
				//playAudio.continuePlay();
				System.out.println("没有收到"+ NOOfAGV+"卸料上料完成信号");
			}else{
				this.car.checkFinishShipment = false;
				this.car.checkFinishUnloading = false;
			}
		}		
	}
	
	class TestAGVFinishStart extends TimerTask{
		private  AGVCar car = null;
		public TestAGVFinishStart(AGVCar car){
			this.car = car;
		}
		

		public void run(){
			if(!this.car.finishStart){
				logger.error("发送指令给"+ NOOfAGV+"AGV，但AGV没有启动");
				//playAudio.continuePlay();
				System.out.println("发送指令给"+ NOOfAGV+"AGV，但AGV没有启动");
			}else{
				this.car.finishStart = false;
			}
		}		
	}
	
	public ArrayList<Edge> getConflictEdgeRockwell(ArrayList<Integer> routeNode){
		ArrayList<Edge> returnEdge = new ArrayList<>();
		int start = 0, end = 0;
		int start1 = 0, end1 = 0;
		if(routeNode.get(0)== 12 && routeNode.get(routeNode.size()-1) == 14){
			start = 15;
			end = 13;
		}else if(routeNode.get(0)== 16 && routeNode.get(routeNode.size()-1) == 18){
			start = 19;
			end = 17;
		}else if(routeNode.get(0)== 20 && routeNode.get(routeNode.size()-1) == 22){
			start = 23;
			end = 21;
		}else if(routeNode.get(0)== 24 && routeNode.get(routeNode.size()-1) == 26){
			start = 45;
			end = 25;
		}else if(routeNode.get(0)== 29 && routeNode.get(routeNode.size()-1) == 27){
			start = 28;
			end = 58;
		}else if(routeNode.get(0)== 33 && routeNode.get(routeNode.size()-1) == 31){
			start = 30;
			end = 32;
		}else if(routeNode.get(0)== 37 && routeNode.get(routeNode.size()-1) == 35){
			start = 34;
			end = 36;
		}else if(routeNode.get(0)== 41 && routeNode.get(routeNode.size()-1) == 39){
			start = 38;
			end = 40;
		}else{
			if(routeNode.get(0) == 14){
				start = 15;
				end = 13;
			}else if(routeNode.get(0) == 18){
				start = 19;
				end = 17;
			}else if(routeNode.get(0) == 22){
				start = 23;
				end = 21;
			}else if(routeNode.get(0) == 26){
				start = 45;
				end = 25;
			}else if(routeNode.get(0) == 27){
				start = 28;
				end = 58;
			}else if(routeNode.get(0) == 31){
				start = 30;
				end = 32;
			}else if(routeNode.get(0) == 35){
				start = 34;
				end = 36;
			}else if(routeNode.get(0) == 39){
				start = 38;
				end = 40;
			}else if(routeNode.get(0) == 12){
				start = 11;
				end = 13;
			}else if(routeNode.get(0) == 16){
				start = 17;
				end = 15;
			}else if(routeNode.get(0) == 20){
				start = 19;
				end = 21;
			}else if(routeNode.get(0) == 24){
				start = 23;
				end = 25;
			}else if(routeNode.get(0) == 29){
				start = 28;
				end = 30;
			}else if(routeNode.get(0) == 33){
				start = 32;
				end = 34;
			}else if(routeNode.get(0) == 37){
				start = 36;
				end = 38;
			}else if(routeNode.get(0) == 41){
				start = 40;
				end = 42;
			}else if(routeNode.get(0) == 1){
				start = 4;
				end = 2;
			}else if(routeNode.get(0) == 3){
				start = 6;
				end = 4;
			}else if(routeNode.get(0) == 5){
				start = 8;
				end = 6;
			}else if(routeNode.get(0) == 7){
				start = 10;
				end = 8;
			}
		}
		
		if((routeNode.get(0)== 12 && routeNode.get(routeNode.size()-1) == 14)
				||(routeNode.get(0)== 16 && routeNode.get(routeNode.size()-1) == 18)
				||(routeNode.get(0)== 20 && routeNode.get(routeNode.size()-1) == 22)
				||(routeNode.get(0)== 24 && routeNode.get(routeNode.size()-1) == 26)
				||(routeNode.get(0)== 29 && routeNode.get(routeNode.size()-1) == 27)
				||(routeNode.get(0)== 33 && routeNode.get(routeNode.size()-1) == 31)
				||(routeNode.get(0)== 37 && routeNode.get(routeNode.size()-1) == 35)
				||(routeNode.get(0)== 41 && routeNode.get(routeNode.size()-1) == 39)){
			if(routeNode.get(0) == 16){
				start1 = 15;
				end1 = 13;
			}else if(routeNode.get(0) == 20){
				start1 = 19;
				end1 = 17;
			}else if(routeNode.get(0) == 24){
				start1 = 23;
				end1 = 21;
			}else if(routeNode.get(0) == 29){
				start1 = 30;
				end1 = 32;
			}else if(routeNode.get(0) == 33){
				start1 = 34;
				end1 = 36;
			}else if(routeNode.get(0) == 37){
				start1 = 38;
				end1 = 40;
			}
		}
		returnEdge.add(new Edge(new Node(0,0,start), new Node(0,0,end)));
		returnEdge.add(new Edge(new Node(0,0,start1), new Node(0,0,end1)));
		return returnEdge;
	}
}