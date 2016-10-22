import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Mainwindow extends JFrame{
	private static final long serialVersionUID = 1L;
	private Dimension windowSize;
	private Dimension panelSize;
	private int numOfAGV;
	private MainPanel mainPanel;
	private ArrayList<AGVCar> AGVArray;
	private Graph graph;
	private boolean firstInit;
	private static Timer timer;
	private ServerSocket serverSocket;
	private ExecutorService receiveExecutor;
	private ToolKit toolKit;
	
	public Mainwindow(){
		super("AGV调度系统");
		graph = new Graph();
		numOfAGV = 10;
		AGVArray = new ArrayList<AGVCar>();
		for(int i = 0; i < numOfAGV; i++){
			AGVArray.add(new AGVCar());
		}
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		windowSize = new Dimension(screenSize.width, (int)((int)screenSize.height*0.94));
		panelSize = new Dimension(0, 0);
		
		JMenuBar menuBar = new JMenuBar();
		  
		JMenu fileMenu = new JMenu("文件");
		JMenu editMenu = new JMenu("编辑");
		JMenu formatMenu = new JMenu("格式");
		JMenu checkMenu = new JMenu("查看");
		JMenu helpMenu = new JMenu("帮助");
		 
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(formatMenu);
		menuBar.add(checkMenu);
		menuBar.add(helpMenu);
		
		mainPanel = new MainPanel();
		timer = new Timer(100, new TimerListener());
		
		this.getContentPane().add(mainPanel);	  
		this.setJMenuBar(menuBar);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		  
		//用来设置窗口随屏幕大小改变
		this.setSize(windowSize);
		this.setVisible(true);		   
		
		try{
			serverSocket = new ServerSocket(8001);
		}catch(Exception e){
			e.printStackTrace();
		}
		//receiveExecutor = Executors.newFixedThreadPool(7);
		toolKit = new ToolKit();
		new Thread(new Runnable(){
			public void run(){
				while(true){
					Socket socket = null;
					try{
						socket = serverSocket.accept();
						new Thread(new HandleReceiveMessage(socket)).start();
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		}).start();
		
	}
	
	class HandleReceiveMessage implements Runnable{
		private InputStream inputStream;
		private OutputStream outputStream;
		HandleReceiveMessage(Socket socket){
			try{
				inputStream = socket.getInputStream();
				outputStream = socket.getOutputStream();
				outputStream.write(toolKit.HexString2Bytes("1234"));
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
		public void run(){
			try{
				byte[] buff = new byte[4];
				int len = inputStream.read(buff);
				toolKit.printHexString(buff);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	
	class MainPanel extends JPanel{
		private static final long serialVersionUID = 1L;
		
		public MainPanel(){
			
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
		g.setColor(Color.green);
		g.fillOval(AGVArray.get(0).getX() - 12, AGVArray.get(0).getY() - 12, 24, 24);
		g.setColor(Color.red);
		g.setFont(new java.awt.Font("Dialog", 1, 18));
		g.drawString("0",AGVArray.get(0).getX() - 5, AGVArray.get(0).getY() + 5);
	}
	
	class TimerListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			repaint();
			if(!firstInit){
				panelSize.width = mainPanel.getWidth();
				panelSize.height = mainPanel.getHeight();
				graph.createGraph(panelSize);
				AGVArray.get(0).setOnEdge(graph.getEdge(3));
				firstInit = true;
			}else {
				AGVArray.get(0).stepForward();
			}
						
		}
	}
	
	public static void main(String[] args) {
		Mainwindow mainwidow = new Mainwindow();
		timer.start();
	}
}
