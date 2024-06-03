package worker;

import java.net.ConnectException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import commClient.CommClient;
import server.CommandLogger;
import server.JobAccount;

public class HandleServerCommands {
	
	public static String[] checkPulse(String[] commands) {
		return new String[] {"Worker", "Alive"};
	}
	
	public static String[] createJob(
		String[] commands, String userIp,
		Dictionary<Integer,JobAccount> jobAccount,
		ReentrantReadWriteLock rwLockJobAccount,
		int availThreads
	) {
		String components = commands[3];
		String connections = commands[5];
		String simulationType = commands[7];
		int logicalEndTime = Integer.parseInt(commands[9]);
		int jobId = Integer.parseInt(commands[11]);
		String subJobId = commands[13];
		String jobName = commands[15];
		String status = "Ready";
		if (Integer.parseInt(commands[17]) == 1) status = "Aborted";
		
		rwLockJobAccount.writeLock().lock();
		
		boolean job_aborted = false;
		Enumeration<Integer> keys = jobAccount.keys();
			while (keys.hasMoreElements()) {
				int key = keys.nextElement();
				if(jobAccount.get(key).jobId / availThreads == jobId
					&& jobAccount.get(key).status.equals("Aborted"))
				{
					job_aborted = true; break;
				}
			}
		
		if (job_aborted) {
			rwLockJobAccount.writeLock().unlock();
			
			String[] toLog = new String[] {
				commands[0], commands[1], "userIp", 
				userIp, "status", "Aborted", commands[6],
				commands[7], commands[8], commands[9],
				"jobId", "" + (-1), commands[12],
				commands[13], commands[14], commands[15],
				commands[16], commands[17]
			};
			CommandLogger.logToConsole("startJob", toLog);
			return new String[] {"StartJob", "Job", "Aborted", "Id", "" + (-1)};
		}
		
		JobAccount jb = new JobAccount(
			subJobId, simulationType, logicalEndTime,
			components, connections, jobName, status
		);
		
		int subpCnt = 0;
		keys = jobAccount.keys();
		while (keys.hasMoreElements()) {
			int key = keys.nextElement();
			if(jobAccount.get(key).jobId / availThreads == jobId) ++subpCnt;
		}
		
		jobId = jobId * availThreads + subpCnt;
		
		jb.jobId = jobId;
		jobAccount.put(jobId, jb);
		
		rwLockJobAccount.writeLock().unlock();
		
		String[] toLog = new String[] {
			commands[0], commands[1], "userIp", 
			userIp, "status", "Ready", commands[6],
			commands[7], commands[8], commands[9],
			"jobId", "" + jobId, commands[12],
			commands[13], commands[14], commands[15],
			commands[16], commands[17]
		}; CommandLogger.logToConsole("startJob", toLog); System.out.println();

		return new String[] {"StartJob", "Job", "Ready", "Id", "" + jobId};

	}

	public static String[] abortJob(
		String selfIp, int selfPort, String[] commands, String userIp, 
		Dictionary<Integer,JobAccount> jobAccount,
		ReentrantReadWriteLock rwLockJobAccount,
		Dictionary<Integer, JExecutor> jExecutorAccount,
		ReentrantReadWriteLock rwLockJExecutorAccount, int availThreads
	) {
		int jobId = Integer.parseInt(commands[3]);

		boolean jobFound = false;
		String[] toLog = new String[] {
			commands[0], commands[1], commands[2],
			commands[3], "userIp", userIp };
		CommandLogger.logToConsole("startJob", toLog); System.out.println();

		rwLockJobAccount.writeLock().lock();
		
			Enumeration<Integer> keys = jobAccount.keys();
			while (keys.hasMoreElements()) {
				int key = keys.nextElement();
				if(jobAccount.get(key).jobId / availThreads == jobId)
				{
					if (!jobAccount.get(key).status.equals("Aborted")) {
						jobAccount.get(key).finishedAt = new SimpleDateFormat("yyyyMMddHHmmss")
							.format(Calendar.getInstance().getTime());
						jobAccount.get(key).status = "Aborted";
					} jobFound = true;
				}
			}
		rwLockJobAccount.writeLock().unlock();
		
		if (!jobFound) {
			jobId = jobId * availThreads;
			String[] params = new String[] {"Server", "CreateJob", "Components", "File"};
			params = CommClient.putFileInRequestParams(params, "");
			params = CommClient.mergeParams(params, new String[] { "Connections", "File"});
			params = CommClient.putFileInRequestParams(params, "");
			params = CommClient.mergeParams(params, new String[] {
				"SimulationType", "SomeSimType", "logicalEndTime", "10"});
			params = CommClient.mergeParams(params, new String[] {
				"jobId", "" + jobId, "subJobId", "" + 0,
				"JobName", "Unknown", "AbortJob", "1"});
			
			String response = null;
			try { response = CommClient.makeUserRequest(selfIp, selfPort, params);
				String[] data = CommClient.processResponse(response); }
			catch (ConnectException e) { System.err.println(
				"\tWorker(this) {ip: " + selfIp + "," + selfPort + "} not responding to abort job request."); }
			catch (Error e) { e.printStackTrace(); }
		}
		else {
			rwLockJExecutorAccount.writeLock().lock();
				Enumeration<Integer> jeKeys = jExecutorAccount.keys();
				while (jeKeys.hasMoreElements()) {
					int jeKey = jeKeys.nextElement();
					if(jExecutorAccount.get(jeKey).getKey() / availThreads == jobId
						&& jExecutorAccount.get(jeKey).isAlive())
						jExecutorAccount.get(jeKey).interrupt();
				}
			rwLockJExecutorAccount.writeLock().unlock();
		}
		
		


		return new String[] { "Abort", "Success" };


		
	}
	

}
