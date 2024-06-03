package server;

import java.net.ConnectException;

import commClient.CommClient;

public class SendJobToWorkerSlave extends Thread {

	int jobId = -1, subJobId = -1, wport = -1;
	String wip = null;
	String components = null;
	String connections = null;
	String jobName = null;
	String simType = null;
	int ltime = -1;
	
	public SendJobToWorkerSlave(
		int jobId, int subJobId, String wip, int wport,
		String components, String connections,
		String jobName, String simType, int ltime
	) {
		this.jobId = jobId;
		this.subJobId = subJobId;
		this.wip = wip;
		this.wport = wport;
		this.components = components;
		this.connections = connections;
		this.jobName = jobName;
		this.simType = simType;
		this.ltime = ltime;
	}
	
	@Override
	public void run() {
		String[] params = new String[] {"Server", "CreateJob", "Components", "File"};
		params = CommClient.putFileInRequestParams(params, components);
		params = CommClient.mergeParams(params, new String[] { "Connections", "File"});
		params = CommClient.putFileInRequestParams(params, connections);
		params = CommClient.mergeParams(params, new String[] {
			"SimulationType", simType, "logicalEndTime", "" + ltime});
		params = CommClient.mergeParams(params, new String[] {
			"jobId", "" + this.jobId, "subJobId", "" + this.subJobId,
			"JobName", jobName, "AbortJob", "0"});
		
		String response = null;
		try { 
			response = CommClient.makeUserRequest(wip, wport, params);
			String[] data = CommClient.processResponse(response);
		}
		catch (ConnectException e) { System.err.println(
			"\tWorker {ip: " + wip + "," + wport + "} not responding to start job request."); }
		catch (Error e) { e.printStackTrace(); }
	}

}
