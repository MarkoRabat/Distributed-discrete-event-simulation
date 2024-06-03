package worker;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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
				System.out.println("key: " + key);
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
		
		System.out.println("jobId: " + jobId);
		System.out.println("availThreads: " + availThreads);
		System.out.println("subpCnt: " + subpCnt);

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
		};
		CommandLogger.logToConsole("startJob", toLog);

		return new String[] {"StartJob", "Job", "Ready", "Id", "" + jobId};

	}

}
