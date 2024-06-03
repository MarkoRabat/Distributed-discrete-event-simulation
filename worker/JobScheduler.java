package worker;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import server.JobAccount;

public class JobScheduler extends Thread {
	private int checkPeriod; // in ms
	private int checkPhase; // in ms
	private Dictionary<Integer,JobAccount> jobAccount = null;
	private ReentrantReadWriteLock rwLockJobAccount = null;
	private Dictionary<Integer, JExecutor> jExecutorAccount = null;
	private ReentrantReadWriteLock rwLockJExecutorAccount = null;
	
	public JobScheduler (int checkPeriod, int checkPhase,
		Dictionary<Integer,JobAccount> jobAccount,
		ReentrantReadWriteLock rwLockJobAccount,
		Dictionary<Integer,JExecutor> jExecutorAccount,
		ReentrantReadWriteLock rwLockJExecutorAccount
	) {
		this.checkPeriod = checkPeriod;
		this.checkPhase = checkPhase;
		this.jobAccount = jobAccount;
		this.rwLockJobAccount = rwLockJobAccount;
		this.jExecutorAccount = jExecutorAccount;
		this.rwLockJExecutorAccount = rwLockJExecutorAccount;
	}
	
	@Override
	public void run() {
		try {
			Thread.sleep(checkPhase);
			while (true) {
				Thread.sleep(checkPeriod);
				rwLockJobAccount.readLock().lock();
				rwLockJExecutorAccount.writeLock().lock();
				Enumeration<Integer> keys = jobAccount.keys();
				while (keys.hasMoreElements()) {
					int key = keys.nextElement();
					if (jobAccount.get(key).status.equals("Ready")) {
						jobAccount.get(key).status = "Executing";
						int subkey = Integer.parseInt(jobAccount.get(key).ip);
						JExecutor jexec = new JExecutor(key, subkey, jobAccount, rwLockJobAccount);
						this.jExecutorAccount.put(key, jexec); jexec.start();
					}
				}
				rwLockJobAccount.readLock().unlock();
				rwLockJExecutorAccount.writeLock().unlock();
			}
		} catch (InterruptedException e) { return; }
	}

}
