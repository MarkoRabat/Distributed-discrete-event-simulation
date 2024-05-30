package server;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import commServer.ExecutorServer;
import commServer.PoolThreadFactory;

public class Server {
	
	private static ReentrantReadWriteLock rwLockWorkerAccounts = new ReentrantReadWriteLock();
	private static Dictionary<Integer, WorkerAccount> workerAccounts = new Hashtable<Integer, WorkerAccount>();
	
	public static void main(String[] args) {
		System.out.println("Server started.");
		ExecutorServer server = new ExecutorServer(5000,
			new PoolServerThreadFactory(Server.workerAccounts, Server.rwLockWorkerAccounts));
		server.start();
		server.waitForUserConsoleQ();
		server.stop();
	}

}
