package schedulingSystem.component;

import java.awt.Frame;

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
	public Main(){
		super("AGV调度系统");
		myToolKit = new MyToolKit();
		graph = new Graph();
		graph = myToolKit.importNewGraph("C:/Users/agv/Documents/testGraph.xls");
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
		Main main = new Main();
	}

}
