package schedulingSystem.gui;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import java.awt.Graphics;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.apache.log4j.Logger;

import schedulingSystem.component.AGVCar;
import schedulingSystem.component.Dijkstra;
import schedulingSystem.component.Graph;
import schedulingSystem.component.Main;
import schedulingSystem.component.Node;
import schedulingSystem.toolKit.HandleReceiveMessage;
import schedulingSystem.toolKit.MyToolKit;
import schedulingSystem.toolKit.RoundButton;
import schedulingSystem.toolKit.RunnableListener;

public class SchedulingGui extends JPanel{
	private static final long serialVersionUID = 1L;
	private static SchedulingGui instance;
	private static Logger logger = Logger.getLogger(SchedulingGui.class.getName());
	private Dimension panelSize;
	private int numOfAGV;
	private ArrayList<AGVCar> AGVArray;
	private Graph graph;
	private boolean firstInit;
	private ServerSocket serverSocket;
	private MyToolKit myToolKit;
	private RoundButton schedulingGuiBtn;
	private RoundButton setingGuiBtn;
	private RoundButton graphGuiBtn;
	private RoundButton importGraphBtn;
	private Timer timer;
	private JLabel stateLabel;
	private StringBuffer stateString;
	private Dimension screenSize;
	private ExecutorService executorService;
	private Dijkstra dijkstra;
	private HandleReceiveMessage handleReceiveMessage;
	
	public static SchedulingGui getInstance(){
		if(instance == null){
			instance = new SchedulingGui();
		}
		return instance;
	}
	
	private SchedulingGui(){
		myToolKit = new MyToolKit();
		graph = new Graph();
		graph = myToolKit.importNewGraph("C:/testGraph.xls");
		dijkstra = new Dijkstra(graph);
		stateString = new StringBuffer();
		panelSize = new Dimension(0, 0);
		executorService = Executors.newFixedThreadPool(15);
		numOfAGV = 10;
		AGVArray = new ArrayList<AGVCar>();
		for(int i = 0; i < numOfAGV; i++){
			AGVArray.add(new AGVCar());
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
		
		importGraphBtn = new RoundButton("导入地图");
		importGraphBtn.setFont(new Font("宋体",Font.BOLD, 23));
		importGraphBtn.setBounds(13*screenSize.width/14, 19*screenSize.height/22, screenSize.width/14, screenSize.height/22);
		importGraphBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				graph = myToolKit.importNewGraph(null);
				dijkstra = new Dijkstra(graph);
			}
		});

		stateLabel = new JLabel();
		stateLabel.setBounds(0, 22*screenSize.height/25, screenSize.width, screenSize.height/25);
		stateLabel.setFont(new Font("宋体", Font.BOLD, 25));
		
		try{
			serverSocket = new ServerSocket(8001);
		}catch(Exception e){
			e.printStackTrace();
			stateString.append(e.toString()).append("//");
			stateLabel.setText(stateString.toString());
			logger.error(e);
		}

		new Thread(new Runnable(){
			public void run(){
				while(true){
					Socket socket = null;
					try{
						if(serverSocket != null){
							socket = serverSocket.accept();
							handleReceiveMessage = new HandleReceiveMessage(socket, AGVArray, graph);
							handleReceiveMessage.setOnRunnableListener(new RunnableListener(){
								public void getAGVNum(int NOOfAGV){
									AGVArray.get(NOOfAGV).setRunnabel(handleReceiveMessage);
									System.out.println(NOOfAGV+"号AGV返回HandleReceiveMessage");
								}
							});
							executorService.execute(handleReceiveMessage);
						}else{
							stateString.append("serverSocket nullPointer//");
							stateLabel.setText(stateString.toString());
						}
					}catch(Exception e){
						e.printStackTrace();
						logger.error(e);
					}
				}
			}
		}).start();
		timer = new Timer(100, new TimerListener());
		timer.start();
		
		
		this.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				if(e.getButton() == MouseEvent.BUTTON1){
					Node node = graph.searchWideNode(e.getX(), e.getY());
					if(node != null){
						for(int i = 0; i < graph.getShipmentNode().size(); i++){
							if(graph.getShipmentNode().get(i).nodeNum  == node.num){
								if(AGVArray.get(1).getStartNode()!=0){
									ArrayList<Integer> route = dijkstra.findRoute(AGVArray.get(1).getStartNode(), node.num);
									System.out.println("result:"+route);
									//myToolKit.routeToOrientation(graph, route);
									AGVArray.get(1).getRunnable().SendMessage(myToolKit.routeToOrientation(graph, route));
								}else{
									System.out.println("没有定位agv");
								}
							}
						}
						
						for(int i = 0; i < graph.getUnloadingNode().size(); i++){
							if(graph.getUnloadingNode().get(i).nodeNum  == node.num){
								if(AGVArray.get(1).getStartNode()!=0){
									ArrayList<Integer> route = dijkstra.findRoute(AGVArray.get(1).getStartNode(), node.num);
									System.out.println("result:"+route);
									//myToolKit.routeToOrientation(graph, route);
									AGVArray.get(1).getRunnable().SendMessage(myToolKit.routeToOrientation(graph, route));
								}else{
									System.out.println("没有定位agv");
								}
								
							}
						}	
					}
				}
			}
		});
	
		this.setLayout(null);
		this.add(schedulingGuiBtn);
		this.add(setingGuiBtn);
		this.add(graphGuiBtn);
		this.add(stateLabel);
		this.add(importGraphBtn);
	}//init
	
	@Override
	public void paint(Graphics g){
		super.paint(g);
		//super.paintComponents(g);
		if(firstInit){
			myToolKit.drawGraph(g, graph);
			myToolKit.drawAGV(g, AGVArray);
		}
	}
	
	public void getGuiInstance(Main main, SchedulingGui schedulingGui, SetingGui setingGui, GraphingGui graphingGui){
		schedulingGuiBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try{					
					
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
	
	class TimerListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			repaint();
			if(!firstInit){
				panelSize.width = screenSize.width;
				panelSize.height = screenSize.height;
				//graph.createGraph(panelSize);
				firstInit = true;
			}else {	
				for(int i = 0; i < AGVArray.size(); i ++){
					AGVArray.get(i).stepForward();
				}
			}		
		}
	}
	
	public void setBtnColor(){
		schedulingGuiBtn.setBackground(Color.WHITE);
		schedulingGuiBtn.setForeground(new Color(30, 144, 255));
		setingGuiBtn.setBackground(new Color(30, 144, 255));
		graphGuiBtn.setBackground(new Color(30, 144, 255));
	}

}
