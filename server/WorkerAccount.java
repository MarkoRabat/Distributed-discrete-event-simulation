package server;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class WorkerAccount {

	private static int currWorkerId = 0;
	public int workerId;
	public String timeStamp = null;
	public String ip = null;
	public String port = null;
	public Boolean tasked = null;
	
	public WorkerAccount(String ip, String port) {
		this.workerId = currWorkerId++;
		this.timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
			.format(Calendar.getInstance().getTime());
		this.ip = ip;
		this.port = port;
		this.tasked = false;
	}

}
