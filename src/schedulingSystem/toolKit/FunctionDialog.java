package schedulingSystem.toolKit;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import schedulingSystem.component.Node;

public class FunctionDialog extends JDialog{
	private FunctionDialogListener functionDialog;
	private RoundButton shipmentBtn;
	private RoundButton unloadingBtn;
	private RoundButton emptyCarBtn;
	private RoundButton addStringBtn;
	private RoundButton chargeBtn;
	public enum FunctionEnum{SHIPMENT, UNLOADING, EMPTYCAR, CHARGE, TAG}
	public  void getInstance(FunctionDialog instance, boolean isNotAddString){
		
		JPanel shipmentPanel = new JPanel(new GridLayout(5,1,10,10));
		setPanel(shipmentPanel, FunctionEnum.SHIPMENT, instance);
		
		JPanel unloadingPanel = new JPanel(new GridLayout(5,1,10,10));
		setPanel(unloadingPanel, FunctionEnum.UNLOADING, instance);
		
		JPanel emptyCarPanel = new JPanel(new GridLayout(5,1,10,10));
		setPanel(emptyCarPanel, FunctionEnum.EMPTYCAR, instance);
		
		JPanel chargePanel = new JPanel(new GridLayout(5,1,10,10));
		setPanel(chargePanel, FunctionEnum.CHARGE, instance);

		JPanel addStringPanel = new JPanel(new GridLayout(4,1,10,10));
		setPanel(addStringPanel, FunctionEnum.TAG, instance);
		
		if(isNotAddString){
			buttonAddActionListener(shipmentBtn, shipmentPanel, instance);
			buttonAddActionListener(unloadingBtn, unloadingPanel, instance);
			buttonAddActionListener(emptyCarBtn, emptyCarPanel, instance);
			buttonAddActionListener(chargeBtn, chargePanel, instance);
			buttonAddActionListener(addStringBtn, addStringPanel, instance);
		}else{
			buttonAddActionListener(addStringBtn, addStringPanel, instance);
		}
	}
	public FunctionDialog(Node position){
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension dialogSize = new Dimension(3*screenSize.width/20, 5*screenSize.height/20);
		this.setSize(dialogSize);
		if(screenSize.width - position.x < dialogSize.width)
			this.setLocation(position.x - dialogSize.width , position.y);
		else if(screenSize.height - position.y < dialogSize.height)
			this.setLocation(position.x, position.y - dialogSize.height);
		else
			this.setLocation(position.x, position.y);
		shipmentBtn = new RoundButton("上料区");
		unloadingBtn = new RoundButton("卸料区");
		emptyCarBtn = new RoundButton("空车区");
		chargeBtn = new RoundButton("充电区");
		addStringBtn = new RoundButton("添加标签");
		
		JPanel funcSeclectPanel = new JPanel(new GridLayout(5,1,10,10));
		funcSeclectPanel.add(shipmentBtn);
		funcSeclectPanel.add(unloadingBtn);
		funcSeclectPanel.add(emptyCarBtn);
		funcSeclectPanel.add(chargeBtn);
		funcSeclectPanel.add(addStringBtn);
		funcSeclectPanel.setBorder(BorderFactory.createEtchedBorder());
		this.getContentPane().add(funcSeclectPanel);
		this.setUndecorated(true);
		this.setVisible(true);	
	}
	
	public void setOnActionListener(FunctionDialogListener listener){
		this.functionDialog = listener;
	}
	
	public void setPanel(JPanel panel, FunctionEnum sec, FunctionDialog instance){
		panel.setBorder(BorderFactory.createEtchedBorder());
		MyTextField num = new MyTextField("通讯编号");
		MyTextField card = new MyTextField("停止卡号");
		MyTextField tag = new MyTextField("功能标记");
		RoundButton comBtn = new RoundButton("确认");
		comBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				functionDialog.getSeclectFunction(sec, num.getText(), card.getText(),tag.getText(),true);
				instance.dispose();
			}
		});
		RoundButton celBtn = new RoundButton("取消");
		celBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				functionDialog.getSeclectFunction(sec, "","", "",false);
				instance.dispose();
			}
		});
				
		panel.add(num);
		panel.add(card);
		panel.add(tag);
		panel.add(comBtn);
		panel.add(celBtn);
	}
	
	public void buttonAddActionListener(RoundButton btn, JPanel panel, FunctionDialog instance){
		btn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				instance.getContentPane().removeAll();
				instance.getContentPane().add(panel);
				instance.validate();
			}
		});
	}
}
