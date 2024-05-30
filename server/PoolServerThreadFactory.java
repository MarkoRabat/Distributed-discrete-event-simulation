package server;

import java.net.Socket;
import java.util.Dictionary;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import commServer.PoolThreadFactory;

public class PoolServerThreadFactory extends PoolThreadFactory {
	
	Dictionary<Integer, WorkerAccount> workerAccounts = null;;
	ReentrantReadWriteLock rwLockWorkerAccounts = null;

	public PoolServerThreadFactory(
		Dictionary<Integer, WorkerAccount> workerAccounts,
		ReentrantReadWriteLock rwLockWorkerAccounts) { 
		super();
		this.workerAccounts = workerAccounts;
		this.rwLockWorkerAccounts = rwLockWorkerAccounts;
	}
	
	@Override
	public PoolServerThread createThreadForConn(Socket socket) {
		return new PoolServerThread(socket, workerAccounts, rwLockWorkerAccounts);
	}

}
