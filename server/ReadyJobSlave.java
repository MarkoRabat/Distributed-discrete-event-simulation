package server;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;
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
		this.key = key;
		this.workerAccounts = workerAccounts;
		this.rwLockWorkerAccounts = rwLockWorkerAccounts;
		this.jobAccount = jobAccount;
		this.rwLockJobAccount = rwLockJobAccount;
	}
	
	@Override
	public void run() {

		String[] workerIps = null;
		int[] workerPorts = null;
		int[] availThreads = null;
		int workerCnt = 0;
		String components = null;
		String connections = null;
		String jobName = null;
		int jobid = -1;
		String simType = null;
		int ltime = -1;
		
		rwLockWorkerAccounts.readLock().lock();
		Enumeration<Integer> keys = workerAccounts.keys();
		while (keys.hasMoreElements()) {
			int key = keys.nextElement(); ++workerCnt; }
		rwLockWorkerAccounts.readLock().unlock();
		
		if (workerCnt <= 0) return;

		workerIps = new String[workerCnt];
		workerPorts = new int[workerCnt];
		availThreads = new int[workerCnt];
		
		rwLockWorkerAccounts.readLock().lock();
		rwLockJobAccount.writeLock().lock();
		
			JobAccount jb = jobAccount.get(this.key);
			if (!jb.status.equals("Ready")) {
				rwLockWorkerAccounts.readLock().unlock();
				rwLockJobAccount.writeLock().unlock();
				return;
			}
			
			int i = 0;
			keys = workerAccounts.keys();
			while (keys.hasMoreElements() && i < workerCnt) {
				int key = keys.nextElement(); 
				WorkerAccount wa = workerAccounts.get(key);
				workerIps[i] = wa.ip;
				workerPorts[i] = wa.port;
				availThreads[i] = wa.availThreads;
				++i;
			}
			
			if (i < workerCnt) {
				rwLockWorkerAccounts.readLock().unlock();
				rwLockJobAccount.writeLock().unlock();
				return;
			}
			
			//do round robin here to lower avail
			//thread coun't for each worker
			

			jb.execIps = workerIps;
			jb.execPorts = workerPorts;
			jb.status = "Scheduled";
			jobid = jb.jobId;
			components = jb.components;
			connections = jb.connections;
			jobName = jb.name;
			simType = jb.requestedSimType;
			ltime = jb.logicalEndTime;
		
		rwLockWorkerAccounts.readLock().unlock();
		rwLockJobAccount.writeLock().unlock();

		// divide up work somehow
		
		Thread[] requestThreads = new Thread [workerCnt];
		for (int j = 0; j < workerCnt; ++j) {
			/*for (int k = 0; k < availThreads[j]; ++k) {
				// put something smart here later
			}*/
			requestThreads[j] = new SendJobToWorkerSlave(
				jobid, j % workerCnt, workerIps[j], workerPorts[j],
				components, connections, jobName, simType, ltime
			);
		}
		
		for (int j = 0; j < workerCnt; ++j)
			requestThreads[j].start();

		for (int j = 0; j < workerCnt; ++j) try {
			requestThreads[j].join();
		} catch (InterruptedException e) {e.printStackTrace(); }
		
		
		rwLockJobAccount.writeLock().lock();
		
			if (!jb.status.equals("Scheduled")) {
				rwLockJobAccount.writeLock().unlock();
				return;
			}
			
			JobAccount jb2 = jobAccount.get(this.key);
			jb2.status = "Running";
		
		rwLockJobAccount.writeLock().unlock();
	}

}
