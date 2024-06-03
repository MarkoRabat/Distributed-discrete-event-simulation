package server;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import commServer.ExecutorServer;

public class Server {
	
	private static int checkWorkerPulse = 10; // in sec
	private static int jobSchedulerPeriod = 10;
	private static int jobSchedulerPhase = 0;
	private static ReentrantReadWriteLock rwLockWorkerAccounts = new ReentrantReadWriteLock();
	private static Dictionary<Integer, WorkerAccount> workerAccounts = new Hashtable<Integer, WorkerAccount>();
	private static ReentrantReadWriteLock rwLockJobAccount = new ReentrantReadWriteLock();
	private static Dictionary<Integer,JobAccount> jobAccount = new Hashtable<Integer, JobAccount>();
	private static ChkWorkerPulseMaster pulseChk = new ChkWorkerPulseMaster(
			checkWorkerPulse * 1000, rwLockWorkerAccounts, workerAccounts, rwLockJobAccount, jobAccount);
	private static JobScheduler jobScheduler = new JobScheduler(
			jobSchedulerPeriod * 1000, jobSchedulerPhase * 1000, workerAccounts, rwLockWorkerAccounts, jobAccount, rwLockJobAccount);
	
	public static void main(String[] args) {
		System.out.println("Server started.");
		ExecutorServer server = new ExecutorServer(5000,
			new PoolServerThreadFactory(
				Server.workerAccounts, Server.rwLockWorkerAccounts,
				Server.jobAccount, Server.rwLockJobAccount
			));
		server.start();
		pulseChk.start();
		jobScheduler.start();
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
		while (true) {
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
		}

		//server.stop();
		//pulseChk.interrupt();
		//jobScheduler.interrupt();
	}

}
