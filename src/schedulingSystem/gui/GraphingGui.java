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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JFrame;

import javax.swing.JPanel;
import javax.swing.Timer;

import schedulingSystem.component.Graph;
import schedulingSystem.component.Node;
import schedulingSystem.toolKit.*;;

public class GraphingGui extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RoundButton schedulingGuiBtn;
	private RoundButton setingGuiBtn;
	private RoundButton graphGuiBtn;
	private int x;
	private int y;
	private ArrayList<Node> tempNodeArray;
	private Node tempStrNode;
	private Node tempEndNode;
	private boolean mouseClicked;
	private boolean onceAddEdge;
	private boolean addStrNode;
	private Graph graph;
	
	public GraphingGui(){
		super("AGV调度系统");
		tempStrNode = new Node();
		tempEndNode = new Node();
		tempNodeArray = new ArrayList<Node>();
		graph = new Graph();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setExtendedState(Frame.MAXIMIZED_BOTH);
		
		MainPanel mainPanel = new MainPanel();

		schedulingGuiBtn = new RoundButton("调度界面");
		schedulingGuiBtn.setBounds(0, 0, screenSize.width/3, screenSize.height/20);
		schedulingGuiBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				
			}
		});
		setingGuiBtn = new RoundButton("设置界面");
		setingGuiBtn.setBounds(screenSize.width/3, 0, screenSize.width/3, screenSize.height/20);
		setingGuiBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				
			}
		});
		graphGuiBtn = new RoundButton("画图界面");
		graphGuiBtn.setBounds(2*screenSize.width/3, 0, screenSize.width/3, screenSize.height/20);
		graphGuiBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				
			}
		});
		mainPanel.setLayout(null);
		mainPanel.add(schedulingGuiBtn);
		mainPanel.add(setingGuiBtn);
		mainPanel.add(graphGuiBtn);

		this.getContentPane().add(mainPanel);
		this.addMouseMotionListener(new MouseAdapter(){
			public void mouseMoved(MouseEvent e){
				if(mouseClicked){
					tempEndNode.x = e.getX() - 16;
					tempEndNode.y = e.getY() - 58;
					repaint();
				}
			}
		});
		this.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				if(e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 1){
					if(!addStrNode){
						tempStrNode = new Node(e.getX() - 16, e.getY() - 58);
						tempEndNode = new Node(e.getX() - 16, e.getY() - 58);
						mouseClicked = true;
						addStrNode = true;
					}else {
						if(Math.abs(e.getX() - tempStrNode.x) > Math.abs(e.getY() - tempStrNode.y)){
							tempEndNode = new Node(e.getX() - 16, tempStrNode.y);
						}else {
							tempEndNode = new Node(tempStrNode.x, e.getY() - 58);
						}
						//弹出对话框判断是否添加
						graph.addEdge(tempStrNode, tempEndNode);
						mouseClicked = false;
						addStrNode = false;
						tempStrNode.x = 0;
						tempStrNode.y = 0;
						tempEndNode.x = 0;
						tempEndNode.y = 0;
					}
					repaint();
				}else if(e.getButton() == MouseEvent.BUTTON3){
					/*
					if(tempNode.x != 0 && tempNode.y != 0 ){
						tempNodeArray.add(tempNode);
						repaint();
					}*/
				}
				
				if(e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2){
					Node node = graph.searchNode(new Node(e.getX(), e.getY()));
					if(node != null){
						if(!addStrNode){
							tempStrNode = new Node(node.x, node.y);
							tempEndNode = new Node(node.x, node.y);
							mouseClicked = true;
							addStrNode = true;
						}
					}
				}
			}
		});
	}
	
	public void getGuiInstance(SchedulingGui schedulingGui, SetingGui setingGui, GraphingGui graphingGui){
		schedulingGuiBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				schedulingGui.setVisible(true);
				setingGui.setVisible(false);
				graphingGui.setVisible(false);
			}
		});

		setingGuiBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				schedulingGui.setVisible(false);
				setingGui.setVisible(true);
				graphingGui.setVisible(false);
			}
		});
		
	}
	
	class MainPanel extends JPanel{
		private static final long serialVersionUID = 1L;
		
		public MainPanel(){
			Timer timer = new Timer(100, new TimerListener());
			//timer.start();
		}
		protected void paintComponent(Graphics g){
			super.paintComponents(g);
			((Graphics2D)g).setStroke(new BasicStroke(6.0f));
			g.setColor(Color.BLACK);
			g.drawLine(tempStrNode.x +3, tempStrNode.y+3, tempEndNode.x+3, tempEndNode.y+3);
			g.setColor(Color.YELLOW);
			g.fillRect(tempStrNode.x, tempStrNode.y, 10, 10);
			g.fillRect(tempEndNode.x, tempEndNode.y, 10, 10);
			drawGraph(g);
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
	
	
	class TimerListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			repaint();
			
		}
	}
	
	
	
}
