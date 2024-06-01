package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Dictionary;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import commServer.PoolThread;

public class PoolServerThread extends PoolThread {
	
	
	private Dictionary<Integer, WorkerAccount> workerAccounts = null;
	private ReentrantReadWriteLock rwLockWorkerAccounts = null;

	public PoolServerThread(Socket client,
		Dictionary<Integer, WorkerAccount> workerAccounts,
		ReentrantReadWriteLock rwLockWorkerAccounts) { 
		super(client);
		this.workerAccounts = workerAccounts;
		this.rwLockWorkerAccounts = rwLockWorkerAccounts;
	}
	
	protected String[] process_request(String[] commands) throws Exception {
		switch (commands[0]) {
		case "Workstation": return handleWorkstationClient(commands, this.clientIp);
		case "User": return handleUserClient(commands);
		default: 
			String[] response = new String[1];
			response[0] = "<h1>Succesful connection to Server</h1>";
			return response;
		}

	}
	
	protected String[] handleWorkstationClient(String[] commands, String workerIp) {
		switch (commands[1]) {
		case "WorkstationStarted":
			return HandleWorkstationCommands.addWorker(
				commands, workerIp, workerAccounts, rwLockWorkerAccounts);
		default:
			return null;
		}
	}
	
	protected String[] handleUserClient(String[] commands) {
		switch(commands[1]) {
		case "TestConnect":
			return HandleUserCommands.testConnection(commands);
		case "StartJob":
			return HandleUserCommands.startJob(commands);
		default:
			return null;
		}
	}
	
}
