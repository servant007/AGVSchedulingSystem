package schedulingSystem.toolKit;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JLabel;

import org.apache.log4j.Logger;

import schedulingSystem.component.AGVCar;
import schedulingSystem.component.AGVCar.State;
import schedulingSystem.component.Dijkstra;
import schedulingSystem.component.Graph;
import schedulingSystem.component.Path;
import schedulingSystem.gui.SchedulingGui;

public class ReceiveStationMessage implements Runnable{
	private static Logger logger = Logger.getLogger(SchedulingGui.class.getName());
	private InputStream inputStream;
	private OutputStream outputStream;
	private Socket socket;
	private MyToolKit myToolKit;
	private Graph graph;
	private ArrayList<AGVCar> AGVArray;
	private Dijkstra dijkstra;
	private JLabel stateLabel;
	private int sec;
	
	public ReceiveStationMessage(Socket socket, Graph graph, ArrayList<AGVCar> AGVArray, Dijkstra dijkstra, JLabel stateLabel, int sec){
		myToolKit = new MyToolKit();
		this.graph = graph;
		this.AGVArray = AGVArray;
		this.dijkstra = dijkstra;
		this.stateLabel = stateLabel;
		this.sec = sec;
		this.socket = socket;
		System.out.println("socket station:"+socket.toString());
		try{
			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();
			outputStream.write(myToolKit.HexString2Bytes("AAC0FFEEBB"));
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e);
		}

	}
	
	public void run(){
		System.out.println("receive station message:  run");
		while(true){
			try{
				if(inputStream.available() > 0){
					System.out.println("receive station message:  available > 0");
					byte[] buff = new byte[7];
					inputStream.read(buff);
					String message = myToolKit.printHexString(buff);
					if(message.startsWith("EE") && message.endsWith("FF")){
						System.out.println("receive station message:" + message );
						int firstDestination = Integer.parseInt(message.substring(2, 4), 16);
						int secondTrigger = Integer.parseInt(message.substring(4, 6), 16);
						int secondDestination = Integer.parseInt(message.substring(6, 8), 16);
						int thirdTrigger = Integer.parseInt(message.substring(8, 10), 16);
						int thirdDestination = Integer.parseInt(message.substring(10, 12), 16);
						ArrayList<State> triggerArray = new ArrayList<State>();
						triggerArray.add(State.NULL);
						triggerArray.add(State.values()[secondTrigger]);
						triggerArray.add(State.values()[thirdTrigger]);
						ArrayList<Integer> destinationArray = new ArrayList<Integer>();
						destinationArray.add(firstDestination);
						destinationArray.add(secondDestination);
						destinationArray.add(thirdDestination);
						if((graph.getFunctionNodeArray().get(sec).callAGVNum = sendingWhichAGV(firstDestination))!=0 
								&& !graph.getFunctionNodeArray().get(sec).clicked){
							graph.getFunctionNodeArray().get(sec).clicked = true;
							AGVArray.get(graph.getFunctionNodeArray().get(sec).callAGVNum - 1).setDestinationNode(triggerArray, destinationArray);
						}
							
					}
					/*
					if(inputStream != null)
						inputStream.close();
					if(outputStream != null)
						outputStream.close();
					if(socket != null)
						socket.close();
					break;*/
				}else{
					Thread.sleep(20);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	private int sendingWhichAGV(int endNodeNum) {
		ArrayList<Path> pathArray = new ArrayList<Path>();
		for(int i = 0; i < AGVArray.size(); i++){			
			if(AGVArray.get(i).getStartEdge().endNode.num!=0 && AGVArray.get(i).isAlived() 
					&& !AGVArray.get(i).isOnMission() && !AGVArray.get(i).getFixRoute()){
				pathArray.add(dijkstra.findRoute(AGVArray.get(i).getStartEdge(), endNodeNum));
				pathArray.get(pathArray.size()-1).setNumOfAGV(i+1);
			}
		}
		
		if(pathArray.size() != 0){
			int minDis = 65535;
			int minIndex = 0;
			for(int i = 0; i < pathArray.size(); i++){
				if(pathArray.get(i).getRealDis() < minDis && pathArray.get(i).getRoute().size() > 2){
					minDis = pathArray.get(i).getRealDis();
					minIndex = i;
				}
			}
			return pathArray.get(minIndex).getNumOfAGV();
		}else{
			return 0;
		}
	}
	
	
	
	private int sendingWhichAGV222(int middleNodeNum, int endNodeNum) {
		int returnAGVNum = 0;
		ArrayList<Integer> noStartNode = new ArrayList<Integer>();
		ArrayList<Integer> isNotAlived = new ArrayList<Integer>();
		ArrayList<Integer> isOnMission = new ArrayList<Integer>();
		ArrayList<Path> pathArray = new ArrayList<Path>();
		for(int i = 0; i < AGVArray.size(); i++){
			
			if(!AGVArray.get(i).isAlived()){
				isNotAlived.add(i+1);
			}else{
				if(AGVArray.get(i).getStartEdge().endNode.num == 0)
					noStartNode.add(i+1);
			}
				
			if(AGVArray.get(i).isOnMission())
				isOnMission.add(i+1);
			
			if(AGVArray.get(i).getStartEdge().endNode.num!=0 && AGVArray.get(i).isAlived() && !AGVArray.get(i).isOnMission()){
				pathArray.add(dijkstra.findRoute(AGVArray.get(i).getStartEdge(), middleNodeNum));
				pathArray.get(pathArray.size()-1).setNumOfAGV(i+1);
			}
		}
		
		if(pathArray.size() != 0){
			int minDis = 65535;
			int minIndex = 0;
			for(int i = 0; i < pathArray.size(); i++){
				if(pathArray.get(i).getRealDis() < minDis && pathArray.get(i).getRoute().size() > 2){
					minDis = pathArray.get(i).getRealDis();
					minIndex = i;
				}
			}
			System.out.println("result:"+pathArray.get(minIndex).getRoute());
			returnAGVNum = pathArray.get(minIndex).getNumOfAGV();
			AGVCar agvCar= AGVArray.get(pathArray.get(minIndex).getNumOfAGV()-1);
			agvCar.getRunnable().SendMessage(myToolKit.routeToOrientation(graph, pathArray.get(minIndex).getRoute(), agvCar));
			//agvCar.setDestinationNode(endNodeNum, true);
		}else{
			stateLabel.setText("没有AGV准备好");
			logger.debug("没有AGV准备好");
		}
		StringBuffer stateString = new StringBuffer();
		if(noStartNode.size()!=0){
			for(int i = 0; i < noStartNode.size(); i++){
				stateString.append(noStartNode.get(i));
				stateString.append(",");
			}
			stateString.append("：起始点错误   //");
		}
		
		if(isNotAlived.size()!=0){
			for(int i = 0; i < isNotAlived.size(); i++){
				stateString.append(isNotAlived.get(i));
				stateString.append(",");
			}
			stateString.append("：失去连接   //");
		}
		
		if(isOnMission.size()!=0){
			for(int i = 0; i < isOnMission.size(); i++){
				stateString.append(isOnMission.get(i));
				stateString.append(",");
			}
			stateString.append("：在执行任务中");
		}
		stateLabel.setText(stateString.toString());
		
		return returnAGVNum;
	}
}