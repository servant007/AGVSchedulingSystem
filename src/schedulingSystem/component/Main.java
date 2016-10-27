package schedulingSystem.component;

import java.awt.Frame;

import javax.swing.JFrame;

import org.apache.log4j.Logger;
import schedulingSystem.gui.*;;

public class Main extends JFrame {
	private SchedulingGui schedulingGui;
	private SetingGui setingGui;
	private GraphingGui graphingGui;
	public Main(){
		super("AGV调度系统");
		schedulingGui = SchedulingGui.getInstance();
		setingGui = SetingGui.getInstance();
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
	private static Logger logger = Logger.getLogger(Main.class.getName());
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Main main = new Main();
	}

}
