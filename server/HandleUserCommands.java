package server;

import java.util.Dictionary;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class HandleUserCommands {
	
	public static String[] testConnection(String[] commands) { 
		return new String[] {"TestConnect:", "Succes"};
	}

	public static String[] startJob(
		String[] commands , String userIp,
		Dictionary<Integer,JobAccount> jobAccount,
		ReentrantReadWriteLock rwLockJobAccount
	) {

		String components = commands[3];
		String connections = commands[5];
		String simulationType = commands[7];
		int logicalEndTime = Integer.parseInt(commands[9]);
		
		int jobId = -1;
		rwLockJobAccount.writeLock().lock();
		jobId = JobAccount.getNextJobId();
		jobAccount.put(jobId, 
			new JobAccount(userIp, simulationType, logicalEndTime, components, connections)
		);
		rwLockJobAccount.writeLock().unlock();
		
		
		String[] toLog = new String[] {
			commands[0], commands[1], "userIp", 
			userIp, "status", "Ready", commands[6],
			commands[7], commands[8], commands[9]
		};
		CommandLogger.logToConsole("startJob", toLog);

		return new String[] {"StartJob", "Job", "Ready", "Id", "" + jobId};
	}
	
	 public static String[] userBlock5s(String[] commands) {
		 try { Thread.sleep(5000);
		} catch (InterruptedException e) { e.printStackTrace(); }
		 return new String[] {"UserBlock5s", "TimeElapsed"};
	 }


}
