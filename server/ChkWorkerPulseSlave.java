package server;

import java.net.ConnectException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import commClient.CommClient;

public class ChkWorkerPulseSlave extends Thread {

	private int workerAccountKey;
	private String workerToChkIp;
	private int workerToChkPort;
	private Dictionary<Integer, WorkerAccount> workerAccounts;
	private ReentrantReadWriteLock rwLockWorkerAccounts;
	private Dictionary<Integer, JobAccount> jobAccount;
	private ReentrantReadWriteLock rwLockJobAccount;

	public ChkWorkerPulseSlave(
		int workerAccountKey,
		String workerToChkIp,
		int workerToChkPort,
		ReentrantReadWriteLock rwLockWorkerAccounts,
		Dictionary<Integer, WorkerAccount> workerAccounts,
		ReentrantReadWriteLock rwLockJobAccount,
		Dictionary<Integer, JobAccount> jobAccount
	) {
		this.workerAccountKey = workerAccountKey;
		this.workerToChkIp = workerToChkIp;
		this.workerToChkPort = workerToChkPort;
		this.workerAccounts = workerAccounts;
		this.rwLockWorkerAccounts = rwLockWorkerAccounts;
		this.rwLockJobAccount = rwLockJobAccount;
		this.jobAccount = jobAccount;
	}
	
	@Override
	public void run() {
		try {
			// must use read lock here or do something else, fine for now
			String response = CommClient.makeRequest(
				this.workerToChkIp, this.workerToChkPort, new String[] {"Server\n", "PulseChk\n"});
			String[] processedResponse = CommClient.processResponse(response);
			if (!processedResponse[0].equals("Worker") || !processedResponse[1].equals("Alive"))
				throw new ConnectException("Invalid response");
		} catch (ConnectException e) {
			
			rwLockWorkerAccounts.writeLock().lock();
				System.err.println("\tWorker " + workerAccounts.get(this.workerAccountKey).toString() + " down.");
				workerAccounts.remove(this.workerAccountKey);
			rwLockWorkerAccounts.writeLock().unlock();
			
			rwLockJobAccount.writeLock().lock();
				Enumeration<Integer> keys = this.jobAccount.keys();
				while (keys.hasMoreElements()) {
					JobAccount jb = jobAccount.get(keys.nextElement());
					if (!jb.status.equals("Scheduled") && !jb.status.equals("Running")) continue;
					for (int i = 0; i < jb.execIps.length; ++i)
						if (this.workerToChkIp.equals(jb.execIps[i])) {
							jb.status = "Failed";
							jb.finishedAt = new SimpleDateFormat("yyyyMMddHHmmss")
								.format(Calendar.getInstance().getTime());
						}
				}
			rwLockJobAccount.writeLock().unlock();
			
			
		} catch (Exception e) { e.printStackTrace(); }
	}

}
