package server;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ChkWorkerPulseMaster extends Thread {
	
	private int pulsePeriod; // in msec
	private Dictionary<Integer, WorkerAccount> workerAccounts;
	private ReentrantReadWriteLock rwLockWorkerAccounts;
	
	public ChkWorkerPulseMaster(int pulsePeriod,
		ReentrantReadWriteLock rwLockWorkerAccounts,
		Dictionary<Integer, WorkerAccount> workerAccounts
	) {
		this.pulsePeriod = pulsePeriod;
		this.workerAccounts = workerAccounts;
		this.rwLockWorkerAccounts = rwLockWorkerAccounts;
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(this.pulsePeriod);
				try {
					rwLockWorkerAccounts.readLock().lock();
					Enumeration<Integer> keys = workerAccounts.keys();
					while (keys.hasMoreElements()) {
						int key = keys.nextElement();
						new ChkWorkerPulseSlave(
							key, workerAccounts.get(key).ip, 
							workerAccounts.get(key).port,
							rwLockWorkerAccounts, workerAccounts
						).start();
					}
				}
				finally { rwLockWorkerAccounts.readLock().unlock(); }
			} catch (InterruptedException e) { return; }
		}
	}
}
