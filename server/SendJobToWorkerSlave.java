package server;

import java.net.ConnectException;

import commClient.CommClient;

public class SendJobToWorkerSlave extends Thread {

	int jobId = -1, subJobId = -1, wport = -1;
	String wip = null;
	String components = null;
	String connections = null;
	
	public SendJobToWorkerSlave(
		int jobId, int subJobId, String wip, int wport,
		String components, String connections
	) {
		this.jobId = jobId;
		this.subJobId = subJobId;
		this.wip = wip;
		this.wport = wport;
		this.components = components;
		this.connections = connections;
	}
		
	@Override
	public void run() {
		String[] params = new String[] {"Server", "CreateJob", "Components", "File"};
		params = CommClient.putFileInRequestParams(params, components);
		params = CommClient.mergeParams(params, new String[] { "Connections", "File"});
		params = CommClient.putFileInRequestParams(params, connections);
		params = CommClient.mergeParams(params, new String[] {
			"SimulationType", "SomeSimType", "logicalEndTime", "10"});
		params = CommClient.mergeParams(params, new String[] {
			"jobId", "" + this.jobId, "subJobId", "" + this.subJobId});
		
		String response = null;
		try { response = CommClient.makeUserRequest(wip, wport, params); }
		catch (ConnectException e) { System.err.println(
			"\tWorker {ip: " + wip + "," + wport + "} not responding to start job request."); }
		String[] data = CommClient.processResponse(response);
		//for (int i = 0; i < data.length; ++i) System.out.print(" " + data[i]);
		//System.out.println();

	}

}
