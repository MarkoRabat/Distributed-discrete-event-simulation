package server;

import java.util.Dictionary;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AbortedJobSlave extends Thread {
	
	
	
	private int key;
	private Dictionary<Integer,JobAccount> jobAccount = null;
	private ReentrantReadWriteLock rwLockJobAccount = null;
	
	public AbortedJobSlave (int key,
		Dictionary<Integer,JobAccount> jobAccount,
		ReentrantReadWriteLock rwLockJobAccount
	) {
		this.key = key;
		this.jobAccount = jobAccount;
		this.rwLockJobAccount = rwLockJobAccount;
	}
	
	@Override
	public void run() {

		rwLockJobAccount.readLock().lock();
	
		// update workers of this job that it is aborted

		rwLockJobAccount.readLock().unlock();
	}
	
}
