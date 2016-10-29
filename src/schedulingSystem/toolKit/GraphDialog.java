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

public class GraphDialog extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RoundButton comfirmBtn;
	private RoundButton cancelBtn;
	private JTextField inputDis;
	private JTextField inputStrCard;
	private JTextField inputEndCard;
	private GraphDialogListener dialogListener;
	
	public GraphDialog( ){
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setSize(screenSize.width/4, screenSize.height/4);
		this.setLocation(3*screenSize.width/8, 3*screenSize.height/8);

		JPanel mainPanel = new JPanel(new GridLayout(5,1, 10,10));

		JPanel disPanel = new JPanel(new GridLayout(1, 2, 0 ,0));
		JLabel label = new JLabel("实际距离：");
		label.setFont(new Font("宋体", Font.BOLD ,25));
		inputDis = new JTextField();
		inputDis.setFont(new Font("宋体", Font.BOLD ,30));
		disPanel.add(label);
		disPanel.add(inputDis);
		
		JPanel strCardPanel = new JPanel(new GridLayout(1, 2, 0 ,0));
		JLabel label1 = new JLabel("开始卡号：");
		label1.setFont(new Font("宋体", Font.BOLD ,25));
		inputStrCard = new JTextField();
		inputStrCard.setFont(new Font("宋体", Font.BOLD ,30));
		strCardPanel.add(label1);
		strCardPanel.add(inputStrCard);
		
		JPanel endCardPanel = new JPanel(new GridLayout(1, 2, 0 ,0));
		JLabel label2 = new JLabel("结束卡号:");
		label2.setFont(new Font("宋体", Font.BOLD ,25));
		inputEndCard = new JTextField();
		inputEndCard.setFont(new Font("宋体", Font.BOLD ,30));
		endCardPanel.add(label2);
		endCardPanel.add(inputEndCard);
		
		JPanel btnPanel = new JPanel(new GridLayout(1,2,20,20));
		comfirmBtn = new RoundButton("确认");
		cancelBtn = new RoundButton("取消");
		comfirmBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dialogListener.getInputString(inputDis.getText(), inputStrCard.getText()
						,inputEndCard.getText(), true);
			}
		});
		
		cancelBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dialogListener.getInputString("", "","",false);
			}
		});
		btnPanel.add(comfirmBtn);
		btnPanel.add(cancelBtn);
		
		mainPanel.add(disPanel);
		mainPanel.add(strCardPanel);
		mainPanel.add(endCardPanel);
		mainPanel.add(btnPanel);
		this.getContentPane().add(mainPanel);
		this.setVisible(true);
		this.setAlwaysOnTop(true);
		//this.setUndecorated(true);
	}
	
	public void setOnDialogListener(GraphDialogListener listener){
		this.dialogListener = listener;
	}
	
}
