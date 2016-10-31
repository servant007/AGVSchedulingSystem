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
	public  void getInstance(FunctionDialog instance, boolean isNotTag){
		
		JPanel shipmentPanel = new JPanel(new GridLayout(4,1,10,10));
		shipmentPanel.setBorder(BorderFactory.createEtchedBorder());
		JTextField shipmentNum = new JTextField();
		shipmentNum.setFont(new Font("����", Font.BOLD, 30));
		RoundButton shipmentComBtn = new RoundButton("ȷ��");
		shipmentComBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				functionDialog.getSeclectFunction(1, shipmentNum.getText(), true);
				instance.dispose();
			}
		});
		RoundButton shipmentCelBtn = new RoundButton("ȡ��");
		shipmentCelBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				functionDialog.getSeclectFunction(0, "", false);
				instance.dispose();
			}
		});
		setPanel(shipmentPanel, " ͨѶ���:", shipmentNum, shipmentComBtn, shipmentCelBtn);
		
		JPanel unloadingPanel = new JPanel(new GridLayout(4,1,10,10));
		unloadingPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		JTextField unloadingNum = new JTextField();
		unloadingNum.setFont(new Font("����", Font.BOLD, 30));
		RoundButton unloadingComBtn = new RoundButton("ȷ��");
		unloadingComBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				functionDialog.getSeclectFunction(2,unloadingNum.getText(),true);
				instance.dispose();
			}
		});
		RoundButton unloadingCelBtn = new RoundButton("ȡ��");
		unloadingCelBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				functionDialog.getSeclectFunction(0,"", false);
				instance.dispose();
			}
		});
		setPanel(unloadingPanel, " ͨѶ��ţ�", unloadingNum, unloadingComBtn, unloadingCelBtn);
		
		JPanel emptyCarPanel = new JPanel(new GridLayout(4,1,10,10));
		emptyCarPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		JTextField emptyCarNum = new JTextField();
		emptyCarNum.setFont(new Font("����", Font.BOLD, 30));
		RoundButton emptyCarComBtn = new RoundButton("ȷ��");
		emptyCarComBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				functionDialog.getSeclectFunction(3,emptyCarNum.getText(), true);
				instance.dispose();
			}
		});
		RoundButton emptyCarCelBtn = new RoundButton("ȡ��");
		emptyCarCelBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				functionDialog.getSeclectFunction(0,"", false);
				instance.dispose();
			}
		});
		setPanel(emptyCarPanel, " ͨѶ��ţ�", emptyCarNum, emptyCarComBtn, emptyCarCelBtn);
		
		JPanel addStringPanel = new JPanel(new GridLayout(4,1,10,10));
		addStringPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		JTextField addStringNum = new JTextField();
		addStringNum.setFont(new Font("����", Font.BOLD, 30));
		RoundButton addStringComBtn = new RoundButton("ȷ��");
		addStringComBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				functionDialog.getSeclectFunction(4, addStringNum.getText(), true);
				instance.dispose();
			}
		});
		RoundButton addStringCelBtn = new RoundButton("ȡ��");
		addStringCelBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				functionDialog.getSeclectFunction(0,"", false);
				instance.dispose();
			}
		});
		setPanel(addStringPanel," ��ǩ���ݣ�", addStringNum, addStringComBtn, addStringCelBtn);
		
		
		
		
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
		Dimension dialogSize = new Dimension(2*screenSize.width/20, 4*screenSize.height/20);
		this.setSize(dialogSize);
		if(screenSize.width - position.x < dialogSize.width)
			this.setLocation(position.x - dialogSize.width , position.y);
		else if(screenSize.height - position.y < dialogSize.height)
			this.setLocation(position.x, position.y - dialogSize.height);
		else
			this.setLocation(position.x, position.y);
		shipmentBtn = new RoundButton("������");
		//shipmentBtn.setBackground(Color.gray);
		unloadingBtn = new RoundButton("ж����");
		//unloadingBtn.setBackground(Color.gray);
		emptyCarBtn = new RoundButton("�ճ���");
		//emptyCarBtn.setBackground(Color.gray);
		addStringBtn = new RoundButton("��ӱ�ǩ");
		//addStringBtn.setBackground(Color.gray);
		
		JPanel funcSeclectPanel = new JPanel(new GridLayout(4,1,10,10));
		funcSeclectPanel.add(shipmentBtn);
		funcSeclectPanel.add(unloadingBtn);
		funcSeclectPanel.add(emptyCarBtn);
		funcSeclectPanel.add(addStringBtn);
		funcSeclectPanel.setBorder(BorderFactory.createEtchedBorder());
		this.getContentPane().add(funcSeclectPanel);
		
		this.setUndecorated(true);
		
		this.setVisible(true);	
	}
	
	public void setOnActionListener(FunctionDialogListener listener){
		this.functionDialog = listener;
	}
	
	public void setPanel(JPanel panel, String str, JTextField num, RoundButton comBtn, RoundButton celBtn){
		//panel.add(new JPanel());
		//panel.add(new JPanel());
		JLabel label = new JLabel(str);
		label.setFont(new Font("����", Font.BOLD, 30));
		panel.add(label);
		panel.add(num);
		//panel.add(new JPanel());
		//panel.add(new JPanel());
		panel.add(comBtn);
		panel.add(celBtn);
	}
}
