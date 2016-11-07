package schedulingSystem.component;

public class FunctionNode {
	public int nodeNum;
	public int cardNum;
	public int communicationNum;
	public Node position;
	public String tag;
	public boolean clicked;
	public int callAGVNum;
	public FunctionNode(int nodeNum, int cardNum, int communicationNum, String tag){
		this.nodeNum = nodeNum;
		this.cardNum = cardNum;
		this.communicationNum = communicationNum;
		this.tag = tag;
	}
	
	public FunctionNode(Node position, String tag){
		this.position = position;
		this.tag = tag;
	}
}
