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
		AbortedJobSlave myJob = new AbortedJobSlave(key, jobAccount, rwLockJobAccount);
		myJob.start();
		try { myJob.join(); } catch (InterruptedException e) { e.printStackTrace(); }
		
		rwLockJobAccount.writeLock().lock();

		JobAccount myJobAcc = jobAccount.get(key);
		myJobAcc.jobId = JobAccount.getNextJobId();
		JobAccount.intJobId();
		myJobAcc.status = "Ready";
		myJobAcc.execIps = null;
		myJobAcc.execPorts = null;

		rwLockJobAccount.writeLock().unlock();
	}
	
	
	
	
	

}



	
	