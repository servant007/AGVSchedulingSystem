package schedulingSystem.component;

public class FunctionNode {
	public enum FunctionNodeEnum{SHIPMENT, UNLOADING, CHARGE, EMPTYCAR, TAG}
	public FunctionNodeEnum function;
	public int nodeNum;
	public int communicationNum;
	public String tag;
	public boolean clicked;
	public int callAGVNum;
	public String ip;
	public int x;
	public int y;
	
	public FunctionNode(FunctionNodeEnum function, int nodeNum, String ip, int communicationNum, String tag){
		this.function = function;
		this.nodeNum = nodeNum;
		this.ip = ip;
		this.communicationNum = communicationNum;
		this.tag = tag;
	}
	
	public FunctionNode(FunctionNodeEnum function, int x, int y, String tag){
		this.x = x;
		this.y = y;
		this.tag = tag;
	}
}
