package server;

import java.util.Dictionary;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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
		// update workers of this job that it has been aborted
		for (int i = 0; i < execIps.length; ++i) {
			// create thread for each worker to alert it that this job
			// has been aborted
		}

	}
	
}
