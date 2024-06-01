package server;

public class HandleUserCommands {
	
	public static String[] testConnection(String[] commands) { 
		return new String[] {"TestConnect:", "Succes"};
	}
	
	public static String[] startJob(String[] commands) {
		
		System.out.println("Commands:");
		for (int i = 0; i < commands.length; ++i)
			System.out.println(commands[i]);
		System.out.println("========================");
		
		/*System.out.println("Components file: ");
		System.out.println(commands[4]);
		System.out.println("Connections file: ");
		System.out.println(commands[6]);*/
		
		return new String[] {"StartJob", "Job", "Started"};
	}

}
