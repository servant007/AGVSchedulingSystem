package schedulingSystem.gui;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.apache.log4j.Logger;

import schedulingSystem.component.AGVCar;
import schedulingSystem.component.AGVCar.Orientation;
import schedulingSystem.component.AGVCar.State;
import schedulingSystem.component.ConflictDetection;
import schedulingSystem.component.Dijkstra;
import schedulingSystem.component.Edge;
import schedulingSystem.component.FunctionNode.FunctionNodeEnum;
import schedulingSystem.component.Graph;
import schedulingSystem.component.Main;
import schedulingSystem.component.Node;
import schedulingSystem.component.Path;
import schedulingSystem.component.PlayAudio;
import schedulingSystem.toolKit.ReceiveStationMessage;
import schedulingSystem.toolKit.ManualModel;
import schedulingSystem.toolKit.MyToolKit;
import schedulingSystem.toolKit.OnDutyBtn;
import schedulingSystem.toolKit.ReceiveAGVMessage;
import schedulingSystem.toolKit.RoundButton;
import schedulingSystem.toolKit.SignUpDialog;
import schedulingSystem.toolKit.SignUpDialogListener;

public class SchedulingGui extends JPanel{
	private static final long serialVersionUID = 1L;
	private static SchedulingGui instance;
	private static Logger logger = Logger.getLogger(SchedulingGui.class.getName());
	private int numOfAGV;
	public ArrayList<AGVCar> AGVArray;
	private static Graph graph;
	private ServerSocket serverSocket;
	private MyToolKit myToolKit;
	private RoundButton schedulingGuiBtn;
	private RoundButton setingGuiBtn;
	private RoundButton graphGuiBtn;
	private Timer timer;
	private Timer chargeTimer;
	private JLabel stateLabel;
	private StringBuffer stateString;
	private Dimension screenSize;
	private ExecutorService executorService;
	private Dijkstra dijkstra;
	private ReceiveAGVMessage receiveAGVMessage;
	private ReceiveStationMessage receiveStationMessage;
	private int time500ms;
	private boolean reverseColor;
	private ConflictDetection conflictDetection;
	private Image leftImage;
	private Image rightImage;
	private Image upImage;
	private Image downImage;
	private Image leftImageG;
	private Image rightImageG;
	private Image upImageG;
	private Image downImageG;
	private Image leftImageR;
	private Image rightImageR;
	private Image upImageR;
	private Image downImageR;
	private static long key = 27940871;
	private long password;
	private String deadline;
	private long systemTime; 
	private int foundAGVNum;
	private boolean shipment;
	private int shipmnetCount;
	private int minChargeIndex = -1;
	private OnDutyBtn onDutyBtnState;
	private ManualModel manualModel;
	private java.util.Timer clearTimer;
	public int chargeGap;
	private PlayAudio playAudio;
	private  ArrayList<AGVCar> shipmentSendMessageAGVArray;
	private  ArrayList<AGVCar> unloadingSendMessageAGVArray;
	private  ArrayList<AGVCar> charge1SendMessageAGVArray;
	private  ArrayList<AGVCar> charge2SendMessageAGVArray;
	private  ArrayList<AGVCar> charge3SendMessageAGVArray;
	private  ArrayList<AGVCar> charge4SendMessageAGVArray;
	
	public static  SchedulingGui getInstance(Graph graph1){
		graph = graph1;
		if(instance == null){
			instance = new SchedulingGui();
		}
		return instance;
	}
	
	private SchedulingGui(){
		shipmentSendMessageAGVArray = new ArrayList<AGVCar>();
		unloadingSendMessageAGVArray = new ArrayList<AGVCar>();
		charge1SendMessageAGVArray = new ArrayList<AGVCar>();
		charge2SendMessageAGVArray = new ArrayList<AGVCar>();
		charge3SendMessageAGVArray = new ArrayList<AGVCar>();
		charge4SendMessageAGVArray = new ArrayList<AGVCar>();
		onDutyBtnState = new OnDutyBtn();
		manualModel = new ManualModel();
		clearTimer = new java.util.Timer();
		playAudio = new PlayAudio();
		try{
			FileReader fr = new FileReader("C:\\Users\\agv\\Documents\\date.txt");
			BufferedReader br = new BufferedReader(fr);//new InputStreamReader(this.getClass().getResourceAsStream("/date.txt"))
			password = Long.parseLong(br.readLine());
			System.out.println("12:"+((20170318)^key));
			br.close();
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e);
		}

		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH)+1;
		int date = c.get(Calendar.DATE);
		systemTime = date + 100*month + 10000*year; 
		System.out.println(systemTime);
		deadline = String.valueOf((password)^key);
		
		
		
		Toolkit tool = Toolkit.getDefaultToolkit();
		leftImageG = tool.createImage(getClass().getResource("/leftImage.png"));
		rightImageG = tool.createImage(getClass().getResource("/rightImage.png"));
		upImageG = tool.createImage(getClass().getResource("/upImage.png"));
		downImageG = tool.createImage(getClass().getResource("/downImage.png"));
		leftImageR = tool.createImage(getClass().getResource("/leftImage2.png"));
		rightImageR = tool.createImage(getClass().getResource("/rightImage2.png"));
		upImageR = tool.createImage(getClass().getResource("/upImage2.png"));
		downImageR = tool.createImage(getClass().getResource("/downImage2.png"));
		myToolKit = new MyToolKit();
		dijkstra = new Dijkstra(graph);
		stateString = new StringBuffer();
		//panelSize = new Dimension(0, 0);
		
		numOfAGV = graph.getAGVSeting().size();
		conflictDetection = new ConflictDetection(graph);
		AGVArray = new ArrayList<AGVCar>();
		for(int i = 0; i < numOfAGV; i++){
			AGVArray.add(new AGVCar(i+1, graph, conflictDetection, playAudio));
		}
		
		for(int i = 0; i < graph.getFunctionNodeArray().size(); i++){
			graph.getFunctionNodeArray().get(i).initFunctionNode(AGVArray, dijkstra, graph);
		}

		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		schedulingGuiBtn = new RoundButton("调度界面");
		schedulingGuiBtn.setBounds(0, 0, screenSize.width/3, screenSize.height/20);
		schedulingGuiBtn.setForeground(new Color(30, 144, 255));
		schedulingGuiBtn.setBackground(Color.WHITE);
		
		setingGuiBtn = new RoundButton("设置界面");
		setingGuiBtn.setBounds(screenSize.width/3, 0, screenSize.width/3, screenSize.height/20);

		graphGuiBtn = new RoundButton("制图界面");
		graphGuiBtn.setBounds(2*screenSize.width/3, 0, screenSize.width/3, screenSize.height/20);

		stateLabel = new JLabel();
		stateLabel.setBounds(0, 22*screenSize.height/25, screenSize.width, screenSize.height/25);
		stateLabel.setFont(new Font("宋体", Font.BOLD, 25));
		timer = new Timer(100, new TimerListener());
		timer.start();
		this.chargeGap = graph.getChargeGap();
		chargeTimer = new Timer(chargeGap, new ChargeTimerListener());
		chargeTimer.start();
		
		
		if(deadline.startsWith("20") && Integer.parseInt(deadline.substring(4,5)) < 2 && Integer.parseInt(deadline.substring(6,7)) < 4){
			System.out.println("deadline:"+deadline);
			if(systemTime < Long.parseLong(deadline)){
				System.out.println("normal:");
				try{
					serverSocket = new ServerSocket(8001);
				}catch(Exception e){
					e.printStackTrace();
					stateString.append(e.toString()).append("//");
					stateLabel.setText(stateString.toString());
					logger.error(e);
				}
				executorService = Executors.newFixedThreadPool(20);
			}else{
				stateLabel.setFont(new Font("宋体", Font.BOLD, 30));
				stateLabel.setForeground(Color.RED);
				stateLabel.setText("已过期，请注册！");
			}
		}else{
			stateLabel.setFont(new Font("宋体", Font.BOLD, 30));
			stateLabel.setForeground(Color.RED);
			stateLabel.setText("已过期，请注册！");
		}
		
		new Thread(new Runnable(){
			public void run(){
				while(true){
					Socket socket = null;
					try{
						if(serverSocket != null){
							socket = serverSocket.accept();
							
							for(int i = 0; i < graph.getFunctionNodeArray().size(); i++){
								if(graph.getFunctionNodeArray().get(i).ip.equals(socket.getInetAddress().getHostAddress().toString())){
									ArrayList<AGVCar> sendMessageAGVArray = null;
									if(socket.getInetAddress().getHostAddress().toString().equals("192.168.0.101")){
										sendMessageAGVArray = shipmentSendMessageAGVArray;
									}else if(socket.getInetAddress().getHostAddress().toString().equals("192.168.0.104")){
										sendMessageAGVArray = unloadingSendMessageAGVArray;
									}else if(socket.getInetAddress().getHostAddress().toString().equals("192.168.0.108")){
										sendMessageAGVArray = charge1SendMessageAGVArray;
									}else if(socket.getInetAddress().getHostAddress().toString().equals("192.168.0.110")){
										sendMessageAGVArray = charge2SendMessageAGVArray;
									}else if(socket.getInetAddress().getHostAddress().toString().equals("192.168.0.118")){
										sendMessageAGVArray = charge3SendMessageAGVArray;
									}else if(socket.getInetAddress().getHostAddress().toString().equals("192.168.0.119")){
										sendMessageAGVArray = charge4SendMessageAGVArray;
									}
									receiveStationMessage = new ReceiveStationMessage(socket, graph, AGVArray, stateLabel, playAudio, sendMessageAGVArray, manualModel);
									executorService.execute(receiveStationMessage);
									break;
								}
							}
							
							if(receiveStationMessage == null){
								//System.out.println("receiveStationMessage == null");
								receiveAGVMessage = new ReceiveAGVMessage(socket, graph, AGVArray, stateLabel, playAudio, onDutyBtnState);
								executorService.execute(receiveAGVMessage);
							}else{
								receiveStationMessage = null;
							}	
							
						}
					}catch(Exception e){
						e.printStackTrace();
						logger.error(e);
					}
				}
			}
		}).start();
		
		
		this.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				handleFunctionNodeClick(e);
			}
		});
	
		this.setLayout(null);
		this.add(schedulingGuiBtn);
		this.add(setingGuiBtn);
		this.add(graphGuiBtn);
		this.add(stateLabel);
	}//init
	
	@Override
	public void paint(Graphics g){
		super.paint(g);
		myToolKit.drawGraph(g, graph, reverseColor, false);
		g.setColor(Color.black);
		g.setFont(new java.awt.Font("Dialog", Font.BOLD, 25));
		for(int i = 0; i < AGVArray.size(); i++){
			if(AGVArray.get(i).initReady){
				leftImage = leftImageG;
				rightImage = rightImageG;
				upImage = upImageG;
				downImage = downImageG;
				
			}else{
				leftImage = leftImageR;
				rightImage = rightImageR;
				upImage = upImageR;
				downImage = downImageR;
			}
			g.setColor(Color.black);
			if(AGVArray.get(i).getOrientation() == Orientation.LEFT){
				g.drawImage(leftImage,AGVArray.get(i).getX() - 20, AGVArray.get(i).getY() - 17, 40, 34, this);
				g.drawString(String.valueOf(i+1), AGVArray.get(i).getX(), AGVArray.get(i).getY()+9);
			}else if(AGVArray.get(i).getOrientation() == Orientation.RIGTH){
				g.drawImage(rightImage,AGVArray.get(i).getX() - 20, AGVArray.get(i).getY() - 17, 40, 34, this);
				g.drawString(String.valueOf(i+1), AGVArray.get(i).getX()-10, AGVArray.get(i).getY()+9);
			}else if(AGVArray.get(i).getOrientation() == Orientation.UP){
				g.drawImage(upImage,AGVArray.get(i).getX() - 17, AGVArray.get(i).getY() - 20, 34, 40, this);
				g.drawString(String.valueOf(i+1), AGVArray.get(i).getX()-10, AGVArray.get(i).getY()+10);
			}else if(AGVArray.get(i).getOrientation() == Orientation.DOWN){
				g.drawImage(downImage,AGVArray.get(i).getX() - 17, AGVArray.get(i).getY() - 20, 34, 40, this);
				g.drawString(String.valueOf(i+1), AGVArray.get(i).getX()-5, AGVArray.get(i).getY()+5);
			}			
			if((AGVArray.get(i).isOnMission() || AGVArray.get(i).realyOffDuty) && AGVArray.get(i).getMissionString() != null)
				g.drawString(AGVArray.get(i).getMissionString(), AGVArray.get(i).getX(), AGVArray.get(i).getY()-25);
			else
				g.drawString("待机", AGVArray.get(i).getX(), AGVArray.get(i).getY()-25);
			g.setColor(Color.BLUE);
			g.drawString(AGVArray.get(i).stateString, AGVArray.get(i).getX(), AGVArray.get(i).getY()+25);			
		}
	
	}
	
	class TimerListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			repaint();
			time500ms++;
			if(time500ms >= 5){
				time500ms = 0;
				reverseColor = !reverseColor;
			}

			for(int i = 0; i < AGVArray.size(); i ++){
				AGVArray.get(i).stepForward();
			}	
		}
	}
	class ChargeTimerListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			int minChargeCount = 65534;
			for(int i = 0; i < AGVArray.size(); i++){
				if(AGVArray.get(i).getStartEdge().endNode.num!=0 && AGVArray.get(i).getRunnable() != null 
						&& AGVArray.get(i).chargeCount < minChargeCount 
						&& AGVArray.get(i).initReady && !AGVArray.get(i).getFixRoute() 
						&& !AGVArray.get(i).charging && !AGVArray.get(i).ReadyToOffDuty){
					minChargeCount = AGVArray.get(i).chargeCount;
					minChargeIndex = i;
				}
			}
			
			if(minChargeIndex >= 0){
				AGVArray.get(minChargeIndex).charging = true;
				AGVArray.get(minChargeIndex).chargeCount++;
				if(!AGVArray.get(minChargeIndex).isOnMission()){
					for(int i = 0; i < graph.getFunctionNodeArray().size(); i++){
						if(graph.getFunctionNodeArray().get(i).function == FunctionNodeEnum.CHARGE
								&& !graph.getFunctionNodeArray().get(i).chargingStationRetract
								&& graph.getFunctionNodeArray().get(i).callAGVNum <= 0
								&& !graph.getFunctionNodeArray().get(i).responsing){
							graph.getFunctionNodeArray().get(i).chargingStationRetract = true;
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
								ArrayList<State> triggerArray = new ArrayList<State>();
								triggerArray.add(State.NULL);
								ArrayList<Integer> destinationArray = new ArrayList<Integer>();
								destinationArray.add(chargeNode);
								synchronized(AGVArray.get(minChargeIndex)){
									if(!AGVArray.get(minChargeIndex).isOnMission() && !AGVArray.get(minChargeIndex).ReadyToOffDuty
											&& !AGVArray.get(minChargeIndex).charging){
										System.out.println(AGVArray.get(minChargeIndex).getAGVNum() + "AGV去充电");
										logger.debug(AGVArray.get(minChargeIndex).getAGVNum() + "AGV去充电");
										AGVArray.get(minChargeIndex).setMission(triggerArray, destinationArray);
									}else{
										System.out.println("充电复位延时5S，AGV已经有任务了，不能去充电了");
										logger.debug("充电复位延时5S，AGV已经有任务了，不能去充电了");
										AGVArray.get(minChargeIndex).charging = false;
										AGVArray.get(minChargeIndex).chargeCount--;
									}
								}								
							}else{
								System.out.println("无充电桩可用");
								logger.debug("无充电桩可用");
								AGVArray.get(minChargeIndex).charging = false;
								AGVArray.get(minChargeIndex).chargeCount--;
							}	
							minChargeIndex = -1;
						}
					}, 5000);		
				}
				
			}
		}
	}
	
	public void getGuiInstance(Main main, SchedulingGui schedulingGui, SetingGui setingGui, GraphingGui graphingGui){
		schedulingGuiBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				/*
				if(true){//!shipment
					if(shipmnetCount %2 == 0)
						AGVArray.get(0).getRunnable().SendActionMessage("CC02DD");
					else
						AGVArray.get(0).getRunnable().SendActionMessage("CC03DD");
					shipment = true;
					shipmnetCount++;
				}else {
					if(shipmnetCount %2 == 0)
						AGVArray.get(9).getRunnable().SendActionMessage("CC02DD");
					else 
						AGVArray.get(9).getRunnable().SendActionMessage("CC03DD");
					shipmnetCount++;
					shipment = false;
				}*/
				
				//System.out.println(dijkstra.findRoute(graph.getEdge(0), 1).getRoute()+"endNode:"+dijkstra.findRoute(graph.getEdge(0), 1).getEndNode());
				//System.out.println(myToolKit.routeToOrientation(graph,dijkstra.findRoute(18, 17).getRoute(), new AGVCar()));
				try{
					String str = "";
					for(int i = 0; i < conflictDetection.getConflictEdgeArray().size(); i++){
						if(conflictDetection.getConflictEdgeArray().get(i).occupy){
							str+=conflictDetection.getConflictEdgeArray().get(i).stratNodeNum + "||" + conflictDetection.getConflictEdgeArray().get(i).endNodeNum +"边";
							str+="/";
							for(AGVCar car : conflictDetection.getConflictEdgeArray().get(i).waitQueue){
								str += car.getAGVNum() + ",";
							}
							str+="]";
						}						
					}
					
					for(int i = 0; i < conflictDetection.getConflictNodeArray().size(); i++){
						if(conflictDetection.getConflictNodeArray().get(i).occupy){
							str+= (String.valueOf(i+1) + "点//");
							str+="/";
							for(AGVCar car :conflictDetection.getConflictNodeArray().get(i).waitQueue){
								str += car.getAGVNum() + ",";
							}
						}						
					}
					
					str+="被占用";
					stateLabel.setText(str);
				}catch (Exception e1){
					e1.printStackTrace();
				}
				
			}
		});
		setingGuiBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				main.getContentPane().removeAll();
				main.getContentPane().add(setingGui);
				main.repaint();
				main.validate();
			}
		});
		graphGuiBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				main.getContentPane().removeAll();
				main.getContentPane().add(graphingGui);
				main.repaint();
				main.validate();
			}
		});
		
	}
	
	public void setBtnColor(){
		schedulingGuiBtn.setBackground(Color.WHITE);
		schedulingGuiBtn.setForeground(new Color(30, 144, 255));
		setingGuiBtn.setBackground(new Color(30, 144, 255));
		graphGuiBtn.setBackground(new Color(30, 144, 255));
	}
	
	public void handleFunctionNodeClick(MouseEvent e){
		foundAGVNum = 0;
		if(e.getButton() == MouseEvent.BUTTON1){
			Node node = graph.searchWideNode(e.getX(), e.getY());
			for(int i = 0; i < AGVArray.size(); i++){
				if(!AGVArray.get(i).isOnMission() && AGVArray.get(i).initReady
						&& AGVArray.get(i).getStartEdge().endNode.num!=0 ){
					if(Math.pow(e.getX() - AGVArray.get(i).getX(), 2) + 
							Math.pow(e.getY() - AGVArray.get(i).getY(), 2) < 1000){
						foundAGVNum = i+1;
					}
				}
			}
			if(foundAGVNum != 0){
				SignUpDialog dialog = new SignUpDialog(foundAGVNum+"AGV路径");
				dialog.setOnDialogListener(new SignUpDialogListener(){
					public void getDialogListener(String routeStr, boolean btn){
						boolean triggerState = true;
						boolean destinationState = true;
						boolean foundNode = false;
						dialog.dispose();
						if(btn){
							if(routeStr.length() > 1 && !routeStr.equals(foundAGVNum+"AGV路径")){
								ArrayList<State> triggerArray = new ArrayList<State>();
								ArrayList<Integer> destinationArray = new ArrayList<Integer>();
								String[] route = routeStr.split("/");
								for(int i = 0; i < route.length; i++){
									if(i%2 == 0){
										if(route[i].equals("1")){
											triggerArray.add(State.SHIPMENT);
										}else if(route[i].equals("2")){
											triggerArray.add(State.UNLOADING);
										}else if(route[i].equals("0")){
											triggerArray.add(State.NULL);
										}else{
											triggerState = false;
										}
									}else{
										for(int j = 0; j < graph.getFunctionNodeArray().size(); j++){
											if(Integer.parseInt(route[i]) == graph.getFunctionNodeArray().get(j).nodeNum){
												destinationArray.add(Integer.parseInt(route[i]));
												foundNode = true;
											}
										}
										if(!foundNode)
											destinationState = false;
									}
								}
								if(destinationState && triggerState)
									AGVArray.get(foundAGVNum-1).setMission(triggerArray, destinationArray);
								else
									stateLabel.setText("设置路径错误");
								
							}
						}
					}
				});
			}else if(node != null){
				for(int i = 0; i < graph.getFunctionNodeArray().size(); i++){
					if(graph.getFunctionNodeArray().get(i).nodeNum  == node.num && !graph.getFunctionNodeArray().get(i).clicked){
						if((graph.getFunctionNodeArray().get(i).callAGVNum = sendingWhichAGV(node.num))!=0)
							graph.getFunctionNodeArray().get(i).clicked = true;
					}
				}
			}
		}
	}
	
	public int sendingWhichAGV(int endNodeNum){		
		int returnAGVNum = 0;
		ArrayList<Integer> noStartNode = new ArrayList<Integer>();
		ArrayList<Integer> isNotAlived = new ArrayList<Integer>();
		ArrayList<Integer> isOnMission = new ArrayList<Integer>();
		ArrayList<Path> pathArray = new ArrayList<Path>();
		for(int i = 0; i < AGVArray.size(); i++){
			/*
			if(!AGVArray.get(i).isAlived()){
				isNotAlived.add(i+1);
			}else{
				if(AGVArray.get(i).getStartEdge().endNode.num == 0)
					noStartNode.add(i+1);
			}*/
				
			if(AGVArray.get(i).isOnMission())
				isOnMission.add(i+1);
			
			if(AGVArray.get(i).getStartEdge().endNode.num!=0 && AGVArray.get(i).initReady
					&& !AGVArray.get(i).isOnMission() && !AGVArray.get(i).getFixRoute() && !AGVArray.get(i).charging){
				Edge edge = AGVArray.get(i).getStartEdge();
				System.out.println("startEdge startNode :" + edge.startNode.num + "endNode:" + edge.endNode.num);
				pathArray.add(dijkstra.findRoute(edge, endNodeNum));
				pathArray.get(pathArray.size()-1).setNumOfAGV(i+1);;
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
			System.out.println("result:"+pathArray.get(minIndex).getRoute());
			returnAGVNum = pathArray.get(minIndex).getNumOfAGV();
			AGVCar agvCar= AGVArray.get(pathArray.get(minIndex).getNumOfAGV()-1);
			ArrayList<State> triggerArray = new ArrayList<State>();
			triggerArray.add(State.NULL);
			ArrayList<Integer> destinationArray = new ArrayList<Integer>();
			destinationArray.add(endNodeNum);
			agvCar.setMission(triggerArray, destinationArray);
		}else{
			stateLabel.setText("没有AGV准备好");
			logger.debug("没有AGV准备好");
		}
		stateString = new StringBuffer();
		if(noStartNode.size()!=0){
			for(int i = 0; i < noStartNode.size(); i++){
				stateString.append(noStartNode.get(i));
				stateString.append(",");
			}
			stateString.append("：起始点错误   //");
		}
		
		if(isNotAlived.size()!=0){
			for(int i = 0; i < isNotAlived.size(); i++){
				stateString.append(isNotAlived.get(i));
				stateString.append(",");
			}
			stateString.append("：失去连接   //");
		}
		
		if(isOnMission.size()!=0){
			for(int i = 0; i < isOnMission.size(); i++){
				stateString.append(isOnMission.get(i));
				stateString.append(",");
			}
			stateString.append("：在执行任务中");
		}
		stateLabel.setText(stateString.toString());
		return returnAGVNum;
	}
	
	public void initConflictDetection(){
		for(int i = 0 ; i < this.conflictDetection.getConflictNodeArray().size(); i++){
			synchronized(this.conflictDetection.getConflictNodeArray().get(i)){
				this.conflictDetection.getConflictNodeArray().get(i).occupy = false;
				this.conflictDetection.getConflictNodeArray().get(i).waitQueue.clear();
			}
		}
		for(int i = 0; i < this.conflictDetection.getConflictEdgeArray().size(); i++){
			this.conflictDetection.getConflictEdgeArray().get(i).occupy = false;
			this.conflictDetection.getConflictEdgeArray().get(i).waitQueue.clear();
		}
	}
	
	public void cancelAGV(int AGVNum){
		this.AGVArray.get(AGVNum - 1).initAGV();
		for(int i = 0; i < graph.getFunctionNodeArray().size(); i++){
			if(graph.getFunctionNodeArray().get(i).callAGVNum == AGVNum){
				graph.getFunctionNodeArray().get(i).callAGVNum = -1;
				graph.getFunctionNodeArray().get(i).clicked = false;
				graph.getFunctionNodeArray().get(i).responsing = false;
			}
		}
		for(int i = 0; i < this.conflictDetection.getConflictEdgeArray().size(); i++){
			if(this.conflictDetection.getConflictEdgeArray().get(i).occupy){
				for(int j = 0; j < this.conflictDetection.getConflictEdgeArray().get(i).waitQueue.size(); j++){
					if(this.conflictDetection.getConflictEdgeArray().get(i).waitQueue.get(j).getAGVNum() == AGVNum)
						this.conflictDetection.getConflictEdgeArray().get(i).removeAGV(this.AGVArray.get(AGVNum-1), null);
				}
			}
		}
		for(int i = 0; i < this.conflictDetection.getConflictNodeArray().size(); i++){
			if(this.conflictDetection.getConflictNodeArray().get(i).occupy){
				for(int j = 0; j < this.conflictDetection.getConflictNodeArray().get(i).waitQueue.size(); j++){
					if(this.conflictDetection.getConflictNodeArray().get(i).waitQueue.get(j).getAGVNum() == AGVNum)
						this.conflictDetection.getConflictNodeArray().get(i).removeAGV(this.AGVArray.get(AGVNum-1));
				}
			}
		}
	}
	
	public void offDuty(){
		System.out.println("下班");
		logger.debug("点击下班按钮");
		for(int i = 0; i < this.AGVArray.size(); i++){
			if(this.AGVArray.get(i).getRunnable() != null){
				if(this.AGVArray.get(i).charging && this.AGVArray.get(i).getAGVStopInNode() >= 0
						&& graph.getFunctionNodeArray().get(this.AGVArray.get(i).getAGVStopInNode()).function == FunctionNodeEnum.CHARGE){
					this.AGVArray.get(i).setOffDuty();
					this.AGVArray.get(i).chargeOffDuty();
				}else if(this.AGVArray.get(i).isOnMission()){
					this.AGVArray.get(i).setOffDuty();
				}else if(!this.AGVArray.get(i).isOnMission() && !this.AGVArray.get(i).charging){
					this.AGVArray.get(i).setOffDuty();
					this.AGVArray.get(i).AGVOffDuty();
				}
			}
		}
	}
	
	public void clickOnDutyBtn(){
		System.out.println("上班");
		logger.debug("点击上班按钮");
		this.onDutyBtnState.state = true;
		this.clearTimer.schedule(new TimerTask(){
			public void run(){
				onDutyBtnState.state = false;
			}
		}, 120000);
	}
	
	public void setChargeTime(int chargeDuration, int chargeGep){
		System.out.println(chargeDuration + "'" + chargeGep);
		this.chargeGap = chargeGep;
		this.chargeTimer.stop();
		chargeTimer = new Timer(this.chargeGap, new ChargeTimerListener());
		chargeTimer.start();
		for(int i = 0; i < this.AGVArray.size(); i++){
			this.AGVArray.get(i).chargeDuration = chargeDuration;
		}
	}
	
	public void cancelPlayWaring(){
		this.playAudio.cancelPlayWaring();
	}
	
	public void setManualModel(boolean state){
		this.manualModel.state = state;
	}
	
	public boolean getManualModel(){
		return this.manualModel.state;
	}
}
