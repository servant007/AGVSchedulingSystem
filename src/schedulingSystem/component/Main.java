package schedulingSystem.component;

import java.awt.AlphaComposite;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import schedulingSystem.gui.*;
import schedulingSystem.toolKit.MyToolKit;;

public class Main extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(Main.class.getName());
	private SchedulingGui schedulingGui;
	private SetingGui setingGui;
	private GraphingGui graphingGui;
	private Graph graph;
	private MyToolKit myToolKit;
	private Image icon;
	public Main(){
		super("AGV调度系统");
		Toolkit tool = Toolkit.getDefaultToolkit();
		icon = tool.createImage(getClass().getResource("/logo.png"));
		//this.setIconImage(icon);
		myToolKit = new MyToolKit();
		graph = new Graph();
		graph = myToolKit.importNewGraph("C:\\Users\\agv\\Documents\\testGraph.xls");
		graph.initIgnoreCard();
		schedulingGui = SchedulingGui.getInstance(graph);
		setingGui = SetingGui.getInstance(graph);
		graphingGui = GraphingGui.getInstance();
		graphingGui.getGuiInstance(Main.this, schedulingGui, setingGui, graphingGui);
		setingGui.getGuiInstance(Main.this, schedulingGui, setingGui, graphingGui);
		schedulingGui.getGuiInstance(Main.this, schedulingGui, setingGui, graphingGui);
		
		this.getContentPane().add(schedulingGui);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setExtendedState(Frame.MAXIMIZED_BOTH);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		
		this.addWindowListener(new WindowAdapter() {
	          @Override
	          public void windowClosing(WindowEvent e)
	          {
	             exit();
	          }
	    });
	}
	
	public void exit(){
		Object[] option = {"确认", "取消"};
		JOptionPane pane = new JOptionPane("确认关闭吗？", JOptionPane.QUESTION_MESSAGE, 
				JOptionPane.YES_NO_OPTION, null, option, option[1]);
		JDialog dialog = pane.createDialog(this, "警告");
		dialog.setVisible(true);
		Object result = pane.getValue();
		if(result == null || result == option[1]){
			setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		}else if(result == option[0]){
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
	}

	public static void main(String[] args) {
		new Main();
	}

}
