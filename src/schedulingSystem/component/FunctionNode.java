package schedulingSystem.component;

public class FunctionNode {
	public int nodeNum;
	public int communicationNum;
	public Node position;
	public String tag;
	public FunctionNode(int nodeNum, int communicationNum){
		this.nodeNum = nodeNum;
		this.communicationNum = communicationNum;
	}
	
	public FunctionNode(Node position, String tag){
		this.position = position;
		this.tag = tag;
	}
}
