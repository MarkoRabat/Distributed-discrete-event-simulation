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
	
	public static void main(String[] args) {
		System.out.println("Server started.");
		ExecutorServer server = new ExecutorServer(5000,
			new PoolServerThreadFactory(Server.workerAccounts, Server.rwLockWorkerAccounts));
		server.start();
		server.waitForUserConsoleQ();
		rwLockWorkerAccounts.readLock().lock();

		Enumeration<Integer> keys = workerAccounts.keys();
		while (keys.hasMoreElements()) {
			int key = keys.nextElement();
			System.out.println(
					key + ": " + workerAccounts.get(key));
		}

		rwLockWorkerAccounts.readLock().unlock();
		server.waitForUserConsoleQ();
		rwLockWorkerAccounts.readLock().lock();

		keys = workerAccounts.keys();
		while (keys.hasMoreElements()) {
			int key = keys.nextElement();
			System.out.println(
					key + ": " + workerAccounts.get(key));
		}

		rwLockWorkerAccounts.readLock().unlock();
		server.stop();
	}

}
