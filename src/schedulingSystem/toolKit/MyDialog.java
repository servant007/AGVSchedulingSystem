package schedulingSystem.toolKit;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class MyDialog extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RoundButton comfirmBtn;
	private RoundButton cancelBtn;
	private JTextField inputField;
	private DialogListener dialogListener;
	
	public MyDialog(String title){
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setSize(screenSize.width/4, screenSize.height/4);
		this.setLocation(3*screenSize.width/8, 3*screenSize.height/8);
		
		JPanel mainPanel = new JPanel(new GridLayout(4,1, 10,10));

		JLabel label = new JLabel(title);
		label.setFont(new Font("宋体", Font.BOLD ,30));
		inputField = new JTextField();
		inputField.setFont(new Font("宋体", Font.BOLD ,30));
		JPanel btnPanel = new JPanel(new GridLayout(1,2,20,20));
		comfirmBtn = new RoundButton("确认");
		cancelBtn = new RoundButton("取消");
		comfirmBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dialogListener.getInputString(inputField.getText());
			}
		});
		
		cancelBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dialogListener.getInputString("");
			}
		});
		btnPanel.add(comfirmBtn);
		btnPanel.add(cancelBtn);
		
		mainPanel.add(label);
		mainPanel.add(inputField);
		mainPanel.add(btnPanel);
		this.getContentPane().add(mainPanel);
		this.setVisible(true);
		this.setAlwaysOnTop(true);
	}
	
	public void setOnDialogListener(DialogListener listener){
		this.dialogListener = listener;
	}
	
}
