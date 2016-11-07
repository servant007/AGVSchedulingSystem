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
		System.out.println();
		return buff;
	}
	
	public void drawGraph(Graphics g, Graph graph, boolean reverseColor){		
		((Graphics2D)g).setStroke(new BasicStroke(6.0f));
		g.setColor(Color.BLACK);
		
		for(int i = 0 ; i < graph.getEdgeSize(); i++){
			g.drawLine(graph.getEdge(i).startNode.x, graph.getEdge(i).startNode.y, graph.getEdge(i).endNode.x, graph.getEdge(i).endNode.y);
		}
			
		
		
		for(int i = 0 ; i < graph.getNodeSize(); i++){
			g.setColor(Color.YELLOW);
			g.fillRect(graph.getNode(i).x - 5, graph.getNode(i).y - 5, 10, 10);
			g.setColor(Color.RED);
			g.setFont(new Font("宋体", Font.BOLD, 25));
			g.drawString(String.valueOf(i+1),graph.getNode(i).x - 5, graph.getNode(i).y - 5);
		}

		
		g.setColor(Color.blue);
		for(int i = 0; i < graph.getShipmentNode().size(); i++){
			if(graph.getShipmentNode().get(i).clicked){
				if(!reverseColor)
					g.setColor(Color.red);
				else
					g.setColor(Color.blue);
			}
			else
				g.setColor(Color.blue);
			g.fillOval(graph.getNode(graph.getShipmentNode().get(i).nodeNum-1).x-20
					, graph.getNode(graph.getShipmentNode().get(i).nodeNum-1).y-20, 40, 40);
		}
			
		
		g.setColor(Color.green);
		for(int i = 0; i < graph.getUnloadingNode().size(); i++){
			if(graph.getUnloadingNode().get(i).clicked){
				if(!reverseColor)
					g.setColor(Color.red);
				else
					g.setColor(Color.green);
			}
			else
				g.setColor(Color.green);
			g.fillOval(graph.getNode(graph.getUnloadingNode().get(i).nodeNum-1).x-20
					, graph.getNode(graph.getUnloadingNode().get(i).nodeNum-1).y-20, 40, 40);
		}
		
		g.setColor(Color.ORANGE);
		for(int i = 0; i < graph.getEmptyCarNode().size(); i++){
			if(graph.getEmptyCarNode().get(i).clicked){
				if(!reverseColor)
					g.setColor(Color.red);
				else
					g.setColor(Color.ORANGE);
			}
			else
				g.setColor(Color.ORANGE);
			g.fillOval(graph.getNode(graph.getEmptyCarNode().get(i).nodeNum-1).x-20
					, graph.getNode(graph.getEmptyCarNode().get(i).nodeNum-1).y-20, 40, 40);
		}
		
		g.setColor(Color.PINK);
		for(int i = 0; i < graph.getChargeNode().size(); i++){
			if(graph.getChargeNode().get(i).clicked){
				if(!reverseColor)
					g.setColor(Color.red);
				else
					g.setColor(Color.PINK);
			}
			else
				g.setColor(Color.PINK);
			g.fillOval(graph.getNode(graph.getChargeNode().get(i).nodeNum-1).x-20
					, graph.getNode(graph.getChargeNode().get(i).nodeNum-1).y-20, 40, 40);
		}
		
		g.setColor(Color.gray);
		g.setFont(new Font("宋体", Font.BOLD, 30));
		for(int i = 0; i < graph.getTagArray().size(); i++)
			g.drawString(graph.getTagArray().get(i).tag, graph.getTagArray().get(i).position.x
					, graph.getTagArray().get(i).position.y);
		
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
							//System.out.println("nodes:"+str);					
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
							//System.out.println("edges:"+str);						
					}
					if(twoWay == 1)
						graph.addEdge(start, end, dis, strCardNum, endCardNum, true);
					if(twoWay == 0)
						graph.addEdge(start, end, dis, strCardNum, endCardNum, false);
				}
				
				Sheet sheetShipment = wb.getSheet("shipment");
				for(int i = 0; i < sheetShipment.getRows(); i++){
					int node=0, com=0, card=0;
					String tag = "";
					for(int j = 0; j < 4; j++){
						Cell cell0 = sheetShipment.getCell(j,i);
							String str = cell0.getContents();
							if(j == 0)
								node = Integer.parseInt(str);
							if(j == 1)
								com = Integer.parseInt(str);
							if(j == 2)
								card = Integer.parseInt(str);
							if(j == 3)
								tag = str;
							//System.out.println("sheetShipment:"+str);					
					}
					graph.addShipmentNode(node, com, card, tag);
				}
				
				
				
				Sheet sheetUnloading = wb.getSheet("unloading");
				for(int i = 0; i < sheetUnloading.getRows(); i++){
					int node=0, com=0, card=0;
					String tag = "";
					for(int j = 0; j < 4; j++){
						Cell cell0 = sheetUnloading.getCell(j,i);
							String str = cell0.getContents();
							if(j == 0)
								node = Integer.parseInt(str);
							if(j == 1)
								com = Integer.parseInt(str);
							if(j == 2)
								card = Integer.parseInt(str);
							if( j == 3)
								tag = str;
							
							//System.out.println("unloading:"+str);					
					}
					graph.addUnloadingNode(node, com, card, tag);
				}
				
				
				Sheet sheetEmptyCar = wb.getSheet("emptyCar");
				for(int i = 0; i < sheetEmptyCar.getRows(); i++){
					int node=0, com=0, card=0;
					String tag = "";
					for(int j = 0; j < 4; j++){
						Cell cell0 = sheetEmptyCar.getCell(j,i);
							String str = cell0.getContents();
							if(j == 0)
								node = Integer.parseInt(str);
							if(j == 1)
								com = Integer.parseInt(str);
							if(j == 2)
								card = Integer.parseInt(str);	
							if(j == 3)
								tag = str;
					}
					graph.addEmptyCarNode(node, com, card, tag);
				}
				
				Sheet sheetCharge = wb.getSheet("charge");
				for(int i = 0; i < sheetCharge.getRows(); i++){
					int node=0, com=0, card=0;
					String tag = "";
					for(int j = 0; j < 4; j++){
						Cell cell0 = sheetCharge.getCell(j,i);
							String str = cell0.getContents();
							if(j == 0)
								node = Integer.parseInt(str);
							if(j == 1)
								com = Integer.parseInt(str);
							if(j == 2)
								card = Integer.parseInt(str);	
							if(j == 3)
								tag = str;
					}
					graph.addChargeNode(node, com, card, tag);
				}
				
				Sheet sheetTag = wb.getSheet("tag");
				for(int i = 0; i < sheetTag.getRows(); i++){
					int x=0, y=0;
					String tag = "";
					for(int j = 0; j < 3; j++){
						Cell cell0 = sheetTag.getCell(j,i);
							String str = cell0.getContents();
							if(j == 0)
								x = Integer.parseInt(str);
							if(j == 1)
								y = Integer.parseInt(str);
							if(j == 2)
								tag = str;			
					}
					graph.addTagArray(x, y, tag);
				}
			}			
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e);
		}
		return graph;
	}
	
	
	public String routeToOrientation(Graph graph, ArrayList<Integer> route, AGVCar agvCar){
		boolean strNodeFunction = agvCar.getStartEdge().endNode.functionNode;
		boolean first = true;
		ArrayList<Node> result = new ArrayList<Node>();
		StringBuffer sendMessage = new StringBuffer();//2*graph.getEdgeSize()+3
		sendMessage.append("AA");
		for(int i = 0; i < graph.getCardQuantity()+1; i++){
			sendMessage.append("00");
		}
		sendMessage.append("BB");
		
		if(strNodeFunction)
			sendMessage.replace(3, 4, "2");//倒退
		else
			sendMessage.replace(3, 4, "1");//前进
		
		for(int i = 0; i+2 < route.size(); i++){
			if(graph.getNode(route.get(i)-1).x == graph.getNode(route.get(i+1)-1).x){
				//System.out.println("x=");
				if(graph.getNode(route.get(i)-1).y < graph.getNode(route.get(i+1)-1).y){//down
					System.out.print("方向下/");
					if(graph.getNode(route.get(i+2)-1).x > graph.getNode(route.get(i+1)-1).x){
						//左1
						System.out.print("命令左/");
						result.add(new Node(route.get(i+1), 1));
						for(int j = 0; j < graph.getEdgeSize(); j++){
							if((graph.getEdge(j).startNode.num == route.get(i) && graph.getEdge(j).endNode.num == route.get(i+1))){
								if(!(strNodeFunction&&first))//左前进时
									sendMessage.replace(2*(graph.getEdge(j).endCardNum+2)-1, 2*(graph.getEdge(j).endCardNum+2), "1");
								else{
									sendMessage.replace(2*(graph.getEdge(j).endCardNum+2)-1, 2*(graph.getEdge(j).endCardNum+2), "2");
									first = false;
								}
							}
						}
					}else if(graph.getNode(route.get(i+2)-1).x == graph.getNode(route.get(i+1)-1).x){
						//前3
						result.add(new Node(route.get(i+1), 3));
						for(int j = 0; j < graph.getEdgeSize(); j++){
							if((graph.getEdge(j).startNode.num == route.get(i) && graph.getEdge(j).endNode.num == route.get(i+1))){
								sendMessage.replace(2*(graph.getEdge(j).endCardNum+2)-1, 2*(graph.getEdge(j).endCardNum+2), "0");
							}
						}
					}else{
						//右2
						System.out.print("命令右/");
						result.add(new Node(route.get(i+1), 2));
						for(int j = 0; j < graph.getEdgeSize(); j++){
							if((graph.getEdge(j).startNode.num == route.get(i) && graph.getEdge(j).endNode.num == route.get(i+1))){
								if(!(strNodeFunction&&first))
									sendMessage.replace(2*(graph.getEdge(j).endCardNum+2)-1, 2*(graph.getEdge(j).endCardNum+2), "2");
								else{
									first = false;
									sendMessage.replace(2*(graph.getEdge(j).endCardNum+2)-1, 2*(graph.getEdge(j).endCardNum+2), "1");
								}
							}
						}
					}
				}else if(graph.getNode(route.get(i)-1).y > graph.getNode(route.get(i+1)-1).y){//up
					System.out.print("方向上/");
					if(graph.getNode(route.get(i+2)-1).x > graph.getNode(route.get(i+1)-1).x){
						//右
						System.out.print("命令右/");
						result.add(new Node(route.get(i+1), 2));
						for(int j = 0; j < graph.getEdgeSize(); j++){
							if((graph.getEdge(j).startNode.num == route.get(i) && graph.getEdge(j).endNode.num == route.get(i+1))){
								if(!(strNodeFunction&&first))
									sendMessage.replace(2*(graph.getEdge(j).endCardNum+2)-1, 2*(graph.getEdge(j).endCardNum+2), "2");
								else{
									sendMessage.replace(2*(graph.getEdge(j).endCardNum+2)-1, 2*(graph.getEdge(j).endCardNum+2), "1");
									first = false;
								}
							}
						}
					}else if(graph.getNode(route.get(i+2)-1).x == graph.getNode(route.get(i+1)-1).x){
						//前
						result.add(new Node(route.get(i+1), 3));
						for(int j = 0; j < graph.getEdgeSize(); j++){
							if((graph.getEdge(j).startNode.num == route.get(i) && graph.getEdge(j).endNode.num == route.get(i+1))){
								sendMessage.replace(2*(graph.getEdge(j).endCardNum+2)-1, 2*(graph.getEdge(j).endCardNum+2), "0");
							}
						}
					}else{
						//左
						System.out.print("命令左/");
						result.add(new Node(route.get(i+1), 1));
						for(int j = 0; j < graph.getEdgeSize(); j++){
							if((graph.getEdge(j).startNode.num == route.get(i) && graph.getEdge(j).endNode.num == route.get(i+1))){
								if(!(strNodeFunction&&first))
									sendMessage.replace(2*(graph.getEdge(j).endCardNum+2)-1, 2*(graph.getEdge(j).endCardNum+2), "1");
								else{
									sendMessage.replace(2*(graph.getEdge(j).endCardNum+2)-1, 2*(graph.getEdge(j).endCardNum+2), "2");
									first =false;
								}
							}
						}
					}
				}
			}else if(graph.getNode(route.get(i)-1).y == graph.getNode(route.get(i+1)-1).y){//right and left
				if(graph.getNode(route.get(i)-1).x < graph.getNode(route.get(i+1)-1).x){//right
					System.out.print("方向右/");
					if(graph.getNode(route.get(i+2)-1).y > graph.getNode(route.get(i+1)-1).y){
						//右
						System.out.print("命令右/");
						result.add(new Node(route.get(i+1), 2));
						for(int j = 0; j < graph.getEdgeSize(); j++){
							if((graph.getEdge(j).startNode.num == route.get(i) && graph.getEdge(j).endNode.num == route.get(i+1))){
								if(!(strNodeFunction&&first))
									sendMessage.replace(2*(graph.getEdge(j).endCardNum+2)-1, 2*(graph.getEdge(j).endCardNum+2), "2");
								else{
									sendMessage.replace(2*(graph.getEdge(j).endCardNum+2)-1, 2*(graph.getEdge(j).endCardNum+2), "1");
									first = false;
								}
							}
						}
					}else if(graph.getNode(route.get(i+2)-1).y == graph.getNode(route.get(i+1)-1).y){
						//前
						result.add(new Node(route.get(i+1), 3));
						for(int j = 0; j < graph.getEdgeSize(); j++){
							if((graph.getEdge(j).startNode.num == route.get(i) && graph.getEdge(j).endNode.num == route.get(i+1))){
								sendMessage.replace(2*(graph.getEdge(j).endCardNum+2)-1, 2*(graph.getEdge(j).endCardNum+2), "0");
							}
						}
					}else {
						//左
						System.out.print("命令左/");
						result.add(new Node(route.get(i+1), 1));
						for(int j = 0; j < graph.getEdgeSize(); j++){
							if((graph.getEdge(j).startNode.num == route.get(i) && graph.getEdge(j).endNode.num == route.get(i+1))){
								if(!(strNodeFunction&&first))									
									sendMessage.replace(2*(graph.getEdge(j).endCardNum+2)-1, 2*(graph.getEdge(j).endCardNum+2), "1");
								else{
									sendMessage.replace(2*(graph.getEdge(j).endCardNum+2)-1, 2*(graph.getEdge(j).endCardNum+2), "2");
									first = false;
								}
							}
						}
					}
				}else if(graph.getNode(route.get(i)-1).x > graph.getNode(route.get(i+1)-1).x){//leftleftleftleftleftleft
					System.out.print("方向左/");
					if(graph.getNode(route.get(i+2)-1).y > graph.getNode(route.get(i+1)-1).y){
						//左
						System.out.print("命令左/");
						result.add(new Node(route.get(i+1), 1));
						for(int j = 0; j < graph.getEdgeSize(); j++){
							if((graph.getEdge(j).startNode.num == route.get(i) && graph.getEdge(j).endNode.num == route.get(i+1))){
								System.out.print("找到");
								if(!(strNodeFunction&&first)){
									//System.out.println("替换");
									sendMessage.replace(2*(graph.getEdge(j).endCardNum+2)-1, 2*(graph.getEdge(j).endCardNum+2), "1");
								}else{
									sendMessage.replace(2*(graph.getEdge(j).endCardNum+2)-1, 2*(graph.getEdge(j).endCardNum+2), "2");
									first = false;
								}
							}
						}
					}else if(graph.getNode(route.get(i+2)-1).y == graph.getNode(route.get(i+1)-1).y){
						//前
						result.add(new Node(route.get(i+1), 3));
						for(int j = 0; j < graph.getEdgeSize(); j++){
							if((graph.getEdge(j).startNode.num == route.get(i) && graph.getEdge(j).endNode.num == route.get(i+1))){
								sendMessage.replace(2*(graph.getEdge(j).endCardNum+2)-1, 2*(graph.getEdge(j).endCardNum+2), "0");
							}
						}
					}else {
						//右
						System.out.print("命令右/");
						result.add(new Node(route.get(i+1), 2));
						for(int j = 0; j < graph.getEdgeSize(); j++){
							if((graph.getEdge(j).startNode.num == route.get(i) && graph.getEdge(j).endNode.num == route.get(i+1))){
								if(!(strNodeFunction&&first))
									sendMessage.replace(2*(graph.getEdge(j).endCardNum+2)-1, 2*(graph.getEdge(j).endCardNum+2), "2");
								else{
									sendMessage.replace(2*(graph.getEdge(j).endCardNum+2)-1, 2*(graph.getEdge(j).endCardNum+2), "1");
									first = false;
								}
							}
						}
					}
				}
			}
		}		
		//开胜项目才要忽略无需命令点，毕业项目不能忽略
		for(int i = 0; i < graph.getIgnoreCard().size(); i++){
			sendMessage.replace(2*(graph.getIgnoreCard().get(i) + 2) - 1, 2*(graph.getIgnoreCard().get(i) + 2), "0");
		}
		
		return sendMessage.toString();
	}
}

