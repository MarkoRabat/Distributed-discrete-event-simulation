package worker;

import java.net.Socket;
import java.util.Dictionary;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import commServer.PoolThread;
import server.JobAccount;
import server.WorkerAccount;

public class PoolWorkerThread extends PoolThread {
	
	Dictionary<Integer,JobAccount> jobAccount = null;
	ReentrantReadWriteLock rwLockJobAccount = null;
	
	public PoolWorkerThread(Socket client,
		Dictionary<Integer,JobAccount> jobAccount,
		ReentrantReadWriteLock rwLockJobAccount
	) { 
		super(client);
		this.jobAccount = jobAccount;
		this.rwLockJobAccount = rwLockJobAccount;
	}
	
	
	protected void process_connection_start(String clientIp, int clientPort) {}
	protected void process_connection_end(String clientIp, int clientPort) throws Exception {}
	
	protected String[] process_request(String[] commands) {
		switch(commands[0]) {
		case "Server": return handleServerClient(commands, this.clientIp);
		default:
			String[] response = new String[1];
			response[0] = "<h1>Succesful connection to Worker</h1>";
			return response;
		}
	}
	
	protected String[] handleServerClient(String[] commands, String userIp) {
		switch (commands[1]) {
		case "PulseChk":
			return HandleServerCommands.checkPulse(commands);
		case "CreateJob":
			return HandleServerCommands.createJob(
				commands, userIp, this.jobAccount, this.rwLockJobAccount);
		default:
			return null;
		}
	}

}
