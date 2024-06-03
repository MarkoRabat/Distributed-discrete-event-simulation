package server;
import java.util.Dictionary;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class FailedJobSlave extends Thread {

	private int key;
	private Dictionary<Integer,JobAccount> jobAccount = null;
	private ReentrantReadWriteLock rwLockJobAccount = null;
	
	public FailedJobSlave (int key,
		Dictionary<Integer,JobAccount> jobAccount,
		ReentrantReadWriteLock rwLockJobAccount
	) {
		this.key = key;
		this.jobAccount = jobAccount;
		this.rwLockJobAccount = rwLockJobAccount;
	}
	
	@Override
	public void run() {
		AbortedJobSlave myJob = new AbortedJobSlave(this.key, this.jobAccount, this.rwLockJobAccount);
		myJob.start();
		try { myJob.join(); } catch (InterruptedException e) { e.printStackTrace(); return; }
		
		String components = null;
		String connections = null;
		String simulationType = null;
		int logicalEndTime = -1;
		String userIp;
		String jobName;
		
		rwLockJobAccount.writeLock().lock();

		JobAccount myJobAcc = jobAccount.get(key);
		
			if (!myJobAcc.status.equals("Failed")) {
				rwLockJobAccount.writeLock().unlock(); return; }
		
			myJobAcc.status = "__Failed";
			myJobAcc.execIps = null;
			myJobAcc.execPorts = null;
			components = myJobAcc.components;
			myJobAcc.components = null;
			connections = myJobAcc.connections;
			myJobAcc.connections = null;
			simulationType = myJobAcc.requestedSimType;
			logicalEndTime = myJobAcc.logicalEndTime;
			userIp = myJobAcc.ip;
			jobName = myJobAcc.name;

		rwLockJobAccount.writeLock().unlock();

		String[] commands = new String[] { "0", "1", "2", components,
			"4", connections, "6", simulationType, "8", "" + logicalEndTime, "JobName", jobName };
		
		HandleUserCommands.startJob(commands, userIp, this.jobAccount, this.rwLockJobAccount);

	}
	
	
	
	
	

}



	
	