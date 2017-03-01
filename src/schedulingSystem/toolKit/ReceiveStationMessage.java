package schedulingSystem.toolKit;

import java.awt.Color;
import java.awt.Font;
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
import schedulingSystem.component.Graph;
import schedulingSystem.component.PlayAudio;
import schedulingSystem.gui.SchedulingGui;

public class ReceiveStationMessage implements Runnable{
	private static Logger logger = Logger.getLogger(SchedulingGui.class.getName());
	private Socket socket;
	private InputStream inputStream;
	private OutputStream outputStream;
	private MyToolKit myToolKit;
	private Graph graph;
	private boolean foundCC;
	private ScheduledExecutorService timer;
	private int n = 0;
	private int lastN=0;
	private int ms = 100;
	private String lastMessage;
	private boolean clearBuffer;
	private boolean f;
	private long lastCommunicationTime;
	private  ArrayList<AGVCar> AGVArray;
	private  ArrayList<AGVCar> sendMessageAGVArray;
	private long lastReceiveRequestMaterielTime;
	private long lastReceiveRequestRecycleTime;
	private ArrayList<Integer> requestMaterielArray;
	private ArrayList<Integer> requestRecycleArray;
	private JLabel stateLabel;
	private int comTag;
	private PlayAudio playAudio;
	private long arriveInChargeStationTime;
	private boolean runCircle;
	private ManualModel manualMode;

	public ReceiveStationMessage(Socket socket, Graph graph, ArrayList<AGVCar> AGVArray
			, JLabel stateLabel, PlayAudio playAudio, ArrayList<AGVCar> sendMessageAGVArray, ManualModel manualModel){
		this.manualMode = manualModel;
		this.runCircle = true;
		this.AGVArray = AGVArray;
		this.socket = socket;
		this.graph = graph;
		this.stateLabel = stateLabel;
		this.playAudio = playAudio;
		requestMaterielArray = new ArrayList<Integer>();
		requestRecycleArray = new ArrayList<Integer>();
		this.sendMessageAGVArray = sendMessageAGVArray;
		timer = Executors.newScheduledThreadPool(3);
		timer.schedule(new TimerTask(){
			public void run(){
				clearBuffer = true;
			}
		}, 3000, TimeUnit.MILLISECONDS);
		this.myToolKit = new MyToolKit();
		System.out.println("socket station:"+socket.toString());
		logger.debug("socket station:"+socket.toString());
		//logger.error("socket station:"+socket.toString() + "hashCode:" + this.hashCode());
		try{
			this.inputStream = this.socket.getInputStream();
			this.outputStream = this.socket.getOutputStream();
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e);
			//playAudio.continuePlay();
			logger.error("(1)stationͨѶ���󣡣���������"+this.socket);
			try{
				if(this.inputStream != null)
					this.inputStream.close();
				if(this.outputStream != null)
					this.outputStream.close();
				if(this.socket != null)
					this.socket.close();
			}catch(Exception e1){
				e1.printStackTrace();
			}
		}
	}
	
	public void SendMessage(String message,int AGVNum){
		if(AGVNum != 0){
			if(message.equals("CC01DD") || message.equals("CC02DD")){
				if(message.equals("CC01DD")){
					arriveInChargeStationTime = System.currentTimeMillis();
					AGVArray.get(AGVNum-1).sendNeedChargeStr = message;
					System.out.println(AGVNum + "AGV��ʼ����CC01DD���");
					logger.debug(AGVNum + "AGV��ʼ����CC01DD���");
				}else{
					AGVArray.get(AGVNum-1).sendFinishChargeStr = message;
					System.out.println(AGVNum + "AGV��ʼ����CC02DD�뿪���");
					logger.debug(AGVNum + "AGV��ʼ����CC02DD�뿪���");
				}
			}else{
				if(Integer.parseInt(message.substring(4, 6), 16) == 1){
					System.out.println(AGVNum + "AGV��ʼ��������Ҫ���ź�");
					logger.debug(AGVNum + "AGV��ʼ��������Ҫ���ź�");
					AGVArray.get(AGVNum-1).sendRequareMatirielMessageStr = message;
				}else if(Integer.parseInt(message.substring(4, 6), 16) == 7){
					System.out.println(AGVNum + "AGV��ʼ���͵�λ�ź�");
					logger.debug(AGVNum + "AGV��ʼ���͵�λ�ź�");
					AGVArray.get(AGVNum-1).sendArriveInMessageStr = message;
				}else if(Integer.parseInt(message.substring(4, 6), 16) == 3){
					System.out.println(AGVNum + "AGV��ʼ����������տ�����");
					logger.debug(AGVNum + "AGV��ʼ����������տ�����");
					AGVArray.get(AGVNum-1).sendRequestRecyclePalletStr = message;
				}
			}
			AGVArray.get(AGVNum-1).finishShipment = false;
			AGVArray.get(AGVNum-1).finishUnloading = false;
			if(this.sendMessageAGVArray.indexOf(AGVArray.get(AGVNum-1)) < 0)
				this.sendMessageAGVArray.add(AGVArray.get(AGVNum-1));
			//System.out.println(AGVNum + "AGV�����ͣ�������������������������������������");
		}else{//AGVNum=0 ��ѭ����
			if(this.runCircle){
				System.out.println("��ѭ����"+message);	
				logger.debug("��ѭ����"+message);	
				for(int i = 0; i < 60; i++){
					timer.schedule(new TimerTask(){
						public void run(){
							try {
								outputStream.write(myToolKit.HexString2Bytes(message));
								//System.out.println(message);
							} catch (IOException e) {							
								e.printStackTrace();
								logger.error(e);
								//playAudio.continuePlay();
							}
						}
					}, 200*i, TimeUnit.MILLISECONDS);
				}
			}			
		}
	}
	
	public void run(){
		while(this.runCircle){	
			for(int i = 0; i < this.sendMessageAGVArray.size(); i++){
				if(this.sendMessageAGVArray.get(i).finishShipment || this.sendMessageAGVArray.get(i).finishUnloading){
					System.out.println(this.sendMessageAGVArray.get(i).getAGVNum()+"AGV�رշ��͵�λ/����Ҫ��/��������������źţ�����������������������������������������������");
					logger.debug(this.sendMessageAGVArray.get(i).getAGVNum()+"AGV�رշ��͵�λ/����Ҫ��/��������������źţ�����������������������������������������������");
					this.sendMessageAGVArray.get(i).sendArriveInMessageStr = null;
					this.sendMessageAGVArray.get(i).sendRequareMatirielMessageStr = null;
					this.sendMessageAGVArray.get(i).sendRequestRecyclePalletStr = null;
					this.sendMessageAGVArray.get(i).finishShipment = false;
					this.sendMessageAGVArray.get(i).finishUnloading = false;
					this.sendMessageAGVArray.remove(i);
				}
				if(this.sendMessageAGVArray.size() > i){
					if((this.sendMessageAGVArray.get(i).sendNeedChargeStr != null && this.sendMessageAGVArray.get(i).getAGVStopInNode() > 0
							&& graph.getFunctionNodeArray().get(this.sendMessageAGVArray.get(i).getAGVStopInNode()).chargingStationRetract)
							|| (this.sendMessageAGVArray.get(i).sendNeedChargeStr != null && System.currentTimeMillis() - arriveInChargeStationTime > 120000)){
						if(System.currentTimeMillis() - arriveInChargeStationTime > 120000){
							this.sendMessageAGVArray.get(i).charging = false;
							this.sendMessageAGVArray.get(i).setIsOnMission(false);
							this.sendMessageAGVArray.get(i).sendNeedChargeStr = null;
							this.sendMessageAGVArray.remove(i);	
							System.out.println("���װû�������");
							logger.debug("���װû�������");
							logger.error("���װû�������");
						}else{
							this.sendMessageAGVArray.get(i).sendNeedChargeStr = null;
							this.sendMessageAGVArray.get(i).chargeTime();//��綨ʱ��ʼ
							this.sendMessageAGVArray.get(i).getRunnable().SendActionMessage("CC04DD");
							this.sendMessageAGVArray.remove(i);								
							System.out.println("���װ�����");
							logger.debug("���װ�����");
						}						
					}
				}
				
				if(this.sendMessageAGVArray.size() > i){
					if(this.sendMessageAGVArray.get(i).sendFinishChargeStr != null 
							&& this.sendMessageAGVArray.get(i).sendNeedChargeStr != null){
						this.sendMessageAGVArray.get(i).sendNeedChargeStr = null;
					}
				}
				
				if(this.sendMessageAGVArray.size() > i){
					if(this.sendMessageAGVArray.get(i).sendFinishChargeStr != null && this.sendMessageAGVArray.get(i).getAGVStopInNode() > 0
							&&!graph.getFunctionNodeArray().get(this.sendMessageAGVArray.get(i).getAGVStopInNode()).chargingStationRetract){
						this.sendMessageAGVArray.get(i).sendFinishChargeStr = null;
						this.sendMessageAGVArray.get(i).charging = false;
						this.sendMessageAGVArray.get(i).setIsOnMission(false);
						if(this.sendMessageAGVArray.get(i).ReadyToOffDuty){
							this.sendMessageAGVArray.get(i).AGVOffDuty();
							System.out.println(this.sendMessageAGVArray.get(i).getAGVNum() + "AGV�ӳ���°ࡣ������");
							logger.debug(this.sendMessageAGVArray.get(i).getAGVNum() + "AGV�ӳ���°ࡣ������");
						}
						this.sendMessageAGVArray.remove(i);
						System.out.println("������,���װ����ȥ��");
						logger.debug("������,���װ����ȥ��");
					}
				}
				
			}
			
			
			if(this.sendMessageAGVArray.size() > 0){
				if(System.currentTimeMillis() - lastCommunicationTime > 200){
					for(AGVCar AGV : this.sendMessageAGVArray){
						String sendMessageStr = null;
						if(AGV.sendArriveInMessageStr != null){
							sendMessageStr = AGV.sendArriveInMessageStr;
						}else if(AGV.sendRequareMatirielMessageStr != null){
							sendMessageStr = AGV.sendRequareMatirielMessageStr;
						}else if(AGV.sendRequestRecyclePalletStr != null){
							sendMessageStr = AGV.sendRequestRecyclePalletStr;
						}else if(AGV.sendNeedChargeStr != null){
							sendMessageStr = AGV.sendNeedChargeStr;
						}else if(AGV.sendFinishChargeStr != null){
							sendMessageStr = AGV.sendFinishChargeStr;
						}
						if(sendMessageStr != null){
							try {
								outputStream.write(myToolKit.HexString2Bytes(sendMessageStr));
								System.out.println(AGV.getAGVNum() + "AGV����" + sendMessageStr);
								logger.debug(AGV.getAGVNum() + "AGV����" + sendMessageStr);
							} catch (IOException e) {
								e.printStackTrace();
								logger.error(e);
								if(e instanceof SocketException || e instanceof IOException){
									logger.error("(2)stationͨѶ���󣡣���������"+this.socket);
									//playAudio.continuePlay();
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
								}
								this.runCircle = false;//�����߳�
							}
							this.lastCommunicationTime = System.currentTimeMillis();
						}
					}
				}
			}

			try{
				if(inputStream.available() > 0){
					String message = "";
					/*
					byte[] endCode = new byte[4];
					inputStream.read(endCode);
					message = myToolKit.printHexString(endCode);
					System.out.println("receive station message:"+message);
					
					*/
					if(!foundCC){
						byte[] endCode = new byte[1];
						inputStream.read(endCode);
						message = myToolKit.printHexString(endCode);
						
						if(message.equals("CC")){
							foundCC = true;
						}
					}
					if(foundCC){
						foundCC = false;
						byte[] buff = new byte[3];
						inputStream.read(buff);
						message = myToolKit.printHexString(buff);
						//System.out.println("receive station message:"+message);
						//logger.debug("receive station message:"+message);
						if(this.clearBuffer&& message.endsWith("DD")){
							int communicationNum = Integer.parseInt(message.substring(0, 2), 16);
							comTag = communicationNum;
							int signal = Integer.parseInt(message.substring(2, 4), 16);
							//System.out.println( communicationNum + "//"+signal);
							for(int i = 0; i < graph.getFunctionNodeArray().size(); i++){
								if(communicationNum == graph.getFunctionNodeArray().get(i).communicationNum){
									graph.getFunctionNodeArray().get(i).setReceiveStationMessage(this);
									if(signal == 9 && !this.manualMode.state){
										if(!graph.getFunctionNodeArray().get(i).responsing)//δ��ǲ
											this.requestMaterielArray.add(i);
										if(System.currentTimeMillis() - this.lastReceiveRequestMaterielTime > 5000 
												&& this.requestMaterielArray.size() > 0){
											//System.out.println("��Ӧ����Ҫ��");
											graph.getFunctionNodeArray().get(this.findMinCount(this.requestMaterielArray)).requestMateriel();
											this.requestMaterielArray.clear();
											this.lastReceiveRequestMaterielTime = System.currentTimeMillis();
										}
									}else if(signal == 16 && !this.manualMode.state){
										if(!graph.getFunctionNodeArray().get(i).responsing)//δ��ǲ
											this.requestRecycleArray.add(i);
										if(System.currentTimeMillis() - this.lastReceiveRequestRecycleTime > 5000
												&& this.requestRecycleArray.size() > 0){
											//System.out.println("��Ӧ����������");
											graph.getFunctionNodeArray().get(this.findMinCount(this.requestRecycleArray)).materielUsedUp();
											this.requestRecycleArray.clear();
											this.lastReceiveRequestRecycleTime = System.currentTimeMillis();
										}
									}else if(signal == 17){
										graph.getFunctionNodeArray().get(i).finishedEnterPallet();
									}else if(signal == 4){
										graph.getFunctionNodeArray().get(i).readyToShipment();
									}else if(signal == 5){
										graph.getFunctionNodeArray().get(i).allowToEnterPallet();
									}else if(signal == 6){
										graph.getFunctionNodeArray().get(i).finishedEnterPallet();
									}else if(signal == 18){
										graph.getFunctionNodeArray().get(i).allowUnloading();
									}else if(signal == 19){
										graph.getFunctionNodeArray().get(i).chargingStationRetractBack();
									}else if(signal == 20){
										graph.getFunctionNodeArray().get(i).chargingStationRetract();
									}
								}
							}
						}else if(this.clearBuffer){
							System.out.println("station��β���ݴ���" + message);
							logger.debug("station��β���ݴ���" + message);
						}
					}
				}else{
					Thread.sleep(20);
				}
			}catch(Exception e){
				e.printStackTrace();
				logger.error(e);
				//playAudio.continuePlay();
				logger.error("(3)stationͨѶ���󣡣���������"+this.socket);
				try{
					if(this.inputStream != null)
						this.inputStream.close();
					if(this.outputStream != null)
						this.outputStream.close();
					if(this.socket != null)
						this.socket.close();
				}catch(Exception e1){
					e1.printStackTrace();
				}
				this.runCircle = false;//�����߳�
			}
		}
		stateLabel.setFont(new Font("����", Font.BOLD, 30));
		stateLabel.setForeground(Color.RED);
		if(comTag < 10){
			stateLabel.setText("����ͨѶ�жϣ���������������������������������");
			logger.error("����ͨѶ�жϣ���������������������������������");
		}else if(comTag > 10 && comTag < 25){
			stateLabel.setText("ж��ͨѶ�жϣ���������������������������������");
			logger.error("ж��ͨѶ�жϣ���������������������������������");
		}else if(comTag == 25){
			stateLabel.setText("���1ͨѶ�жϣ���������������������������������");
			logger.error("���1ͨѶ�жϣ���������������������������������");
		}else if(comTag == 26){
			stateLabel.setText("���2ͨѶ�жϣ���������������������������������");
			logger.error("���2ͨѶ�жϣ���������������������������������");
		}else if(comTag == 27){
			stateLabel.setText("���3ͨѶ�жϣ���������������������������������");
			logger.error("���3ͨѶ�жϣ���������������������������������");
		}else if(comTag == 28){
			stateLabel.setText("���4ͨѶ�жϣ���������������������������������");
			logger.error("���4ͨѶ�жϣ���������������������������������");
		}
		//this.playAudio.continuePlay();
	}

	public int findMinCount(ArrayList<Integer> requestArray){
		int minShipmentCount = 65534;
		int minIndex = -1;
		int exitCount = 0;
		for(int j = 0; j < requestArray.size(); j++){
			for(int m = 0; m < graph.getFunctionNodeArray().size(); m++){
				if((graph.getFunctionNodeArray().get(requestArray.get(j)).communicationNum == 17 
						&& graph.getFunctionNodeArray().get(m).communicationNum == 18)
						|| (graph.getFunctionNodeArray().get(requestArray.get(j)).communicationNum == 18 
						&& graph.getFunctionNodeArray().get(m).communicationNum == 17)){
					exitCount = graph.getFunctionNodeArray().get(m).getShipmentCount();
				}
				if((graph.getFunctionNodeArray().get(requestArray.get(j)).communicationNum == 19
						 && graph.getFunctionNodeArray().get(m).communicationNum == 20)
						 || (graph.getFunctionNodeArray().get(requestArray.get(j)).communicationNum == 20
						 && graph.getFunctionNodeArray().get(m).communicationNum == 19)){
					exitCount = graph.getFunctionNodeArray().get(m).getShipmentCount();
				}
				if((graph.getFunctionNodeArray().get(requestArray.get(j)).communicationNum == 21
						 && graph.getFunctionNodeArray().get(m).communicationNum == 22)
						 || (graph.getFunctionNodeArray().get(requestArray.get(j)).communicationNum == 22
						 && graph.getFunctionNodeArray().get(m).communicationNum == 21)){
					exitCount = graph.getFunctionNodeArray().get(m).getShipmentCount();
				}
				if((graph.getFunctionNodeArray().get(requestArray.get(j)).communicationNum == 23
						 && graph.getFunctionNodeArray().get(m).communicationNum == 24)
						 || (graph.getFunctionNodeArray().get(requestArray.get(j)).communicationNum == 24
						 && graph.getFunctionNodeArray().get(m).communicationNum == 23)){
					exitCount = graph.getFunctionNodeArray().get(m).getShipmentCount();
				}
			}
			
			if(graph.getFunctionNodeArray().get(requestArray.get(j)).getShipmentCount() + exitCount < minShipmentCount){
				minShipmentCount = graph.getFunctionNodeArray().get(requestArray.get(j)).getShipmentCount();
				minIndex = requestArray.get(j);
			}
		}
		return minIndex;
	}
	
	
	
	
	
	
	
	/*
	 * if(n == 0 && n == lastN){
				lastN = -1;
				timer.schedule(new TimerTask(){
					public void run(){
						SendMessage("CC0101DD");//move
						n++;
						lastN = n;
					}
				}, ms);
				//System.out.println();
			}

			if(n == 1&& n == lastN){
				lastN = -1;
				timer.schedule(new TimerTask(){
					public void run(){
						SendMessage("CC0102DD");//move
						n++;
						lastN = n;
					}
				}, ms);
				//System.out.println();
			}
			
			if(n == 2&& n == lastN){
				lastN = -1;
				timer.schedule(new TimerTask(){
					public void run(){
						SendMessage("CC0201DD");//move
						n++;
						lastN = n;
					}
				}, ms);
				//System.out.println();
			}
				
			 
			if(n == 3&& n == lastN){
				lastN = -1;
				timer.schedule(new TimerTask(){
					public void run(){
						SendMessage("CC0202DD");//move
						n++;
						lastN = n;
					}
				}, ms);
				//System.out.println();
			}
				
			 
			if(n == 4&& n == lastN){
				lastN = -1;
				timer.schedule(new TimerTask(){
					public void run(){
						SendMessage("CC0301DD");//move
						n++;
						lastN = n;
					}
				}, ms);
				//System.out.println();
			}
				
			
			if(n == 5&& n == lastN){
				lastN = -1;
				timer.schedule(new TimerTask(){
					public void run(){
						SendMessage("CC0302DD");//move
						n++;
						lastN = n;
					}
				}, ms);
				//System.out.println();
			}
				
			
			if(n == 6&& n == lastN){
				lastN = -1;
				timer.schedule(new TimerTask(){
					public void run(){
						SendMessage("CC0401DD");//move
						n++;
						lastN = n;
					}
				}, ms);
				//System.out.println();
			}
				
			
			if(n == 7&& n == lastN){
				lastN = -1;
				timer.schedule(new TimerTask(){
					public void run(){
						SendMessage("CC0402DD");//move
						n++;
						lastN = n;
					}
				}, ms);
				//System.out.println();
			}
				
			 
			if(n == 8&& n == lastN){
				lastN = -1;
				timer.schedule(new TimerTask(){
					public void run(){
						SendMessage("CC0503DD");//move
						n++;
						lastN = n;
					}
				}, ms);
				//System.out.println();
				
			}
			
			if(n == 9 && n == lastN){
				lastN = -1;
				timer.schedule(new TimerTask(){
					public void run(){
						SendMessage("CC0101DD");//move
						n++;
						lastN = n;
					}
				}, ms);
				//System.out.println();
				n = 0;
			}
			
			
			
			
			
			
			
			
			
			
			if(n == 0 && n == lastN){
				lastN = -1;
				timer.schedule(new TimerTask(){
					public void run(){
						SendMessage("CC1107DD");//move
						n++;
						lastN = n;
					}
				}, ms);
				//System.out.println();
			}

			if(n == 1&& n == lastN){
				lastN = -1;
				timer.schedule(new TimerTask(){
					public void run(){
						SendMessage("CC1207DD");//move
						n++;
						lastN = n;
					}
				}, ms);
				//System.out.println();
			}
			
			if(n == 2&& n == lastN){
				lastN = -1;
				timer.schedule(new TimerTask(){
					public void run(){
						SendMessage("CC1307DD");//move
						n++;
						lastN = n;
					}
				}, ms);
				//System.out.println();
			}
				
			 
			if(n == 3&& n == lastN){
				lastN = -1;
				timer.schedule(new TimerTask(){
					public void run(){
						SendMessage("CC1407DD");//move
						n++;
						lastN = n;
					}
				}, ms);
				//System.out.println();
			}
				
			 
			if(n == 4&& n == lastN){
				lastN = -1;
				timer.schedule(new TimerTask(){
					public void run(){
						SendMessage("CC1507DD");//move
						n++;
						lastN = n;
					}
				}, ms);
				//System.out.println();
			}
				
			
			if(n == 5&& n == lastN){
				lastN = -1;
				timer.schedule(new TimerTask(){
					public void run(){
						SendMessage("CC1607DD");//move
						n++;
						lastN = n;
					}
				}, ms);
				//System.out.println();
			}
				
			
			if(n == 6&& n == lastN){
				lastN = -1;
				timer.schedule(new TimerTask(){
					public void run(){
						SendMessage("CC1707DD");//move
						n++;
						lastN = n;
					}
				}, ms);
				//System.out.println();
			}
				
			
			if(n == 7&& n == lastN){
				lastN = -1;
				timer.schedule(new TimerTask(){
					public void run(){
						SendMessage("CC1807DD");//move
						n++;
						lastN = n;
					}
				}, ms);
				//System.out.println();
			}
				
			 
			if(n == 8&& n == lastN){
				lastN = -1;
				timer.schedule(new TimerTask(){
					public void run(){
						SendMessage("CC1108DD");//move
						n++;
						lastN = n;
					}
				}, ms);
				//System.out.println();
				
			}
			if(n == 9&& n == lastN){
				lastN = -1;
				timer.schedule(new TimerTask(){
					public void run(){
						SendMessage("CC1208DD");//move
						n++;
						lastN = n;
					}
				}, ms);
				//System.out.println();
				
			}
			if(n == 10&& n == lastN){
				lastN = -1;
				timer.schedule(new TimerTask(){
					public void run(){
						SendMessage("CC1308DD");//move
						n++;
						lastN = n;
					}
				}, ms);
				//System.out.println();
				
			}
			if(n == 11&& n == lastN){
				lastN = -1;
				timer.schedule(new TimerTask(){
					public void run(){
						SendMessage("CC1408DD");//move
						n++;
						lastN = n;
					}
				}, ms);
				//System.out.println();
				
			}
			if(n == 12&& n == lastN){
				lastN = -1;
				timer.schedule(new TimerTask(){
					public void run(){
						SendMessage("CC1508DD");//move
						n++;
						lastN = n;
					}
				}, ms);
				//System.out.println();
				
			}
			if(n == 13&& n == lastN){
				lastN = -1;
				timer.schedule(new TimerTask(){
					public void run(){
						SendMessage("CC1608DD");//move
						n++;
						lastN = n;
					}
				}, ms);
				//System.out.println();
				
			}
			if(n == 14&& n == lastN){
				lastN = -1;
				timer.schedule(new TimerTask(){
					public void run(){
						SendMessage("CC1708DD");//move
						n++;
						lastN = n;
					}
				}, ms);
				//System.out.println();
				
			}
			if(n == 15&& n == lastN){
				lastN = -1;
				timer.schedule(new TimerTask(){
					public void run(){
						SendMessage("CC1808DD");//move
						n++;
						lastN = n;
					}
				}, ms);
				//System.out.println();
				
			}
			
			
			if(n == 16 && n == lastN){
				lastN = -1;
				timer.schedule(new TimerTask(){
					public void run(){
						SendMessage("CC1107DD");//move
						n++;
						lastN = n;
					}
				}, ms);
				//System.out.println();
				n = 0;
			}	
	 */
	

}