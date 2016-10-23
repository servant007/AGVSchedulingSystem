package schedulingSystem.component;

import schedulingSystem.gui.GraphingGui;
import schedulingSystem.gui.SchedulingGui;
import schedulingSystem.toolKit.SetingGui;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SchedulingGui schedulingGui = new SchedulingGui();
		GraphingGui graphingGui = new GraphingGui();
		SetingGui setingGui = new SetingGui();
		schedulingGui.getGuiInstance(schedulingGui, setingGui, graphingGui);
		graphingGui.getGuiInstance(schedulingGui, setingGui, graphingGui);
		setingGui.getGuiInstance(schedulingGui, setingGui, graphingGui);
	}

}
