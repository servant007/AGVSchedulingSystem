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
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import schedulingSystem.component.Graph;
import schedulingSystem.component.Main;
import schedulingSystem.component.Node;
import schedulingSystem.component.Path;
import schedulingSystem.toolKit.ReceiveStationMessage;
import schedulingSystem.toolKit.MyToolKit;
import schedulingSystem.toolKit.ReceiveAGVMessage;
import schedulingSystem.toolKit.RoundButton;
import schedulingSystem.toolKit.SignUpDialog;
import schedulingSystem.toolKit.SignUpDialogListener;


public class SchedulingGui extends JPanel{
	private static final long serialVersionUID = 1L;
	private static SchedulingGui instance;
	private static Logger logger = Logger.getLogger(SchedulingGui.class.getName());
	private Dimension panelSize;
	private int numOfAGV;
	private ArrayList<AGVCar> AGVArray;
	private static Graph graph;
	private boolean firstInit;
	private ServerSocket serverSocket;
	private MyToolKit myToolKit;
	private RoundButton schedulingGuiBtn;
	private RoundButton setingGuiBtn;
	private RoundButton graphGuiBtn;
	private Timer timer;
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
	
	
	public static  SchedulingGui getInstance(Graph graph1){
		graph = graph1;
		if(instance == null){
			instance = new SchedulingGui();
		}
		return instance;
	}
	
	private SchedulingGui(){
		System.out.println("jisuan:" + String.valueOf((long)20161111^key));
		try{
			FileReader fr = new FileReader(".\\data\\date.txt");
			BufferedReader br = new BufferedReader(fr);
			password = Long.parseLong(br.readLine());
			System.out.println(password);
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}

		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH)+1;
		int date = c.get(Calendar.DATE);
		systemTime = date + 100*month + 10000*year; 
		System.out.println(systemTime);
		deadline = String.valueOf(password^key);
		
		
		
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
		panelSize = new Dimension(0, 0);
		
		numOfAGV = graph.getAGVSeting().size();
		conflictDetection = new ConflictDetection(graph);
		AGVArray = new ArrayList<AGVCar>();
		for(int i = 0; i < numOfAGV; i++){
			AGVArray.add(new AGVCar(i+1, graph, conflictDetection));
		}

		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		schedulingGuiBtn = new RoundButton("调度界面");
		schedulingGuiBtn.setBounds(0, 0, screenSize.width/3, screenSize.height/20);
		schedulingGuiBtn.setForeground(new Color(30, 144, 255));
		schedulingGuiBtn.setBackground(Color.WHITE);
		
		setingGuiBtn = new RoundButton("设置界面");
		setingGuiBtn.setBounds(screenSize.width/3, 0, screenSize.width/3, screenSize.height/20);

		graphGuiBtn = new RoundButton("管理界面");
		graphGuiBtn.setBounds(2*screenSize.width/3, 0, screenSize.width/3, screenSize.height/20);

		stateLabel = new JLabel();
		stateLabel.setBounds(0, 22*screenSize.height/25, screenSize.width, screenSize.height/25);
		stateLabel.setFont(new Font("宋体", Font.BOLD, 25));
		timer = new Timer(100, new TimerListener());
		timer.start();
		
		
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
									receiveStationMessage = new ReceiveStationMessage(socket, graph, AGVArray, dijkstra, stateLabel, i);
									executorService.execute(receiveStationMessage);
								}
							}
							
							if(receiveStationMessage == null){
								System.out.println("receiveStationMessage == null");
								receiveAGVMessage = new ReceiveAGVMessage(socket, AGVArray, graph);
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
		if(firstInit){
			myToolKit.drawGraph(g, graph, reverseColor);
			g.setColor(Color.black);
			g.setFont(new java.awt.Font("Dialog", Font.BOLD, 25));
			for(int i = 0; i < AGVArray.size(); i++){
				if(AGVArray.get(i).isAlived()){
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
				if(AGVArray.get(i).isOnMission())
					g.drawString(AGVArray.get(i).getMissionString(), AGVArray.get(i).getX(), AGVArray.get(i).getY()-25);
			}
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

			if(!firstInit){
				panelSize.width = screenSize.width;
				panelSize.height = screenSize.height;
				firstInit = true;
			}else {	
				for(int i = 0; i < AGVArray.size(); i ++){
					AGVArray.get(i).stepForward();
				}
			}		
		}
	}
	
	public void getGuiInstance(Main main, SchedulingGui schedulingGui, SetingGui setingGui, GraphingGui graphingGui){
		schedulingGuiBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				//System.out.println(dijkstra.findRoute(18, 17).getRoute()+"endNode:"+dijkstra.findRoute(18, 17).getEndNode());
				//System.out.println(myToolKit.routeToOrientation(graph,dijkstra.findRoute(18, 17).getRoute(), new AGVCar()));
				try{
					String str = "";
					for(int i = 0; i < conflictDetection.getConflictEdgeArray().size(); i++){
						if(conflictDetection.getConflictEdgeArray().get(i).occupy){
							str+=conflictDetection.getConflictEdgeArray().get(i).stratNodeNum + "||" + conflictDetection.getConflictEdgeArray().get(i).endNodeNum +"边";
							str+="/";
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
				if(!AGVArray.get(i).isOnMission()){
					if(Math.pow(e.getX() - AGVArray.get(i).getX(), 2) + 
							Math.pow(e.getY() - AGVArray.get(i).getY(), 2) < 1000){
						foundAGVNum = i+1;
					}
				}
			}
			if(foundAGVNum != 0){
				SignUpDialog dialog = new SignUpDialog("AGV路径");
				dialog.setOnDialogListener(new SignUpDialogListener(){
					public void getDialogListener(String routeStr, boolean btn){
						boolean triggerState = true;
						boolean destinationState = true;
						boolean foundNode = false;
						dialog.dispose();
						if(btn){
							if(routeStr.length() > 1 && !routeStr.equals("AGV路径")){
								ArrayList<State> triggerArray = new ArrayList<State>();
								ArrayList<Integer> destinationArray = new ArrayList<Integer>();
								String[] route = routeStr.split("/");
								for(int i = 0; i < route.length; i++){
									if(i%2 == 0){
										if(route[i].equals("4")){
											triggerArray.add(State.SHIPMENT);
										}else if(route[i].equals("5")){
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
									AGVArray.get(foundAGVNum-1).setDestinationNode(triggerArray, destinationArray);
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
			
			if(!AGVArray.get(i).isAlived()){
				isNotAlived.add(i+1);
			}else{
				if(AGVArray.get(i).getStartEdge().endNode.num == 0)
					noStartNode.add(i+1);
			}
				
			if(AGVArray.get(i).isOnMission())
				isOnMission.add(i+1);
			
			if(AGVArray.get(i).getStartEdge().endNode.num!=0 && AGVArray.get(i).isAlived() 
					&& !AGVArray.get(i).isOnMission() && !AGVArray.get(i).getFixRoute()){
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
			agvCar.setDestinationNode(triggerArray, destinationArray);
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
}
