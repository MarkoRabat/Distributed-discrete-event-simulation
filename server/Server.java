package server;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import commServer.ExecutorServer;

public class Server {
	
	private static int checkWorkerPulse = 10; // in sec
	private static ReentrantReadWriteLock rwLockWorkerAccounts = new ReentrantReadWriteLock();
	private static Dictionary<Integer, WorkerAccount> workerAccounts = new Hashtable<Integer, WorkerAccount>();
	private static ChkWorkerPulseMaster pulseChk = new ChkWorkerPulseMaster(
			checkWorkerPulse * 1000, rwLockWorkerAccounts, workerAccounts);
	private static ReentrantReadWriteLock rwLockJobAccount = new ReentrantReadWriteLock();
	private static Dictionary<Integer,JobAccount> jobAccount = new Hashtable<Integer, JobAccount>();
	
	public static void main(String[] args) {
		System.out.println("Server started.");
		ExecutorServer server = new ExecutorServer(5000,
			new PoolServerThreadFactory(
				Server.workerAccounts, Server.rwLockWorkerAccounts,
				Server.jobAccount, Server.rwLockJobAccount
			));
		server.start();
		pulseChk.start();
		server.waitForUserConsoleQ();
		rwLockWorkerAccounts.readLock().lock();

		System.out.println("===============workers:================");
		Enumeration<Integer> keys = workerAccounts.keys();
		while (keys.hasMoreElements()) {
			int key = keys.nextElement();
			System.out.println(
					key + ": " + workerAccounts.get(key));
		}
		System.out.println("===============jobs:================");
		keys = jobAccount.keys();
		while (keys.hasMoreElements()) {
			int key = keys.nextElement();
			System.out.println(
					key + ": " + jobAccount.get(key));
		}

		rwLockWorkerAccounts.readLock().unlock();
		server.waitForUserConsoleQ();
		rwLockWorkerAccounts.readLock().lock();

		System.out.println("===============workers:================");
		keys = workerAccounts.keys();
		while (keys.hasMoreElements()) {
			int key = keys.nextElement();
			System.out.println(
					key + ": " + workerAccounts.get(key));
		}
		
		System.out.println("===============jobs:================");
		keys = jobAccount.keys();
		while (keys.hasMoreElements()) {
			int key = keys.nextElement();
			System.out.println(
					key + ": " + jobAccount.get(key));
		}

		rwLockWorkerAccounts.readLock().unlock();

		server.stop();
		pulseChk.interrupt();
	}

}
