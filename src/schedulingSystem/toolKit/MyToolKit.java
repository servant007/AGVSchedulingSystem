package schedulingSystem.toolKit;

import java.awt.BasicStroke;
import java.awt.Color;
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
import jxl.Sheet;
import jxl.Workbook;
import schedulingSystem.component.AGVCar;
import schedulingSystem.component.Graph;
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
			System.out.print(hex.toUpperCase());
		} 
		System.out.println();
		return buff;
	}
	
	
	public void drawGraph(Graphics g, Graph graph){
		((Graphics2D)g).setStroke(new BasicStroke(6.0f));
		g.setColor(Color.BLACK);
		for(int i = 0 ; i < graph.getEdgeSize(); i++)
			g.drawLine(graph.getEdge(i).startNode.x, graph.getEdge(i).startNode.y, graph.getEdge(i).endNode.x, graph.getEdge(i).endNode.y);
		
		g.setColor(Color.YELLOW);
		for(int i = 0 ; i < graph.getNodeSize(); i++)
			g.fillRect(graph.getNode(i).x - 5, graph.getNode(i).y - 5, 10, 10);
	}
	
	public void drawAGV(Graphics g, ArrayList<AGVCar> AGVArray){
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
	
	public Graph importNewGraph(String fileName){
		Graph graph = new Graph();
		File file = null;
		try{			
			if(fileName == null){
				JFileChooser jfc = new JFileChooser();
				jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				jfc.showDialog(new Label(), "Ñ¡ÔñµØÍ¼");
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
							//System.out.println("++:"+str);						
					}
					graph.addImportNode(x, y, num);
				}
				
				Sheet sheetEdges = wb.getSheet("edges");
				for(int i = 0; i < sheetEdges.getRows(); i++){
					int start=0, end=0, dis=0, strCardNum=0, endCardNum=0;
					for(int j = 0; j < 5; j++){
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
							//System.out.println("++:"+str);						
					}
					graph.addEdge(start, end, dis, strCardNum, endCardNum);
				}
				
			}			
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e);
		}
		return graph;
	}
}

