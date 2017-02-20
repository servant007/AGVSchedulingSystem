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
	//private MyTextField agvNum;
	private MyTextField route;
	private SetAGVListener dialogListener;
	private boolean fixRoute;
	
	public SetAGVDialog(String string){
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setSize(screenSize.width/4, screenSize.height/4);
		this.setLocation(3*screenSize.width/8, 3*screenSize.height/8);

		JPanel mainPanel = new JPanel(new GridLayout(3,1, 10,10));

		route = new MyTextField(string);
		
		JPanel fixRoutePanel = new JPanel();
		JRadioButton fixRouteBtn = new JRadioButton("�̶�·��");
		fixRouteBtn.setFont(new Font("����", Font.BOLD, 30));
		fixRouteBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(e.getActionCommand().equals("�̶�·��"))
					fixRoute = true;
			}
		});
		fixRoutePanel.add(fixRouteBtn);
		
		JPanel btnPanel = new JPanel(new GridLayout(1,2,20,20));
		comfirmBtn = new RoundButton("ȷ��");
		cancelBtn = new RoundButton("ȡ��");
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
		mainPanel.add(route);
		mainPanel.add(fixRoutePanel);
		mainPanel.add(btnPanel);
		this.getContentPane().add(mainPanel);
		this.setVisible(true);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.setAlwaysOnTop(true);
	}
	
	public void setOnDialogListener(SetAGVListener listener){
		this.dialogListener = listener;
	}
}
