package server;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class WorkerAccount {

	private static int nextWorkerId = 0;
	public int workerId;
	public String timeStamp = null;
	public String ip = null;
	public int port = -1;
	public Boolean tasked = null;
	int availThreads = -1;
	
	public WorkerAccount(String ip, int port, int availThreads) {
		this.workerId = nextWorkerId++;
		this.timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
			.format(Calendar.getInstance().getTime());
		this.ip = ip;
		this.port = port;
		this.tasked = false;
		this.availThreads = availThreads;
	}
	
	public static int getNextWorkerId() { return nextWorkerId; }
	
	@Override
    public String toString() {
        return "{id: " + workerId
        	+ ", timeStamp: " + timeStamp
        	+ ", ip: " + ip
        	+ ", port: " + port
        	+ ", avail_threads" + availThreads
        	+ ", tasked: " + tasked
        	+ "}";
    }

}
