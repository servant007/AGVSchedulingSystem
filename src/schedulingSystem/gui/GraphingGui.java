package schedulingSystem.gui;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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
import jxl.write.Label;
import jxl.write.Number;   
import schedulingSystem.component.Graph;
import schedulingSystem.component.Main;
import schedulingSystem.component.Node;
import schedulingSystem.toolKit.*;;

public class GraphingGui extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(GraphingGui.class.getName());
	private RoundButton schedulingGuiBtn;
	private RoundButton setingGuiBtn;
	private RoundButton graphGuiBtn;
	private RoundButton comfirmBtn;
	private RoundButton importGraphBtn;
	private Node tempStrNode;
	private Node tempEndNode;
	private boolean mouseClicked;
	private boolean addStrNode;
	private Graph graph;
	private int countNode = 0;
	private static GraphingGui instance;
	private MyToolKit myToolKit;
	private Dimension screenSize;
	private Node mousePosition;
	
	public static GraphingGui getInstance(){
		if(instance == null){
			instance = new GraphingGui();
		}
		
		return instance;
	}
	
	private GraphingGui(){
		tempStrNode = new Node();
		tempEndNode = new Node();
		graph = new Graph();
		myToolKit = new MyToolKit();
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		mousePosition = new Node();

		schedulingGuiBtn = new RoundButton("调度界面");
		schedulingGuiBtn.setBounds(0, 0, screenSize.width/3, screenSize.height/20);
		
		setingGuiBtn = new RoundButton("设置界面");
		setingGuiBtn.setBounds(screenSize.width/3, 0, screenSize.width/3, screenSize.height/20);
		
		graphGuiBtn = new RoundButton("管理界面");
		graphGuiBtn.setBounds(2*screenSize.width/3, 0, screenSize.width/3, screenSize.height/20);
		
		comfirmBtn = new RoundButton("确认使用");
		comfirmBtn.setFont(new Font("宋体", Font.BOLD, 23));
		comfirmBtn.setBounds(13*screenSize.width/14, 19*screenSize.height/22, screenSize.width/14, screenSize.height/22);
		
		importGraphBtn = new RoundButton("导入地图");
		importGraphBtn.setFont(new Font("宋体", Font.BOLD, 23));
		importGraphBtn.setBounds(13*screenSize.width/14, 18*screenSize.height/22, screenSize.width/14, screenSize.height/22);
		importGraphBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				graph = myToolKit.importNewGraph(null);
				countNode = graph.getNodeSize();
				repaint();
			}
		});
		setBtnColor();
		this.setLayout(null);
		this.add(schedulingGuiBtn);
		this.add(setingGuiBtn);
		this.add(graphGuiBtn);
		this.add(comfirmBtn);
		this.add(importGraphBtn);

		this.addMouseMotionListener(new MouseAdapter(){
			public void mouseMoved(MouseEvent e){
				repaint();
				mousePosition.x = e.getX();
				mousePosition.y = e.getY(); 
				if(mouseClicked){
					tempEndNode.x = e.getX();
					tempEndNode.y = e.getY();
				}
			}
		});
		this.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				if(e.getButton() == MouseEvent.BUTTON1){
					Node node = graph.searchNode(e.getX(), e.getY());
					if(node != null){
						if(!addStrNode){
							setStrNode(node.x, node.y, node.num);
							tempStrNode.setOldNode();
							setEndNode(node.x, node.y, 0);
							mouseClicked = true;
							addStrNode = true;
						}else{
							setEndNode(node.x, node.y, node.num);
							mouseClicked = false;
							addStrNode = false;
							GraphDialog dialog = new GraphDialog();
							dialog.setOnDialogListener(new GraphDialogListener(){
								@Override
								public void getInputString(String realDis, String strCard, String endCard, boolean twoWay,boolean buttonState){
									dialog.dispose();
									if(buttonState && realDis.length() > 0
											&& strCard.length() > 0  && endCard.length() > 0){
										//弹出对话框判断是否添加
										if(tempStrNode.questIsNewNode()){
											graph.addNode(tempStrNode);
											graph.addEdge(tempStrNode.num, node.num, Integer.parseInt(realDis)
													,Integer.parseInt(strCard), Integer.parseInt(endCard), twoWay);
											initNode();
											repaint();
										}else{
											graph.addEdge(tempStrNode.num, node.num, Integer.parseInt(realDis)
													,Integer.parseInt(strCard), Integer.parseInt(endCard), twoWay);
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
							int searchX = graph.searchHorizontal(e.getX());
							int searchY = graph.searchVertical(e.getY());
							
							if(searchX != 0 && searchY == 0){
								setStrNode(searchX, e.getY(), countNode);
								setEndNode(searchX, e.getY(), 0);
								System.out.println("hasx");
							}else if(searchX == 0 && searchY != 0){
								setStrNode(e.getX(), searchY, countNode);
								setEndNode(e.getX(), searchY, 0);
								System.out.println("hasy");
							}else if(searchX != 0 && searchY != 0){
								setStrNode(searchX, searchY, countNode);
								setEndNode(searchX, searchY, 0);
								System.out.println("hasxy");
							}else{
								setStrNode(e.getX(), e.getY(), countNode);
								setEndNode(e.getX(), e.getY(), 0);
								System.out.println("null");
							}							
							mouseClicked = true;
							addStrNode = true;
							//System.out.println("clicked-1/" + String.valueOf(countNode));
						}else {
							countNode++;
							if(Math.abs(e.getX() - tempStrNode.x) > Math.abs(e.getY()  - tempStrNode.y)){
								int searchX = graph.searchHorizontal(e.getX());
								if(searchX != 0)
									setEndNode(searchX, tempStrNode.y, countNode);
								else
									setEndNode(e.getX(), tempStrNode.y, countNode);
									
							}else {
								int searchY = graph.searchVertical(e.getY());
								if(searchY != 0)
									setEndNode(tempStrNode.x, searchY, countNode);
								else
									setEndNode(tempStrNode.x, e.getY(), countNode);
							}
							
							mouseClicked = false;
							addStrNode = false;
							GraphDialog dialog = new GraphDialog();
							dialog.setOnDialogListener(new GraphDialogListener(){
								@Override
								public void getInputString(String realDis,String strCard, String endCard, boolean twoWay, boolean buttonState){
									dialog.dispose();
									if(buttonState && realDis.length() > 0 
											&& strCard.length() > 0  && endCard.length() > 0){
										System.out.println("comfirmed");
										//弹出对话框判断是否添加
										if(tempStrNode.questIsNewNode()){
											graph.addNode(tempStrNode);
											graph.addNode(tempEndNode);
											//System.out.println("tempStrNode"+tempStrNode.x+tempStrNode.y);
											graph.addEdge(tempStrNode.num, tempEndNode.num, Integer.parseInt(realDis)
													,Integer.parseInt(strCard), Integer.parseInt(endCard),twoWay);
											initNode();
											repaint();
										}else{
											graph.addNode(tempEndNode);
											graph.addEdge(tempStrNode.num, tempEndNode.num, Integer.parseInt(realDis)
													,Integer.parseInt(strCard), Integer.parseInt(endCard), twoWay);
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
					repaint();
				}else if(e.getButton() == MouseEvent.BUTTON3){
					Node node = graph.searchNode(e.getX(), e.getY());
					if(node != null){
						FunctionDialog dialog = new FunctionDialog(mousePosition);
						dialog.getInstance(dialog, true);
						dialog.setOnActionListener(new FunctionDialogListener(){
							public void getSeclectFunction(int function, String str, boolean btnState){
								if(str.length() > 0){
									if(function == 1)
										graph.addShipmentNode(node.num, Integer.parseInt(str));
									else if(function == 2)
										graph.addUnloadingNode(node.num, Integer.parseInt(str));
									else if(function == 3)
										graph.addEmptyCarNode(node.num, Integer.parseInt(str));
									else if(function == 4)
										graph.addTagArray(node.x, node.y, str);
								}
								System.out.println(str);
							}
						});
					}
					
					if(node == null ){
						Node node1 = mousePosition;
						FunctionDialog dialog = new FunctionDialog(mousePosition);
						dialog.getInstance(dialog, false);
						dialog.setOnActionListener(new FunctionDialogListener(){
							public void getSeclectFunction(int function, String str, boolean btnState){
								if(str.length() > 0){
									if(function == 4)
										graph.addTagArray(node1.x, node1.y, str);
								}
								System.out.println(str);
							}
						});
					}
						
					
				}
				
			}
		});
	}
	
	public void getGuiInstance(Main main , SchedulingGui schedulingGui, SetingGui setingGui, GraphingGui graphingGui){
		schedulingGuiBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				main.getContentPane().removeAll();
				main.getContentPane().add(schedulingGui);
				main.repaint();
				main.validate();
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
		
		comfirmBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				FileNameDialog dialog = new FileNameDialog();
				dialog.setOnDialogListener(new FileNameDialogListener(){
					@Override
					public void getFileName(String fileName, boolean buttonState){
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
	
	@Override
	public void paint(Graphics g){
		super.paint(g);
		//super.paintComponents(g);
		((Graphics2D)g).setStroke(new BasicStroke(6.0f));
		g.setColor(Color.BLACK);
		g.drawLine(tempStrNode.x , tempStrNode.y , tempEndNode.x , tempEndNode.y );
		g.setColor(Color.YELLOW);
		g.fillRect(tempStrNode.x - 5, tempStrNode.y - 5, 10, 10);
		g.fillRect(tempEndNode.x - 5, tempEndNode.y - 5, 10, 10);
		
		((Graphics2D)g).setStroke(new BasicStroke(2.0f));
		g.setColor(Color.GRAY);
		g.drawLine(mousePosition.x, 0, mousePosition.x,screenSize.height);
		g.drawLine(0, mousePosition.y, screenSize.width, mousePosition.y);
		((Graphics2D)g).setStroke(new BasicStroke(4.0f));
		g.setFont(new Font("宋体", Font.BOLD, 20));
		g.drawString("("+String.valueOf(mousePosition.x)+","+String.valueOf(mousePosition.y)+")"
				, mousePosition.x+10, mousePosition.y - 10);

		
		myToolKit.drawGraph(g, graph);
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
		
		g.setColor(Color.blue);
		for(int i = 0; i < graph.getShipmentNode().size(); i++)
			g.fillOval(graph.getNode(graph.getShipmentNode().get(i).nodeNum-1).x-20
					, graph.getNode(graph.getShipmentNode().get(i).nodeNum-1).y-20, 40, 40);
		
		g.setColor(Color.green);
		for(int i = 0; i < graph.getUnloadingNode().size(); i++)
			g.fillOval(graph.getNode(graph.getUnloadingNode().get(i).nodeNum-1).x-20
					, graph.getNode(graph.getUnloadingNode().get(i).nodeNum-1).y-20, 40, 40);
		
		g.setColor(Color.ORANGE);
		for(int i = 0; i < graph.getEmptyCarNode().size(); i++)
			g.fillOval(graph.getNode(graph.getEmptyCarNode().get(i).nodeNum-1).x-20
					, graph.getNode(graph.getEmptyCarNode().get(i).nodeNum-1).y-20, 40, 40);
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
				Number numberStrCard = new Number(3, i, graph.getEdge(i).strCardNum);
				Number numberEndCard = new Number(4, i, graph.getEdge(i).endCardNum);
				Number numTwoWay;
				if(graph.getEdge(i).twoWay)
					numTwoWay = new Number(5, i, 1);
				else
					numTwoWay = new Number(5, i, 0);
				wsEdge.addCell(numberStrNode);
				wsEdge.addCell(numberEndNode);
				wsEdge.addCell(numberDis);
				wsEdge.addCell(numberStrCard);
				wsEdge.addCell(numberEndCard);
				wsEdge.addCell(numTwoWay);
			}
			
			WritableSheet wsShipment = wwb.createSheet("shipment", 2);
			for(int i = 0; i < graph.getShipmentNode().size(); i++){
				Number numberNode = new Number(0, i, graph.getShipmentNode().get(i).nodeNum);
				Number numberCom = new Number(1, i, graph.getShipmentNode().get(i).communicationNum);
				wsShipment.addCell(numberNode);
				wsShipment.addCell(numberCom);
			}
			
			WritableSheet wsUnloading = wwb.createSheet("unloading", 3);
			for(int i = 0; i < graph.getUnloadingNode().size(); i++){
				Number numberNode = new Number(0, i, graph.getUnloadingNode().get(i).nodeNum);
				Number numberCom = new Number(1, i, graph.getUnloadingNode().get(i).communicationNum);
				wsUnloading.addCell(numberNode);
				wsUnloading.addCell(numberCom);
			}
			
			WritableSheet wsEmptyCar = wwb.createSheet("emptyCar", 4);
			for(int i = 0; i < graph.getEmptyCarNode().size(); i++){
				Number numberNode = new Number(0, i, graph.getEmptyCarNode().get(i).nodeNum);
				Number numberCom = new Number(1, i, graph.getEmptyCarNode().get(i).communicationNum);
				wsEmptyCar.addCell(numberNode);
				wsEmptyCar.addCell(numberCom);
			}
			
			WritableSheet wsTag = wwb.createSheet("tag", 5);
			for(int i = 0; i < graph.getTagArray().size(); i++){
				Number x = new Number(0, i, graph.getTagArray().get(i).position.x);
				Number y = new Number(1, i, graph.getTagArray().get(i).position.y);
				Label tag = new Label(2, i, graph.getTagArray().get(i).tag);
				//Label label = (Label) wsTag.getWritableCell(2, i);
				//label.setString(graph.getTagArray().get(i).tag);
				wsTag.addCell(x);
				wsTag.addCell(y);
				wsTag.addCell(tag);
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
		tempStrNode.setNewNode();
	}
	
	public void setEndNode(int x, int y, int num){
		tempEndNode.x = x;
		tempEndNode.y = y;
		tempEndNode.num = num;
		tempEndNode.setNewNode();
	}
}
