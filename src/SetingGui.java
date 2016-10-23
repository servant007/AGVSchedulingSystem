import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SetingGui extends JFrame{
	private Dimension windowSize;
	private RoundButton schedulingGuiBtn;
	private RoundButton setingGuiBtn;
	private RoundButton graphGuiBtn;
	
	public SetingGui(){
		super("AGV调度系统");
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		windowSize = new Dimension(screenSize.width, (int)((int)screenSize.height*0.94));
		this.setSize(windowSize);
		
		JPanel mainPanel = new JPanel();

		schedulingGuiBtn = new RoundButton("调度界面1");
		schedulingGuiBtn.setBounds(0, 0, screenSize.width/3, 40);
		schedulingGuiBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				
			}
		});
		setingGuiBtn = new RoundButton("设置界面1");
		setingGuiBtn.setBounds(screenSize.width/3, 0, screenSize.width/3, 40);
		setingGuiBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				
			}
		});
		graphGuiBtn = new RoundButton("画图界面1");
		graphGuiBtn.setBounds(2*screenSize.width/3, 0, screenSize.width/3, 40);
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
