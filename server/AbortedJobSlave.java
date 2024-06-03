package server;

import java.net.ConnectException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Dictionary;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import commClient.CommClient;

public class AbortedJobSlave extends Thread {
	
	
	
	private int key;
	private Dictionary<Integer,JobAccount> jobAccounts = null;
	private ReentrantReadWriteLock rwLockJobAccount = null;
	
	public AbortedJobSlave (int key,
		Dictionary<Integer,JobAccount> jobAccounts,
		ReentrantReadWriteLock rwLockJobAccount
	) {
		this.key = key;
		this.jobAccounts = jobAccounts;
		this.rwLockJobAccount = rwLockJobAccount;
	}
	
	@Override
	public void run() {
		
		String[] execIps = null;
		int[] execPorts = null;
		int jobId = -1;

		rwLockJobAccount.writeLock().lock();
		
		JobAccount myJobAccount = jobAccounts.get(this.key);
		
		// many add here !ConfError
		// add ConfError
		if (!myJobAccount.status.equals("Aborted")
			&& !myJobAccount.status.equals("Failed"))
		{
			rwLockJobAccount.writeLock().unlock();
			return;
		}
		
		if (myJobAccount.status.equals("Aborted"))
			myJobAccount.status = "__Abotred";
		
		execIps = myJobAccount.execIps;
		execPorts = myJobAccount.execPorts;
		jobId = myJobAccount.jobId;
	
		rwLockJobAccount.writeLock().unlock();

		if (execIps == null) return;

		for (int i = 0; i < execIps.length; ++i) {
			// create thread for each worker to alert it that this job
			// has been aborted
			
			new Thread(new Runnable() {
				private String execIp;
				private int execPort;
				private int jobId;
				
				public Runnable init(String execIp, int execPort, int jobId) {
					this.execIp = execIp;
					this.execPort = execPort;
					this.jobId = jobId;
					return this;
				}
				
				@Override
				public void run() {

					String[] params = {"Server", "AbortJob", "JobId", "" + jobId};
					String response = null;
					try { 
						response = CommClient.makeUserRequest(execIp, execPort, params);
						String[] data = CommClient.processResponse(response);
					}
					catch (ConnectException e) { System.err.println(
						"\tWorker {ip: " + execIp + "," + execPort + "} not responding to abort job request."); }
					catch (Error e) { e.printStackTrace(); }
					
				}
				
			}.init(execIps[i], execPorts[i], jobId)).start();
			
		}

	}
	
}
