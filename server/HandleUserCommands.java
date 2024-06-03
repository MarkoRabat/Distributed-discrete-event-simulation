package server;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Dictionary;
import java.util.Enumeration;
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
		String jobName = commands[11];
		
		int jobId = -1;
		rwLockJobAccount.writeLock().lock();
		jobId = JobAccount.getNextJobId();
		jobAccount.put(jobId, 
			new JobAccount(
				userIp, simulationType, logicalEndTime,
				components, connections, jobName, "Ready"
			)
		);
		rwLockJobAccount.writeLock().unlock();
		
		String[] toLog = new String[] {
			commands[0], commands[1], "userIp", userIp, 
			"status", "Ready", commands[6], commands[7], 
			commands[8], commands[9], commands[11]
		};
		CommandLogger.logToConsole("startJob", toLog);

		return new String[] {"StartJob", "Job", "Ready", "Id", "" + jobId};
	}
	
	 public static String[] userBlock5s(String[] commands) {
		 try { Thread.sleep(5000);
		} catch (InterruptedException e) { e.printStackTrace(); }
		 return new String[] {"UserBlock5s", "TimeElapsed"};
	 }

	public static String[] abort(String[] commands, String userIp,
		Dictionary<Integer,JobAccount> jobAccount,
		ReentrantReadWriteLock rwLockJobAccount
	) {
		int jid_to_abort = Integer.parseInt(commands[3]);
		rwLockJobAccount.writeLock().lock();
		
			JobAccount jb = jobAccount.get(jid_to_abort);
			if (jb == null) {
				rwLockJobAccount.writeLock().unlock();
				return new String[] {"Abort", "Failed"};
			}
			if (!jb.ip.equals(userIp)) {
				rwLockJobAccount.writeLock().unlock();
				return new String[] {"Abort", "Failed"};
			}

			if (!jb.status.equals("Ready")
				&& !jb.status.equals("Scheduled")
				&& !jb.status.equals("Running"))
			{
				rwLockJobAccount.writeLock().unlock();
				return new String[] {"Abort", "Failed"};
			}

			jb.status = "Aborted";
			jb.finishedAt = new SimpleDateFormat("yyyyMMddHHmmss")
				.format(Calendar.getInstance().getTime());
		
		rwLockJobAccount.writeLock().unlock();
		return new String[] {"Abort", "Success"};
	}
	
	
	public static String[] infoJob(String[] commands,
			Dictionary<Integer,JobAccount> jobAccount,
		ReentrantReadWriteLock rwLockJobAccount) {
		
		String status = null;
		int jobId = Integer.parseInt(commands[3]);
		
		rwLockJobAccount.readLock().lock();
			
			JobAccount jb = jobAccount.get(jobId);
			if (jb == null) { rwLockJobAccount.readLock().unlock();
				return new String[] {"Server", "InfoJobStatus", "NoJob", "Failed"}; }
			status = jb.status;
			
		rwLockJobAccount.readLock().unlock();
		if (status.startsWith("__")) status = status.substring(2);
		
		return new String[] {"Server", "InfoJob", status, "Success"};
	}

	public static String[] listJobs(String[] commands,
		Dictionary<Integer, JobAccount> jobAccount,
		ReentrantReadWriteLock rwLockJobAccount) {
		
		String[] result = null;
		int jobCnt = 0;
		
		rwLockJobAccount.readLock().lock();
		
		Enumeration<Integer> keys = jobAccount.keys();
		while (keys.hasMoreElements()) {
			int key = keys.nextElement(); ++jobCnt; }
		
		result = new String[jobCnt + 2];
		result[0] = "Server"; result[1] = "ListJobs";

		jobCnt = 2;
		keys = jobAccount.keys();
		while (keys.hasMoreElements())
			result[jobCnt++] = jobAccount.get(keys.nextElement()).toString();
		
		rwLockJobAccount.readLock().unlock();
		return result;
	}
			

}









