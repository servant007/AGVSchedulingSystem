package schedulingSystem.gui;

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
		super("AGV����ϵͳ");
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setExtendedState(Frame.MAXIMIZED_BOTH);
		
		JPanel mainPanel = new JPanel();

		schedulingGuiBtn = new RoundButton("���Ƚ���");
		schedulingGuiBtn.setBounds(0, 0, screenSize.width/3, screenSize.height/20);
		schedulingGuiBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				
			}
		});
		setingGuiBtn = new RoundButton("���ý���");
		setingGuiBtn.setBounds(screenSize.width/3, 0, screenSize.width/3, screenSize.height/20);
		setingGuiBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				
			}
		});
		graphGuiBtn = new RoundButton("��ͼ����");
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
				setingGui.setVisible(false);
				graphingGui.setVisible(false);
			}
		});

		graphGuiBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				schedulingGui.setVisible(false);
				setingGui.setVisible(false);
				graphingGui.setVisible(true);
			}
		});
	}
}
