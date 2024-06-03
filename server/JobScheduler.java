package server;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class JobScheduler extends Thread {
	private int checkPeriod; // in ms
	private int checkPhase; // in ms
	private Dictionary<Integer, WorkerAccount> workerAccounts = null;
	private ReentrantReadWriteLock rwLockWorkerAccounts = null;
	private Dictionary<Integer,JobAccount> jobAccount = null;
	private ReentrantReadWriteLock rwLockJobAccount = null;
	
	public JobScheduler (int checkPeriod, int checkPhase,
		Dictionary<Integer, WorkerAccount> workerAccounts,
		ReentrantReadWriteLock rwLockWorkerAccounts,
		Dictionary<Integer,JobAccount> jobAccount,
		ReentrantReadWriteLock rwLockJobAccount
	) {
		this.checkPeriod = checkPeriod;
		this.checkPhase = checkPhase;
		this.workerAccounts = workerAccounts;
		this.rwLockWorkerAccounts = rwLockWorkerAccounts;
		this.jobAccount = jobAccount;
		this.rwLockJobAccount = rwLockJobAccount;
	}
	
	@Override
	public void run() {
		try {
			Thread.sleep(checkPhase);
			while (true) {
				Thread.sleep(checkPeriod);
				rwLockJobAccount.readLock().lock();
				Enumeration<Integer> keys = jobAccount.keys();
				while (keys.hasMoreElements()) {
					int key = keys.nextElement();
					if (jobAccount.get(key).status.equals("Ready"))
						new ReadyJobSlave(key, workerAccounts, rwLockWorkerAccounts, jobAccount, rwLockJobAccount).start();
					if (jobAccount.get(key).status.equals("Failed"))
						new FailedJobSlave(key, jobAccount, rwLockJobAccount).start();
					// maby add here or ConfError
					if (jobAccount.get(key).status.equals("Aborted"))
						new AbortedJobSlave(key, jobAccount, rwLockJobAccount).start();
				}
				rwLockJobAccount.readLock().unlock();
			}
		} catch (InterruptedException e) { return; }
	}

}
