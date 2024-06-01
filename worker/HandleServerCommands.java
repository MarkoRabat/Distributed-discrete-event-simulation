package worker;

import java.util.Dictionary;
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
		ReentrantReadWriteLock rwLockJobAccount
	) {
		String components = commands[3];
		String connections = commands[5];
		String simulationType = commands[7];
		int logicalEndTime = Integer.parseInt(commands[9]);
		int jobId = Integer.parseInt(commands[11]);
		int subJobId = Integer.parseInt(commands[13]);
		
		rwLockJobAccount.writeLock().lock();
		JobAccount jb = new JobAccount("" + subJobId, simulationType, logicalEndTime, components, connections);
		jb.jobId = jobId;
		jobAccount.put(jobId, jb);
		rwLockJobAccount.writeLock().unlock();
		
		String[] toLog = new String[] {
			commands[0], commands[1], "userIp", 
			userIp, "status", "Ready", commands[6],
			commands[7], commands[8], commands[9],
			"jobId", "" + jobId
		};
		CommandLogger.logToConsole("startJob", toLog);

		return new String[] {"StartJob", "Job", "Ready", "Id", "" + jobId};

	}

}
