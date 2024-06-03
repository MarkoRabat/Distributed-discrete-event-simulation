package worker;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import server.JobAccount;

public class JExecutor extends Thread {
	
	private int key;
	private Dictionary<Integer,JobAccount> jobAccount = null;
	private ReentrantReadWriteLock rwLockJobAccount = null;
	
	public JExecutor (int key,
		Dictionary<Integer,JobAccount> jobAccount,
		ReentrantReadWriteLock rwLockJobAccount
	) {
		this.key = key;
		this.jobAccount = jobAccount;
		this.rwLockJobAccount = rwLockJobAccount;
	}
	
	public int getKey() { return key; }
	
	@Override
	public void run() {
		try {
			while (true) {
				Random rand = new Random();
				int sleep_time = rand.nextInt(8);			
				Thread.sleep(sleep_time * 1000);
			}
		}
		catch (InterruptedException e) {
			System.err.println("\tExecutor for the job " + key + " terminated.");
			// send aborted notification
			return;
		}
		// send results of job execution
	}

}
