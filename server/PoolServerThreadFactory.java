package server;

import java.net.Socket;
import java.util.Dictionary;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import commServer.PoolThreadFactory;

public class PoolServerThreadFactory extends PoolThreadFactory {
	
	Dictionary<Integer, WorkerAccount> workerAccounts = null;;
	ReentrantReadWriteLock rwLockWorkerAccounts = null;
	Dictionary<Integer,JobAccount> jobAccount = null;
	ReentrantReadWriteLock rwLockJobAccount = null;

	public PoolServerThreadFactory(
		Dictionary<Integer, WorkerAccount> workerAccounts,
		ReentrantReadWriteLock rwLockWorkerAccounts,
		Dictionary<Integer, JobAccount> jobAccount,
		ReentrantReadWriteLock rwLockJobAccount
	) { 
		super();
		this.workerAccounts = workerAccounts;
		this.rwLockWorkerAccounts = rwLockWorkerAccounts;
		this.jobAccount = jobAccount;
		this.rwLockJobAccount = rwLockJobAccount;
	}
	
	@Override
	public PoolServerThread createThreadForConn(Socket socket) {
		return new PoolServerThread(
			socket, workerAccounts, rwLockWorkerAccounts, jobAccount, rwLockJobAccount);
	}

}
