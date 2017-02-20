package schedulingSystem.component;

public class AGVComVar {
	//ReceiveAGVMessage
			public boolean requartSend;
			public boolean AGVWiat;
			public ConflictEdge conflictEdgeRockwell;
			public ConflictEdge conflictEdgeRockwell1;
			public ConflictEdge conflictEdgeRoute;
			public boolean sendRouteToAGV;
			public boolean sendStopToAGV;
			public boolean sendShipmentToAGV;
			public boolean sendUnloadingToAGV;
			public boolean sendChargeAGV;
			public boolean sendStartingToAGV;
			public boolean sendAccessToAGV;
			public boolean sendOffDutyToAGV;
			public boolean sendOnDutyToAGV;
			public boolean firstSendOnDutyToAGV;
			public String routeString;
			public String stopString;
			public String shipmentString;
			public String unloadingString;
			public String chargeString;
			public String startString;
			public String accessString;
			public String offDutyString;
			public String onDutyString;
			public boolean firstWait;
			public long sendChargeTime;
			public ConflictNode conflictNode;
			public AGVComVar(){
				this.firstSendOnDutyToAGV = true;
			}
}
