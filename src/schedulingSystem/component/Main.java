package schedulingSystem.component;

import java.awt.AlphaComposite;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JFrame;

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
		this.setIconImage(icon);
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
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*
		new Thread(){
			public void run(){
				try{
					SplashScreen splash = SplashScreen.getSplashScreen();
					//Graphics2D g = splash.createGraphics();
					//g.setComposite(AlphaComposite.Clear);
					splash.setImageURL(Main.class.getResource("/logo.png"));
					splash.update();
					Thread.sleep(1000);

				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}.start();*/
		
		Main main = new Main();
	}

}
