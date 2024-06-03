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
	Dictionary<Integer,JobAccount> jobAccount = null;
	ReentrantReadWriteLock rwLockJobAccount = null;

	public PoolServerThread(Socket client,
		Dictionary<Integer, WorkerAccount> workerAccounts,
		ReentrantReadWriteLock rwLockWorkerAccounts,
		Dictionary<Integer,JobAccount> jobAccount,
		ReentrantReadWriteLock rwLockJobAccount
	) { 
		super(client);
		this.workerAccounts = workerAccounts;
		this.rwLockWorkerAccounts = rwLockWorkerAccounts;
		this.jobAccount = jobAccount;
		this.rwLockJobAccount = rwLockJobAccount;
	}
	
	protected String[] process_request(String[] commands) throws Exception {
		switch (commands[0]) {
		case "Workstation": return handleWorkstationClient(commands, this.clientIp);
		case "User": return handleUserClient(commands, this.clientIp);
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
		/*case "SUBJOB_DONE":
			INCREMENT AVAIL THREADS FOR THIS WORKER BY ONE
			return SMTH
		case "SUBJOB_ABORTED":
			INCREMENT AVAIL THREADS FOR THIS WORKER BY ONE
			return SMTH*/
		default:
			return null;
		}
	}
	
	protected String[] handleUserClient(String[] commands, String userIp) {
		switch(commands[1]) {
		case "TestConnect":
			return HandleUserCommands.testConnection(commands);
		case "UserBlock5s":
			return HandleUserCommands.userBlock5s(commands);
		case "StartJob":
			return HandleUserCommands.startJob(
				commands, userIp, this.jobAccount, this.rwLockJobAccount);
		case "Abort":
			return HandleUserCommands.abort(commands, userIp,
				this.jobAccount, this.rwLockJobAccount);
		case "InfoJobStatus":
			return HandleUserCommands.infoJob(
				commands, this.jobAccount, this.rwLockJobAccount);
		case "ListJobs":
			return HandleUserCommands.listJobs(
				commands, this.jobAccount, this.rwLockJobAccount);
		default:
			return null;
		}
	}
	
}
