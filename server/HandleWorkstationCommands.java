package server;

import java.util.Dictionary;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class HandleWorkstationCommands {

	public static String[] addWorker(
		String[] commands, String workerIp,
		Dictionary<Integer, WorkerAccount> workerAccounts,
		ReentrantReadWriteLock rwLockWorkerAccounts
	) {
		if (commands.length != 6
			|| !commands[2].equals("NAvailThreads")
			|| !commands[4].equals("WorkerPort")) return null;

		int workerPort = -1, availThreads = -1;
		try { workerPort = Integer.parseInt(commands[5]);
			availThreads = Integer.parseInt(commands[3]);
		} catch (Exception e) { return null; }
		int newWorkerId;
		rwLockWorkerAccounts.writeLock().lock();
		newWorkerId = WorkerAccount.getNextWorkerId();
		workerAccounts.put(
			newWorkerId, new WorkerAccount(workerIp, workerPort, availThreads));
		rwLockWorkerAccounts.writeLock().unlock();

		String[] toLog = CommandLogger.mergeLoggerCommands(commands, new String[] {"WorkerIp", workerIp, "WorkerId", "" + newWorkerId});
		CommandLogger.logToConsole("addWorker", toLog);

		return new String[] { "WorkerId", "" + newWorkerId };
	}

}
