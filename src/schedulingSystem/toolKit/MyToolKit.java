package schedulingSystem.toolKit;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Label;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import javax.swing.JFileChooser;

import org.apache.log4j.Logger;

import jxl.Cell;
import jxl.CellType;
import jxl.LabelCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.NumberCell;
import schedulingSystem.component.AGVCar;
import schedulingSystem.component.AGVCar.Orientation;
import schedulingSystem.component.FunctionNode.FunctionNodeEnum;
import schedulingSystem.component.Graph;
import schedulingSystem.component.Node;
import schedulingSystem.gui.SchedulingGui;

public class MyToolKit {
	private static Logger logger = Logger.getLogger(SchedulingGui.class.getName());
	
	public byte[] HexString2Bytes(String src) {
		if (null == src || 0 == src.length()) {
			return null;
        }
        byte[] ret = new byte[src.length() / 2];
        byte[] tmp = src.getBytes();
        for (int i = 0; i < (tmp.length / 2); i++) {
        	ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }

	public byte uniteBytes(byte src0, byte src1) { 
			byte _b0 = Byte.decode("0x" + new String(new byte[] {src0})).byteValue(); 
			_b0 = (byte) (_b0 << 4); 
			byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 })).byteValue(); 
			byte ret = (byte) (_b0 ^ _b1); 
			return ret; 
	}

	public String printHexString( byte[] b) { 
		String buff = "";
		for (int i = 0; i < b.length; i++) { 
			String hex = Integer.toHexString(b[i] & 0xFF); 
			if (hex.length() == 1) { 
				hex = '0' + hex; 
			} 
			buff+=hex.toUpperCase();
		} 
		//System.out.println();
		return buff;
	}
	
	public void drawGraph(Graphics g, Graph graph, boolean reverseColor,  boolean graphGui){		
		((Graphics2D)g).setStroke(new BasicStroke(6.0f));
		g.setColor(Color.BLACK);
		
		for(int i = 0 ; i < graph.getEdgeSize(); i++){
			g.drawLine(graph.getEdge(i).startNode.x, graph.getEdge(i).startNode.y, graph.getEdge(i).endNode.x, graph.getEdge(i).endNode.y);
		}
		
		for(int i = 0 ; i < graph.getNodeSize(); i++){
			g.setColor(Color.YELLOW);
			g.fillRect(graph.getNode(i).x - 5, graph.getNode(i).y - 5, 10, 10);
			if(graphGui){
				g.setColor(Color.RED);
				g.setFont(new Font("宋体", Font.BOLD, 30));
				g.drawString(String.valueOf(i+1),graph.getNode(i).x + 10, graph.getNode(i).y - 10);//String.valueOf(i+1)
			}
			
		}
		
		g.setFont(new Font("宋体", Font.BOLD, 30));
		for(int i = 0; i < graph.getFunctionNodeArray().size(); i++){
			if(graph.getFunctionNodeArray().get(i).clicked){
				if(!reverseColor){
					g.setColor(Color.red);
				}else{
					if(graph.getFunctionNodeArray().get(i).function == FunctionNodeEnum.SHIPMENT)
						g.setColor(Color.BLUE);
					if(graph.getFunctionNodeArray().get(i).function == FunctionNodeEnum.UNLOADING)
						g.setColor(Color.GREEN);
					if(graph.getFunctionNodeArray().get(i).function == FunctionNodeEnum.CHARGE)
						g.setColor(Color.PINK);
					if(graph.getFunctionNodeArray().get(i).function == FunctionNodeEnum.EMPTYCAR)
						g.setColor(Color.ORANGE);
					if(graph.getFunctionNodeArray().get(i).function == FunctionNodeEnum.TAG)
						g.setColor(Color.GRAY);
				}
			}else{
				if(graph.getFunctionNodeArray().get(i).function == FunctionNodeEnum.SHIPMENT)
					g.setColor(Color.BLUE);
				if(graph.getFunctionNodeArray().get(i).function == FunctionNodeEnum.UNLOADING)
					g.setColor(Color.GREEN);
				if(graph.getFunctionNodeArray().get(i).function == FunctionNodeEnum.CHARGE)
					g.setColor(Color.PINK);
				if(graph.getFunctionNodeArray().get(i).function == FunctionNodeEnum.EMPTYCAR)
					g.setColor(Color.ORANGE);
				if(graph.getFunctionNodeArray().get(i).function == FunctionNodeEnum.TAG)
					g.setColor(Color.GRAY);
			}
			
			if(graph.getFunctionNodeArray().get(i).function != FunctionNodeEnum.TAG){
				if(graph.getFunctionNodeArray().get(i).nodeNum != 0){
					g.setFont(new Font("宋体", Font.BOLD, 20));
					g.fillOval(graph.getNode(graph.getFunctionNodeArray().get(i).nodeNum-1).x-20
							, graph.getNode(graph.getFunctionNodeArray().get(i).nodeNum-1).y-20, 40, 40);
					g.setColor(Color.BLACK);
					g.drawString(graph.getNode(graph.getFunctionNodeArray().get(i).nodeNum -1).tag, graph.getNode(graph.getFunctionNodeArray().get(i).nodeNum - 1).x - 30
							,graph.getNode(graph.getFunctionNodeArray().get(i).nodeNum - 1).y -30);
					if(!graphGui){
						g.setColor(Color.RED);
						g.drawString(String.valueOf(graph.getFunctionNodeArray().get(i).nodeNum), graph.getNode(graph.getFunctionNodeArray().get(i).nodeNum - 1).x-13
								,graph.getNode(graph.getFunctionNodeArray().get(i).nodeNum - 1).y+10);
					}
					
				}else {
					g.drawString(graph.getFunctionNodeArray().get(i).tag, graph.getFunctionNodeArray().get(i).x, graph.getFunctionNodeArray().get(i).y);
				}
		
			}else{
				g.drawString(graph.getFunctionNodeArray().get(i).tag, graph.getFunctionNodeArray().get(i).x
						, graph.getFunctionNodeArray().get(i).y);
			}
		}
		
		if(graphGui){
			g.setColor(Color.GRAY);
			for(int i = 0; i < graph.getEdgeSize(); i++){
				if(graph.getEdge(i).startNode.y == graph.getEdge(i).endNode.y){
					if(graph.getEdge(i).startNode.x > graph.getEdge(i).endNode.x){
						g.drawString(String.valueOf(graph.getEdge(i).strCardNum), graph.getEdge(i).startNode.x-60, graph.getEdge(i).startNode.y);
						g.drawString(String.valueOf(graph.getEdge(i).endCardNum), graph.getEdge(i).endNode.x+60, graph.getEdge(i).endNode.y);
					}else{
						g.drawString(String.valueOf(graph.getEdge(i).strCardNum), graph.getEdge(i).startNode.x+60, graph.getEdge(i).startNode.y);
						g.drawString(String.valueOf(graph.getEdge(i).endCardNum), graph.getEdge(i).endNode.x-60, graph.getEdge(i).endNode.y);
					}
					
				}
				if(graph.getEdge(i).startNode.x == graph.getEdge(i).endNode.x){
					if(graph.getEdge(i).startNode.y > graph.getEdge(i).endNode.y){
						g.drawString(String.valueOf(graph.getEdge(i).strCardNum), graph.getEdge(i).startNode.x, graph.getEdge(i).startNode.y-60);
						g.drawString(String.valueOf(graph.getEdge(i).endCardNum), graph.getEdge(i).endNode.x, graph.getEdge(i).endNode.y+60);
					}else{
						g.drawString(String.valueOf(graph.getEdge(i).strCardNum), graph.getEdge(i).startNode.x, graph.getEdge(i).startNode.y+60);
						g.drawString(String.valueOf(graph.getEdge(i).endCardNum), graph.getEdge(i).endNode.x, graph.getEdge(i).endNode.y-60);
					}	
				}
			}
		}
		
		
	}
	
	
	
	public Graph importNewGraph(String fileName){
		Graph graph = new Graph();
		File file = null;
		try{			
			if(fileName == null){
				JFileChooser jfc = new JFileChooser();
				jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				jfc.showDialog(new Label(), "选择地图");
				file = jfc.getSelectedFile();
			}else{
				file = new File(fileName);
			}
			
			if(file != null){
				System.out.println(file.getPath());
				InputStream is = new FileInputStream(file.getPath());//this.getClass().getResourceAsStream("/testGraph.xls");
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
					}
					graph.addImportNode(x, y, num);
				}
				
				Sheet sheetEdges = wb.getSheet("edges");
				for(int i = 0; i < sheetEdges.getRows(); i++){
					int start=0, end=0, dis=0, strCardNum=0, endCardNum=0, twoWay = 0;
					for(int j = 0; j < 6; j++){
						Cell cell0 = sheetEdges.getCell(j,i);
							String str = cell0.getContents();
							if(j == 0)
								start = Integer.parseInt(str);
							if(j == 1)
								end = Integer.parseInt(str);
							if(j == 2)
								dis = Integer.parseInt(str);
							if(j == 3)
								strCardNum = Integer.parseInt(str);
							if(j == 4)
								endCardNum = Integer.parseInt(str);
							if(j == 5)
								twoWay = Integer.parseInt(str);				
					}
					if(twoWay == 1)
						graph.addEdge(start, end, dis, strCardNum, endCardNum, true);
					if(twoWay == 0)
						graph.addEdge(start, end, dis, strCardNum, endCardNum, false);
				}
				
				Sheet sheetFunctionNode = wb.getSheet("functionNode");
				for(int i = 0; i < sheetFunctionNode.getRows(); i++){
					int node=0, com=0, x = 0, y = 0, ordinal = 0;
					String ip = "";
					String tag = "";
					for(int j = 0; j < 7; j++){
						Cell cell0 = sheetFunctionNode.getCell(j,i);
							String str = cell0.getContents();
							if(j == 0)
								node = Integer.parseInt(str);
							if(j == 1)
								ip = str;
							if(j == 2)
								com = Integer.parseInt(str);
							if(j == 3)
								tag = str;
							if(j == 4)
								x = Integer.parseInt(str);
							if(j == 5)
								y = Integer.parseInt(str);
							if(j == 6)
								ordinal = Integer.parseInt(str);			
					}
					graph.addFunctionNode(FunctionNodeEnum.values()[ordinal],node, ip, com, tag);
				}
				
				Sheet sheetAGVSeting = wb.getSheet("AGVSeting");
				for(int i = 0; i < sheetAGVSeting.getRows(); i++){
					Cell cell0 = sheetAGVSeting.getCell(0,i);
					String seting = cell0.getContents();
					graph.addAGVSeting(seting);
				}
				
				Sheet sheetCardNum = wb.getSheet("cardNum");
				Cell cell0 = sheetCardNum.getCell(0,0);
				Cell cell1 = sheetCardNum.getCell(0,1);
				String stopCard = cell0.getContents();
				String executeCard = cell1.getContents();
				graph.setStopCard(Integer.parseInt(stopCard));
				graph.setExecuteCard(Integer.parseInt(executeCard));
				
				Sheet chargeTime = wb.getSheet("chargeTime");
				Cell chargeTimeCell0 = chargeTime.getCell(0,0);
				Cell chargeTimeCell1 = chargeTime.getCell(0,1);
				String duration = chargeTimeCell0.getContents();
				String gap = chargeTimeCell1.getContents();
				graph.setChargeDuration(Integer.parseInt(duration));
				graph.setChargeGap(Integer.parseInt(gap));
					
			}			
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e);
		}
		return graph;
	}
	
	
	public String routeToOrientation(Graph graph, ArrayList<Integer> route, AGVCar agvCar){
		boolean returnRoute = false;
		if((route.get(0)== 12 && route.get(route.size()-1) == 14)
				||(route.get(0)== 16 && route.get(route.size()-1) == 18)
				||(route.get(0)== 20 && route.get(route.size()-1) == 22)
				||(route.get(0)== 24 && route.get(route.size()-1) == 26)
				||(route.get(0)== 29 && route.get(route.size()-1) == 27)
				||(route.get(0)== 33 && route.get(route.size()-1) == 31)
				||(route.get(0)== 37 && route.get(route.size()-1) == 35)
				||(route.get(0)== 41 && route.get(route.size()-1) == 39)){
			returnRoute = true;
		}
		
		System.out.println(agvCar.getAGVNum() + "AGV route:" + route);
		logger.debug(agvCar.getAGVNum() + "AGV route:" + route);
		boolean backward = false;
		if(graph.getNode(route.get(0)-1).x == graph.getNode(route.get(1)-1).x){
			if(graph.getNode(route.get(0)-1).y < graph.getNode(route.get(1)-1).y){//down
				if(agvCar.getOrientation() == Orientation.UP)
					backward = true;
				else if(agvCar.getOrientation() == Orientation.DOWN)
					backward = false;
			}else if(graph.getNode(route.get(0)-1).y > graph.getNode(route.get(1)-1).y){//up
				if(agvCar.getOrientation() == Orientation.DOWN)
					backward = true;
				else if(agvCar.getOrientation() == Orientation.UP)
					backward = false;
			}
		}else if(graph.getNode(route.get(0)-1).y == graph.getNode(route.get(1)-1).y){
			if(graph.getNode(route.get(0)-1).x < graph.getNode(route.get(1)-1).x){//right
				if(agvCar.getOrientation() == Orientation.LEFT)
					backward = true;
				else if(agvCar.getOrientation() == Orientation.RIGTH)
					backward = false;
			}else if(graph.getNode(route.get(0)-1).x > graph.getNode(route.get(1)-1).x){//left
				if(agvCar.getOrientation() == Orientation.RIGTH)
					backward = true;
				else if(agvCar.getOrientation() == Orientation.LEFT)
					backward = false;
			}
		}
		
		boolean first = true;
		ArrayList<Node> result = new ArrayList<Node>();
		StringBuffer sendMessage = new StringBuffer();//2*graph.getEdgeSize()+3
		if(returnRoute)
			sendMessage.append("02");
		else
			sendMessage.append("01");
		sendMessage.append("AA");
		for(int i = 0; i < 120+1; i++){//graph.getCardQuantity(),rockwell
			sendMessage.append("00");
		}
		sendMessage.append("BB");
		
		if(backward){
			sendMessage.replace(5, 6, "2");//倒退
		}else{
			sendMessage.replace(5, 6, "1");//前进
		}
			
		
		for(int i = 0; i+2 < route.size(); i++){
			if(graph.getNode(route.get(i)-1).x == graph.getNode(route.get(i+1)-1).x){
				//System.out.println("x=");
				if(graph.getNode(route.get(i)-1).y < graph.getNode(route.get(i+1)-1).y){//down
					System.out.print("方向下/");
					logger.debug("方向下/");
					if(graph.getNode(route.get(i+2)-1).x > graph.getNode(route.get(i+1)-1).x){
						//左1
						System.out.print("命令左/");
						logger.debug("命令左/");
						result.add(new Node(route.get(i+1), 1));
						for(int j = 0; j < graph.getEdgeSize(); j++){
							if((graph.getEdge(j).startNode.num == route.get(i) && graph.getEdge(j).endNode.num == route.get(i+1))){
								if(!(backward&&first)){//左前进时
									if(!returnRoute){
										System.out.println(String.valueOf(graph.getEdge(j).endCardNum) + ":左转");
										logger.debug(String.valueOf(graph.getEdge(j).endCardNum) + ":左转");
										sendMessage.replace(2*(graph.getEdge(j).endCardNum+3)-1, 2*(graph.getEdge(j).endCardNum+3), "1");
									}else{
										System.out.println(String.valueOf(graph.getEdge(j).endCardNum) + ":右转");
										logger.debug(String.valueOf(graph.getEdge(j).endCardNum) + "右转");
										sendMessage.replace(2*(graph.getEdge(j).endCardNum+3)-1, 2*(graph.getEdge(j).endCardNum+3), "2");
									}									
								}else{
									first = false;
									if(!returnRoute){
										sendMessage.replace(2*(graph.getEdge(j).endCardNum+3)-1, 2*(graph.getEdge(j).endCardNum+3), "2");									
										System.out.println(String.valueOf(graph.getEdge(j).endCardNum) + ":右转");
										logger.debug(String.valueOf(graph.getEdge(j).endCardNum) + "右转");
									}else{
										sendMessage.replace(2*(graph.getEdge(j).endCardNum+3)-1, 2*(graph.getEdge(j).endCardNum+3), "1");									
										System.out.println(String.valueOf(graph.getEdge(j).endCardNum) + ":左转");
										logger.debug(String.valueOf(graph.getEdge(j).endCardNum) + ":左转");
									}									
								}
							}
						}
					}else if(graph.getNode(route.get(i+2)-1).x == graph.getNode(route.get(i+1)-1).x){
						//前3
						result.add(new Node(route.get(i+1), 3));
						/*
						for(int j = 0; j < graph.getEdgeSize(); j++){
							if((graph.getEdge(j).startNode.num == route.get(i) && graph.getEdge(j).endNode.num == route.get(i+1))){
								sendMessage.replace(2*(graph.getEdge(j).endCardNum+2)-1, 2*(graph.getEdge(j).endCardNum+2), "0");
							}
						}*/
					}else{
						//右2
						System.out.print("命令右/");
						logger.debug("命令右/");
						result.add(new Node(route.get(i+1), 2));
						for(int j = 0; j < graph.getEdgeSize(); j++){
							if((graph.getEdge(j).startNode.num == route.get(i) && graph.getEdge(j).endNode.num == route.get(i+1))){
								if(!(backward&&first)){
									if(!returnRoute){
										sendMessage.replace(2*(graph.getEdge(j).endCardNum+3)-1, 2*(graph.getEdge(j).endCardNum+3), "2");
										System.out.println(String.valueOf(graph.getEdge(j).endCardNum) + ":右转");
										logger.debug(String.valueOf(graph.getEdge(j).endCardNum) + ":右转");
									}else{
										sendMessage.replace(2*(graph.getEdge(j).endCardNum+3)-1, 2*(graph.getEdge(j).endCardNum+3), "1");
										System.out.println(String.valueOf(graph.getEdge(j).endCardNum) + ":左转");
										logger.debug(String.valueOf(graph.getEdge(j).endCardNum) + ":左转");
									}									
								}else{
									first = false;
									if(!returnRoute){
										sendMessage.replace(2*(graph.getEdge(j).endCardNum+3)-1, 2*(graph.getEdge(j).endCardNum+3), "1");
										System.out.println(String.valueOf(graph.getEdge(j).endCardNum) + ":左转");
										logger.debug(String.valueOf(graph.getEdge(j).endCardNum) + ":左转");
									}else{
										sendMessage.replace(2*(graph.getEdge(j).endCardNum+3)-1, 2*(graph.getEdge(j).endCardNum+3), "2");
										System.out.println(String.valueOf(graph.getEdge(j).endCardNum) + ":右转");
										logger.debug(String.valueOf(graph.getEdge(j).endCardNum) + ":右转");
									}									
								}
							}
						}
					}
				}else if(graph.getNode(route.get(i)-1).y > graph.getNode(route.get(i+1)-1).y){//up
					System.out.print("方向上/");
					logger.debug("方向上/");
					if(graph.getNode(route.get(i+2)-1).x > graph.getNode(route.get(i+1)-1).x){
						//右
						System.out.print("命令右/");
						logger.debug("命令右/");
						result.add(new Node(route.get(i+1), 2));
						for(int j = 0; j < graph.getEdgeSize(); j++){
							if((graph.getEdge(j).startNode.num == route.get(i) && graph.getEdge(j).endNode.num == route.get(i+1))){
								if(!(backward&&first)){
									if(!returnRoute){
										sendMessage.replace(2*(graph.getEdge(j).endCardNum+3)-1, 2*(graph.getEdge(j).endCardNum+3), "2");
										System.out.println(String.valueOf(graph.getEdge(j).endCardNum) + ":右转");
										logger.debug(String.valueOf(graph.getEdge(j).endCardNum) + ":右转");
									}else{
										sendMessage.replace(2*(graph.getEdge(j).endCardNum+3)-1, 2*(graph.getEdge(j).endCardNum+3), "1");
										System.out.println(String.valueOf(graph.getEdge(j).endCardNum) + ":左转");
										logger.debug(String.valueOf(graph.getEdge(j).endCardNum) + ":左转");
									}									
								}else{
									first = false;
									if(!returnRoute){
										sendMessage.replace(2*(graph.getEdge(j).endCardNum+3)-1, 2*(graph.getEdge(j).endCardNum+3), "1");									
										System.out.println(String.valueOf(graph.getEdge(j).endCardNum) + ":左转");
										logger.debug(String.valueOf(graph.getEdge(j).endCardNum) + ":左转");
									}else{
										sendMessage.replace(2*(graph.getEdge(j).endCardNum+3)-1, 2*(graph.getEdge(j).endCardNum+3), "2");									
										System.out.println(String.valueOf(graph.getEdge(j).endCardNum) + ":右转");
										logger.debug(String.valueOf(graph.getEdge(j).endCardNum) + ":右转");
									}									
								}
							}
						}
					}else if(graph.getNode(route.get(i+2)-1).x == graph.getNode(route.get(i+1)-1).x){
						//前
						result.add(new Node(route.get(i+1), 3));
						/*
						for(int j = 0; j < graph.getEdgeSize(); j++){
							if((graph.getEdge(j).startNode.num == route.get(i) && graph.getEdge(j).endNode.num == route.get(i+1))){
								sendMessage.replace(2*(graph.getEdge(j).endCardNum+2)-1, 2*(graph.getEdge(j).endCardNum+2), "0");
							}
						}*/
					}else{
						//左
						System.out.print("命令左/");
						logger.debug("命令左/");
						result.add(new Node(route.get(i+1), 1));
						for(int j = 0; j < graph.getEdgeSize(); j++){
							if((graph.getEdge(j).startNode.num == route.get(i) && graph.getEdge(j).endNode.num == route.get(i+1))){
								if(!(backward&&first)){
									if(!returnRoute){
										sendMessage.replace(2*(graph.getEdge(j).endCardNum+3)-1, 2*(graph.getEdge(j).endCardNum+3), "1");
										System.out.println(String.valueOf(graph.getEdge(j).endCardNum) + ":左转");
									}else{
										sendMessage.replace(2*(graph.getEdge(j).endCardNum+3)-1, 2*(graph.getEdge(j).endCardNum+3), "2");
										System.out.println(String.valueOf(graph.getEdge(j).endCardNum) + ":右转");
										logger.debug(String.valueOf(graph.getEdge(j).endCardNum) + ":右转");
									}									
								}else{
									first =false;
									if(!returnRoute){
										sendMessage.replace(2*(graph.getEdge(j).endCardNum+3)-1, 2*(graph.getEdge(j).endCardNum+3), "2");									
										System.out.println(String.valueOf(graph.getEdge(j).endCardNum) + ":右转");
										logger.debug(String.valueOf(graph.getEdge(j).endCardNum) + ":右转");
									}else{
										sendMessage.replace(2*(graph.getEdge(j).endCardNum+3)-1, 2*(graph.getEdge(j).endCardNum+3), "1");									
										System.out.println(String.valueOf(graph.getEdge(j).endCardNum) + ":左转");
										logger.debug(String.valueOf(graph.getEdge(j).endCardNum) + ":左转");
									}
									
								}
							}
						}
					}
				}
			}else if(graph.getNode(route.get(i)-1).y == graph.getNode(route.get(i+1)-1).y){//right and left
				if(graph.getNode(route.get(i)-1).x < graph.getNode(route.get(i+1)-1).x){//right
					System.out.print("方向右/");
					logger.debug("方向右/");
					if(graph.getNode(route.get(i+2)-1).y > graph.getNode(route.get(i+1)-1).y){
						//右
						System.out.print("命令右/");
						logger.debug("命令右/");
						result.add(new Node(route.get(i+1), 2));
						for(int j = 0; j < graph.getEdgeSize(); j++){
							if((graph.getEdge(j).startNode.num == route.get(i) && graph.getEdge(j).endNode.num == route.get(i+1))){
								if(!(backward&&first)){
									if(!returnRoute){
										sendMessage.replace(2*(graph.getEdge(j).endCardNum+3)-1, 2*(graph.getEdge(j).endCardNum+3), "2");
										System.out.println(String.valueOf(graph.getEdge(j).endCardNum) + ":右转");
										logger.debug(String.valueOf(graph.getEdge(j).endCardNum) + ":右转");
									}else{
										sendMessage.replace(2*(graph.getEdge(j).endCardNum+3)-1, 2*(graph.getEdge(j).endCardNum+3), "1");
										System.out.println(String.valueOf(graph.getEdge(j).endCardNum) + ":左转");
										logger.debug(String.valueOf(graph.getEdge(j).endCardNum) + ":左转");
									}
									
								}else{
									first = false;
									if(!returnRoute){
										sendMessage.replace(2*(graph.getEdge(j).endCardNum+3)-1, 2*(graph.getEdge(j).endCardNum+3), "1");									
										System.out.println(String.valueOf(graph.getEdge(j).endCardNum) + ":左转");
										logger.debug(String.valueOf(graph.getEdge(j).endCardNum) + ":左转");
									}else{
										sendMessage.replace(2*(graph.getEdge(j).endCardNum+3)-1, 2*(graph.getEdge(j).endCardNum+3), "2");									
										System.out.println(String.valueOf(graph.getEdge(j).endCardNum) + ":右转");
										logger.debug(String.valueOf(graph.getEdge(j).endCardNum) + ":右转");
									}
									
								}
							}
						}
					}else if(graph.getNode(route.get(i+2)-1).y == graph.getNode(route.get(i+1)-1).y){
						//前
						result.add(new Node(route.get(i+1), 3));
						/*
						for(int j = 0; j < graph.getEdgeSize(); j++){
							if((graph.getEdge(j).startNode.num == route.get(i) && graph.getEdge(j).endNode.num == route.get(i+1))){
								sendMessage.replace(2*(graph.getEdge(j).endCardNum+2)-1, 2*(graph.getEdge(j).endCardNum+2), "0");
							}
						}*/
					}else {
						//左
						System.out.print("命令左/");
						logger.debug("命令左/");
						result.add(new Node(route.get(i+1), 1));
						for(int j = 0; j < graph.getEdgeSize(); j++){
							if((graph.getEdge(j).startNode.num == route.get(i) && graph.getEdge(j).endNode.num == route.get(i+1))){
								if(!(backward&&first)){
									if(!returnRoute){
										System.out.println(String.valueOf(graph.getEdge(j).endCardNum) + ":左转");
										logger.debug(String.valueOf(graph.getEdge(j).endCardNum) + ":左转");
										sendMessage.replace(2*(graph.getEdge(j).endCardNum+3)-1, 2*(graph.getEdge(j).endCardNum+3), "1");
									}else{
										System.out.println(String.valueOf(graph.getEdge(j).endCardNum) + ":右转");
										logger.debug(String.valueOf(graph.getEdge(j).endCardNum) + ":右转");
										sendMessage.replace(2*(graph.getEdge(j).endCardNum+3)-1, 2*(graph.getEdge(j).endCardNum+3), "2");
									}									
								}else{
									first = false;
									if(!returnRoute){
										sendMessage.replace(2*(graph.getEdge(j).endCardNum+3)-1, 2*(graph.getEdge(j).endCardNum+3), "2");									
										System.out.println(String.valueOf(graph.getEdge(j).endCardNum) + ":右转");
										logger.debug(String.valueOf(graph.getEdge(j).endCardNum) + ":右转");
									}else{
										sendMessage.replace(2*(graph.getEdge(j).endCardNum+3)-1, 2*(graph.getEdge(j).endCardNum+3), "1");									
										System.out.println(String.valueOf(graph.getEdge(j).endCardNum) + ":左转");
										logger.debug(String.valueOf(graph.getEdge(j).endCardNum) + ":左转");
									}									
								}
							}
						}
					}
				}else if(graph.getNode(route.get(i)-1).x > graph.getNode(route.get(i+1)-1).x){//leftleftleftleftleftleft
					System.out.print("方向左/");
					logger.debug("方向左/");
					if(graph.getNode(route.get(i+2)-1).y > graph.getNode(route.get(i+1)-1).y){
						//左
						System.out.print("命令左/");
						logger.debug("命令左/");
						result.add(new Node(route.get(i+1), 1));
						for(int j = 0; j < graph.getEdgeSize(); j++){
							if((graph.getEdge(j).startNode.num == route.get(i) && graph.getEdge(j).endNode.num == route.get(i+1))){
								System.out.print("找到");
								if(!(backward&&first)){
									if(!returnRoute){
										System.out.println(String.valueOf(graph.getEdge(j).endCardNum) + ":左转");
										logger.debug(String.valueOf(graph.getEdge(j).endCardNum) + ":左转");
										sendMessage.replace(2*(graph.getEdge(j).endCardNum+3)-1, 2*(graph.getEdge(j).endCardNum+3), "1");
									}else{
										System.out.println(String.valueOf(graph.getEdge(j).endCardNum) + ":右转");
										logger.debug(String.valueOf(graph.getEdge(j).endCardNum) + ":右转");
										sendMessage.replace(2*(graph.getEdge(j).endCardNum+3)-1, 2*(graph.getEdge(j).endCardNum+3), "2");
									}
									
								}else{
									first = false;
									if(!returnRoute){
										sendMessage.replace(2*(graph.getEdge(j).endCardNum+3)-1, 2*(graph.getEdge(j).endCardNum+3), "2");									
										System.out.println(String.valueOf(graph.getEdge(j).endCardNum) + ":右转");
										logger.debug(String.valueOf(graph.getEdge(j).endCardNum) + ":右转");
									}else{
										sendMessage.replace(2*(graph.getEdge(j).endCardNum+3)-1, 2*(graph.getEdge(j).endCardNum+3), "1");									
										System.out.println(String.valueOf(graph.getEdge(j).endCardNum) + ":左转");
										logger.debug(String.valueOf(graph.getEdge(j).endCardNum) + ":左转");
									}
								}
							}
						}
					}else if(graph.getNode(route.get(i+2)-1).y == graph.getNode(route.get(i+1)-1).y){
						//前
						result.add(new Node(route.get(i+1), 3));
						/*
						for(int j = 0; j < graph.getEdgeSize(); j++){
							if((graph.getEdge(j).startNode.num == route.get(i) && graph.getEdge(j).endNode.num == route.get(i+1))){
								sendMessage.replace(2*(graph.getEdge(j).endCardNum+2)-1, 2*(graph.getEdge(j).endCardNum+2), "0");
							}
						}*/
					}else {
						//右
						System.out.print("命令右/");
						logger.debug("命令右/");
						result.add(new Node(route.get(i+1), 2));
						for(int j = 0; j < graph.getEdgeSize(); j++){
							if((graph.getEdge(j).startNode.num == route.get(i) && graph.getEdge(j).endNode.num == route.get(i+1))){
								if(!(backward&&first)){
									if(!returnRoute){
										sendMessage.replace(2*(graph.getEdge(j).endCardNum+3)-1, 2*(graph.getEdge(j).endCardNum+3), "2");
										System.out.println(String.valueOf(graph.getEdge(j).endCardNum) + ":右转");
										logger.debug(String.valueOf(graph.getEdge(j).endCardNum) + ":右转");
									}else{
										sendMessage.replace(2*(graph.getEdge(j).endCardNum+3)-1, 2*(graph.getEdge(j).endCardNum+3), "1");
										System.out.println(String.valueOf(graph.getEdge(j).endCardNum) + ":左转");
										logger.debug(String.valueOf(graph.getEdge(j).endCardNum) + ":左转");
									}
								}else{
									first = false;
									if(!returnRoute){
										sendMessage.replace(2*(graph.getEdge(j).endCardNum+3)-1, 2*(graph.getEdge(j).endCardNum+3), "1");
										System.out.println(String.valueOf(graph.getEdge(j).endCardNum) + ":左转");
										logger.debug(String.valueOf(graph.getEdge(j).endCardNum) + ":左转");
									}else{
										sendMessage.replace(2*(graph.getEdge(j).endCardNum+3)-1, 2*(graph.getEdge(j).endCardNum+3), "2");
										System.out.println(String.valueOf(graph.getEdge(j).endCardNum) + ":右转");
										logger.debug(String.valueOf(graph.getEdge(j).endCardNum) + ":右转");
									}
								}
							}
						}
					}
				}
			}
		}		
		//开胜项目才要忽略无需命令点，毕业项目不能忽略
		for(int i = 0; i < graph.getIgnoreCard().size(); i++){
			sendMessage.replace(2*(graph.getIgnoreCard().get(i) + 3) - 1, 2*(graph.getIgnoreCard().get(i) + 3), "0");
		}
		System.out.println("mytoolkit"+sendMessage);
		return sendMessage.toString();
	}
}

