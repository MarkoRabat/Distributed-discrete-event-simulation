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
	
	public PoolWorkerThreadFactory(
		Dictionary<Integer, JobAccount> jobAccount,
		ReentrantReadWriteLock rwLockJobAccount
	) { 
		super();
		this.jobAccount = jobAccount;
		this.rwLockJobAccount = rwLockJobAccount;
	}
	
	public PoolWorkerThread createThreadForConn(Socket socket) {
		return new PoolWorkerThread(
			socket, jobAccount, rwLockJobAccount);
	}

}
