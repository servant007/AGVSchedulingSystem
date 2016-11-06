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
	public  void getInstance(FunctionDialog instance, boolean isNotTag){
		
		JPanel shipmentPanel = new JPanel(new GridLayout(5,1,10,10));
		shipmentPanel.setBorder(BorderFactory.createEtchedBorder());
		MyTextField shipmentNum = new MyTextField("通讯编号");
		MyTextField shipmentCard = new MyTextField("停止卡号");
		MyTextField shipmentTag = new MyTextField("功能标记");
		RoundButton shipmentComBtn = new RoundButton("确认");
		shipmentComBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				functionDialog.getSeclectFunction(1, shipmentNum.getText(), shipmentCard.getText(),shipmentTag.getText(),true);
				instance.dispose();
			}
		});
		RoundButton shipmentCelBtn = new RoundButton("取消");
		shipmentCelBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				functionDialog.getSeclectFunction(0, "","", "",false);
				instance.dispose();
			}
		});
		setPanel(shipmentPanel, shipmentNum,shipmentCard, shipmentTag, shipmentComBtn, shipmentCelBtn);
		
		JPanel unloadingPanel = new JPanel(new GridLayout(5,1,10,10));
		unloadingPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		MyTextField unloadingNum = new MyTextField("通讯编号");
		MyTextField unloadingCard = new MyTextField("停止卡号");
		MyTextField unloadingTag = new MyTextField("功能标记");
		RoundButton unloadingComBtn = new RoundButton("确认");
		unloadingComBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				functionDialog.getSeclectFunction(2,unloadingNum.getText(),unloadingCard.getText(), unloadingTag.getText(),true);
				instance.dispose();
			}
		});
		RoundButton unloadingCelBtn = new RoundButton("取消");
		unloadingCelBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				functionDialog.getSeclectFunction(0,"","","", false);
				instance.dispose();
			}
		});
		setPanel(unloadingPanel, unloadingNum,unloadingCard,unloadingTag, unloadingComBtn, unloadingCelBtn);
		
		JPanel emptyCarPanel = new JPanel(new GridLayout(5,1,10,10));
		emptyCarPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		MyTextField emptyCarNum = new MyTextField("通讯编号");
		MyTextField emptyCarCard = new MyTextField("停止卡号");
		MyTextField emptyCarTag = new MyTextField("功能标记");
		RoundButton emptyCarComBtn = new RoundButton("确认");
		emptyCarComBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				functionDialog.getSeclectFunction(3,emptyCarNum.getText(), emptyCarCard.getText(), emptyCarTag.getText(), true);
				instance.dispose();
			}
		});
		RoundButton emptyCarCelBtn = new RoundButton("取消");
		emptyCarCelBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				functionDialog.getSeclectFunction(0,"", "", "",false);
				instance.dispose();
			}
		});
		setPanel(emptyCarPanel, emptyCarNum, emptyCarCard,emptyCarTag, emptyCarComBtn, emptyCarCelBtn);
		
		JPanel chargePanel = new JPanel(new GridLayout(5,1,10,10));
		emptyCarPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		MyTextField chargeNum = new MyTextField("通讯编号");
		MyTextField chargeCard = new MyTextField("停止卡号");
		MyTextField chargeTag = new MyTextField("功能标记");
		RoundButton chargeComBtn = new RoundButton("确认");
		emptyCarComBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				functionDialog.getSeclectFunction(3,emptyCarNum.getText(), emptyCarCard.getText(), emptyCarTag.getText(), true);
				instance.dispose();
			}
		});
		RoundButton chargeCelBtn = new RoundButton("取消");
		emptyCarCelBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				functionDialog.getSeclectFunction(0,"", "", "",false);
				instance.dispose();
			}
		});
		setPanel(chargePanel, chargeNum, chargeCard,chargeTag, chargeComBtn, chargeCelBtn);
		
		
		
		JPanel addStringPanel = new JPanel(new GridLayout(4,1,10,10));
		addStringPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		MyTextField addStringNum = new MyTextField("标记内容");
		addStringNum.setFont(new Font("宋体", Font.BOLD, 30));
		RoundButton addStringComBtn = new RoundButton("确认");
		addStringComBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				functionDialog.getSeclectFunction(4, addStringNum.getText(), " "," ", true);
				instance.dispose();
			}
		});
		RoundButton addStringCelBtn = new RoundButton("取消");
		addStringCelBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				functionDialog.getSeclectFunction(0,"", "", "",false);
				instance.dispose();
			}
		});
		setPanel(addStringPanel, addStringNum,  new MyTextField(" "), new MyTextField(" "), addStringComBtn, addStringCelBtn);
		
		
		
		
		if(isNotTag){
			shipmentBtn.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					instance.getContentPane().removeAll();
					instance.getContentPane().add(shipmentPanel);
					instance.validate();
				}
			});
					
			unloadingBtn.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					instance.getContentPane().removeAll();
					instance.getContentPane().add(unloadingPanel);
					instance.validate();
				}
			});
			
			emptyCarBtn.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					instance.getContentPane().removeAll();
					instance.getContentPane().add(emptyCarPanel);
					instance.validate();
				}
			});
			
			chargeBtn.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					instance.getContentPane().removeAll();
					instance.getContentPane().add(chargePanel);
					instance.validate();
				}
			});
			
			addStringBtn.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					instance.getContentPane().removeAll();
					instance.getContentPane().add(addStringPanel);
					instance.validate();
				}
			});
		}else{
			addStringBtn.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					instance.getContentPane().removeAll();
					instance.getContentPane().add(addStringPanel);
					instance.validate();
				}
			});
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
	
	public void setPanel(JPanel panel, MyTextField num, MyTextField card, MyTextField tag, RoundButton comBtn, RoundButton celBtn){
		panel.add(num);
		panel.add(card);
		panel.add(tag);
		panel.add(comBtn);
		panel.add(celBtn);
	}
}
