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
		// create AbortedJobSlave
		
		rwLockJobAccount.writeLock().lock();
	
		// update this job to Ready
		// update workers to null

		rwLockJobAccount.writeLock().unlock();
	}
	
	
	
	
	

}



	
	