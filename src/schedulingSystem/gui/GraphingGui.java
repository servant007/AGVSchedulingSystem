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
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import jxl.Workbook;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.Number;   
import schedulingSystem.component.Graph;
import schedulingSystem.component.Node;
import schedulingSystem.toolKit.*;;

public class GraphingGui extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(GraphingGui.class.getName());
	private RoundButton schedulingGuiBtn;
	private RoundButton setingGuiBtn;
	private RoundButton graphGuiBtn;
	private RoundButton comfirmBtn;
	private Node tempStrNode;
	private Node tempEndNode;
	private boolean mouseClicked;
	private boolean addStrNode;
	private Graph graph;
	private int countNode = 0;
	
	public GraphingGui(){
		super("AGV调度系统");
		tempStrNode = new Node();
		tempEndNode = new Node();
		graph = new Graph();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setExtendedState(Frame.MAXIMIZED_BOTH);
		
		MainPanel mainPanel = new MainPanel();

		schedulingGuiBtn = new RoundButton("调度界面");
		schedulingGuiBtn.setBounds(0, 0, screenSize.width/3, screenSize.height/20);
		
		setingGuiBtn = new RoundButton("设置界面");
		setingGuiBtn.setBounds(screenSize.width/3, 0, screenSize.width/3, screenSize.height/20);
		
		graphGuiBtn = new RoundButton("管理界面");
		graphGuiBtn.setBounds(2*screenSize.width/3, 0, screenSize.width/3, screenSize.height/20);
		
		comfirmBtn = new RoundButton("确认使用");
		comfirmBtn.setBounds(9*screenSize.width/10, 17*screenSize.height/20, screenSize.width/10, screenSize.height/20);
		
		mainPanel.setLayout(null);
		mainPanel.add(schedulingGuiBtn);
		mainPanel.add(setingGuiBtn);
		mainPanel.add(graphGuiBtn);
		mainPanel.add(comfirmBtn);

		this.getContentPane().add(mainPanel);
		this.addMouseMotionListener(new MouseAdapter(){
			public void mouseMoved(MouseEvent e){
				repaint();
				if(mouseClicked){
					tempEndNode.x = e.getX() - 16;
					tempEndNode.y = e.getY() - 58;
				}
			}
		});
		this.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				if(e.getButton() == MouseEvent.BUTTON1){
					Node node = graph.searchNode(new Node(e.getX() - 16, e.getY() - 58, 0));
					if(node != null){
						if(!addStrNode){
							tempStrNode = new Node(node.x, node.y, node.num);
							tempStrNode.setOldNode();
							tempEndNode = new Node(node.x, node.y, 0);
							mouseClicked = true;
							addStrNode = true;
						}else{
							tempEndNode.x = node.x;
							tempEndNode.y = node.y;
							mouseClicked = false;
							addStrNode = false;
							MyDialog dialog = new MyDialog("请输入实际长度（cm）：");
							dialog.setOnDialogListener(new DialogListener(){
								@Override
								public void getInputString(String fileName, boolean buttonState){
									dialog.dispose();
									if(buttonState){
										//弹出对话框判断是否添加
										if(tempStrNode.getNewNode()){
											graph.addNode(tempStrNode);
											graph.addEdge(tempStrNode.num, node.num, 0);
											initNode();
											repaint();
										}else{
											graph.addEdge(tempStrNode.num, node.num, 0);
											initNode();
											repaint();
										}
										
									}else{
										initNode();
										repaint();
									}
								}
							});	
							
						}
					}
					if(node ==null){
						if(!addStrNode){
							countNode++;
							tempStrNode = new Node(e.getX() - 16, e.getY() - 58, countNode);
							tempEndNode = new Node(e.getX() - 16, e.getY() - 58, 0);
							mouseClicked = true;
							addStrNode = true;
							System.out.println("clicked-1/" + String.valueOf(countNode));
						}else {
							countNode++;
							if(Math.abs(e.getX() - 16 - tempStrNode.x) > Math.abs(e.getY() - 58 - tempStrNode.y)){
								int searchX = graph.searchHorizontal(e.getX() - 16);
								//System.out.println("searchX:"+ String.valueOf(searchX));
								if(searchX != 0){
									System.out.println("clicked-2-x-!=" + String.valueOf(countNode));
									tempEndNode = new Node(searchX, tempStrNode.y, countNode);
								}else{
									tempEndNode = new Node(e.getX() - 16, tempStrNode.y, countNode);
									System.out.println("clicked-2-x-=" + String.valueOf(countNode));
								}
									
							}else {
								System.out.println("clicked-2-y");
								int searchY = graph.searchVertical(e.getY() - 58);
								if(searchY != 0)
									tempEndNode = new Node(tempStrNode.x, searchY, countNode);
								else
									tempEndNode = new Node(tempStrNode.x, e.getY() - 58, countNode);
							}
							
							mouseClicked = false;
							addStrNode = false;
							MyDialog dialog = new MyDialog("请输入实际长度（cm）：");
							dialog.setOnDialogListener(new DialogListener(){
								@Override
								public void getInputString(String fileName, boolean buttonState){
									dialog.dispose();
									if(buttonState){
										System.out.println("comfirmed");
										//弹出对话框判断是否添加
										if(tempStrNode.getNewNode()){
											graph.addNode(tempStrNode);
											graph.addNode(tempEndNode);
											System.out.println("tempStrNode"+tempStrNode.x+tempStrNode.y);
											graph.addEdge(tempStrNode.num, tempEndNode.num, 0);
											initNode();
											System.out.println("comfirmed"+tempEndNode.x);
											repaint();
										}else{
											graph.addNode(tempEndNode);
											graph.addEdge(tempStrNode.num, tempEndNode.num, 0);
											initNode();
											System.out.println("comfirmed"+tempEndNode.x);
											repaint();
										}
										
									}else{
										initNode();
										repaint();
									}
								}
							});					
							
						}
					}
					repaint();
				}else if(e.getButton() == MouseEvent.BUTTON3){
					
				}
				
			}
		});
	}
	
	public void getGuiInstance(SchedulingGui schedulingGui, SetingGui setingGui, GraphingGui graphingGui){
		schedulingGuiBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				schedulingGui.setVisible(true);
				schedulingGui.setBtnColor();
				setingGui.setVisible(false);
				graphingGui.setVisible(false);
			}
		});

		setingGuiBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				schedulingGui.setVisible(false);
				setingGui.setVisible(true);
				setingGui.setBtnColor();
				graphingGui.setVisible(false);
			}
		});
		
		comfirmBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				MyDialog dialog = new MyDialog("请输入文件名：");
				dialog.setOnDialogListener(new DialogListener(){
					@Override
					public void getInputString(String fileName, boolean buttonState){
						dialog.dispose();
						if(buttonState){
							try{
								StringBuffer filePath = new StringBuffer();
								File graphExcelFile = new File(filePath.append("C:/").append(fileName).append(".xls").toString());//
								graphExcelFile.createNewFile();
								OutputStream os = new FileOutputStream(graphExcelFile);
								writeExcel(os, graph);
							}catch(Exception ex){
								ex.printStackTrace();
								logger.error(ex);
							}
						}
					}
				});
			}
		});
		
	}
	
	
	
	class MainPanel extends JPanel{
		private static final long serialVersionUID = 1L;
		
		public MainPanel(){
		}
		protected void paintComponent(Graphics g){
			super.paintComponents(g);
			((Graphics2D)g).setStroke(new BasicStroke(6.0f));
			g.setColor(Color.BLACK);
			g.drawLine(tempStrNode.x , tempStrNode.y , tempEndNode.x , tempEndNode.y );
			g.setColor(Color.YELLOW);
			g.fillRect(tempStrNode.x - 5, tempStrNode.y - 5, 10, 10);
			g.fillRect(tempEndNode.x - 5, tempEndNode.y - 5, 10, 10);
			System.out.println("tempEndNode:"+tempEndNode.x);
			drawGraph(g);
		}
	}
	
	public void drawGraph(Graphics g){
		((Graphics2D)g).setStroke(new BasicStroke(6.0f));
		g.setColor(Color.BLACK);
		
		for(int i = 0 ; i < graph.getEdgeSize(); i++){
			g.drawLine(graph.getEdge(i).startNode.x, graph.getEdge(i).startNode.y, graph.getEdge(i).endNode.x, graph.getEdge(i).endNode.y);
		}
			
		
		g.setColor(Color.YELLOW);
		for(int i = 0 ; i < graph.getNodeSize(); i++)
			g.fillRect(graph.getNode(i).x - 5, graph.getNode(i).y - 5, 10, 10);
	}
	
	
	class TimerListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			repaint();
			
		}
	}
	
	public void setBtnColor(){
		schedulingGuiBtn.setBackground(new Color(30, 144, 255));
		setingGuiBtn.setBackground(new Color(30, 144, 255));
		graphGuiBtn.setBackground(Color.WHITE);
		graphGuiBtn.setForeground(new Color(30, 144, 255));
	}
	
	public static void writeExcel(OutputStream os, Graph graph){
		try{
			WritableWorkbook wwb = Workbook.createWorkbook(os);
			WritableSheet wsNode = wwb.createSheet("nodes", 0);
			for(int i = 0; i < graph.getNodeSize(); i++){				
				Number numberNum = new Number(0, i, graph.getNode(i).num);
				Number numberX = new Number(1, i, graph.getNode(i).x);
				Number numberY = new Number(2, i, graph.getNode(i).y);
				wsNode.addCell(numberX);
				wsNode.addCell(numberY);
				wsNode.addCell(numberNum);					
			}
			WritableSheet wsEdge = wwb.createSheet("edges", 1);
			for(int i = 0; i < graph.getEdgeSize(); i++){
				Number numberStrNode = new Number(0, i, graph.getEdge(i).startNode.num);
				Number numberEndNode = new Number(1, i, graph.getEdge(i).endNode.num);
				Number numberDis = new Number(2, i, graph.getEdge(i).realDis);
				wsEdge.addCell(numberStrNode);
				wsEdge.addCell(numberEndNode);
				wsEdge.addCell(numberDis);
			}
			wwb.write();
			wwb.close();
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e);
		}
	} 
	
	public void initNode(){
		tempStrNode.x = 0;
		tempStrNode.y = 0;
		tempEndNode.x = 0;
		tempEndNode.y = 0;
		tempEndNode.num = 0;
		tempStrNode.num = 0;
	}
	public void setStrNode(int x, int y, int num){
		tempStrNode.x = x;
		tempStrNode.y = y;
		tempStrNode.num = num;
	}
	
	public void setEndNode(int x, int y, int num){
		tempEndNode.x = x;
		tempEndNode.y = y;
		tempEndNode.num = num;
	}
}
