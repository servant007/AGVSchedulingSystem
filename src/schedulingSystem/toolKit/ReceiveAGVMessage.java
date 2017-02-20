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

import org.apache.log4j.Logger;
import schedulingSystem.component.AGVCar;
import schedulingSystem.component.ConflictEdge;
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
	private boolean cancelAGV;
	private OnDutyBtn onDutyBtn;
	private PlayAudio playAudio;
	private boolean firstSend;
	private AGVCar agvCar;
	private boolean canNotStop;

	public ReceiveAGVMessage(Socket socket, ArrayList<AGVCar> AGVArray, Graph graph, OnDutyBtn onDutyBtn, PlayAudio playAudio){
		this.onDutyBtn = onDutyBtn;
		timer = Executors.newScheduledThreadPool(3);
		this.playAudio = playAudio;
		timer.schedule(new TimerTask(){
			public void run(){
				clearBuffer = true;
			}
		}, 3000, TimeUnit.MILLISECONDS);		
		this.lastMessage = "";
		this.myToolKit = new MyToolKit();
		this.graph = graph;
		this.AGVArray = AGVArray;
		System.out.println("socket connect agv message:"+socket.toString());
		logger.debug("socket connect agv message:"+socket.toString() + this.hashCode());
		//logger.error("socket connect agv message:"+socket.toString() + this.hashCode());
		this.socket = socket;
		try{
			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e);
			//playAudio.continuePlay();
		}
		lastCommunicationTime = System.currentTimeMillis();
	}
	
	public void SendActionMessage(String message){//���ϡ�ж�ϡ�ֹͣ�����С�����
		this.lastSignal = 0;//����һ�������ȴ�һ��״̬��������һ��״̬��ʲô
		this.firstSend = true;
		agvCar.getAGVComVar().sendChargeTime = System.currentTimeMillis();
		if(message.equals("CC01DD")){//ֹͣ
			agvCar.getAGVComVar().sendStopToAGV = true;
			agvCar.getAGVComVar().stopString = message;
			System.out.println("��ʼ����ָֹͣ��" + this.NOOfAGV + "AGV");
			logger.debug("��ʼ����ָֹͣ��" + this.NOOfAGV + "AGV");
		}else if(message.equals("CC02DD")){//����
			agvCar.getAGVComVar().sendShipmentToAGV = true;
			agvCar.getAGVComVar().shipmentString = message;
			//timer.schedule(new TestFinishShipmentUnloading(AGVArray.get(NOOfAGV -1)), 60000);
			System.out.println("��ʼ��������ָ��" + this.NOOfAGV + "AGV");
			logger.debug("��ʼ��������ָ��" + this.NOOfAGV + "AGV");
		}else if(message.equals("CC03DD")){//ж��
			agvCar.getAGVComVar().sendUnloadingToAGV = true;
			agvCar.getAGVComVar().unloadingString = message;
			//timer.schedule(new TestFinishShipmentUnloading(AGVArray.get(NOOfAGV -1)), 60000);
			System.out.println("��ʼ����ж��ָ��" + this.NOOfAGV + "AGV");
			logger.debug("��ʼ����ж��ָ��" + this.NOOfAGV + "AGV");
		}else if(message.equals("CC04DD")){//���
			agvCar.getAGVComVar().sendChargeAGV = true;
			agvCar.getAGVComVar().chargeString = message;
			System.out.println("��ʼ���ͳ��ָ��" + this.NOOfAGV + "AGV");
			logger.debug("��ʼ���ͳ��ָ��" + this.NOOfAGV + "AGV");
		}else if(message.equals("CC05DD")||message.equals("CC06DD")){//����
			agvCar.getAGVComVar().sendStartingToAGV = true;
			agvCar.getAGVComVar().startString = message;
			System.out.println("��ʼ��������ָ��" + message + this.NOOfAGV + "AGV");
			logger.debug("��ʼ��������ָ��" + message + this.NOOfAGV + "AGV");
		}else if(message.equals("CC07DD")){//����
			agvCar.getAGVComVar().sendAccessToAGV = true;
			agvCar.getAGVComVar().accessString = message;
			System.out.println("��ʼ���ͼ���ָ��" + this.NOOfAGV + "AGV");
			logger.debug("��ʼ���ͼ���ָ��" + this.NOOfAGV + "AGV");
		}else if(message.equals("CC09DD")){
			agvCar.getAGVComVar().sendOffDutyToAGV = true;
			agvCar.getAGVComVar().offDutyString = message;
			System.out.println("��ʼ�����°�ָ��" + this.NOOfAGV + "AGV");
			logger.debug("��ʼ�����°�ָ��" + this.NOOfAGV + "AGV");
		}			
		//System.out.println("��ʼ���Ͷ���ָ��" + this.NOOfAGV + "AGV");
	}
	
	public ArrayList<ConflictEdge> SendRouteMessageRockwell(String sendMessageStr, ArrayList<Integer> routeNode){//����·��
		ArrayList<ConflictEdge> returnConflictEdge = new ArrayList<ConflictEdge>();
		this.lastSignal = 0;//����һ�������ȴ�һ��״̬
		agvCar.getAGVComVar().firstWait = true;
		agvCar.getAGVComVar().requartSend = true;
		agvCar.getAGVComVar().routeString = sendMessageStr;
		agvCar.getAGVComVar().conflictNode = AGVArray.get(NOOfAGV - 1).getConflictDetection().getConflictNodeArray().get(routeNode.get(1) - 1);
		this.conflictEdgeArray = AGVArray.get(NOOfAGV - 1).getConflictDetection().getConflictEdgeArray();
	//	System.out.println("routeNode"+ routeNode.get(1) + "||" + routeNode.get(2));
		ArrayList<Edge> returnEdge = getConflictEdgeRockwell(routeNode);
		for(int i = 0; i < conflictEdgeArray.size(); i++){
			if(returnEdge.size() >= 1){
				if((returnEdge.get(0).startNode.num == conflictEdgeArray.get(i).stratNodeNum && returnEdge.get(0).endNode.num == conflictEdgeArray.get(i).endNodeNum)
						||(returnEdge.get(0).startNode.num == conflictEdgeArray.get(i).endNodeNum && returnEdge.get(0).endNode.num == conflictEdgeArray.get(i).stratNodeNum)){
					agvCar.getAGVComVar().conflictEdgeRockwell = conflictEdgeArray.get(i);
					System.out.println("conflictEdgeRockwell"+ conflictEdgeArray.get(i).endNodeNum + "||" + conflictEdgeArray.get(i).stratNodeNum);
				}
				if(returnEdge.size() == 2){
					if((returnEdge.get(1).startNode.num == conflictEdgeArray.get(i).stratNodeNum && returnEdge.get(1).endNode.num == conflictEdgeArray.get(i).endNodeNum)
							||(returnEdge.get(1).startNode.num == conflictEdgeArray.get(i).endNodeNum && returnEdge.get(1).endNode.num == conflictEdgeArray.get(i).stratNodeNum)){
						agvCar.getAGVComVar().conflictEdgeRockwell1 = conflictEdgeArray.get(i);
						System.out.println("conflictEdgeRockwell1"+ conflictEdgeArray.get(i).endNodeNum + "||" + conflictEdgeArray.get(i).stratNodeNum);
					}
				}
			}
			
			if((routeNode.get(1) == conflictEdgeArray.get(i).stratNodeNum && routeNode.get(2) == conflictEdgeArray.get(i).endNodeNum)
					||(routeNode.get(1) == conflictEdgeArray.get(i).endNodeNum && routeNode.get(2) == conflictEdgeArray.get(i).stratNodeNum)){
				agvCar.getAGVComVar().conflictEdgeRoute = conflictEdgeArray.get(i);
				//System.out.println("conflictEdgeRoute"+ conflictEdgeArray.get(i).endNodeNum + "||" + conflictEdgeArray.get(i).stratNodeNum);
			}
			if(agvCar.getAGVComVar().conflictEdgeRockwell != null && agvCar.getAGVComVar().conflictEdgeRoute != null && agvCar.getAGVComVar().conflictEdgeRockwell1 != null){
				break;
			}
		}
		if(agvCar.getAGVComVar().conflictEdgeRockwell1 != null)
			returnConflictEdge.add(agvCar.getAGVComVar().conflictEdgeRockwell1);
		if(agvCar.getAGVComVar().conflictEdgeRockwell != null)
			returnConflictEdge.add(agvCar.getAGVComVar().conflictEdgeRockwell);
		return returnConflictEdge;
	}
	
	public void run(){
		while(!this.cancelAGV){
			if(agvCar != null){
				if(agvCar.getAGVComVar().requartSend ){
					synchronized(agvCar.getAGVComVar().conflictNode){
						if(!agvCar.getAGVComVar().conflictNode.occupy || (agvCar.getAGVComVar().conflictNode.waitQueue.size() > 0 &&agvCar.getAGVComVar().conflictNode.waitQueue.get(0).getAGVNum() == this.NOOfAGV)
								|| (agvCar.getAGVComVar().conflictNode.occupy && agvCar.getAGVComVar().conflictNode.waitQueue.size() > 0 && agvCar.getAGVComVar().conflictNode.waitQueue.get(0).getRouteNode().size() > 0 &&
										this.graph.getNode(agvCar.getAGVComVar().conflictNode.waitQueue.get(0).getRouteNode().get(agvCar.getAGVComVar().conflictNode.waitQueue.get(0).getRouteNode().size()-1) - 1).num == 9)){
							if(agvCar.getAGVComVar().conflictEdgeRockwell != null && agvCar.getAGVComVar().conflictEdgeRockwell1 != null && agvCar.getAGVComVar().conflictEdgeRoute != null){
								if(!agvCar.getAGVComVar().conflictEdgeRoute.occupy && !agvCar.getAGVComVar().conflictEdgeRockwell.occupy && !agvCar.getAGVComVar().conflictEdgeRockwell1.occupy){
									agvCar.getAGVComVar().requartSend = false;
									agvCar.getAGVComVar().sendRouteToAGV = true;
									this.AGVArray.get(this.NOOfAGV - 1).readyToLeft = true;
									System.out.println("conflictEdgeRockwell1��ʼ����·��ָ���" + this.NOOfAGV + "AGV");
									if(agvCar.getAGVComVar().AGVWiat){
										agvCar.getAGVComVar().AGVWiat = false;
									}		
									agvCar.getAGVComVar().conflictNode.occupy = true;
									agvCar.getAGVComVar().conflictNode.waitQueue.add(AGVArray.get(NOOfAGV - 1));
									agvCar.getAGVComVar().conflictEdgeRockwell.occupy = true;
									agvCar.getAGVComVar().conflictEdgeRockwell.waitQueue.add(AGVArray.get(NOOfAGV - 1));
									agvCar.getAGVComVar().conflictEdgeRockwell = null;
									agvCar.getAGVComVar().conflictEdgeRockwell1.occupy = true;
									agvCar.getAGVComVar().conflictEdgeRockwell1.waitQueue.add(AGVArray.get(NOOfAGV - 1));
									agvCar.getAGVComVar().conflictEdgeRockwell1 = null;
									agvCar.getAGVComVar().conflictEdgeRoute.occupy = true;
									agvCar.getAGVComVar().conflictEdgeRoute.waitQueue.add(AGVArray.get(NOOfAGV - 1));
									agvCar.getAGVComVar().conflictEdgeRoute = null;
									System.out.println("ռ��conflictEdgeRockwell1");
									logger.debug("ռ��conflictEdgeRockwell1");
								}else{
									agvCar.getAGVComVar().AGVWiat = true;
									if(agvCar.getAGVComVar().firstWait){
										agvCar.getAGVComVar().firstWait = false;
										System.out.println(this.NOOfAGV +"AGV wait conflictEdgeRockwell1");
									}
								}
							}else if(agvCar.getAGVComVar().conflictEdgeRockwell != null && agvCar.getAGVComVar().conflictEdgeRoute != null){
								if(!agvCar.getAGVComVar().conflictEdgeRoute.occupy && !agvCar.getAGVComVar().conflictEdgeRockwell.occupy){
									agvCar.getAGVComVar().requartSend = false;
									agvCar.getAGVComVar().sendRouteToAGV = true;
									this.AGVArray.get(this.NOOfAGV - 1).readyToLeft = true;
									System.out.println("conflictEdgeRockwell��ʼ����·��ָ���" + this.NOOfAGV + "AGV");
									if(agvCar.getAGVComVar().AGVWiat){
										agvCar.getAGVComVar().AGVWiat = false;
									}		
									agvCar.getAGVComVar().conflictNode.occupy = true;
									agvCar.getAGVComVar().conflictNode.waitQueue.add(AGVArray.get(NOOfAGV - 1));
									agvCar.getAGVComVar().conflictEdgeRockwell.occupy = true;
									agvCar.getAGVComVar().conflictEdgeRockwell.waitQueue.add(AGVArray.get(NOOfAGV - 1));
									agvCar.getAGVComVar().conflictEdgeRockwell = null;
									agvCar.getAGVComVar().conflictEdgeRoute.occupy = true;
									agvCar.getAGVComVar().conflictEdgeRoute.waitQueue.add(AGVArray.get(NOOfAGV - 1));
									agvCar.getAGVComVar().conflictEdgeRoute = null;
									System.out.println("ռ��conflictEdgeRockwell");
									logger.debug("ռ��conflictEdgeRockwell");
								}else{
									agvCar.getAGVComVar().AGVWiat = true;
									if(agvCar.getAGVComVar().firstWait){
										agvCar.getAGVComVar().firstWait = false;
										System.out.println(this.NOOfAGV +"AGV wait conflictEdgeRockwell");
									}
								}
							}else if(agvCar.getAGVComVar().conflictEdgeRoute != null){
								if(!agvCar.getAGVComVar().conflictEdgeRoute.occupy){
									agvCar.getAGVComVar().requartSend = false;
									agvCar.getAGVComVar().sendRouteToAGV = true;
									this.AGVArray.get(this.NOOfAGV - 1).readyToLeft = true;
									System.out.println("conflictEdgeRoute��ʼ����·��ָ���" + this.NOOfAGV + "AGV");
									if(agvCar.getAGVComVar().AGVWiat){
										agvCar.getAGVComVar().AGVWiat = false;
									}			
									agvCar.getAGVComVar().conflictNode.occupy = true;
									agvCar.getAGVComVar().conflictNode.waitQueue.add(AGVArray.get(NOOfAGV - 1));
									agvCar.getAGVComVar().conflictEdgeRoute.occupy = true;
									agvCar.getAGVComVar().conflictEdgeRoute.waitQueue.add(AGVArray.get(NOOfAGV - 1));
									agvCar.getAGVComVar().conflictEdgeRoute = null;
									System.out.println("ռ��conflictEdgeRoute");
									logger.debug("ռ��conflictEdgeRoute");
								}else{
									agvCar.getAGVComVar().AGVWiat = true;
									if(agvCar.getAGVComVar().firstWait){
										agvCar.getAGVComVar().firstWait = false;
										System.out.println(this.NOOfAGV +"AGV wait conflictEdgeRoute");
									}
								}
							}else{
								agvCar.getAGVComVar().requartSend = false;
								if(agvCar.getAGVComVar().AGVWiat){
									System.out.println("��ʱ5s��ʼ����·��ָ��" + NOOfAGV + "AGV");
									timer.schedule(new TimerTask(){
										public void run(){
											agvCar.getAGVComVar().sendRouteToAGV = true;
											AGVArray.get(NOOfAGV - 1).readyToLeft = true;
										}
									}, 8000, TimeUnit.MILLISECONDS);
									timer.schedule(new TestAGVFinishStart(AGVArray.get(NOOfAGV -1)), 15000, TimeUnit.MILLISECONDS);
									agvCar.getAGVComVar().AGVWiat = false;
								}else {
									agvCar.getAGVComVar().sendRouteToAGV = true;
									this.AGVArray.get(this.NOOfAGV - 1).readyToLeft = true;
									System.out.println("node��ʼ����·��ָ���" + this.NOOfAGV + "AGV");
									timer.schedule(new TestAGVFinishStart(AGVArray.get(NOOfAGV -1)), 10000, TimeUnit.MILLISECONDS);
								}			
								agvCar.getAGVComVar().conflictNode.occupy = true;
								agvCar.getAGVComVar().conflictNode.waitQueue.add(AGVArray.get(NOOfAGV - 1));
							}
						}else{
							agvCar.getAGVComVar().AGVWiat = true;
							if(agvCar.getAGVComVar().firstWait){
								System.out.println(this.NOOfAGV +"AGV wait node");
								agvCar.getAGVComVar().firstWait = false;
							}
						}				
					
					}
				}
				String sendMessage = null;
				if(agvCar.getAGVComVar().sendRouteToAGV || agvCar.getAGVComVar().sendShipmentToAGV || agvCar.getAGVComVar().sendUnloadingToAGV || agvCar.getAGVComVar().sendStartingToAGV
						|| agvCar.getAGVComVar().sendStopToAGV || agvCar.getAGVComVar().sendAccessToAGV || agvCar.getAGVComVar().sendChargeAGV || agvCar.getAGVComVar().sendOffDutyToAGV 
						|| (agvCar.getAGVComVar().sendOnDutyToAGV && this.onDutyBtn.state)){
					if(System.currentTimeMillis() - lastCommunicationTime > 1000 || this.firstSend 
							|| (agvCar.getAGVComVar().sendStopToAGV && System.currentTimeMillis() - lastCommunicationTime > 300)){
						if(agvCar.getAGVComVar().sendShipmentToAGV){
							sendMessage = agvCar.getAGVComVar().shipmentString;
							//System.out.println("��shipment��" + this.NOOfAGV);
						}
						if(agvCar.getAGVComVar().sendUnloadingToAGV){
							sendMessage = agvCar.getAGVComVar().unloadingString;
							//System.out.println("��unloading��" + this.NOOfAGV);
						}
						if(agvCar.getAGVComVar().sendStopToAGV){
							sendMessage = agvCar.getAGVComVar().stopString;
							//System.out.println("��stop��" + this.NOOfAGV);
						}
						if(agvCar.getAGVComVar().sendStartingToAGV){
							sendMessage = agvCar.getAGVComVar().startString;
							//System.out.println("��starting��" + this.NOOfAGV);
						}
						if(agvCar.getAGVComVar().sendAccessToAGV){
							sendMessage = agvCar.getAGVComVar().accessString;
							//System.out.println("��access��" + this.NOOfAGV);
						}
						if(agvCar.getAGVComVar().sendChargeAGV){
							sendMessage = agvCar.getAGVComVar().chargeString;
							//System.out.println("��charge��" + this.NOOfAGV);
						}
						if(agvCar.getAGVComVar().sendOnDutyToAGV && this.onDutyBtn.state){
							sendMessage = agvCar.getAGVComVar().onDutyString;
							System.out.println("��ʼ�����ϰ�ָ��" + this.NOOfAGV + "AGV");
							//System.out.println("��onDuty��" + this.NOOfAGV);
						}
						
						if(agvCar.getAGVComVar().sendOffDutyToAGV){
							sendMessage = agvCar.getAGVComVar().offDutyString;
							//System.out.println("��offDuty��" + this.NOOfAGV);
						}
						if(agvCar.getAGVComVar().sendRouteToAGV){
						//	System.out.println("��route��" + this.NOOfAGV);
							sendMessage = agvCar.getAGVComVar().routeString;
						}
						if(agvCar.getAGVComVar().sendChargeAGV && System.currentTimeMillis() - agvCar.getAGVComVar().sendChargeTime > 10000){
							agvCar.getAGVComVar().sendChargeAGV = false;
							agvCar.getAGVComVar().chargeString = "BBBBBB";
						}
						System.out.println(sendMessage + "��//" + this.NOOfAGV + "AGV");
						logger.debug(sendMessage + "��//" + this.NOOfAGV + "AGV");
						if(this.firstSend){
							this.firstSend = false;
							for(int i = 0; i < 3; i++){
								try{
									if(!sendMessage.equals("BBBBBB"))
										outputStream.write(myToolKit.HexString2Bytes(sendMessage));
									else
										System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
									//if(this.sendMessageStr.equals("CC05DD")|| this.sendMessageStr.equals("CC06DD"))
										//System.out.print(this.sendMessageStr + "��" + this.NOOfAGV + "AGV");
								}catch(Exception e){
									if(e instanceof SocketException || e instanceof IOException){
										System.out.println(this.NOOfAGV + "AGV����ͨѶ���󣿣�����������������������������");
										logger.debug(this.NOOfAGV + "AGV����ͨѶ���󣿣�����������������������������");
										logger.error(this.NOOfAGV + "AGV����ͨѶ���󣿣�����������������������������");
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
								System.out.println(sendMessage + "��//" + this.NOOfAGV + "AGV");
								System.out.println(">>>>>>>>>>>>>>>>>//>>>>>>>>>>>>>>>>>>>>>");
							}
							//if(this.sendMessageStr.equals("CC05DD")|| this.sendMessageStr.equals("CC06DD"))
								//System.out.print(this.sendMessageStr + "��" + this.NOOfAGV + "AGV");
						}catch(Exception e){
							if(e instanceof SocketException || e instanceof IOException){
								System.out.println(this.NOOfAGV + "AGV����ͨѶ���󣿣�����������������������������");
								logger.debug(this.NOOfAGV + "AGV����ͨѶ���󣿣�����������������������������");
								logger.error(this.NOOfAGV + "AGV����ͨѶ���󣿣�����������������������������");
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
							break;
						}
						this.lastCommunicationTime = System.currentTimeMillis();
					}
				}
				
				
				
				if(this.cancelAGV){
					System.out.println(this.NOOfAGV + "AGVsocket cancelAGV true");
					logger.debug(this.NOOfAGV + "AGVsocket cancelAGV true");
				}			
			}
			
			try{
				if(inputStream.available() > 0){
					String message = "";
					if(!foundStart){
						byte[] endCode = new byte[1];
						inputStream.read(endCode);
						message = myToolKit.printHexString(endCode);
						if(message.equals("CC") || message.equals("AA")){
							foundStart = true;
						}else if(this.clearBuffer){
							System.out.println(this.NOOfAGV + "AGV��ͷ���ݴ���" + message);
							logger.debug(this.NOOfAGV + "AGV��ͷ���ݴ���" + message);
						}
					}
					if(foundStart){
						foundStart = false;
						byte[] buff = new byte[4];
						inputStream.read(buff);
						message = myToolKit.printHexString(buff);
						//System.out.print(message);
						if(this.clearBuffer && (!message.equals(lastMessage))){	
							if(message.endsWith("BB")){
								NOOfAGV = Integer.parseInt(message.substring(0, 2), 16);
								if(!oldRunnable && (NOOfAGV <= AGVArray.size())){
									if(AGVArray.get(NOOfAGV-1).getRunnable() != null ){
										AGVArray.get(NOOfAGV-1).getRunnable().setCancelAGV();
									}
									System.out.println(NOOfAGV +"AGV����ʱȷ��runnable");
									logger.debug(NOOfAGV +"AGV����ʱȷ��runnable");
									AGVArray.get(NOOfAGV-1).setRunnabel(this);
									agvCar = AGVArray.get(NOOfAGV-1);
									oldRunnable = true;
									this.lastNOOfAGV = this.NOOfAGV;
								}else{
									if(this.lastNOOfAGV != this.NOOfAGV){
										System.out.println(this.NOOfAGV + "||" + this.lastNOOfAGV + "AGV����NOOfAGV��Ȼ��ͬ��������������������");
										logger.debug(this.NOOfAGV + "||" + this.lastNOOfAGV + "AGV����NOOfAGV��Ȼ��ͬ��������������������");
										this.lastNOOfAGV = this.NOOfAGV;
									}
								}
									
								if(!message.substring(2, 6).equals("BABA") && NOOfAGV <= AGVArray.size()){
									agvCar.setLastCommunicationTime(System.currentTimeMillis());
									NOOfCard = Integer.parseInt(message.substring(2, 4), 16);
									agvCar.setElectricity(Integer.parseInt(message.substring(4, 6), 16));
									if(NOOfCard != lastCard){										
										if(agvCar.getAGVComVar().sendOffDutyToAGV){
											agvCar.getAGVComVar().sendOffDutyToAGV = false;
											agvCar.getAGVComVar().offDutyString = "BBBBBB";
											System.out.println("���������°�ָ���" + this.NOOfAGV + "AGV");
											logger.debug("���������°�ָ���" + this.NOOfAGV + "AGV");
										}
										if(agvCar.getAGVComVar().sendOnDutyToAGV){
											agvCar.getAGVComVar().sendOnDutyToAGV = false;
											agvCar.getAGVComVar().onDutyString = "BBBBBB";
											System.out.println("���������ϰ�ָ���"+ this.NOOfAGV + "AGV");
											logger.debug("���������ϰ�ָ���"+ this.NOOfAGV + "AGV");
										}
										if(agvCar.getAGVComVar().sendRouteToAGV){
											agvCar.getAGVComVar().sendRouteToAGV = false;
											agvCar.getAGVComVar().routeString = "BBBBBB";
											System.out.println("��������·��ָ���"+ this.NOOfAGV + "AGV");
											logger.debug("��������·��ָ���"+ this.NOOfAGV + "AGV");
										}
										if(agvCar.getAGVComVar().sendAccessToAGV){
											agvCar.getAGVComVar().sendAccessToAGV = false;
											agvCar.getAGVComVar().accessString = "BBBBBB";
											System.out.println("�������ͼ���ָ���"+ this.NOOfAGV + "AGV");
											logger.debug("�������ͼ���ָ���"+ this.NOOfAGV + "AGV");
										}
										if(agvCar.getAGVComVar().sendStartingToAGV){
											agvCar.getAGVComVar().sendStartingToAGV = false;
											agvCar.getAGVComVar().startString = "BBBBBB";
											System.out.println("������������ָ���"+ this.NOOfAGV + "AGV");
											logger.debug("������������ָ���"+ this.NOOfAGV + "AGV");
										}
										
										if(agvCar.getAGVComVar().sendStopToAGV && agvCar.getAGVComVar().stopString.equals("CC01DD")){
											agvCar.getAGVComVar().sendStopToAGV = false;
											agvCar.getAGVComVar().stopString = "BBBBBB";
											this.canNotStop = true;
											System.out.println(this.NOOfAGV + "AGV����ֹͣ����ûͣ����");
											logger.debug(this.NOOfAGV + "AGV����ֹͣ����ûͣ����");
											logger.error(this.NOOfAGV + "AGV����ֹͣ����ûͣ����");
											//playAudio.continuePlay();
										}
										System.out.println(NOOfAGV + "AGVreceive card number:"+NOOfCard);
										logger.debug(NOOfAGV + "AGVreceive card number:"+NOOfCard);
										
										if(NOOfCard == 50 && !agvCar.initReady){//ֹͣ���г�ʼ��
											this.SendActionMessage("CC01DD");
										}
										
										if(!agvCar.initReady){
											agvCar.setEdgeCard(NOOfCard);
										}
										
										agvCar.setReadCard(NOOfCard);
										
										if(agvCar.initReady && NOOfCard%2 == 0){
											agvCar.firstInit = false;
										}
										
										lastCard = NOOfCard;
									}
								}else{
									agvCar.setLastCommunicationTime(System.currentTimeMillis());
									//outputStream.write(myToolKit.HexString2Bytes("AAC0FFEEBB"));
								}
								lastCommunicationTime = System.currentTimeMillis();
								lastMessage = message;
							}else if(message.endsWith("DD")){
								NOOfAGV = Integer.parseInt(message.substring(0, 2), 16);
								int state = Integer.parseInt(message.substring(2, 4), 16);
								//System.out.println(NOOfAGV);
								if(!oldRunnable && NOOfAGV <= AGVArray.size()){
									if(AGVArray.get(NOOfAGV-1).getRunnable() != null ){
										AGVArray.get(NOOfAGV-1).getRunnable().setCancelAGV();
									}
									AGVArray.get(NOOfAGV-1).setRunnabel(this);
									agvCar = AGVArray.get(NOOfAGV-1);
									oldRunnable = true;
									this.lastNOOfAGV = this.NOOfAGV;
									System.out.println(NOOfAGV +"AGV״̬ʱȷ��runnable");
									logger.debug(NOOfAGV +"AGV״̬ʱȷ��runnable");
								}
								if(agvCar != null && state != lastSignal && this.NOOfAGV <= this.AGVArray.size()){
									if(state == 1){//����
										if(agvCar.getAGVComVar().firstSendOnDutyToAGV 
												&& agvCar.AGVInit && !agvCar.initReady){
											agvCar.getAGVComVar().firstSendOnDutyToAGV = false;
											agvCar.getAGVComVar().sendOnDutyToAGV = true;
											agvCar.getAGVComVar().onDutyString = "CC08DD";	
											System.out.println("׼�������ϰ�ָ��" + this.NOOfAGV + "AGV");
											logger.debug("׼�������ϰ�ָ��" + this.NOOfAGV + "AGV");
										}
										if(agvCar.getAGVComVar().sendStopToAGV){
											agvCar.getAGVComVar().sendStopToAGV = false;
											agvCar.getAGVComVar().stopString = "BBBBBB";
											System.out.println("��������ָֹͣ���" + this.NOOfAGV + "AGV");
											logger.debug("��������ָֹͣ���" + this.NOOfAGV + "AGV");
																							
											if(!agvCar.initReady && !this.canNotStop){//ֱ��ֹͣ�����ʼ�����
												agvCar.initReady = true;
												agvCar.firstInit = true;
												System.out.println(agvCar.getAGVNum() + "AGV��ʼ�����");
												logger.debug(agvCar.getAGVNum() + "AGV��ʼ�����");
												/*
												timer.schedule(new TimerTask(){
													public void run(){
														
													}
												}, 500);*/
											}									
										}
										
									}else if(state == 2){//����
										if(agvCar.getAGVComVar().sendAccessToAGV){
											agvCar.getAGVComVar().sendAccessToAGV = false;
											agvCar.getAGVComVar().accessString = "BBBBBB";
											System.out.println("�������ͼ���ָ���"+ this.NOOfAGV + "AGV");
											logger.debug("�������ͼ���ָ���"+ this.NOOfAGV + "AGV");
										}
										if(agvCar.getAGVComVar().sendRouteToAGV || agvCar.getAGVComVar().sendStartingToAGV){
											agvCar.getAGVComVar().sendRouteToAGV = false;
											agvCar.getAGVComVar().sendStartingToAGV = false;
											agvCar.getAGVComVar().startString = "BBBBBB";
											System.out.println("�����������л�·��ָ���"+ this.NOOfAGV + "AGV");
											logger.debug("�����������л�·��ָ���"+ this.NOOfAGV + "AGV");
										}
										if(agvCar.getAGVComVar().sendOnDutyToAGV){
											agvCar.getAGVComVar().sendOnDutyToAGV = false;
											agvCar.getAGVComVar().onDutyString = "BBBBBB";
											System.out.println("���������ϰ�ָ���"+ this.NOOfAGV + "AGV");
											logger.debug("���������ϰ�ָ���"+ this.NOOfAGV + "AGV");
										}
										if(agvCar.getAGVComVar().sendOffDutyToAGV){
											agvCar.getAGVComVar().sendOffDutyToAGV = false;
											agvCar.getAGVComVar().offDutyString = "BBBBBB";
											System.out.println("���������°�ָ���" + this.NOOfAGV + "AGV");
											logger.debug("���������°�ָ���" + this.NOOfAGV + "AGV");
										}
									}else if(state == 3){//�������
										if(agvCar.getAGVComVar().sendShipmentToAGV){
											agvCar.getAGVComVar().sendShipmentToAGV = false;
											if(!agvCar.getAGVComVar().sendRouteToAGV)
												agvCar.getAGVComVar().shipmentString = "BBBBBB";
											System.out.println("�����������ϸ�"+ this.NOOfAGV + "AGV");
											logger.debug("�����������ϸ�"+ this.NOOfAGV + "AGV");
										}
										
									}else if(state == 4){//ж�����
										if(agvCar.getAGVComVar().sendUnloadingToAGV){
											agvCar.getAGVComVar().sendUnloadingToAGV = false;
											if(!agvCar.getAGVComVar().sendRouteToAGV)
												agvCar.getAGVComVar().routeString = "BBBBBB";
											System.out.println("��������ж�ϸ�"+ this.NOOfAGV + "AGV");
											logger.debug("��������ж�ϸ�"+ this.NOOfAGV + "AGV");
										}
										
									}
									agvCar.setAGVState(state);
									lastSignal = state;
								}									
								lastCommunicationTime = System.currentTimeMillis();
								lastMessage = message;
							}else{
								System.out.println(this.NOOfAGV + "AGV�������ݴ���" + message);
								logger.debug(this.NOOfAGV + "AGV�������ݴ���" + message);
							}
						}
					}						
				}else {
					Thread.sleep(10);
				}					
			}catch(Exception e){
				e.printStackTrace();
				logger.error(e);
				if(e instanceof SocketException || e instanceof IOException){
					//playAudio.continuePlay();
					System.out.println(this.NOOfAGV + "AGV inputStream?????????");
					logger.debug(this.NOOfAGV + "AGV inputStream?????????");
				}				
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
		}//while
		if(!this.cancelAGV){
			this.agvCar.setIsOnMission(true);
			this.agvCar.setMissionString("ͨѶ�ж�");
			this.playAudio.continuePlay();
			System.out.println(this.NOOfAGV + "AGVsocket ������");
			logger.debug(this.NOOfAGV + "AGVsocket ������");
		}	
	}
	
	public void setCancelAGV(){
		this.cancelAGV = true;
	}	
	
	class TestFinishShipmentUnloading extends TimerTask{
		private  AGVCar car = null;
		public TestFinishShipmentUnloading(AGVCar car){
			this.car = car;
		}
		

		public void run(){
			if(!this.car.checkFinishShipment && !this.car.checkFinishUnloading){
				logger.error("û���յ�"+ NOOfAGV+"ж����������ź�");
				//playAudio.continuePlay();
				System.out.println("û���յ�"+ NOOfAGV+"ж����������ź�");
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
				logger.error("����ָ���"+ NOOfAGV+"AGV����AGVû������");
				//playAudio.continuePlay();
				System.out.println("����ָ���"+ NOOfAGV+"AGV����AGVû������");
			}else{
				this.car.finishStart = false;
			}
		}		
	}
	
	public ArrayList<Edge> getConflictEdgeRockwell(ArrayList<Integer> routeNode){
		ArrayList<Edge> returnEdge = new ArrayList();
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