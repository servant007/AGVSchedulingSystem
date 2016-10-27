package schedulingSystem.component;
public class Node {
	public int num;
	public int x;
	public int y;
	public boolean newNode = true;
	public Node(){}
	
	public Node(int x, int y, int num){
		this.x = x;
		this.y = y;
		this.num = num;
	}
	
	public void setOldNode(){
		newNode = false;
	}
	
	public void setNewNode(){
		newNode = true;
	}
	
	public boolean questIsNewNode(){
		return newNode;
	}
}
