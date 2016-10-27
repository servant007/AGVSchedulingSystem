package schedulingSystem.gui;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Label;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.apache.log4j.Logger;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import schedulingSystem.component.AGVCar;
import schedulingSystem.component.Graph;
import schedulingSystem.component.Main;
import schedulingSystem.toolKit.MyToolKit;
import schedulingSystem.toolKit.RoundButton;

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
	private MyToolKit toolKit;
	private RoundButton schedulingGuiBtn;
	private RoundButton setingGuiBtn;
	private RoundButton graphGuiBtn;
	private RoundButton importGraphBtn;
	private Timer timer;
	private JLabel stateLabel;
	private StringBuffer stateString;
	private Dimension screenSize;
	private Main main;
	
	
	public static SchedulingGui getInstance(){
		if(instance == null){
			instance = new SchedulingGui();
		}
		return instance;
	}
	
	private SchedulingGui(){
		stateString = new StringBuffer();
		graph = new Graph();
		toolKit = new MyToolKit();
		panelSize = new Dimension(0, 0);
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
		importGraphBtn.setBounds(9*screenSize.width/10, 17*screenSize.height/20, screenSize.width/10, screenSize.height/20);
		importGraphBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				
				importNewGraph();
			}
		});
		stateLabel = new JLabel();
		stateLabel.setBounds(0, 22*screenSize.height/25, screenSize.width, screenSize.height/25);
		stateLabel.setFont(new Font("宋体", Font.BOLD, 25));
		

		this.setLayout(null);
		this.add(schedulingGuiBtn);
		this.add(setingGuiBtn);
		this.add(graphGuiBtn);
		this.add(stateLabel);
		this.add(importGraphBtn);
		
		
		
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
							new Thread(new HandleReceiveMessage(socket)).start();
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
	}//init
	
	@Override
	public void paint(Graphics g){
		super.paint(g);
		//super.paintComponents(g);
		if(firstInit){
			drawGraph(g);
			drawAGV(g);
		}
	}
	
	public void getGuiInstance(Main main, SchedulingGui schedulingGui, SetingGui setingGui, GraphingGui graphingGui){
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
	
	class HandleReceiveMessage implements Runnable{
		private InputStream inputStream;
		private OutputStream outputStream;
		private Socket socket;
		private long lastCommunicationTime;
		private long reciveDelayTime = 6000;
		
		HandleReceiveMessage(Socket socket){
			System.out.println("socket connect:"+socket.toString());
			this.socket = socket;
			try{
				inputStream = socket.getInputStream();
				outputStream = socket.getOutputStream();
				outputStream.write(toolKit.HexString2Bytes("AAC0FFEEBB"));
			}catch(Exception e){
				e.printStackTrace();
				logger.error(e);
			}
			lastCommunicationTime = System.currentTimeMillis();
		}
		public void run(){
			while(true){
				if(System.currentTimeMillis() - lastCommunicationTime < reciveDelayTime){//			
					try{
						if(inputStream.available() > 0){
							lastCommunicationTime = System.currentTimeMillis();
							byte[] buff = new byte[5];
							inputStream.read(buff);
							String message = toolKit.printHexString(buff);
							if(message.startsWith("AA")&&message.endsWith("BB")){
								int noOfAGV = Integer.parseInt(message.substring(2, 4), 16);
								if(!message.substring(4, 8).equals("BABA")){
									//System.out.println("4-8:"+message.substring(4, 8));
									AGVArray.get(noOfAGV).setTime(System.currentTimeMillis());
									int noOfEdge = Integer.parseInt(message.substring(4, 6), 16);
									int electricity = Integer.parseInt(message.substring(6, 8), 16);
									//System.out.println("noOfEdge:"+String.valueOf(noOfEdge)+"//"+String.valueOf(electricity));
									AGVArray.get(noOfAGV).setOnEdge(graph.getEdge(noOfEdge - 1));
									AGVArray.get(noOfAGV).setElectricity(electricity);
									
								}else{
									AGVArray.get(noOfAGV).setTime(System.currentTimeMillis());
									outputStream.write(toolKit.HexString2Bytes("AAC0FFEEBB"));
								}
							}
						}else {
							Thread.sleep(10);
						}
						
						
						
					}catch(Exception e){
						e.printStackTrace();
						logger.error(e);
					}
				}else{
					try{
						if(inputStream != null)
							inputStream.close();
						if(outputStream != null)
							outputStream.close();
						if(socket != null)
							socket.close();
					}catch(Exception e){
						e.printStackTrace();
						logger.error(e);
					}
					break;//退出while循环
				}				
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
			if(System.currentTimeMillis() - AGVArray.get(i).getLastTime() < 6000.0){
				g.setColor(Color.green);
				g.fillOval(AGVArray.get(i).getX() - 17, AGVArray.get(i).getY() - 17, 34, 34);
				g.setColor(Color.black);
				g.setFont(new java.awt.Font("Dialog", 1, 20));
				g.drawString(String.valueOf(i), AGVArray.get(i).getX() - 5, AGVArray.get(i).getY() + 5);
				//System.out.println("X:"+String.valueOf(AGVArray.get(i).getX()));
				//System.out.println("Y:"+String.valueOf(AGVArray.get(i).getX()));
			}else{
				g.setColor(Color.red);
				g.fillOval(AGVArray.get(i).getX() - 17, AGVArray.get(i).getY() - 17, 34, 34);
				g.setColor(Color.BLACK);
				g.setFont(new java.awt.Font("Dialog", 1, 20));
				g.drawString(String.valueOf(i), AGVArray.get(i).getX() - 4, AGVArray.get(i).getY() + 8);
				//System.out.println("X:"+String.valueOf(AGVArray.get(i).getX()));
				//System.out.println("Y:"+String.valueOf(AGVArray.get(i).getX()));
			}
		}
	}
	
	class TimerListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			repaint();
			if(!firstInit){
				panelSize.width = screenSize.width;
				panelSize.height = screenSize.height;
				graph.createGraph(panelSize);
				firstInit = true;
			}else {	
				for(int i = 0; i < AGVArray.size(); i ++){
					AGVArray.get(i).stepForward();
				}
			}		
		}
	}
	
	public void importNewGraph(){
		Graph graph = new Graph();
		try{
			JFileChooser jfc = new JFileChooser();
			jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			jfc.showDialog(new Label(), "选择地图");
			File file = jfc.getSelectedFile();
			if(file != null){
				System.out.println(file.getPath());
				InputStream is = new FileInputStream(file.getPath());
				Workbook wb = Workbook.getWorkbook(is);
				
				Sheet sheetNodes = wb.getSheet("nodes");
				for(int i = 0; i < sheetNodes.getRows(); i++){
					int x=0, y=0, num=0;
					for(int j = 0; j < 3; j++){
						Cell cell0 = sheetNodes.getCell(j,i);
							String str = cell0.getContents();
							if(j == 0)
								num = Integer.parseInt(str);
							if(j == 1)
								x = Integer.parseInt(str);
							if(j == 2)
								y = Integer.parseInt(str);
							System.out.println("++:"+str);						
					}
					graph.addImportNode(x, y, num);
				}
				
				Sheet sheetEdges = wb.getSheet("edges");
				for(int i = 0; i < sheetEdges.getRows(); i++){
					int start=0, end=0, dis=0;
					for(int j = 0; j < 3; j++){
						Cell cell0 = sheetEdges.getCell(j,i);
							String str = cell0.getContents();
							if(j == 0)
								start = Integer.parseInt(str);
							if(j == 1)
								end = Integer.parseInt(str);
							if(j == 2)
								dis = Integer.parseInt(str);
							System.out.println("++:"+str);						
					}
					graph.addEdge(start, end, dis);
				}
				this.graph = graph;
			}			
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e);
		}
		
	}
	
	public void setBtnColor(){
		schedulingGuiBtn.setBackground(Color.WHITE);
		schedulingGuiBtn.setForeground(new Color(30, 144, 255));
		setingGuiBtn.setBackground(new Color(30, 144, 255));
		graphGuiBtn.setBackground(new Color(30, 144, 255));
	}
}
