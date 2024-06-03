package server;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class JobAccount {
	private static int nextJobId = 0;
	public int jobId;
	public String startedAt = null;
	public String finishedAt = null;
	public String ip = null;
	public String status = null;
	public String[] execIps = null;
	public int[] execPorts = null;
	public String requestedSimType = null;
	public int logicalEndTime = -1;
	public String components = null;
	public String connections = null;
	public int componentsFs = -1;
	public int connectionsFs = -1;
	public boolean returnedToUser = false;
	public String name = null;
	
	public JobAccount(
		String ip, String requestedSimType, int logicalEndTime,
		String components, String connections, String name, String status
	) {
		this.jobId = nextJobId++;
		this.startedAt = new SimpleDateFormat("yyyyMMddHHmmss")
			.format(Calendar.getInstance().getTime());
		this.ip = ip;
		this.status = status;
		this.requestedSimType = requestedSimType;
		this.logicalEndTime = logicalEndTime;
		this.components = components;
		this.connections = connections;
		this.componentsFs = components.length();
		this.connectionsFs = connections.length();
		this.name = name;
	}

	public static int getNextJobId() { return nextJobId; }
	public static void intJobId() { ++nextJobId; }
	
	@Override
	public String toString() {
		String repr = "{id: " + jobId
			+ ", jobName: " + name
			+ ", startedAt: " + startedAt
			+ ", finishedAt: " + finishedAt
			+ ", ip: " + ip
			+ ", status: " + status;
		
		repr += ", execIps: ";
		if (execIps == null) repr += execIps;
		else { repr += "{";
			for (int i = 0; i < execIps.length; repr += execIps[i++] + ", "); repr += "}";
		}

		repr += ", execPorts: ";
		if (execPorts == null) repr += execPorts;
		else { repr += "{";
			for (int i = 0; i < execPorts.length; repr += execPorts[i++] + ", "); repr += "}";
		}
		
		repr += ", reqSimtype: " + requestedSimType
			+ ", lendTime: " + logicalEndTime
			+ ", compFs: " + componentsFs + "B"
			+ ", connFs: " + connectionsFs + "B"
			+ "}";
		
		return repr;
	}

}
