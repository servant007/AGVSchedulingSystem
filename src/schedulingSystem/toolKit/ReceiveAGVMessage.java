package schedulingSystem.toolKit;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import schedulingSystem.component.AGVCar;
import schedulingSystem.component.ConflictDetection;
import schedulingSystem.component.Edge;
import schedulingSystem.component.Graph;
import schedulingSystem.gui.SchedulingGui;

public class ReceiveAGVMessage implements Runnable{
	private static Logger logger = Logger.getLogger(SchedulingGui.class.getName());
	private InputStream inputStream;
	private OutputStream outputStream;
	private Socket socket;
	private long lastCommunicationTime;
	private long reciveDelayTime = 7000;
	private MyToolKit myToolKit;
	private ArrayList<AGVCar> AGVArray;
	private int noOfAGV;
	private int lastCard;
	private boolean oldRunnable;
	private Graph graph;
	
	public ReceiveAGVMessage(Socket socket, ArrayList<AGVCar> AGVArray, Graph graph){
		myToolKit = new MyToolKit();
		this.graph = graph;
		this.AGVArray = AGVArray;
		System.out.println("socket connect:"+socket.toString());
		this.socket = socket;
		try{
			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e);
		}
		lastCommunicationTime = System.currentTimeMillis();
	}
	

	public void SendMessage(String message){
		try{
			outputStream.write(myToolKit.HexString2Bytes(message));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		System.out.println(message);
		
	}
	
	public void run(){
		while(true){
			if(System.currentTimeMillis() - lastCommunicationTime < reciveDelayTime){//			
				try{
					if(inputStream.available() > 0){
						lastCommunicationTime = System.currentTimeMillis();
						byte[] buff = new byte[5];
						inputStream.read(buff);
						String message = myToolKit.printHexString(buff);
						System.out.println(message);
						if(message.startsWith("AA")&&message.endsWith("BB")){
							noOfAGV = Integer.parseInt(message.substring(2, 4), 16);
							if(!oldRunnable){
								AGVArray.get(noOfAGV-1).setRunnabel(this);
								oldRunnable = true;
							}
								
							if(!message.substring(4, 8).equals("BABA")){
								AGVArray.get(noOfAGV-1).setLastCommunicationTime(System.currentTimeMillis());
								int NOOfCard = Integer.parseInt(message.substring(4, 6), 16);
								
								if(NOOfCard != lastCard){
									System.out.println(noOfAGV + "AGVreceive card number:"+NOOfCard);
									AGVArray.get(noOfAGV-1).setLastCard(NOOfCard);
									int electricity = Integer.parseInt(message.substring(6, 8), 16);
									AGVArray.get(noOfAGV-1).setElectricity(electricity);
									lastCard = NOOfCard;
								}
							}else{
								AGVArray.get(noOfAGV-1).setLastCommunicationTime(System.currentTimeMillis());
								//outputStream.write(myToolKit.HexString2Bytes("AAC0FFEEBB"));
							}
						}
						
						if(message.startsWith("CC")){
							noOfAGV = Integer.parseInt(message.substring(2, 4), 16);
							int state = Integer.parseInt(message.substring(4, 6), 16);
							AGVArray.get(noOfAGV-1).setAGVState(state);
						}
						
					}else {
						Thread.sleep(10);
					}					
				}catch(Exception e){
					e.printStackTrace();
					logger.error(e);
				}
			}else{
				try{
					if(inputStream != null)
						inputStream.close();
					if(outputStream != null)
						outputStream.close();
					if(socket != null)
						socket.close();
				}catch(Exception e){
					e.printStackTrace();
					logger.error(e);
				}
				break;//ÍË³öwhileÑ­»·
			}				
		}//while
	}
}