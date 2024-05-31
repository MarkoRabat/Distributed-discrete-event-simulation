package server;

import java.net.ConnectException;
import java.util.Dictionary;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import commClient.CommClient;

public class ChkWorkerPulseSlave extends Thread {

	private int workerAccountKey;
	private WorkerAccount workerToCkh;
	private Dictionary<Integer, WorkerAccount> workerAccounts;
	private ReentrantReadWriteLock rwLockWorkerAccounts;

	public ChkWorkerPulseSlave(
		int workerAccountKey,
		WorkerAccount workerToChk,
		ReentrantReadWriteLock rwLockWorkerAccounts,
		Dictionary<Integer, WorkerAccount> workerAccounts
	) {
		this.workerAccountKey = workerAccountKey;
		this.workerToCkh = workerToChk;
		this.workerAccounts = workerAccounts;
		this.rwLockWorkerAccounts = rwLockWorkerAccounts;
	}
	
	@Override
	public void run() {
		try {
			System.out.println(CommClient.makeRequest(
				this.workerToCkh.ip, this.workerToCkh.port, new String[] {"Server\n", "PulseChk\n"}));
		} catch (ConnectException e) {
			rwLockWorkerAccounts.writeLock().lock();
			workerAccounts.remove(this.workerAccountKey);
			rwLockWorkerAccounts.writeLock().unlock();
		}
	}

}
