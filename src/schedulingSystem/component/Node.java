package schedulingSystem.component;
public class Node {
	public int num;
	public int x;
	public int y;
	public boolean newNode = true;
	public boolean functionNode = false;
	public String tag;
	public int orientation; 
	public Node(){}
	
	public Node(int x, int y, int num){
		this.x = x;
		this.y = y;
		this.num = num;
	}
	
	public Node(int num, int orientation){
		this.num = num;
		this.orientation = orientation;
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
