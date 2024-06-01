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
	public String executorIp = null;
	public String requestedSimType = null;
	public int logicalEndTime = -1;
	public String components = null;
	public String connections = null;
	public int componentsFs = -1;
	public int connectionsFs = -1;
	
	public JobAccount(
		String ip, String requestedSimType, int logicalEndTime,
		String components, String connections
	) {
		this.jobId = nextJobId++;
		this.startedAt = new SimpleDateFormat("yyyyMMddHHmmss")
			.format(Calendar.getInstance().getTime());
		this.ip = ip;
		this.status = "Ready";
		this.requestedSimType = requestedSimType;
		this.logicalEndTime = logicalEndTime;
		this.components = components;
		this.connections = connections;
		this.componentsFs = components.length();
		this.connectionsFs = connections.length();
	}

	public static int getNextJobId() { return nextJobId; }
	
	@Override
	public String toString() {
		return "{id: " + jobId
			+ ", startedAt: " + startedAt
			+ ", finishedAt: " + finishedAt
			+ ", ip: " + ip
			+ ", status: " + status
			+ ", execIp: " + executorIp
			+ ", reqSimtype: " + requestedSimType
			+ ", lendTime: " + logicalEndTime
			+ ", compFs: " + componentsFs + "B"
			+ ", connFs: " + connectionsFs + "B"
			+ "}";
	}

}
