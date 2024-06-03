package worker;

import java.net.Socket;
import java.util.Dictionary;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import commServer.PoolThreadFactory;
import server.JobAccount;
import server.WorkerAccount;

public class PoolWorkerThreadFactory extends PoolThreadFactory {

	Dictionary<Integer,JobAccount> jobAccount = null;
	ReentrantReadWriteLock rwLockJobAccount = null;
	int serverAvailThreads = -1;
	
	public PoolWorkerThreadFactory(
		Dictionary<Integer, JobAccount> jobAccount,
		ReentrantReadWriteLock rwLockJobAccount,
		int serverAvailThreads
	) { 
		super();
		this.jobAccount = jobAccount;
		this.rwLockJobAccount = rwLockJobAccount;
		this.serverAvailThreads = serverAvailThreads;
	}
	
	public PoolWorkerThread createThreadForConn(Socket socket) {
		return new PoolWorkerThread(
			socket, jobAccount, rwLockJobAccount, serverAvailThreads);
	}

}
