package schedulingSystem.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import schedulingSystem.gui.GraphingGui;
import schedulingSystem.toolKit.*;
import schedulingSystem.gui.SchedulingGui;

public class SetingGui extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RoundButton schedulingGuiBtn;
	private RoundButton setingGuiBtn;
	private RoundButton graphGuiBtn;
	
	public SetingGui(){
		super("AGV调度系统");
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setExtendedState(Frame.MAXIMIZED_BOTH);
		
		JPanel mainPanel = new JPanel();

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

		graphGuiBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				schedulingGui.setVisible(false);
				setingGui.setVisible(false);
				graphingGui.setVisible(true);
				graphingGui.setBtnColor();
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
