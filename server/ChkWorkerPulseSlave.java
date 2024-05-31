package server;

import java.net.ConnectException;
import java.util.Dictionary;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import commClient.CommClient;

public class ChkWorkerPulseSlave extends Thread {

	private int workerAccountKey;
	private String workerToChkIp;
	private int workerToChkPort;
	private Dictionary<Integer, WorkerAccount> workerAccounts;
	private ReentrantReadWriteLock rwLockWorkerAccounts;

	public ChkWorkerPulseSlave(
		int workerAccountKey,
		String workerToChkIp,
		int workerToChkPort,
		ReentrantReadWriteLock rwLockWorkerAccounts,
		Dictionary<Integer, WorkerAccount> workerAccounts
	) {
		this.workerAccountKey = workerAccountKey;
		this.workerToChkIp = workerToChkIp;
		this.workerToChkPort = workerToChkPort;
		this.workerAccounts = workerAccounts;
		this.rwLockWorkerAccounts = rwLockWorkerAccounts;
	}
	
	@Override
	public void run() {
		try {
			// must use read lock here or do something else, fine for now
			String response = CommClient.makeRequest(
				this.workerToChkIp, this.workerToChkPort, new String[] {"Server\n", "PulseChk\n"});
			String[] processedResponse = CommClient.processResponse(response);
			for (int i = 0; i < processedResponse.length; ++i) {
				System.out.print("\t" + processedResponse[i]);
			}
		} catch (ConnectException e) {
			System.out.print("\t worker not alive pstart");
			rwLockWorkerAccounts.writeLock().lock();
			workerAccounts.remove(this.workerAccountKey);
			rwLockWorkerAccounts.writeLock().unlock();
			System.out.print("\t worker not alive pend");
		}
	}

}
