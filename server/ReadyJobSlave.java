package server;

import java.util.Dictionary;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadyJobSlave extends Thread {
	
	private int key;
	private Dictionary<Integer, WorkerAccount> workerAccounts = null;
	private ReentrantReadWriteLock rwLockWorkerAccounts = null;
	private Dictionary<Integer,JobAccount> jobAccount = null;
	private ReentrantReadWriteLock rwLockJobAccount = null;
	
	public ReadyJobSlave (int key,
		Dictionary<Integer, WorkerAccount> workerAccounts,
		ReentrantReadWriteLock rwLockWorkerAccounts,
		Dictionary<Integer,JobAccount> jobAccount,
		ReentrantReadWriteLock rwLockJobAccount
	) {
		this.workerAccounts = workerAccounts;
		this.rwLockWorkerAccounts = rwLockWorkerAccounts;
		this.jobAccount = jobAccount;
		this.rwLockJobAccount = rwLockJobAccount;
	}
	
	@Override
	public void run() {
		rwLockWorkerAccounts.writeLock().lock();
		rwLockJobAccount.writeLock().lock();
	
		
		
		
		
		rwLockJobAccount.writeLock().unlock();
		rwLockWorkerAccounts.writeLock().unlock();
	}

}
