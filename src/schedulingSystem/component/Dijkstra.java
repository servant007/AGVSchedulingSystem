package schedulingSystem.component;

import java.util.ArrayList;

public class Dijkstra {
	private final static int MAXINT = 655535;
	private int[][] graphArray; 
	private Graph graph;
	private int size;
	private ArrayList<Path> sArray;
	private ArrayList<Path> uArray;
	public Dijkstra(Graph graph){
		this.graph = graph;
		size = graph.getNodeSize();
		graphArray = new int[size][size];
		for(int i = 0; i < size; i++){
			for(int j = 0; j < size; j++){
				graphArray[i][j] = MAXINT;
			}
		}
		for(int i = 0; i < graph.getEdgeSize(); i++){
			graphArray[graph.getEdge(i).startNode.num-1][graph.getEdge(i).endNode.num-1]=graph.getEdge(i).realDis;
			//if(graph.getEdge(i).twoWay){
			//	graphArray[graph.getEdge(i).startNode.num-1][graph.getEdge(i).endNode.num-1]=graph.getEdge(i).realDis;
				//graphArray[graph.getEdge(i).endNode.num-1][graph.getEdge(i).startNode.num-1]=graph.getEdge(i).realDis;
			//}else{
				//graphArray[graph.getEdge(i).startNode.num-1][graph.getEdge(i).endNode.num-1]=graph.getEdge(i).realDis;
			//}
		}	
	}//end init
	
	public Path findRoute(int startNode, int endNode){
		Path returnPath = new Path(startNode, endNode);
		boolean adjoin = false;
		sArray = new ArrayList<Path>();
		sArray.add(new Path(startNode, startNode));
		uArray = new ArrayList<Path>();
		for(int i = 0; i < size; i++){//初始化
			uArray.add(new Path(startNode, i + 1));
			for(int j = 0; j < graph.getEdgeSize(); j++){
				if((graph.getEdge(j).startNode.num == startNode && graph.getEdge(j).endNode.num == (i+1))){//|| (graph.getEdge(j).endNode.num == startNode && graph.getEdge(j).startNode.num == (i+1) && graph.getEdge(j).twoWay)
					uArray.get(i).setRealDis(graph.getEdge(j).realDis);
					uArray.get(i).addRouteNode(i+1);
					adjoin = true;
				}
			}
			if(!adjoin)
				uArray.get(i).setRealDis(MAXINT);
			adjoin = false;
		}

		uArray.get(startNode - 1).setRemove();
		int removedCount = 1;
		
		while(uArray.size() != removedCount){//
			int tempMin = MAXINT;
			int indexMin = 0;
			for(int i = 0; i < uArray.size(); i++){//取u中权值最小的点放进s中
				if(!uArray.get(i).getRemove()){
					if(uArray.get(i).getRealDis() < tempMin){
						tempMin = uArray.get(i).getRealDis();
						indexMin = i;
					}
				}
			}
			
			sArray.add(uArray.get(indexMin));
			uArray.get(indexMin).setRemove();
			removedCount++;
			
			int tempStart = sArray.get(sArray.size()-1).getEndNode();
			for(int i = 0; i < size; i++){
				for(int j = 0; j < graph.getEdgeSize(); j++){
					if((graph.getEdge(j).startNode.num == tempStart && graph.getEdge(j).endNode.num == (i+1))){//|| (graph.getEdge(j).endNode.num == tempStart && graph.getEdge(j).startNode.num == (i+1) && graph.getEdge(j).twoWay)
						if(graph.getEdge(j).realDis + sArray.get(sArray.size() - 1).getRealDis() < uArray.get(i).getRealDis()){
							uArray.get(i).setRealDis(graph.getEdge(j).realDis + sArray.get(sArray.size() - 1).getRealDis());
							uArray.get(i).newRoute(sArray.get(sArray.size()-1).getRoute());
							//System.out.println("route:"+uArray.get(i).getRoute());
							uArray.get(i).addRouteNode(i+1);
							
						}
					}
				}
			}	
		}//end while

		for(int i = 0; i < sArray.size(); i++){
			if(sArray.get(i).getEndNode() == endNode)
				returnPath = sArray.get(i);
		}
		return returnPath;
	}//end countPath
}
