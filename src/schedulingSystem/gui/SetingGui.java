package schedulingSystem.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.apache.log4j.Logger;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import schedulingSystem.component.AGVCar;
import schedulingSystem.component.ConflictDetection;
import schedulingSystem.component.Dijkstra;
import schedulingSystem.component.Graph;
import schedulingSystem.component.Main;
import schedulingSystem.gui.GraphingGui;
import schedulingSystem.toolKit.*;
import schedulingSystem.gui.SchedulingGui;
import schedulingSystem.gui.SchedulingGui.TimerListener;

public class SetingGui extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(SetingGui.class.getName());
	private RoundButton schedulingGuiBtn;
	private RoundButton setingGuiBtn;
	private RoundButton graphGuiBtn;
	private RoundButton signUpGraphBtn;
	private RoundButton setAGVBtn;
	private RoundButton confirmBtn;
	private ArrayList<String> AGVSeting;
	private Timer timer;
	private static Graph graph;
	private static SetingGui instance;
	public static SetingGui getInstance(Graph graph1){
		graph = graph1;
		if(instance == null){
			instance = new SetingGui();
		}
		return instance;
	}
	private SetingGui(){
		AGVSeting = new ArrayList<String>();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		schedulingGuiBtn = new RoundButton("调度界面");
		schedulingGuiBtn.setBounds(0, 0, screenSize.width/3, screenSize.height/20);
		
		setingGuiBtn = new RoundButton("设置界面");
		setingGuiBtn.setBounds(screenSize.width/3, 0, screenSize.width/3, screenSize.height/20);
		
		graphGuiBtn = new RoundButton("管理界面");
		graphGuiBtn.setBounds(2*screenSize.width/3, 0, screenSize.width/3, screenSize.height/20);
		
		setAGVBtn = new RoundButton("设置AGV");
		setAGVBtn.setFont(new Font("宋体",Font.BOLD, 23));
		setAGVBtn.setBounds(6*screenSize.width/14, 19*screenSize.height/22, screenSize.width/14, screenSize.height/22);
		setAGVBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				SetAGVDialog setAGVDialog = new SetAGVDialog();
				setAGVDialog.setOnDialogListener(new SetAGVListener(){
					public void getSetAGVListener(String route, boolean fix, boolean btn){
						setAGVDialog.dispose();
						if(btn){
							if(fix){
								AGVSeting.add(route);
							}else{
								AGVSeting.add("/");
							}
							
						}
					}
				});
			}
		});
		
		confirmBtn = new RoundButton("确认");
		confirmBtn.setFont(new Font("宋体",Font.BOLD, 23));
		confirmBtn.setBounds(8*screenSize.width/14, 19*screenSize.height/22, screenSize.width/14, screenSize.height/22);
		confirmBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				File file = new File("C:/Users/agv/Documents/testGraph.xls");
				try{
					InputStream inputStream = new FileInputStream(file.getPath());
					Workbook wb = Workbook.getWorkbook(inputStream);
					WritableWorkbook wwb = Workbook.createWorkbook(new File(".\\data\\testGraph.xls"), wb);
					wwb.removeSheet(3);
					WritableSheet wsAGVSeting = wwb.createSheet("AGVSeting", 3);
					for(int i = 0; i < AGVSeting.size(); i++){				
						Label seting = new Label(0, i, AGVSeting.get(i));
						wsAGVSeting.addCell(seting);					
					}
					wwb.write();
					wwb.close();
					wb.close();
				}catch(Exception e1){
					e1.printStackTrace();
					logger.error(e);
				}
				
			}
		});

		signUpGraphBtn = new RoundButton("注册");
		signUpGraphBtn.setFont(new Font("宋体",Font.BOLD, 23));
		signUpGraphBtn.setBounds(13*screenSize.width/14, 19*screenSize.height/22, screenSize.width/14, screenSize.height/22);
		signUpGraphBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				SignUpDialog dialog = new SignUpDialog();
				dialog.setOnDialogListener(new SignUpDialogListener(){
					public void getDialogListener(String password, boolean btn){
						dialog.dispose();
						if(btn){
							File file = new File(".\\data\\date.txt");
							try{
								FileWriter fw = new FileWriter(file);
								fw.write(password);
								fw.close();
							}catch(Exception e1){
								e1.printStackTrace();
								logger.error(e);
							}
						}
					}
				});
			}
		});
		timer = new Timer(100, new TimerListener());
		timer.start();
		setBtnColor();
		this.setLayout(null);
		this.add(schedulingGuiBtn);
		this.add(setingGuiBtn);
		this.add(graphGuiBtn);
		this.add(signUpGraphBtn);
		this.add(setAGVBtn);
		this.add(confirmBtn);
		
	}
	
	@Override
	public void paint(Graphics g){
		super.paint(g);
		g.setFont(new Font("宋体", Font.BOLD, 35));
		g.setColor(Color.BLACK);
		if(AGVSeting.size() > 0){
			for(int i = 0; i < AGVSeting.size(); i++){
				if(AGVSeting.get(i).length() > 1)
					g.drawString(String.valueOf(i+1)+"AGV固定轨迹："+AGVSeting.get(i), 300,  200 + i*50 );
				else
					g.drawString(String.valueOf(i+1)+"AGV不固定轨迹",300, 200 + i*50);
			}
		}else{
			for(int i = 0; i < graph.getAGVSeting().size(); i++){
				if(graph.getAGVSeting().get(i).length() > 0)
					g.drawString(String.valueOf(i+1)+"AGV固定轨迹："+graph.getAGVSeting().get(i), 300,  200 + i*50 );
				else
					g.drawString(String.valueOf(i+1)+"AGV不固定轨迹",300, 200 + i*50);
			}
		}
		
	}
	
	class TimerListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			repaint();
		}
	}
	public void getGuiInstance(Main main, SchedulingGui schedulingGui, SetingGui setingGui, GraphingGui graphingGui){
		schedulingGuiBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				main.getContentPane().removeAll();
				main.getContentPane().add(schedulingGui);
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
	
	public void setBtnColor(){
		schedulingGuiBtn.setBackground(new Color(30, 144, 255));
		setingGuiBtn.setBackground(Color.WHITE);
		setingGuiBtn.setForeground(new Color(30, 144, 255));
		graphGuiBtn.setBackground(new Color(30, 144, 255));
	}
}
