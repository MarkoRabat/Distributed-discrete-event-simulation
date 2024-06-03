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
		System.out.println("jid_to_abort");
		System.out.println(jid_to_abort);
		
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

}









