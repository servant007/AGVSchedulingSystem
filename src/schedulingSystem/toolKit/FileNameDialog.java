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
	private MyTextField stopCard;
	private MyTextField executeCard;
	private FileNameDialogListener dialogListener;
	
	public FileNameDialog(String str1, String str2){
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setSize(screenSize.width/4, screenSize.height/4);
		this.setLocation(3*screenSize.width/8, 3*screenSize.height/8);

		JPanel mainPanel = new JPanel(new GridLayout(4,1, 10,10));

		stopCard = new MyTextField(str1);
		executeCard = new MyTextField(str2);
		stopCard.setFont(new Font("宋体", Font.BOLD ,30));
		executeCard.setFont(new Font("宋体", Font.BOLD ,30));
		

		comfirmBtn = new RoundButton("确认");
		cancelBtn = new RoundButton("取消");
		comfirmBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dialogListener.getFileName(stopCard.getText(), executeCard.getText(), true);
			}
		});
		
		cancelBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dialogListener.getFileName("","",false);
			}
		});

		mainPanel.add(stopCard);
		mainPanel.add(executeCard);
		mainPanel.add(comfirmBtn);
		mainPanel.add(cancelBtn);
		this.getContentPane().add(mainPanel);
		this.setVisible(true);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.setAlwaysOnTop(true);
	}
	
	public void setOnDialogListener(FileNameDialogListener listener){
		this.dialogListener = listener;
	}
}
