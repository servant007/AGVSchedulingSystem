package schedulingSystem.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
	private RoundButton initConflictDetection;
	private RoundButton cancelAGV;
	private RoundButton signUpGraphBtn;
	private RoundButton setAGVBtn;
	private RoundButton confirmBtn;
	private RoundButton offDutyBtn;
	private RoundButton onDutyBtn;
	private RoundButton setChargeTimeBtn;
	private RoundButton cancelPlayWaring;
	private RoundButton switchButton;
	private ArrayList<String> AGVSeting;
	private Timer timer;
	private static Graph graph;
	private static SetingGui instance;
	private Dimension screenSize;
	private Image logo;
	private JLabel stateLabel;
	private boolean isClickOffDuty;
	
	public static SetingGui getInstance(Graph graph1){
		graph = graph1;
		if(instance == null){
			instance = new SetingGui();
		}
		return instance;
	}
	private SetingGui(){
		Toolkit tool = Toolkit.getDefaultToolkit();
		logo = tool.createImage(getClass().getResource("/logo3.png"));
		AGVSeting = new ArrayList<String>();
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		schedulingGuiBtn = new RoundButton("调度界面");
		schedulingGuiBtn.setBounds(0, 0, screenSize.width/3, screenSize.height/20);
		
		setingGuiBtn = new RoundButton("设置界面");
		setingGuiBtn.setBounds(screenSize.width/3, 0, screenSize.width/3, screenSize.height/20);
		
		graphGuiBtn = new RoundButton("制图界面");
		graphGuiBtn.setBounds(2*screenSize.width/3, 0, screenSize.width/3, screenSize.height/20);
		
		initConflictDetection = new RoundButton("交通管制初始化");
		initConflictDetection.setFont(new Font("宋体",Font.BOLD, 21));
		initConflictDetection.setBounds(11*screenSize.width/17, 19*screenSize.height/22, screenSize.width/10, screenSize.height/22);
		
		cancelAGV = new RoundButton("删除故障AGV");
		cancelAGV.setFont(new Font("宋体",Font.BOLD, 25));
		cancelAGV.setBounds(9*screenSize.width/17, 19*screenSize.height/22, screenSize.width/10, screenSize.height/22);
		
		offDutyBtn = new RoundButton("下班");
		offDutyBtn.setFont(new Font("宋体",Font.BOLD, 25));
		offDutyBtn.setBounds(5*screenSize.width/17, 19*screenSize.height/22, screenSize.width/12, screenSize.height/22);
		
		onDutyBtn = new RoundButton("上班");
		onDutyBtn.setFont(new Font("宋体",Font.BOLD, 25));
		onDutyBtn.setBounds(3*screenSize.width/17, 19*screenSize.height/22, screenSize.width/12, screenSize.height/22);
		
		setChargeTimeBtn = new RoundButton("设置充电时间");
		setChargeTimeBtn.setFont(new Font("宋体",Font.BOLD, 25));
		setChargeTimeBtn.setBounds(7*screenSize.width/17, 19*screenSize.height/22, screenSize.width/10, screenSize.height/22);
		
		cancelPlayWaring = new RoundButton("解除报警");
		cancelPlayWaring.setFont(new Font("宋体",Font.BOLD, 25));
		cancelPlayWaring.setBounds(13*screenSize.width/17, 19*screenSize.height/22, screenSize.width/10, screenSize.height/22);
		
		setAGVBtn = new RoundButton("设置AGV");
		setAGVBtn.setFont(new Font("宋体",Font.BOLD, 25));
		setAGVBtn.setBounds(5*screenSize.width/14, 19*screenSize.height/22, screenSize.width/12, screenSize.height/22);
		setAGVBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				SetAGVDialog setAGVDialog = new SetAGVDialog("AGV固定路径");
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
		
		stateLabel = new JLabel();
		stateLabel.setBounds(0, 21*screenSize.height/25, screenSize.width, screenSize.height/25);
		stateLabel.setFont(new Font("宋体", Font.BOLD, 25));
		
		confirmBtn = new RoundButton("确认设置");
		confirmBtn.setFont(new Font("宋体",Font.BOLD, 25));
		confirmBtn.setBounds(7*screenSize.width/14, 19*screenSize.height/22, screenSize.width/12, screenSize.height/22);
		confirmBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				//File file = new File(".\\data\\testGraph.xls");
				try{
					//String path = Thread.currentThread().getContextClassLoader().getResource("testGraph.xls").getPath();
					//System.out.println("path:"+ path);
					//stateLabel.setText(path);
					File file = new File("C:\\Users\\agv\\Documents\\testGraph.xls");
					InputStream inputStream = new FileInputStream(file.getPath());
					//InputStream inputStream = this.getClass().getResourceAsStream("C:\\Users\\agv\\Documents\\testGraph.xls");
					Workbook wb = Workbook.getWorkbook(inputStream);
					WritableWorkbook wwb = Workbook.createWorkbook(new File("C:\\Users\\agv\\Documents\\testGraph.xls"), wb);
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
				SignUpDialog dialog = new SignUpDialog("注册序列号");
				dialog.setOnDialogListener(new SignUpDialogListener(){
					public void getDialogListener(String password, boolean btn){
						dialog.dispose();
						if(btn){
							File file = new File("C:\\Users\\agv\\Documents\\date.txt");
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
		
		switchButton = new RoundButton("自动模式");
		switchButton.setFont(new Font("宋体",Font.BOLD, 23));
		switchButton.setBounds(7*screenSize.width/14, 17*screenSize.height/22, screenSize.width/14, screenSize.height/22);
		
		timer = new Timer(100, new TimerListener());
		timer.start();
		setBtnColor();
		this.setLayout(null);
		this.add(schedulingGuiBtn);
		this.add(setingGuiBtn);
		this.add(graphGuiBtn);
		this.add(signUpGraphBtn);
		this.add(stateLabel);
		//this.add(setAGVBtn);
		//this.add(confirmBtn);
		this.add(initConflictDetection);
		this.add(cancelAGV);
		this.add(offDutyBtn);
		this.add(this.onDutyBtn);
		this.add(setChargeTimeBtn);
		this.add(this.switchButton);
		this.add(this.cancelPlayWaring);
	}
	
	@Override
	public void paint(Graphics g){
		super.paint(g);
		g.setFont(new Font("Dialog", Font.BOLD, 25));
		g.drawString("服务电话：13751402059",1*screenSize.width/30, 23*screenSize.height/26);
		g.drawImage(logo,1*screenSize.width/30, 19*screenSize.height/26, this);
		g.setFont(new Font("宋体", Font.BOLD, 35));
		g.setColor(Color.BLACK);
		if(AGVSeting.size() > 0){
			for(int i = 0; i < AGVSeting.size(); i++){
				if(AGVSeting.get(i).length() > 1)
					g.drawString(String.valueOf(i+1)+"号AGV：固定轨迹："+AGVSeting.get(i), 3*screenSize.width/8, 1*screenSize.height/8 + i*70);
				else
					g.drawString(String.valueOf(i+1)+"号AGV：自由调度",3*screenSize.width/8, 1*screenSize.height/8 + i*70);
			}
		}else{
			for(int i = 0; i < graph.getAGVSeting().size(); i++){
				if(graph.getAGVSeting().get(i).length() > 1)
					g.drawString(String.valueOf(i+1)+"号AGV：固定轨迹："+graph.getAGVSeting().get(i),3*screenSize.width/8, 1*screenSize.height/8 + i*70);
				else
					g.drawString(String.valueOf(i+1)+"号AGV：自由调度",3*screenSize.width/8, 1*screenSize.height/8 + i*70);
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
		
		switchButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(!schedulingGui.getManualModel()){
					switchButton.setText("手动模式");
					schedulingGui.setManualModel(true);
				}else{
					switchButton.setText("自动模式");
					schedulingGui.setManualModel(false);
				}
				
				
			}
		});

		initConflictDetection.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				ComfirmedDialog dialog = new ComfirmedDialog("确认初始化吗？");
				dialog.setDialogListener(new FileNameDialogListener(){
					@Override
					public void getFileName(String stopCard, String executeCard, boolean buttonState) {
						dialog.dispose();
						if(buttonState){
							schedulingGui.initConflictDetection();
						}
					}
				});
								
			}
		});
		
		cancelPlayWaring.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				schedulingGui.cancelPlayWaring();
			}
		});
		
		cancelAGV.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				SignUpDialog dialog = new SignUpDialog("AGV号");
				dialog.setOnDialogListener(new SignUpDialogListener(){
					public void getDialogListener(String AGVNum, boolean btn){
						dialog.dispose();
						if(btn && AGVNum.length() > 0 && !AGVNum.equals("AGV号")){
							schedulingGui.cancelAGV(Integer.parseInt(AGVNum));
						}
					}
				});
				
			}
		});
		
		offDutyBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(!isClickOffDuty){					
					ComfirmedDialog dialog = new ComfirmedDialog("确认下班吗？");
					dialog.setDialogListener(new FileNameDialogListener(){
						@Override
						public void getFileName(String stopCard, String executeCard, boolean buttonState) {
							dialog.dispose();
							if(buttonState){
								isClickOffDuty = true;
								schedulingGui.offDuty();
							}
						}
					});					
				}				
			}
		});
		
		setChargeTimeBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){				
				FileNameDialog dialog = new FileNameDialog("充电多久（分钟）" + schedulingGui.AGVArray.get(0).chargeDuration/60000, "充电间隔时间（分钟）"+ schedulingGui.chargeGap/60000);
				dialog.setOnDialogListener(new FileNameDialogListener(){
					@Override
					public void getFileName(String chargeDuration, String chargeGap, boolean buttonState){
						dialog.dispose();
						if(buttonState && chargeDuration.length() < 4 && chargeGap.length() < 4){
							try{
								File file = new File("C:\\Users\\agv\\Documents\\testGraph.xls");
								InputStream inputStream = new FileInputStream(file.getPath());
								//InputStream inputStream = this.getClass().getResourceAsStream("C:\\Users\\agv\\Documents\\testGraph.xls");
								Workbook wb = Workbook.getWorkbook(inputStream);
								WritableWorkbook wwb = Workbook.createWorkbook(new File("C:\\Users\\agv\\Documents\\testGraph.xls"), wb);
								wwb.removeSheet(5);
								WritableSheet wsAGVSeting = wwb.createSheet("chargeTime", 5);
								Number duration = new Number(0, 0, Integer.parseInt(chargeDuration)*60000);
								Number gap = new Number(0, 1, Integer.parseInt(chargeGap)*60000);
								wsAGVSeting.addCell(duration);
								wsAGVSeting.addCell(gap);

								wwb.write();
								wwb.close();
								wb.close();
							}catch(Exception e1){
								e1.printStackTrace();
								logger.error(e);
							}
							schedulingGui.setChargeTime(Integer.parseInt(chargeDuration)*60000, Integer.parseInt(chargeGap)*60000);;
						}
					}
				});
			}
		});
		
		onDutyBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				ComfirmedDialog dialog = new ComfirmedDialog("确认上班吗？");
				dialog.setDialogListener(new FileNameDialogListener(){
					@Override
					public void getFileName(String stopCard, String executeCard, boolean buttonState) {
						dialog.dispose();
						if(buttonState){
							schedulingGui.clickOnDutyBtn();;
						}
					}
				});								
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
