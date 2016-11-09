package schedulingSystem.toolKit;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class SetAGVDialog extends JDialog{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RoundButton comfirmBtn;
	private RoundButton cancelBtn;
	private MyTextField agvNum;
	private MyTextField route;
	private SetAGVListener dialogListener;
	private boolean fixRoute;
	
	public SetAGVDialog(){
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setSize(screenSize.width/4, screenSize.height/4);
		this.setLocation(3*screenSize.width/8, 3*screenSize.height/8);

		JPanel mainPanel = new JPanel(new GridLayout(5,1, 10,10));

		agvNum = new MyTextField("AGV编号");
		route = new MyTextField("AGV固定路径");
		
		JRadioButton twoWayBtn = new JRadioButton("固定路径");
		twoWayBtn.setFont(new Font("宋体", Font.BOLD, 30));
		twoWayBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(e.getActionCommand().equals("固定路径"))
					fixRoute = true;
			}
		});
		
		JPanel btnPanel = new JPanel(new GridLayout(1,2,20,20));
		comfirmBtn = new RoundButton("确认");
		cancelBtn = new RoundButton("取消");
		comfirmBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dialogListener.getSetAGVListener(route.getText(), fixRoute, true);
			}
		});
		
		cancelBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dialogListener.getSetAGVListener("", false, false);
			}
		});
		btnPanel.add(comfirmBtn);
		btnPanel.add(cancelBtn);
		mainPanel.add(agvNum);
		mainPanel.add(route);
		mainPanel.add(twoWayBtn);
		mainPanel.add(btnPanel);
		this.getContentPane().add(mainPanel);
		this.setVisible(true);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.setAlwaysOnTop(true);
		//this.setUndecorated(true);
	}
	
	public void setOnDialogListener(SetAGVListener listener){
		this.dialogListener = listener;
	}
}
