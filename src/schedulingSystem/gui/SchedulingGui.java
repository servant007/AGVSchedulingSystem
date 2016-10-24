package schedulingSystem.gui;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import schedulingSystem.component.AGVCar;
import schedulingSystem.component.Graph;
import schedulingSystem.toolKit.MyToolKit;
import schedulingSystem.toolKit.RoundButton;

public class SchedulingGui extends JFrame{
	private static final long serialVersionUID = 1L;
	private Dimension panelSize;
	private int numOfAGV;
	private MainPanel mainPanel;
	private ArrayList<AGVCar> AGVArray;
	private Graph graph;
	private boolean firstInit;
	private ServerSocket serverSocket;
	private MyToolKit toolKit;
	private RoundButton schedulingGuiBtn;
	private RoundButton setingGuiBtn;
	private RoundButton graphGuiBtn;
	
	public SchedulingGui(){
		super("AGV调度系统");
		graph = new Graph();
		toolKit = new MyToolKit();
		panelSize = new Dimension(0, 0);
		numOfAGV = 10;
		AGVArray = new ArrayList<AGVCar>();
		
		for(int i = 0; i < numOfAGV; i++){
			AGVArray.add(new AGVCar());
		}
		
		try{
			serverSocket = new ServerSocket(8001);
		}catch(Exception e){
			//e.printStackTrace();
		}

		new Thread(new Runnable(){
			public void run(){
				while(true){
					Socket socket = null;
					try{
						socket = serverSocket.accept();
						new Thread(new HandleReceiveMessage(socket)).start();
					}catch(Exception e){
						//e.printStackTrace();
					}
				}
			}
		}).start();
		
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		schedulingGuiBtn = new RoundButton("调度界面");
		schedulingGuiBtn.setBounds(0, 0, screenSize.width/3, screenSize.height/20);
		setingGuiBtn = new RoundButton("设置界面");
		setingGuiBtn.setBounds(screenSize.width/3, 0, screenSize.width/3, screenSize.height/20);
		graphGuiBtn = new RoundButton("画图界面");
		graphGuiBtn.setBounds(2*screenSize.width/3, 0, screenSize.width/3, screenSize.height/20);
		
		mainPanel = new MainPanel();
		mainPanel.setLayout(null);
		mainPanel.add(schedulingGuiBtn);
		mainPanel.add(setingGuiBtn);
		mainPanel.add(graphGuiBtn);
		
		this.getContentPane().add(mainPanel);	  
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setExtendedState(Frame.MAXIMIZED_BOTH);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	public void getGuiInstance(SchedulingGui schedulingGui, SetingGui setingGui, GraphingGui graphingGui){
		schedulingGuiBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				
			}
		});

		setingGuiBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				schedulingGui.setVisible(false);
				setingGui.setVisible(true);
				graphingGui.setVisible(false);
			}
		});

		graphGuiBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				schedulingGui.setVisible(false);
				setingGui.setVisible(false);
				graphingGui.setVisible(true);
			}
		});
	}
	
	class HandleReceiveMessage implements Runnable{
		private InputStream inputStream;
		private OutputStream outputStream;
		HandleReceiveMessage(Socket socket){
			try{
				inputStream = socket.getInputStream();
				outputStream = socket.getOutputStream();
				outputStream.write(toolKit.HexString2Bytes("AAC0FFEEBB"));
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		public void run(){
			while(true){
				try{
					byte[] buff = new byte[5];
					inputStream.read(buff);
					String message = toolKit.printHexString(buff);
					if(message.startsWith("AA")&&message.endsWith("BB")){
						int noOfAGV = Integer.parseInt(message.substring(2, 4), 16);
						if(message.substring(4, 8) != "BABY"){
							AGVArray.get(noOfAGV).setTime(System.currentTimeMillis());
							int noOfEdge = Integer.parseInt(message.substring(4, 6), 16);
							int electricity = Integer.parseInt(message.substring(6, 8), 16);
							AGVArray.get(noOfAGV).setOnEdge(graph.getEdge(noOfEdge));
							AGVArray.get(noOfAGV).setElectricity(electricity);
						}else{
							AGVArray.get(noOfAGV).setTime(System.currentTimeMillis());
							outputStream.write(toolKit.HexString2Bytes("AAC0FFEEBB"));
						}
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	
	
	class MainPanel extends JPanel{
		private static final long serialVersionUID = 1L;
		
		public MainPanel(){
			Timer timer = new Timer(100, new TimerListener());
			timer.start();
		}
		protected void paintComponent(Graphics g){
			super.paintComponents(g);
			if(firstInit){
				drawGraph(g);
				drawAGV(g);
			}
		}
	}
	
	public void drawGraph(Graphics g){
		((Graphics2D)g).setStroke(new BasicStroke(6.0f));
		g.setColor(Color.BLACK);
		for(int i = 0 ; i < graph.getEdgeSize(); i++)
			g.drawLine(graph.getEdge(i).startNode.x, graph.getEdge(i).startNode.y, graph.getEdge(i).endNode.x, graph.getEdge(i).endNode.y);
		
		g.setColor(Color.YELLOW);
		for(int i = 0 ; i < graph.getNodeSize(); i++)
			g.fillRect(graph.getNode(i).x - 5, graph.getNode(i).y - 5, 10, 10);
	}
	
	public void drawAGV(Graphics g){
		for(int i = 0; i < AGVArray.size(); i++){
			if(System.currentTimeMillis() - AGVArray.get(i).getLastTime() < 5000.0){
				g.setColor(Color.green);
				g.fillOval(AGVArray.get(i).getX() - 17, AGVArray.get(i).getY() - 17, 34, 34);
				g.setColor(Color.black);
				g.setFont(new java.awt.Font("Dialog", 1, 20));
				g.drawString(String.valueOf(i), AGVArray.get(i).getX() - 5, AGVArray.get(i).getY() + 5);
			}else{
				g.setColor(Color.red);
				g.fillOval(AGVArray.get(i).getX() - 17, AGVArray.get(i).getY() - 17, 34, 34);
				g.setColor(Color.BLACK);
				g.setFont(new java.awt.Font("Dialog", 1, 20));
				g.drawString(String.valueOf(i), AGVArray.get(i).getX() - 4, AGVArray.get(i).getY() + 8);
			}
		}
	}
	
	class TimerListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			repaint();
			if(!firstInit){
				panelSize.width = mainPanel.getWidth();
				panelSize.height = mainPanel.getHeight();
				graph.createGraph(panelSize);
				firstInit = true;
			}else {	
				for(int i = 0; i < AGVArray.size(); i ++){
					AGVArray.get(i).stepForward();
				}
			}		
		}
	}
}
