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

public class FileNameDialog extends JDialog{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RoundButton comfirmBtn;
	private RoundButton cancelBtn;
	private JTextField inputDis;
	private FileNameDialogListener dialogListener;
	
	public FileNameDialog(){
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setSize(screenSize.width/4, screenSize.height/4);
		this.setLocation(3*screenSize.width/8, 3*screenSize.height/8);

		JPanel mainPanel = new JPanel(new GridLayout(4,1, 10,10));

		JPanel namePanel = new JPanel(new GridLayout(1, 2, 0 ,0));
		JLabel label = new JLabel("保存的文件名：");
		label.setFont(new Font("宋体", Font.BOLD ,25));
		inputDis = new JTextField();
		inputDis.setFont(new Font("宋体", Font.BOLD ,30));
		namePanel.add(label);
		namePanel.add(inputDis);
		
		
		
		JPanel btnPanel = new JPanel(new GridLayout(1,2,20,20));
		comfirmBtn = new RoundButton("确认");
		cancelBtn = new RoundButton("取消");
		comfirmBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dialogListener.getFileName(inputDis.getText(), true);
			}
		});
		
		cancelBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dialogListener.getFileName("",false);
			}
		});
		btnPanel.add(comfirmBtn);
		btnPanel.add(cancelBtn);
		mainPanel.add(namePanel);
		mainPanel.add(new JPanel());
		mainPanel.add(btnPanel);
		this.getContentPane().add(mainPanel);
		this.setVisible(true);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.setAlwaysOnTop(true);
		//this.setUndecorated(true);
	}
	
	public void setOnDialogListener(FileNameDialogListener listener){
		this.dialogListener = listener;
	}
}
