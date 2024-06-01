package server;

public class HandleUserCommands {
	
	public static String[] testConnection(String[] commands) { 
		return new String[] {"TestConnect:", "Succes"};
	}

	public static String[] startJob(String[] commands) {

		String components = commands[3];
		String connections = commands[5];

		return new String[] {"StartJob", "Job", "Started"};
	}
	
	 public static String[] userBlock5s(String[] commands) {
		 try { Thread.sleep(5000);
		} catch (InterruptedException e) { e.printStackTrace(); }
		 return new String[] {"UserBlock5s", "TimeElapsed"};
	 }


}
