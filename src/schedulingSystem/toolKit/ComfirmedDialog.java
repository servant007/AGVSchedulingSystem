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

public class ComfirmedDialog extends JDialog{
	private FileNameDialogListener dialogListener;
	private RoundButton comfirmBtn;
	private RoundButton cancelBtn;
	private JLabel label;
	public ComfirmedDialog(String str){
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setSize(screenSize.width/4, screenSize.height/4);
		this.setLocation(3*screenSize.width/8, 3*screenSize.height/8);

		JPanel mainPanel = new JPanel(new GridLayout(3,1, 10,10));
		label = new JLabel(str);
		label.setFont(new Font("宋体", Font.BOLD, 35));
		comfirmBtn = new RoundButton("确认");
		cancelBtn = new RoundButton("取消");
		comfirmBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dialogListener.getFileName("", "", true);
			}
		});
		
		cancelBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dialogListener.getFileName("","",false);
			}
		});
		mainPanel.add(label);
		mainPanel.add(comfirmBtn);
		mainPanel.add(cancelBtn);
		this.getContentPane().add(mainPanel);
		this.setVisible(true);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.setAlwaysOnTop(true);
	}
	
	public void setDialogListener(FileNameDialogListener listener){
		this.dialogListener = listener;
	}

}
